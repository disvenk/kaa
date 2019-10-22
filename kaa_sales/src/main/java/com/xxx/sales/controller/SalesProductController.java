package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.*;
import com.xxx.sales.service.SalesProductService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;


@Controller
@RequestMapping("/salesProduct")
public class SalesProductController {

    @Autowired
    private SalesProductService salesProductService;


    /**
     * @Description: 获取商品的展示价格
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    public String checkSalesProductPrice(SalesProduct product) {
        //商品最低价
//        double price = 0.0;
//        for (SalesProductPrice productPrice : product.getSalesProductPriceList()) {
//            if (productPrice.getOfflinePrice() == null || productPrice.getOfflinePrice() == 0.0) continue;
//            if (price == 0.0 || price >= productPrice.getOfflinePrice()) price = productPrice.getOfflinePrice();
//        }
//        return price + "";
//        return product.getMinPrice() + "-" + product.getMaxPrice();
        return product.getMinPrice() + "";
    }

    /**
     * @Description: 首页banner图
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/salesBannerList", method = {RequestMethod.POST})
    public ResponseEntity salesBannerList() throws Exception {
        List<SalesBanner> salesBanners = salesProductService.salesBannerList();
        JSONArray data = new JSONArray();
        for (SalesBanner banner : salesBanners) {
            JSONObject json = new JSONObject();
            json.put("id", banner.getId());
            json.put("name", banner.getName());
            json.put("picaddress", OSSClientUtil.getObjectUrl(banner.getPicaddress()));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 首页banner中的商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/salesBannerProductList", method = {RequestMethod.POST})
    public ResponseEntity salesBannerProductList(@RequestBody BannerProductForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        if (form.bannerId == null)
            return new ResponseEntity(new RestResponseEntity(120, "Banner不能为空", null), HttpStatus.OK);
        SalesBanner banner = salesProductService.get2(SalesBanner.class, form.bannerId);
        if (banner == null)
            return new ResponseEntity(new RestResponseEntity(120, "Banner不存在", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        PageList<SalesBannerProduct> salesBanners = salesProductService.salesBannerProductList(pageQuery, form.bannerId);
        JSONObject data = new JSONObject();
        data.put("id", banner.getId());
        data.put("picaddress", OSSClientUtil.getObjectUrl(banner.getPicaddress()));
        JSONArray productList = new JSONArray();
        for (SalesBannerProduct bannerProduct : salesBanners) {
            SalesProduct product = bannerProduct.getSalesProduct();
            if (product == null) continue;
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("name", product.getName());
            json.put("views", product.getViews());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("price", checkSalesProductPrice(product));
            productList.add(json);
        }
        data.put("productList", productList);
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, salesBanners.total), HttpStatus.OK);
    }

    /**
     * @Description: 近期上新商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/productNearList", method = {RequestMethod.POST})
    public ResponseEntity productNearList(@RequestBody TypeForm form) throws Exception {
        PageQuery pageQuery = new PageQuery(0, 8);
        //1.本周   2.上周
        Date startDate = null;
        Date endDate = null;
        switch (form.type == null ? 0 : form.type) {
            case 0: break;
            case 1:
                startDate = DateTimeUtils.getDateByAddDay(new Date(), -7);
                endDate = DateTimeUtils.getDateByAddDay(new Date(), 1);
                break;
            case 2:
                startDate = DateTimeUtils.getDateByAddDay(new Date(), -14);
                endDate = DateTimeUtils.getDateByAddDay(new Date(), -7);
                break;
        }
        PageList<SalesProduct> salesProducts = salesProductService.productNearList(pageQuery, startDate, endDate);
        JSONArray data = new JSONArray();
        for (SalesProduct product : salesProducts) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("name", product.getName());
            json.put("views", product.getViews());
            json.put("price", checkSalesProductPrice(product));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 首页楼层类别及其商品
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/salesHeadcategoryList", method = {RequestMethod.POST})
    public ResponseEntity salesHeadcategoryList() throws Exception {
        List<SalesHeadCategory> salesHeadcategories = salesProductService.salesHeadcategoryList();
        JSONArray data = new JSONArray();
        for (SalesHeadCategory salesHeadCategory : salesHeadcategories) {
            JSONObject headcategory = new JSONObject();
            headcategory.put("id", salesHeadCategory.getId());
            headcategory.put("name", salesHeadCategory.getName());
            JSONArray productList = new JSONArray();
            for (SalesHeadCategoryProduct headcategoryProduct : salesHeadCategory.getSalesHeadCategoryProductList()) {
                SalesProduct product = headcategoryProduct.getSalesProduct();
                if (product == null || product.getStatus() != 1) continue; //过滤异常数据和非上架商品
                JSONObject json = new JSONObject();
                json.put("id", product.getId());
                json.put("name", product.getName());
                json.put("views", product.getViews());
                json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
                json.put("price", checkSalesProductPrice(product));
                productList.add(json);
            }
            headcategory.put("productList", productList);
            data.add(headcategory);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 商品列表侧边的 最热款式商品
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/productTideList", method = {RequestMethod.POST})
    public ResponseEntity productTideList() throws Exception {
        PageQuery pageQuery = new PageQuery(0, 3);
        PageList<SalesProduct> salesProducts = salesProductService.productNearList(pageQuery, null, null);
        JSONArray data = new JSONArray();
        for (SalesProduct product : salesProducts) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("name", product.getName());
            json.put("views", product.getViews());
            json.put("price", checkSalesProductPrice(product));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 商品列表
     * @Author: Chen.zm
     * @Date: 2017/11/17 0017
     */
    @RequestMapping(value = "/salesProductList", method = {RequestMethod.POST})
    public ResponseEntity salesProductList(@RequestBody SalesProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
//        PageQuery pageQuery = new PageQuery(form.pageNum);
//        pageQuery.limit = 40;
//        PageList<SalesProduct> salesProductList = salesProductService.salesProductList(pageQuery, form);
        StringBuffer categoryIds = new StringBuffer();
        for (CategoryIdForm categoryIdForm : form.categoryIdList) categoryIds.append(categoryIdForm.categoryId + ",");
        StringBuffer labelIds = new StringBuffer();
        for (LabelIdForm labelIdForm : form.labelIdList) labelIds.append(labelIdForm.labelId + ",");
        MybatisPageQuery pageQuery = new MybatisPageQuery(form.pageNum, 40);
        pageQuery.getParams().put("categoryIdList", categoryIds.length() == 0 ? "" : new String(categoryIds.substring(0, categoryIds.length() - 1)));
        pageQuery.getParams().put("nameOrCode", form.nameOrCode);
        pageQuery.getParams().put("labelIdList", labelIds.length() == 0 ? "" : new String(labelIds.substring(0, labelIds.length() - 1)));
        pageQuery.getParams().put("startPrice", form.startPrice);
        pageQuery.getParams().put("endPrice", form.endPrice);
        pageQuery.getParams().put("sortType", form.sortType);
        PageList<JSONObject> salesProductList = salesProductService.findSalesProductList(pageQuery);
        JSONArray data = new JSONArray();
//        for (SalesProduct product : salesProductList) {
//            JSONObject json = new JSONObject();
//            json.put("id", product.getId());
//            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
//            json.put("name", product.getName());
//            json.put("views", product.getViews());
//            json.put("price", checkSalesProductPrice(product));
//            data.add(json);
//        }
        for (JSONObject js : salesProductList) {
            JSONObject json = new JSONObject();
            json.put("id", js.get("id"));
            json.put("href", js.get("href") == null ? "" : OSSClientUtil.getObjectUrl(js.get("href").toString()));
            json.put("name", js.get("name"));
            json.put("views", js.get("views"));
            json.put("price", js.get("price"));
            data.add(json);
        }
//        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, salesProductList.total), HttpStatus.OK);
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.getOffset(), pageQuery.getLimit(), salesProductList.total), HttpStatus.OK);
    }


    /**
     * @Description: 商品详情
     * @Author: Chen.zm
     * @Date: 2017/11/18 0018
     */
    @RequestMapping(value = "/productDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SalesProduct salesProduct = salesProductService.productDetail(form.id);
        if (salesProduct == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品不存在", null), HttpStatus.OK);
        //累加商品浏览量
        salesProductService.addProductViews(form.id);
        JSONObject data = new JSONObject();
        data.put("id", salesProduct.getId());
        data.put("name", salesProduct.getName());
        data.put("href", OSSClientUtil.getObjectUrl(salesProduct.getHref()));
        data.put("sales", salesProduct.getSales());
        data.put("views", salesProduct.getViews());
        data.put("vedioUrl", OSSClientUtil.getObjectUrl(salesProduct.getVedioUrl()));
        //2017.12.07 商品详情字段隔离
//        data.put("description", salesProduct.getDescription());
     //   data.put("description", salesProductService.getProductDescription(form.id));

        data.put("categoryName", salesProduct.getPlaProductCategory() == null ? "" : salesProduct.getPlaProductCategory());
        data.put("price", checkSalesProductPrice(salesProduct));
        data.put("productCode", salesProduct.getPlaProduct() == null ? "" : salesProduct.getPlaProduct().getProductCode());
        JSONArray imgList = new JSONArray();
        for (SalesProductPicture picture : salesProduct.getSalesProductPictureList()) {
            JSONObject json = new JSONObject();
            json.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            imgList.add(json);
        }
        data.put("imgList", imgList);
        JSONArray productPrice = new JSONArray();
        Set<String> colors = new HashSet<>();
        Set<String> sizes = new HashSet<>();
        for (SalesProductPrice price : salesProduct.getSalesProductPriceList()) {
            JSONObject json = new JSONObject();
            json.put("id", price.getId());
            json.put("color", price.getColor());
            json.put("size", price.getSize());
            json.put("price", price.getOfflinePrice() == null ? "" : price.getOfflinePrice());
            json.put("stock", price.getStock());
            productPrice.add(json);
            colors.add(price.getColor());
            sizes.add(price.getSize());
        }
        data.put("productPrice", productPrice);
        data.put("colors", colors);
        data.put("sizes", sizes);
        for(String key : data.keySet()){
            if(data.get(key) == null) data.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 单独取得图文详情
     * @Author: steven
     * @Date: 2017/12/11
     */
    @RequestMapping(value = "/getProductDescription", method = {RequestMethod.POST})
    public ResponseEntity getProductDescription(@RequestBody IdForm form) throws Exception {
        JSONObject json = new JSONObject();
        json.put("description", salesProductService.getProductDescription(form.id));
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 获取商品标签
     * @Author: Chen.zm
     * @Date: 2017/12/28 0028
     */
    @RequestMapping(value = "/findProductLabel", method = {RequestMethod.POST})
    public ResponseEntity findProductLabel(@RequestBody IdForm form) throws Exception {
        List<JSONObject> list = salesProductService.findProductLabel(form.id);
        JSONArray data = new JSONArray();
        for (JSONObject js : list) {
            JSONObject json = new JSONObject();
            json.put("id", js.get("id"));
            json.put("name", js.get("name"));
            json.put("sort", js.get("sort"));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


}
