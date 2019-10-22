package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.service.SalesHeadCategoryService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.admin.form.*;
import com.xxx.model.business.SalesHeadCategory;
import com.xxx.model.business.SalesHeadCategoryProduct;
import com.xxx.model.business.SalesProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/salesHeadCategory")
public class SalesHeadCategoryController {

    @Autowired
    private SalesHeadCategoryService salesHeadCategoryService;

    /**
     * @Description:  运营位商品板块管理页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesHeadCategoryManageHtml", method = {RequestMethod.GET})
    public String SalesHeadCategoryManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesHeadCategoryManage";
    }

    /**
     * @Description:  运营位商品板块：新增页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesHeadCategoryAddHtml", method = {RequestMethod.GET})
    public String salesHeadCategoryAdd(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesHeadCategoryAdd";
    }
    //    底部视频管理
    @RequestMapping(value = "/bottomVideoHtml", method = {RequestMethod.GET})
    public String bottomVideoHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bottomVideo";
    }

    @RequestMapping(value = "/bottomVideoEditHtml", method = {RequestMethod.GET})
    public String bottomVideoEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bottomVideEdit";
    }

    @RequestMapping(value = "/bottomVideoAddHtml", method = {RequestMethod.GET})
    public String bottomVideoAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bottomVideoAdd";
    }

    /**
     * @Description:  运营位商品板块：编辑页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesHeadCategoryEditHtml", method = {RequestMethod.GET})
    public String salesHeadCategoryEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesHeadCategoryEdit";
    }

    /**
     * @Description:  运营位商品列表：
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesHeadCategoryProductManageHtml", method = {RequestMethod.GET})
    public String salesHeadCategoryProductManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesHeadCategoryProductManage";
    }

    /**
     * @Description:  销售平台商品列表：
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesGoodsManageHeadCategoryHtml", method = {RequestMethod.GET})
    public String salesGoodsManageHeadCategoryHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesGoodsManageHeadCategory";
    }

    /**
     * @Description: 查询 运营位的板块列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findSalesHeadCategoryList", method = {RequestMethod.POST})
    public ResponseEntity findSalesHeadCategoryList(@RequestBody HeadCategoryQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesHeadCategory> salesHeadCategoryPageList = salesHeadCategoryService.findSalesHeadCategoryList(pageQuery,form.name);
        JSONArray jsonArray=new JSONArray();
        for(SalesHeadCategory category:salesHeadCategoryPageList) {
            JSONObject json = new JSONObject();
            json.put("id", category.getId());
            json.put("name", category.getName());
            json.put("description", category.getDescription());
            json.put("updateDate", DateTimeUtils.parseStr(category.getUpdateDate()));
            json.put("sort", category.getSort());
            json.put("status", category.getStatus());
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesHeadCategoryPageList.total), HttpStatus.OK);
    }

    /**
     * @Description: 根据Id 取得运营位信息
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @RequestMapping(value = "/getSalesHeadCategory", method = {RequestMethod.POST})
    public ResponseEntity getSalesHeadCategory(@RequestBody IdForm form) throws Exception {
       SalesHeadCategory category= salesHeadCategoryService.getSalesHeadCategory(form.id);
       JSONObject json=new JSONObject();
        json.put("name",category.getName());
        json.put("description",category.getDescription());
        json.put("sort",category.getSort());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 删除 运营位
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/deleteSalesHeadCategory", method = {RequestMethod.POST})
    public ResponseEntity deleteSalesHeadCategory(@RequestBody IdForm form) throws Exception {
        salesHeadCategoryService.deleteSalesHeadCategory(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 设置 显示/隐藏 运营位   1:显示 0：隐藏
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/setDisabledSalesHeadCategory", method = {RequestMethod.POST})
    public ResponseEntity setDisabledSalesHeadCategory(@RequestBody HeadCategoryDisabledForm form) throws Exception {
        salesHeadCategoryService.setDisabledSalesHeadCategory(form.id, form.status);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 运营位：编辑后保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/saveEditSalesHeadCategory", method = {RequestMethod.POST})
    public ResponseEntity saveEditSalesHeadCategory(@RequestBody HeadCategoryEditForm form) throws Exception {
        salesHeadCategoryService.saveEditSalesHeadCategory(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 运营位：新增
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/addSalesHeadCategory", method = {RequestMethod.POST})
    public ResponseEntity addSalesHeadCategory(@RequestBody HeadCategoryAddForm form) throws Exception {
        salesHeadCategoryService.addSalesHeadCategory(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 根据运营位 Id,进入商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findSalesHeadCategoryProductList", method = {RequestMethod.POST})
    public ResponseEntity findSalesHeadCategoryProductList(@RequestBody HeadCategoryProductListQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesHeadCategoryProduct> salesHeadCategoryProductPageList = salesHeadCategoryService.findSalesHeadCategoryProductList(pageQuery, form.categoryId);
        JSONArray jsonArray = new JSONArray();
        for (SalesHeadCategoryProduct product : salesHeadCategoryProductPageList) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("name", product.getSalesProduct() == null ? "" : product.getSalesProduct().getName());
            json.put("href", product.getSalesProduct() == null ? "" : OSSClientUtil.getObjectUrl(product.getSalesProduct().getHref()));
            json.put("productCode", product.getSalesProduct().getPlaProduct() == null ? "" : product.getSalesProduct().getPlaProduct().getProductCode());
            json.put("categoryName", product.getSalesProduct().getPlaProductCategory() == null ? "" : product.getSalesProduct().getPlaProductCategory().getName());
            json.put("updateDate", product.getUpdateDate());
            json.put("sort", product.getSort());
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesHeadCategoryProductPageList.total), HttpStatus.OK);
    }

    /**
     * @Description: 删除运营位的商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/deleteSalesHeadCategoryProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteSalesHeadCategoryProduct(@RequestBody IdForm form) throws Exception {
        salesHeadCategoryService.deleteSalesHeadCategoryProduct(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 查询平台商品库的商品，以便增加到运营位的商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findSalesProductList", method = {RequestMethod.POST})
    public ResponseEntity findSalesProductList(@RequestBody HeadCategorySalesProductListQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesProduct> salesProductList = salesHeadCategoryService.findSalesProductList(pageQuery, form.name, form.categoryId,form.headCategoryId,form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (SalesProduct product : salesProductList) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("productCode", product.getPlaProduct() == null ? "" : product.getPlaProduct().getProductCode());
            json.put("name", product.getName());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            json.put("updateDate", DateTimeUtils.parseStr(product.getUpdateDate()));
            json.put("sort", product.getSort());
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesProductList.total), HttpStatus.OK);
    }

    /**
     * @Description:  从平台商品库，选择相关的商品加入到 运营位列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @RequestMapping(value = "/addSalesHeadCategoryProduct", method = {RequestMethod.POST})
    public ResponseEntity addSalesHeadCategoryProduct(@RequestBody HeadCategoryProductIdsForm form) throws Exception {
        if (form.headCategoryId == null)
            return new ResponseEntity(new RestResponseEntity(110, "请选择对应的运营位", null), HttpStatus.OK);
        if (form.productIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "请选择要加入的商品", null), HttpStatus.OK);
        salesHeadCategoryService.addSalesHeadCategoryProduct(form.headCategoryId, form.productIds);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
