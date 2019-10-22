package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.service.DesignerProductManageService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.admin.form.*;
import com.xxx.model.business.*;
import javafx.concurrent.Worker;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/designerProduct")
public class DesignerProductManageController {

    @Autowired
    private DesignerProductManageService designerProductManageService;

    @RequestMapping(value = "/designerGoodsHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/DesignerGoodsManage/DesignerGoodsManage";
    }

    @RequestMapping(value = "/designerGoodsEditHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "DesignerGoodsManage/DesignerGoodsEdit";
    }

    @RequestMapping(value = "/designerGoodsDetailHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "DesignerGoodsManage/DesignerGoodsDetail";
    }

    @RequestMapping(value = "/designerManageFromStoreHtml", method = {RequestMethod.GET})
    public String data4_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/DesignerGoodsManage/DesignerGoodsFromStore";
    }


    /**
     * @Description:商品库商品添加到设计师管理商品
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/insertDesignerProducManage", method = {RequestMethod.POST})
    public ResponseEntity insertDesignerProducManage(@RequestBody IdForm form) throws Exception {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        designerProductManageService.insertDesignerProducManage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:商品库商品详情(要判断商品是否已经添加到设计师商品库里面,Product已经被销售平台共用了,所以这边在重写一个)
     * @Author: hanchao
     * @Date: 2017/11/21 0021
     */
    @RequestMapping(value = "/productDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProduct pla = designerProductManageService.getPlaProduct(form.id);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode",pla.getProductCode());
        json.put("name", pla.getName());
        json.put("vedioUrl", OSSClientUtil.getObjectUrl(pla.getVedioUrl()));
        //2017.12.07 图文详情字段隔离
