package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.BoxManageService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
@RequestMapping("/boxManage")
public class BoxManageController {

    @Autowired
    private BoxManageService boxManageService;
    //盒子商品管理
    @RequestMapping(value = "/boxManageHtml", method = {RequestMethod.GET})
    public String boxManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "box/boxGoods";
    }
    //盒子订阅记录
    @RequestMapping(value = "/boxRecordHtml", method = {RequestMethod.GET})
    public String boxRecordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "box/boxRecord";
    }

    //盒子商品管理编辑
    @RequestMapping(value = "/boxManageEditHtml", method = {RequestMethod.GET})
    public String boxManageEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "box/boxGoodsEdit";
    }

    //盒子商品从商品库添加
    @RequestMapping(value = "/boxGoodsFromStoreHtml", method = {RequestMethod.GET})
    public String boxGoodsFromStoreHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id",id);
        return "box/boxGoodsFromStore";
    }

    //体验师管理
    @RequestMapping(value = "/boxExperienceUserHtml", method = {RequestMethod.GET})
    public String boxExperienceUserHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "box/boxExperienceUser";
    }

    //跳转到平台首页页面
    @RequestMapping(value = "/productHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/index";
    }

    /**
     * @Description:点击确认按钮修改订单状态为已完成
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @RequestMapping(value = "/boxRecordPayStatus", method = {RequestMethod.POST})
    public ResponseEntity boxRecordPayStatus(@RequestBody BoxRecordDeliverForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        boxManageService.boxRecordPayStatus(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:获取当前记录号的物流信息
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @RequestMapping(value = "/boxRecordDeliveryList", method = {RequestMethod.POST})
    public ResponseEntity boxRecordDeliveryList(@RequestBody BoxRecordDeliverForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        List<BoxUseLog> boxList = boxManageService.findBoxRecordDeliveryList(form.id);
        JSONArray data = new JSONArray();
        for (BoxUseLog box : boxList) {
            //商品已发出的快递信息
            if(box.getStatus() == 1){
                JSONObject issueDelivery = new JSONObject();
                issueDelivery.put("createTime", DateTimeUtils.parseStr(box.getCreatedDate()));
                issueDelivery.put("deliveryCompanyName", box.getIssueDeliveryCompanyName());
                issueDelivery.put("deliveryNo", box.getIssueDeliveryNo());
                PlaProductBase plaProductBase = boxManageService.get2(PlaProductBase.class,"id",box.getIssueDeliveryCompany());
                issueDelivery.put("deliveryCompany",plaProductBase == null ? "" : plaProductBase.getDescription());
                data.add(issueDelivery);
            }else if(box.getStatus() == 2){
                //商品已退回的快递信息
                JSONObject returnDelivery = new JSONObject();
                returnDelivery.put("createTime", DateTimeUtils.parseStr(box.getCreatedDate()));
                returnDelivery.put("deliveryCompanyName", box.getReturnDeliveryCompanyName());
                returnDelivery.put("deliveryNo", box.getReturnDeliveryNo());
                PlaProductBase plaProductBase = boxManageService.get2(PlaProductBase.class,box.getReturnDeliveryCompany());
                returnDelivery.put("deliveryCompany",plaProductBase == null ? "" : plaProductBase.getDescription());
                data.add(returnDelivery);
            }
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }
    
    /**
     * @Description:盒子商品快递寄出
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @RequestMapping(value = "/boxRecordDeliver", method = {RequestMethod.POST})
    public ResponseEntity saveBoxRecordDeliver(@RequestBody BoxRecordDeliverForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        boxManageService.saveBoxRecordDeliver(form.id,form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }
    
    /**
     * @Description:盒子订阅记录
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxManageRecordList", method = {RequestMethod.POST})
    public ResponseEntity findBoxRecordManageList(@RequestBody BoxRecordDetailForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 99;
        PageList<BoxUseLog> boxManageList = boxManageService.findBoxRecordManageList(pageQuery, form.username, form.startTime, form.endTime, form.status);
        JSONArray data = new JSONArray();
        for (BoxUseLog box : boxManageList) {
            JSONObject json = new JSONObject();
            json.put("id", box.getId());
            json.put("orderNo", box.getOrderNo());
            json.put("createTime", DateTimeUtils.parseStr(box.getCreatedDate()));
            json.put("status", box.getStatus());
            json.put("statusName", Commo.parseBoxUseLogStatus(box.getStatus()));
            //入驻的门店名称
            json.put("username", box.getStoStoreInfo() == null ? "" : box.getStoStoreInfo().getStoreName());
            //账号对应的姓名
            json.put("loginName", box.getStoStoreInfo() == null ? "" : box.getStoStoreInfo().getPubUserLogin() == null ? ""
                    : box.getStoStoreInfo().getPubUserLogin().getPubUserBase() == null ? ""
                    : box.getStoStoreInfo().getPubUserLogin().getPubUserBase().getName());
            //注册手机号
            json.put("mobile", box.getStoStoreInfo() == null ? "" : box.getStoStoreInfo().getPubUserLogin() == null ? ""
                    : box.getStoStoreInfo().getPubUserLogin().getPubUserBase() == null ? ""
                    : box.getStoStoreInfo().getPubUserLogin().getPubUserBase().getMobile());
            //2018.1.8 新增需求：显示订阅人的地址信息：省+市+区+地址信息
            json.put("address", box.getProvinceName() + box.getCityName() + box.getZoneName() + box.getAddress());
            json.put("receiver", box.getReceiver());
            json.put("receiveTel", box.getMobile());

            JSONArray boxRecordList = new JSONArray();
            for (BoxUseLogProduct boxs : box.getBoxUseLogProductList()) {
                JSONObject json1 = new JSONObject();
                json1.put("id", boxs.getId());
                json1.put("boxProductId", boxs.getBoxProductId());
                //2018.1.8 新增需求：增加显示商品ID , 供应商产品编号
                json1.put("productCode", boxs.getBoxProduct().getPlaProduct() == null ? "" : StringUtils.trimToEmpty(boxs.getBoxProduct().getPlaProduct().getProductCode()));
                json1.put("pno", boxs.getBoxProduct().getPlaProduct() == null ? "" : StringUtils.trimToEmpty(boxs.getBoxProduct().getPlaProduct().getPno()));

                json1.put("href", OSSClientUtil.getObjectUrl(boxs.getHref()));
                json1.put("name", boxs.getName());
                json1.put("price", boxs.getPrice());
                json1.put("count", boxs.getCount());
                //付款状态
                json1.put("payStatus", boxs.getStatus());
                json1.put("payStatusName", Commo.parseBoxUseLogProductStatus(boxs.getStatus()));
                boxRecordList.add(json1);
            }
            json.put("boxRecordList", boxRecordList);
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, boxManageList.total), HttpStatus.OK);
    }

    
    /**
     * @Description:商品库商品详情页
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    //商品详情页共用平台商品库详情,直接调用/product/productDetail,不需要保存

    /**
     * @Description:将商品库商品添加到盒子
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/insertBoxProduct", method = {RequestMethod.POST})
    public ResponseEntity insertBoxProduct(@RequestBody IdForm form) throws Exception {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        boxManageService.insertBoxProduct(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:合一盒子从商品库新增商品(列表筛选显示)
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/BoxAddBoxManageList", method = {RequestMethod.POST})
    public ResponseEntity BoxAddBoxManageList(@RequestBody BoxProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        List<Integer> ids = new ArrayList<>();
        for (BoxProductCategoryIdListForm id : form.plaProductCategory) {
            ids.add(id.categoryId);
            ids.add(id.relateCategoryId);
        }
        StringBuffer str = new StringBuffer();
        for (BoxProductLabelIdListForm id : form.plaProductLabelId) {
            str.append(id.labelId + ",");
        }
        if (str.length() > 0) {
            str = new StringBuffer(str.substring(0, str.length() - 1));
        }

        //遍历已添加的商品集合
        PageList<PlaProduct> productList = boxManageService.findProductList(pageQuery, ids, str.toString(), form.startPrice, form.endPrice,form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct pla : productList) {
            JSONObject json = new JSONObject();
            json.put("id", pla.getId());
            json.put("name", pla.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(pla.getHref()));//主图地址
            json.put("price", pla.getMinPrice());
            json.put("suplierDay", pla.getSuplierDay());

           /*判断盒子里面是否已经存在商品库的商品*/
            BoxProduct boxProduct = boxManageService.get2(BoxProduct.class,"pid",pla.getId(),"logicDeleted",false);
            json.put("platProductIdStatus", boxProduct != null ? 1 : 0);//判断商品库商品是否存在盒子里面,有,1,没有,0
            for(String key : json.keySet()){
                if (json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, productList.total), HttpStatus.OK);
    }

    /**
     * @Description:盒子商品上下架
     * @Author: hanchao
     * @Date: 2017/12/23 0023
     */
    @RequestMapping(value = "/updateProductStatus", method = {RequestMethod.POST})
    public ResponseEntity updateProductStatus(@RequestBody ProductStatusForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        boxManageService.updateProductStatus(form.id,form.status);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:盒子商品删除
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/deleteProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteProduct(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        boxManageService.updateProductId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:合一盒子详情保存
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxManageDetailEdit", method = {RequestMethod.POST})
    public ResponseEntity updateBoxManageDetailEdit(@RequestBody BoxDetailForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        boxManageService.updateBoxManageDetailEdit(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:合一盒子商品详情
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxManageDetail", method = {RequestMethod.POST})
    public ResponseEntity boxManageDetail(@RequestBody IdForm form) throws Exception {
        BoxProduct box = boxManageService.get2(BoxProduct.class,form.id);
        if (box == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONArray jsonArray = new JSONArray();
        if (box.getPlaProduct() == null) box.setPlaProduct(new PlaProduct());
        if (box.getPlaProduct().getPlaProductCategory() == null)
            box.getPlaProduct().setPlaProductCategory(new PlaProductCategory());
        JSONObject json = new JSONObject();
        json.put("id", box.getId());
        json.put("name", box.getPlaProduct().getName());
        json.put("productCode", box.getPlaProduct().getProductCode());
        json.put("categoryName", box.getPlaProduct().getPlaProductCategory().getName());
        json.put("updateDate", DateTimeUtils.parseStr(box.getPlaProduct().getUpdateDate()));
        //0:下架   1:上架
        json.put("status", box.getStatus());
        json.put("statusName",Commo.parseBoxProductStatus(box.getStatus()));
        //二维码显示商品详情
        json.put("description", box.getDescription());
        json.put("stock",box.getStock());
        //订购数量
        JSONObject js = boxManageService.findOrderCount(box.getId());
        json.put("orderCount", js == null ? 0 : js.get("orderCount"));
//        //二级分类
//        json.put("secondCategoryId",box.getPlaProduct().getPlaProductCategory() == null ? "" : box.getPlaProduct().getPlaProductCategory().getId());
//        json.put("secondCategoryName",box.getPlaProduct().getPlaProductCategory() == null ? "" : box.getPlaProduct().getPlaProductCategory().getName());
//
//        //一级分类
//        json.put("firstCategoryId",box.getPlaProduct().getPlaProductCategory().getPlaProductCategory() == null ? "" : box.getPlaProduct().getPlaProductCategory().getPlaProductCategory().getId());
//        json.put("firstCategoryName",box.getPlaProduct().getPlaProductCategory().getPlaProductCategory() == null ? "" : box.getPlaProduct().getPlaProductCategory().getPlaProductCategory().getName());
        JSONArray jsonSupplier=new JSONArray();
        if(box.getPlaProduct().getPlaProductPriceList().size() > 0){
            for (PlaProductPrice plaProductPrice: box.getPlaProduct().getPlaProductPriceList()){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("offlinePrice",plaProductPrice.getOfflinePrice());//线下指导价
                jsonObject.put("color", plaProductPrice.getColor());
                jsonObject.put("size", plaProductPrice.getSize());
                jsonSupplier.add(jsonObject);
            }
            json.put("jsonSupplier",jsonSupplier);
        }

        JSONArray picture = new JSONArray();
        if(box.getPlaProduct().getPlaProductPictureList().size() > 0){
            for (PlaProductPicture plaProductPicture : box.getPlaProduct().getPlaProductPictureList()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", plaProductPicture.getHref());
                jsonObject.put("href",OSSClientUtil.getObjectUrl(plaProductPicture.getHref()));
                picture.add(jsonObject);
            }
            json.put("picture",picture);
        }
        //去除NULL值
        for(String key : json.keySet()){
            if(json.get(key) == null)json.put(key,"");
        }
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

  /**
   * @Description: 体验师管理列表
   * @Author: Steven.Xiao
   * @Date: 2017/12/24
   */
    @RequestMapping(value = "/findSalesTeacherList", method = {RequestMethod.POST})
    public ResponseEntity findSalesTeacherList(@RequestBody SalesTeacherPageForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesTeacher> salesTeacherList = boxManageService.findSalesTeacherList(pageQuery);
        JSONArray jsonArray = new JSONArray();
        for (SalesTeacher teacher : salesTeacherList) {
            JSONObject json = new JSONObject();
            json.put("id", teacher.getId());
            json.put("name", teacher.getName());
            json.put("age", teacher.getAge());
            json.put("mobile", teacher.getMobile());
            json.put("createdDate", DateTimeUtils.parseStr(teacher.getCreatedDate()));

            //去除NULL值
            for(String key : json.keySet()){
                if(json.get(key) == null)json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesTeacherList.total), HttpStatus.OK);
    }
    

    /**
     * @Description:合一盒子平台管理
     * @Author: hanchao
     * @Date: 2017/12/22 0022
     */
    @RequestMapping(value = "/boxManageList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody BoxListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<BoxProduct> boxProductList = boxManageService.findBoxManageList(pageQuery, form.name, form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (BoxProduct box : boxProductList) {
            if (box.getPlaProduct() == null) box.setPlaProduct(new PlaProduct());
            if (box.getPlaProduct().getPlaProductCategory() == null)
                box.getPlaProduct().setPlaProductCategory(new PlaProductCategory());
            JSONObject json = new JSONObject();
            json.put("id", box.getId());
            json.put("name", box.getPlaProduct().getName());
            json.put("href", OSSClientUtil.getObjectUrl(box.getPlaProduct().getHref()));
            json.put("productCode", box.getPlaProduct().getProductCode());
            json.put("categoryId", box.getPlaProduct().getCategoryId());
            json.put("categoryName",box.getPlaProduct().getPlaProductCategory().getName());
            json.put("maxPrice", box.getPlaProduct().getMaxPrice());
            json.put("updateDate", DateTimeUtils.parseStr(box.getUpdateDate()));
            //0:下架   1:上架
            json.put("status", box.getStatus());
            json.put("statusName",Commo.parseBoxProductStatus(box.getStatus()));
            //二维码显示商品详情
//            json.put("description", box.getDescription());
            json.put("description","");
            json.put("stock",box.getStock());
            //订购数量
            JSONObject js = boxManageService.findOrderCount(box.getId());
            json.put("orderCount", js == null ? 0 : js.get("orderCount"));
            //去除NULL值
            for(String key : json.keySet()){
                if(json.get(key) == null)json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, boxProductList.total), HttpStatus.OK);
    }
}
