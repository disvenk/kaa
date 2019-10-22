package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.BannerService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SalesBanner;
import com.xxx.model.business.SalesBannerProduct;
import com.xxx.model.business.SalesProduct;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
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
@RequestMapping("/banner")
public class BannerController {


    @Autowired
    private BannerService bannerService;

    /**
     * @Description: Banner管理列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/bannerManageHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bannerManage";
    }

    /**
     * @Description:  Banner：新增页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/bannerAddHtml", method = {RequestMethod.GET})
    public String bannerAddHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bannerAdd";
    }

    /**
     * @Description:  Banner：编辑页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/bannerEditHtml", method = {RequestMethod.GET})
    public String bannerEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bannerEdit";
    }

    /**
     * @Description:  Banner：销售平台的商品管理列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/salesGoodsManageBannerHtml", method = {RequestMethod.GET})
    public String salesGoodsManageBannerHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/salesGoodsManageBanner";
    }

    /**
     * @Description:  Banner：下面商品管理列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/bannerProductManageHtml", method = {RequestMethod.GET})
    public String bannerProductManageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/salesHeadCategoryManage/bannerProductManage";
    }

    /**
     * @Description: 查询banner位列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findBannerList", method = {RequestMethod.POST})
    public ResponseEntity findBannerList(@RequestBody BannerQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesBanner> bannerList = bannerService.findBannerList(pageQuery, form.name);
        JSONArray jsonArray = new JSONArray();
        for (SalesBanner banner : bannerList) {
            JSONObject json = new JSONObject();
            json.put("id", banner.getId());
            json.put("name", banner.getName());
            json.put("href", OSSClientUtil.getObjectUrl(banner.getPicaddress()));
            json.put("description", banner.getDescription());
            json.put("updateDate", DateTimeUtils.parseStr(banner.getUpdateDate()));
            json.put("sort", banner.getSort());
            json.put("status", banner.getStatus());
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, bannerList.total), HttpStatus.OK);
    }

    /**
     * @Description: 删除Banner
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/deleteBanner", method = {RequestMethod.POST})
    public ResponseEntity deleteBanner(@RequestBody IdForm form) throws Exception {
        bannerService.deleteBanner(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 设置 显示/隐藏 Banner    1:显示 0：隐藏
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/setDisabledBanner", method = {RequestMethod.POST})
    public ResponseEntity setDisabledBanner(@RequestBody BannerDisabledForm form) throws Exception {
        bannerService.setDisabledBanner(form.id, form.status);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: Banner编辑后保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/saveEditBanner", method = {RequestMethod.POST})
    public ResponseEntity saveEditBanner(@RequestBody BannerSaveEditForm form) throws Exception {
        bannerService.saveEditBanner(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: Banner新增
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/addBanner", method = {RequestMethod.POST})
    public ResponseEntity addBanner(@RequestBody BannerAddForm form) throws Exception {
        bannerService.addBanner(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 根据Banner Id,进入商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findBannerProductList", method = {RequestMethod.POST})
    public ResponseEntity findBannerProductList(@RequestBody BannerProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesBannerProduct> salesBannerProductPageList = bannerService.findBannerProductList(pageQuery, form.bannerId);
        JSONArray jsonArray = new JSONArray();
        for (SalesBannerProduct salesBannerProduct : salesBannerProductPageList) {
            JSONObject json = new JSONObject();
            json.put("id", salesBannerProduct.getId());
            json.put("name", salesBannerProduct.getSalesProduct() == null ? "" : salesBannerProduct.getSalesProduct().getName());
            json.put("href", salesBannerProduct.getSalesProduct() == null ? "" : OSSClientUtil.getObjectUrl(salesBannerProduct.getSalesProduct().getHref()));
            json.put("productCode", salesBannerProduct.getSalesProduct().getPlaProduct() == null ? "" : salesBannerProduct.getSalesProduct().getPlaProduct().getProductCode());
            json.put("categoryName", salesBannerProduct.getSalesProduct().getPlaProductCategory() == null ? "" : salesBannerProduct.getSalesProduct().getPlaProductCategory().getName());
            json.put("updateDate",DateTimeUtils.parseStr(salesBannerProduct.getUpdateDate()));
            json.put("sort", salesBannerProduct.getSort());
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesBannerProductPageList.total), HttpStatus.OK);
    }

    /**
     * @Description: 删除Banner位的商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/deleteBannerProduct", method = {RequestMethod.POST})
    public ResponseEntity deleteBannerProduct(@RequestBody IdForm form) throws Exception {
        bannerService.deleteBannerProduct(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 查询平台商品库的商品，以便增加到banner位的商品列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/17
     */
    @RequestMapping(value = "/findSalesProductList", method = {RequestMethod.POST})
    public ResponseEntity findSalesProductList(@RequestBody BannerSalesProductListQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SalesProduct> salesProductList = bannerService.findSalesProductList(pageQuery, form.name, form.categoryId, form.bannerId,form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (SalesProduct product : salesProductList) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("name", product.getName());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("productCode", product.getPlaProduct() == null ? "" : product.getPlaProduct().getProductCode());
            json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            json.put("updateDate",DateTimeUtils.parseStr(product.getUpdateDate()));
            json.put("sort", product.getSort());
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, salesProductList.total), HttpStatus.OK);
    }

    /**
     * @Description:  从平台商品库，选择相关的商品加入到 运营位列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @RequestMapping(value = "/addSalesBannerProduct", method = {RequestMethod.POST})
    public ResponseEntity addSalesBannerProduct(@RequestBody BannerProductIdsForm form) throws Exception {
        if (form.bannerId == null)
            return new ResponseEntity(new RestResponseEntity(110, "请选择对应Banner位", null), HttpStatus.OK);
        if (form.productIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "请选择要加入的商品", null), HttpStatus.OK);
        bannerService.addSalesBannerProduct(form.bannerId, form.productIds);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 返回Banner编辑信息
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */

    @RequestMapping(value = "/getBanner", method = {RequestMethod.POST})
    public ResponseEntity getBanner(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "请选择对应Banner位", null), HttpStatus.OK);
        SalesBanner banner = bannerService.getBanner(form.id);
        JSONObject json = new JSONObject();
        json.put("name",banner.getName());
        json.put("href", OSSClientUtil.getObjectUrl(banner.getPicaddress()));
        json.put("description",banner.getDescription());
        json.put("sort",banner.getSort());
        for (String key : json.keySet()){
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }
}