//        json.put("description",pla.getDescription());
        json.put("description",designerProductManageService.getProductDescription(form.id));

        json.put("pno",pla.getPno());
        json.put("price", pla.getPrice());//零售价
        json.put("isDesigner", pla.getDesigner());
        json.put("updateDate", DateTimeUtils.parseStr(pla.getUpdateDate()));
        json.put("remark",pla.getRemarks());
        json.put("colligate",pla.getColligate());
        json.put("sales",pla.getSales());
        json.put("views",pla.getViews());
        json.put("createTime", pla.getCreatedDate() == null ? "" : pla.getCreatedDate().getTime());

        /*判断销售平台是否已经存在商品库的商品(在销售平台的商品详情页会调用这个接口所以多加了这个判定)*/
        DesignerProduct designerProduct = designerProductManageService.getAdminProduct(pla.getId());
        json.put("platProductIdStatus", designerProduct != null ? 1 : 0);//门店里面还没有从商品库添加数据,有,1,没有,0

        JSONArray picture=new JSONArray();
        for (PlaProductPicture plaProductPicture1:pla.getPlaProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("href", OSSClientUtil.getObjectUrl(plaProductPicture1.getHref()));
            picture.add(jsonObject);
        }
        json.put("picture",picture);
        JSONArray labelName = new JSONArray();
        for (PlaProductLabel plaProductLabel : pla.getPlaProductLabelList()){
            JSONObject labelJson = new JSONObject();
            labelJson.put("labelName",plaProductLabel.getLabelNmae());
            labelJson.put("labelId",plaProductLabel.getLabelId());
            labelName.add(labelJson);
        }
        json.put("labelName",labelName);
        json.put("brand",pla.getBrand());
        json.put("productSupplierName", pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//产品供应商名称
        json.put("suplierDay",pla.getSuplierDay());
        json.put("material",pla.getMaterial());
        JSONArray jsonSupplier=new JSONArray();
        for (PlaProductPrice plaProductPrice: pla.getPlaProductPriceList()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("priceNormal",plaProductPrice.getPrice() == null ? "" : plaProductPrice.getPrice() );//正常价格
            jsonObject.put("color", plaProductPrice.getColor());
            jsonObject.put("categoryName",pla.getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getName());
            jsonObject.put("size", plaProductPrice.getSize());
            jsonObject.put("onlinePrice",plaProductPrice.getOnlinePrice() == null ? "" : plaProductPrice.getOnlinePrice());
            jsonObject.put("offlinePrice",plaProductPrice.getOfflinePrice() == null ? "" : plaProductPrice.getOfflinePrice());
            jsonObject.put("stock", plaProductPrice.getStock());
            jsonObject.put("categoryRemark",plaProductPrice.getRemarks());
            jsonSupplier.add(jsonObject);
        }

        JSONArray schannelName = new JSONArray();
        for (PlaProductSaleschannel plaProductSaleschannel: pla.getPlaProductSaleschannelList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("saleschannelName",plaProductSaleschannel.getSaleschannelName());
            jsonObject.put("saleschannelId",plaProductSaleschannel.getSaleschannelId());
            schannelName.add(jsonObject);
        }
        json.put("schannelName",schannelName);
        json.put("jsonSupplier",jsonSupplier);
        for(String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:设计师从商品库新增商品(筛选列表显示)
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/designerAddProductManageList", method = {RequestMethod.POST})
    public ResponseEntity designerAddProductManageList(@RequestBody DesignerAddProductManageForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        List<Integer> ids = new ArrayList<>();
        for (DesignerAddProductCategoryForm id : form.designerProductCategoryList) {
            ids.add(id.categoryId);//父分类
            ids.add(id.relateCategoryId); //子分类
        }
        StringBuffer str = new StringBuffer();
        for (DesignerAddProductLabelForm id : form.designerProductLabelList) str.append(id.labelId + ",");
        if (str.length() > 0) str = new StringBuffer(str.substring(0, str.length() - 1));
        PageList<PlaProduct> plaProductList = designerProductManageService.findDesignerProductManageList(pageQuery, ids, str.toString());
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct pla : plaProductList) {
            JSONObject json = new JSONObject();
            json.put("id", pla.getId());
            json.put("name", pla.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(pla.getHref()));//主图地址
            json.put("price", pla.getMinPrice());
            json.put("suplierDay", pla.getSuplierDay());
            /*判断销售平台是否已经存在商品库的商品*/
            DesignerProduct designerProduct = designerProductManageService.getAdminProduct(pla.getId());
            json.put("platProductIdStatus", designerProduct != null ? 1 : 0);//门店里面还没有从商品库添加数据,有,1,没有,0
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, plaProductList.total), HttpStatus.OK);
    }


    /**
    * @Description:设计师商品上下架
    * @Author: hanchao
    * @Date: 2017/11/17 0017
    */
    @RequestMapping(value = "/deleteDesignerProductStatus", method = {RequestMethod.POST})
    public ResponseEntity deleteDesignerProductStatus(@RequestBody DesignerStatusForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        designerProductManageService.updateDesignerProductStatus(form.id,form.status);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
    * @Description:设计师商品删除
    * @Author: hanchao
    * @Date: 2017/11/17 0017
    */
    @RequestMapping(value = "/deleteDesignerProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteDesignerProduct(@RequestBody DesignerStatusForm form) throws ResponseEntityException, UpsertException {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        designerProductManageService.updateDesignerProductStatus(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:设计师商品编辑详情保存
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/updateDesignerProductManage", method = {RequestMethod.POST})
    public ResponseEntity updateDesignerProductManage(@RequestBody DesignerProductManageForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        designerProductManageService.updateDesignerProduct(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:设计师管理商品编辑详情
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @RequestMapping(value = "/designerProductManageEditDetail", method = {RequestMethod.POST})
    public ResponseEntity salesProductManageEditDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        DesignerProduct des = designerProductManageService.getDesignerProduct(form.id);
        if (des == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode",des.getPlaProduct() == null ? "" : des.getPlaProduct().getProductCode());
        json.put("name", des.getName());
        json.put("platProductId",des.getPlatProductId());
        json.put("vedioKey", des.getVedioUrl());
        json.put("vedioUrl", (OSSClientUtil.getObjectUrl(des.getVedioUrl())));
        json.put("pno",des.getPlaProduct() == null ? "" : des.getPlaProduct().getPno());
        json.put("remarks",des.getRemarks());
        json.put("colligate",des.getColligate());
        json.put("sales",des.getSales());
        json.put("views",des.getViews());
        json.put("description",des.getDescription());
        json.put("categoryName",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getName());
        json.put("categoryId",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getId());
        json.put("brand",des.getBrand());
        json.put("status",des.getStatus());
        //二级分类
        json.put("secondCategoryId",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getId());
        json.put("secondCategoryName",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getName());

        //一级分类
        json.put("firstCategoryId",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getPlaProductCategory().getId());
        json.put("firstCategoryName",des.getPlaProductCategory() == null ? "" : des.getPlaProductCategory().getPlaProductCategory()== null ? "" : des.getPlaProductCategory().getPlaProductCategory().getName());
        /*图片*/
        JSONArray picture=new JSONArray();
        for (DesignerProductPicture designerProductPicture : des.getDesignerProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", designerProductPicture.getHref());
            jsonObject.put("href",OSSClientUtil.getObjectUrl(designerProductPicture.getHref()));
            picture.add(jsonObject);
        }
        json.put("picture",picture);

        /*商品标签*/
        JSONArray jsonLabel = new JSONArray();
        for (DesignerProductLabel designerProductLabel : des.getDesignerProductLabelList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("labelId",designerProductLabel.getLabelId());
            jsonObject.put("labelName",designerProductLabel.getLabelNmae());
            jsonLabel.add(jsonObject);
        }
        json.put("jsonLabel",jsonLabel);

        /*商品规格*/
        JSONArray jsonProduct=new JSONArray();
        for (DesignerProductPrice designerProductPrice : des.getDesignerProductPriceList()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("color", designerProductPrice.getColor());
            jsonObject.put("size", designerProductPrice.getSize());
            jsonObject.put("offlinePrice",designerProductPrice.getOfflinePrice());
            jsonObject.put("stock", designerProductPrice.getStock());
            jsonObject.put("specRemark",designerProductPrice.getRemarks());
            jsonProduct.add(jsonObject);
            //去除null值
            for (String key : jsonObject.keySet()){
                if(jsonObject.get(key) == null) jsonObject.put(key,"");
            }
        }
        json.put("jsonSupplier",jsonProduct);
        //去除null值
        for (String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

   /**
    * @Description:设计师管理的商品列表
    * @Author: hanchao
    * @Date: 2017/11/21 0021
    */
    @RequestMapping(value = "/designerProductManageList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody DesignerProductManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<DesignerProduct> designerProductList = designerProductManageService.findDesignerProductList(pageQuery, form.name, form.categoryId);
        JSONArray jsonArray = new JSONArray();
        for (DesignerProduct designerProduct : designerProductList) {
            JSONObject json = new JSONObject();
            json.put("productCode",designerProduct.getPlaProduct() == null ? "" : designerProduct.getPlaProduct().getProductCode());
            json.put("id",designerProduct.getId());
            json.put("name", designerProduct.getName());
            json.put("categoryName",designerProduct.getPlaProductCategory() == null ? "" : designerProduct.getPlaProductCategory().getName());
            json.put("mainpicHref",OSSClientUtil.getObjectUrl(designerProduct.getHref()));
            json.put("pno", designerProduct.getPlaProduct() == null ? "" : designerProduct.getPlaProduct().getPno());
            json.put("updateDate", DateTimeUtils.parseStr(designerProduct.getUpdateDate()));
            json.put("status",designerProduct.getStatus());
            json.put("brand", designerProduct.getBrand());

            JSONArray supplierList = new JSONArray();
            for (DesignerProductPrice designerProductPrice : designerProduct.getDesignerProductPriceList()) {
                JSONObject supplierJson = new JSONObject();
                supplierJson.put("color", designerProductPrice.getColor());
                supplierJson.put("size", designerProductPrice.getSize());
                supplierJson.put("stock", designerProductPrice.getStock());
                supplierJson.put("offlinePrice", designerProductPrice.getOfflinePrice());
                supplierJson.put("remark", designerProductPrice.getRemarks());
                //去除null值
                for (String key : supplierJson.keySet()){
                    if(supplierJson.get(key) == null) supplierJson.put(key,"");
                }
                supplierList.add(supplierJson);
            }
            json.put("supplierList", supplierList);
            //去除null值
            for (String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, designerProductList.total), HttpStatus.OK);
    }



}
