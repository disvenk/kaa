package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.service.SuplierService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.ImportExcelUtil;
import com.xxx.utils.MD5Utils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.admin.form.*;
import com.xxx.model.business.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/suplier")
public class SuplierManageController {

    @Autowired
    private SuplierService suplierService;

    //跳转到供应商管理页面
    @RequestMapping(value = "/suplierHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierManage";
    }

    //跳转到供应商新增页面
    @RequestMapping(value = "/supplierManageAddHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierManageAdd";
    }

    //跳转到供应商编辑页面
    @RequestMapping(value = "/supplierManageEditHtml", method = {RequestMethod.GET})
    public String dataEdit_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
//        String userId = request.getParameter("userId");
        modelMap.put("id", id);
//        modelMap.put("userId", userId);
        return "/supplierManage/supplierManageEdit";
    }

    //跳转到供应商货品管理页面
    @RequestMapping(value = "/supplierGoodsManageHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierGoodsManage";
    }

    //跳转到供应商货品编辑页面
    @RequestMapping(value = "/supplierGoodsManageEditHtml", method = {RequestMethod.GET})
    public String data4_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/supplierManage/supplierGoodsManageEdit";
    }

    //跳转到供应商货品新增页面
    @RequestMapping(value = "/supplierGoodsManageAddHtml", method = {RequestMethod.GET})
    public String data5_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierGoodsManageAdd";
    }

    //跳转到采购价管理列表页面
    @RequestMapping(value = "/supplierGoodsListManageHtml", method = {RequestMethod.GET})
    public String data6_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierGoodsList";
    }

    //跳转到采购价管理列表新增页面
    @RequestMapping(value = "/supplierGoodsListAddHtml", method = {RequestMethod.GET})
    public String data7_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/supplierManage/supplierGoodsListAdd";
    }

    //跳转到采购价管理列表编辑页面
    @RequestMapping(value = "/supplierGoodsListEditHtml", method = {RequestMethod.GET})
    public String data8_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/supplierManage/supplierGoodsListEdit";
    }

    /**
     * @Description:供货价批量导入
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @RequestMapping(value="/uploadSupplierManage",method = {RequestMethod.POST})
    private ResponseEntity uploadSupplierManage(MultipartFile myFile) throws IOException, UpsertException, ResponseEntityException {
        List<String[]> list = ImportExcelUtil.readExcel(myFile); //这里得到的是一个集合，里面的每一个元素是String[]数组
        suplierService.saveBath(list); //service实现方法
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }



    /**
     * @Description:供应商用户批量删除
     * @Author: hanchao
     * @Date: 2017/11/16 0016
     */
    @RequestMapping(value = "/deleteMoreSuplier", method = {RequestMethod.POST})
    public ResponseEntity deleteMoreSuplier(@RequestBody SuplierMoreIdForm form) throws UpsertException,ResponseEntityException {
        if (form.suplierIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
        List<Integer> ids = new ArrayList<>();
        for (IdForm id : form.suplierIds){
            ids.add(id.id);
        }
        suplierService.updateMoreSuplier(ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:供应商商品删除
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/deleteSuplierGoods", method = {RequestMethod.POST})
    public ResponseEntity deleteSuplierGoods(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        suplierService.updateSuplierGoodsId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:供货价货品根据商品id带出数据
     * @Author: hanchao
     * @Date: 2017/11/29 0029
     */
    @RequestMapping(value = "/suplierGoodsAddListFormId", method = {RequestMethod.POST})
    public ResponseEntity suplierGoodsAddListFormId(@RequestBody ProductCodeForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        if(StringUtils.isBlank(form.productCode))
            return new ResponseEntity(new RestResponseEntity(120, "商品编号不能为空", null), HttpStatus.OK);
        PlaProduct pla = suplierService.getPlaProductByProductCode(form.productCode);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode", pla.getProductCode());//商品id
        json.put("pno", pla.getPno());
        json.put("name", pla.getName());//商品名称
        json.put("suplierDay", pla.getSuplierDay());
        json.put("mainHref",OSSClientUtil.getObjectUrl(pla.getHref()));
        json.put("suplierName", pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//供应商名称
        json.put("suplierRemark", pla.getSupplierRemarks());//备注
        json.put("suplierPrice", pla.getSuplierPrice());
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:供货价货品列表新增
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @RequestMapping(value = "/insertSuplierGoodsList", method = {RequestMethod.POST})
    public ResponseEntity insertSuplierGoodsList(@RequestBody SuplierManageGoodsListForm form) throws Exception {
        PlaProduct pla = suplierService.getPlaProductByProductCode(form.productCode);
        if(pla == null){
            throw new ResponseEntityException(120, "您修改的商品编号不存在");
        }
        if(StringUtils.isNotBlank(form.pno)){
            PlaProduct plaProduct = suplierService.selectAddPlaproductPno(form.productCode,form.pno);
            if (plaProduct != null)
                throw new ResponseEntityException(120, "货号已存在");
        }
        if (form.SupplierId == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商名称不能为空", null), HttpStatus.OK);
        if (form.suplierDay == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应周期不能为空", null), HttpStatus.OK);
        if (form.suplierPrice == null)
            return new ResponseEntity(new RestResponseEntity(110, "供货价不能为空", null), HttpStatus.OK);
        suplierService.insertSuplierGoodsList(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    
    /**
     * @Description:供货价货品列表编辑保存
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @RequestMapping(value = "/updateSuplierGoodsList", method = {RequestMethod.POST})
    public ResponseEntity updateSuplierGoodsList(@RequestBody SuplierManageGoodsListForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
       if(StringUtils.isNotBlank(form.pno)){
           PlaProduct plaProduct = suplierService.selectPlaproductPno(form.id,form.pno);
           if (plaProduct != null)
               throw new ResponseEntityException(120, "货号已存在");
       }
       if (form.SupplierId == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商名称不能为空", null), HttpStatus.OK);
       if (form.suplierDay == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应周期不能为空", null), HttpStatus.OK);
       if (form.suplierPrice == null)
            return new ResponseEntity(new RestResponseEntity(110, "供货价不能为空", null), HttpStatus.OK);
        suplierService.updateSuplierGoodsList(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:供货价列表编辑页面
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @RequestMapping(value = "/suplierGoodsEditListDetail", method = {RequestMethod.POST})
    public ResponseEntity suplierGoodsEditListDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProduct pla = suplierService.getPlaProduct(form.id);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", pla.getId());//商品id
        json.put("productCode", pla.getProductCode());//商品编码
        json.put("pno", pla.getPno());//商品编码
        json.put("name", pla.getName());
        json.put("updateDate", DateTimeUtils.parseStr(pla.getUpdateDate()));
        json.put("suplierName", pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//供应商名称
        json.put("suplierDay", pla.getSuplierDay());
        json.put("suplierRemark", pla.getSupplierRemarks());//备注
        json.put("suplierPrice", pla.getSuplierPrice());
        json.put("mainHref",OSSClientUtil.getObjectUrl(pla.getHref()));
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);

    }
    /**
     * @Description:供货价商品删除
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/deleteSuplierGoodsListGoods", method = {RequestMethod.POST})
    public ResponseEntity deleteSuplierGoodsListGoods(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        suplierService.updateSuplierGoodsListId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:供货价列表数据显示
     * @Author: hanchao
     * @Date: 2017/11/27 0027
     */
    @RequestMapping(value = "/suplierGoodsListManage", method = {RequestMethod.POST})
    public ResponseEntity suplierGoodsListManage(@RequestBody SuplierGoodsManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<PlaProduct> PlaProductList = suplierService.findSuplierGoodsList(pageQuery, form.name, form.productCode,form.suplierName);
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct pla : PlaProductList) {
            JSONObject json = new JSONObject();
            json.put("id",pla.getId());//商品id
            json.put("productCode", pla.getProductCode() == null ? "" : pla.getProductCode());//商品编码
            json.put("name", pla.getName() == null ? "" : pla.getName());
            json.put("pno", pla.getPno() == null ? "" : pla.getPno());
            json.put("suplierName",pla.getSupSuplier() == null ? "" : pla.getSupSuplier().getName());//供应商名称
            json.put("mainpicHref",OSSClientUtil.getObjectUrl(pla.getHref()));//主图地址
            json.put("suplierDay", pla.getSuplierDay() == null ? "" : pla.getSuplierDay());//供货周期
            json.put("price", pla.getSuplierPrice() == null ? "" : pla.getSuplierPrice());//供货价
            json.put("updateDate",DateTimeUtils.parseStr(pla.getUpdateDate()));
            json.put("remark", pla.getRemarks() == null ? "" : pla.getRemarks());
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, PlaProductList.total), HttpStatus.OK);
    }
    
    /**
     * @Description:货品管理详情编辑数据新增
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/insertSuplierGoods", method = {RequestMethod.POST})
    public ResponseEntity insertSuplierGoods(@RequestBody SuplierManageGoodsForm form) throws Exception {
        SupProduct supProduct = suplierService.selectPno(form.pno);
        if (supProduct != null)
            throw new ResponseEntityException(120, "货号已存在");
        suplierService.insertSuplierGoods(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:货品管理详情编辑数据保存
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/updateSuplierGoods", method = {RequestMethod.POST})
    public ResponseEntity updateSuplierGoods(@RequestBody SuplierManageGoodsForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
        SupProduct supProduct = suplierService.selectPno(form.id,form.pno);
        if (supProduct != null)
            throw new ResponseEntityException(120, "货号已存在");
        suplierService.updateSuplierGoods(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:货品管理详情编辑数据显示
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/suplierGoodsEditDetail", method = {RequestMethod.POST})
    public ResponseEntity suplierGoodsEditDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        SupProduct sup = suplierService.getSupProduct(form.id);
//        if (sup == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商商品信息不存在", null), HttpStatus.OK);

//        JSONObject json = new JSONObject();
//        json.put("id", sup.getId());//商品id
//        json.put("name", sup.getName());
//        json.put("pno", sup.getPno());
//        json.put("updateDate", DateTimeUtils.parseStr(sup.getUpdateDate()));
//        json.put("sort", sup.getSort());//排序
//        json.put("suplierDay", sup.getSuplierDay());
//        json.put("sort", sup.getSort());
//        json.put("suplierRemark", sup.getRemarks());//供应商商品备注
//        json.put("brand", sup.getBrand());
//        //json.put("categoryName", sup.getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getName());
//        json.put("material", sup.getMaterial());//面料使用
//
//        //二级分类
//        json.put("secondCategoryId", sup.getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getId());
//        json.put("secondCategoryName", sup.getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getName());
//
//        //一级分类
//        json.put("firstCategoryId", sup.getPlaProductCategory().getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getPlaProductCategory().getId());
//        json.put("firstCategoryName", sup.getPlaProductCategory().getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getPlaProductCategory().getName());
//
//
//        JSONArray supplierList = new JSONArray();
//        for (SupProductPrice supProductPrice : sup.getSupProductPriceList()) {
//            JSONObject suplierJson = new JSONObject();
//            suplierJson.put("offlinePrice", sup.getPrice());//线下价
//            suplierJson.put("color", supProductPrice.getColor());
//            suplierJson.put("size", supProductPrice.getSize());
//            suplierJson.put("stock", sup.getStock());
//            suplierJson.put("categoryRemark", supProductPrice.getRemarks());
//            supplierList.add(suplierJson);
//        }
//        JSONArray picture = new JSONArray();
//        for (SupProductPicture supProductPicture : sup.getSupProductPictureList()) {
//            JSONObject pictureJson = new JSONObject();
//            pictureJson.put("key", supProductPicture.getHref());
//            pictureJson.put("href", OSSClientUtil.getObjectUrl(supProductPicture.getHref()));
//            picture.add(pictureJson);
//        }
//        json.put("supplierList", supplierList);
//        json.put("picture", picture);
//        jsonArray.add(json);
//        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);

    }

    /**
     * @Description:货品管理列表显示
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/goodsManageList", method = {RequestMethod.POST})
    public ResponseEntity goodsManageList(@RequestBody GoodsManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupProduct> SupProductList = suplierService.findgoodsManageList(pageQuery, form.name, form.categoryId,form.suplierName);
        JSONArray jsonArray = new JSONArray();
//        for (SupProduct sup : SupProductList) {
//            JSONObject json = new JSONObject();
//            json.put("id",sup.getId());//商品id
//            json.put("name", sup.getName());
//            json.put("suplierName",sup.getSupSuplier() == null ? "" : sup.getSupSuplier().getName());//供应商名称
//            json.put("mainpicHref",OSSClientUtil.getObjectUrl(sup.getHref()));//主图地址
//            json.put("pno", sup.getPno());
//            json.put("updateDate",DateTimeUtils.parseStr(sup.getUpdateDate()));
//            json.put("brand",sup.getBrand());
//            json.put("categoryName",sup.getPlaProductCategory() == null ? "" : sup.getPlaProductCategory().getName());
//            json.put("material",sup.getMaterial());
//
//            JSONArray supplierList = new JSONArray();
//            for(SupProductPrice supProductPrice : sup.getSupProductPriceList()){
//                JSONObject suplierJson = new JSONObject();
//                suplierJson.put("offlinePrice",sup.getPrice());//线下价格
//                suplierJson.put("stock",sup.getStock());//库存
//                suplierJson.put("color",supProductPrice.getColor());
//                suplierJson.put("size",supProductPrice.getSize());
//                suplierJson.put("categoryRemark",supProductPrice.getRemarks());
//                for (String key : suplierJson.keySet()){
//                    if (suplierJson.get(key) == null) suplierJson.put(key, "");
//                }
//                supplierList.add(suplierJson);
//            }
//
//            json.put("supplierList", supplierList);
//            jsonArray.add(json);
//        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, SupProductList.total), HttpStatus.OK);
    }

    /**
     * @Description:重置密码
     * @Author: hanchao
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/updateSuplierPassword", method = {RequestMethod.POST})
    public ResponseEntity updateSuplierPassword(@RequestBody IdForm form) throws Exception {
        suplierService.updateSuplierPassword(form.id, MD5Utils.md5Hex("888888"));
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:供应商审核历史
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/suplierApproveHistory", method = {RequestMethod.POST})
    public ResponseEntity suplierApproveHistory(@RequestBody SuplierIdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        List<SupApproveLog> supApproveLogs = suplierService.getSupApproveLog(form.suplierId);
        for (SupApproveLog sup :  supApproveLogs){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", sup.getSupSuplier() == null ? "" : sup.getSupSuplier().getName());//供应商名称
            jsonObject.put("explain",sup.getDescription() == null ? " " : sup.getDescription());//说明
            jsonObject.put("operate",sup.getOperate());//操作
            jsonObject.put("approveDate", DateTimeUtils.parseStr(sup.getApproveDate()));//审核时间
            jsonArray.add(jsonObject);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:供应商审核通过/不通过
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/updateApproveStatus", method = {RequestMethod.POST})
    public ResponseEntity updateApproveStatus(@RequestBody SuplierManageEditListForm form) throws Exception {
        if (form.id == null)//1,通过,2,不通过
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.code))
            return new ResponseEntity(new RestResponseEntity(120, "供应商编码不能为空", null), HttpStatus.OK);
        SupSuplier sup = suplierService.selectCode(form.id,form.code);
        if (sup != null)
            return new ResponseEntity(new RestResponseEntity(130, "供应商编码已存在", null), HttpStatus.OK);
        suplierService.updateApproveStatus(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:供应商审核接口
     * @Author: hanchao
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/suplierApproveDetail", method = {RequestMethod.POST})
    public ResponseEntity suplierApproveDetail(@RequestBody IdForm form) throws Exception {
        SupSuplier sup=suplierService.getSupSuplier(form.id);
        if (sup == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("name", sup.getName());//供应商名称
        json.put("code", sup.getCode());//供应商编号
        json.put("explain","");//说明
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }


    /**
     * @Description:供应商用户删除
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @RequestMapping(value = "/deleteSuplier", method = {RequestMethod.POST})
    public ResponseEntity deleteSuplier(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
        suplierService.updateSupliertId(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    
    /**
     * @Description:供应商管理后台新增
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @RequestMapping(value = "/insertSuplier", method = {RequestMethod.POST})
    public ResponseEntity insertSuplier(@RequestBody SuplierManageEditListForm form) throws Exception {
        SupSuplier supSuplier = suplierService.selectMobile(form.mobile);
        if (supSuplier != null)
            throw new ResponseEntityException(120, "手机号已注册");
         supSuplier = suplierService.selectCode(form.code);
        if (supSuplier != null)
            throw new ResponseEntityException(120, "供应商编号已存在");
        suplierService.insertSuplier(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:供应商管理后台编辑保存
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @RequestMapping(value = "/updateSuplier", method = {RequestMethod.POST})
    public ResponseEntity updateSuplier(@RequestBody SuplierManageEditListForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商id不能为空", null), HttpStatus.OK);
        SupSuplier supSuplier = suplierService.selectMobile(form.id,form.mobile);
        if (supSuplier != null)
            throw new ResponseEntityException(120, "手机号已注册");
        supSuplier = suplierService.selectCode(form.id,form.code);
        if (supSuplier != null)
            throw new ResponseEntityException(130, "供应商编号已存在");
        suplierService.updateSuplier(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:供应商信息编辑数据显示
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @RequestMapping(value = "/suplierEditDetail", method = {RequestMethod.POST})
    public ResponseEntity suplierEditDetail(@RequestBody IdForm form) throws Exception {
        SupSuplier sup=suplierService.getSupSuplier(form.id);
        if (sup == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("name", sup.getName());//供应商名称
        json.put("icon",(sup.getPubUserLogin() == null ? "" : sup.getPubUserLogin().getPubUserBase()) == null ? "" : (OSSClientUtil.getObjectUrl(sup.getPubUserLogin().getPubUserBase().getIcon())));//头像
        json.put("address",sup.getAddress());
        json.put("code",sup.getCode());//供应商编号
        json.put("openYears", sup.getOpenYears());
        json.put("smith", sup.getSmith());//车工
        json.put("sewer",sup.getSewer());//裁剪
        json.put("editer",sup.getEditer());//版型师
        json.put("modelSet",sup.getModelSet());//模架
        json.put("description",sup.getDescription());//公司介绍
        json.put("qualifications", OSSClientUtil.getObjectUrl(sup.getQualifications()));//资质信息
        json.put("contact",sup.getContact());//联系人
        json.put("openYears",sup.getOpenYears());//开始年限
        json.put("names",(sup.getPubUserLogin() == null ? "" : sup.getPubUserLogin().getPubUserBase()) == null ? "" : sup.getPubUserLogin().getPubUserBase().getName());//联系人姓名
        json.put("userId",sup.getPubUserLogin().getId());//用户id
        json.put("sex",(sup.getPubUserLogin() == null ? "" : sup.getPubUserLogin().getPubUserBase()) == null ? "" : sup.getPubUserLogin().getPubUserBase().getSex());//性别
        json.put("personID",(sup.getPubUserLogin() == null ? "" : sup.getPubUserLogin().getPubUserBase()) == null ? "" : sup.getPubUserLogin().getPubUserBase().getPersonID());//身份证号
        json.put("mobile",(sup.getPubUserLogin() == null ? "" : sup.getPubUserLogin().getPubUserBase()) == null ? "" : sup.getPubUserLogin().getPubUserBase().getMobile());//手机号
        json.put("approveStatus",sup.getApproveStatus());//审核状态
        json.put("scopeName",sup.getScope());//主营业务
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description:供应商管理列表显示
     * @Author: hanchao
     * @Date: 2017/11/2 0002
     */
    @RequestMapping(value = "/suplierList", method = {RequestMethod.POST})
    public ResponseEntity suplierList(@RequestBody SuplierManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupSuplier> supSuplierList = suplierService.findSupSuplierList(pageQuery, form.name, form.approveStatus);
        JSONArray jsonArray = new JSONArray();
        for (SupSuplier sup : supSuplierList) {
            JSONObject json = new JSONObject();
            json.put("id",sup.getId());
            json.put("code",sup.getCode());//供应商编号
            json.put("name", sup.getName());
            json.put("contact",sup.getContact());//联系人
            json.put("contactPhone",sup.getContactPhone());//联系电话
            json.put("address", sup.getAddress());
            json.put("openYears",sup.getOpenYears());//开厂年限
            json.put("approveStatus",sup.getApproveStatus());//审核状态
            json.put("icon",sup.getPubUserLogin() == null ? "" :
                    sup.getPubUserLogin().getPubUserBase() == null ? "" :
                            OSSClientUtil.getObjectUrl(sup.getPubUserLogin().getPubUserBase().getIcon()));//头像
            //2017.12.11 增加显示供应商注册账号，注册手机号
            //用户名
            json.put("userCode",sup.getPubUserLogin() == null ? "":sup.getPubUserLogin().getUserCode());
            //注册手机号
            json.put("registerMobile",sup.getPubUserLogin() == null ? "" :
                    sup.getPubUserLogin().getPubUserBase() == null ? "" :
                            sup.getPubUserLogin().getPubUserBase().getMobile());

//            json.put("qualifications", OSSClientUtil.getObjectUrl(sup.getQualifications()));//营业执照,资质信息

            //2018.1.22 增加显示注册时间
            json.put("createdDate",DateTimeUtils.parseStr(sup.getCreatedDate()));

                json.put("scope",sup.getScope());//主营业务
            // 过滤value == null 的数据    使其为空
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supSuplierList.total), HttpStatus.OK);
    }
}
