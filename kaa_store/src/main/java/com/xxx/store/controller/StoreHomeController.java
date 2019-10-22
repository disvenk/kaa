package com.xxx.store.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.store.form.IdForm;
import com.xxx.store.form.StoreProductListForm;
import com.xxx.store.service.StoreHomeService;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.security.GenericLogin;
import com.xxx.user.service.AccountService;
import com.xxx.utils.OSSClientUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("/storeHome")
public class StoreHomeController {

    @Autowired
    private StoreHomeService storeHomeService;
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String log_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "login";
    }

    @RequestMapping(value = "/storeHomeHtml", method = {RequestMethod.GET})
    public String storeHomeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "store";
    }

    @RequestMapping(value = "/indexHtml", method = {RequestMethod.GET})
    public String indexHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        //附带门店信息   通过该信息生成身份信息
        String store = request.getParameter("store");
        if (StringUtils.isBlank(store) || store.split("-").length < 1)
            return "login";
        //userId-storeId
        Integer userId = Integer.parseInt(store.split("-")[0]);
        Integer storeId = Integer.parseInt(store.split("-")[1]);
        PubUserLogin login = accountService.get2(PubUserLogin.class, "id", userId, "relationId", storeId);
        modelMap.put("storeImg", login.getPubUserBase() == null ? "" : OSSClientUtil.getObjectUrl(login.getPubUserBase().getIcon()));

        //获取门店登录信息
        GenericLogin genericLogin = accountService.processMerchantLogin(login.getUserCode(), login.getUserPassword(), 1);
        modelMap.put("userName", genericLogin.userName);
//        modelMap.put("loginType", genericLogin.loginType);
        modelMap.put("authToken", CurrentUser.generateAuthToken(genericLogin));
//        modelMap.put("userId", genericLogin.userId);
        return "index";
    }

    /**
     * @Description: 获取商品主分类
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/productCategoryList", method = {RequestMethod.POST})
    public ResponseEntity productCategoryList() throws Exception {
        List<JSONObject> productCategories = storeHomeService.findProductCategoryList(CurrentUser.get().getStoreId());
        JSONArray data = new JSONArray();
        for (JSONObject productCategory : productCategories) {
            JSONObject json = new JSONObject();
            json.put("name", productCategory.get("name"));
            json.put("id", productCategory.get("id"));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 获取商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/storeProductList", method = {RequestMethod.POST})
    public ResponseEntity storeProductList(@RequestBody StoreProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        PageList<StoProduct> products = storeHomeService.findStoreProductList(pageQuery, CurrentUser.get().getStoreId(), form.categoryId, form.sortType, form.startPrice, form.endPrice);
        JSONArray data = new JSONArray();
        for (StoProduct stoProduct : products) {
            JSONObject json = new JSONObject();
            json.put("id", stoProduct.getId());
            json.put("name", stoProduct.getName() == null ? "" : stoProduct.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(stoProduct.getHref()));
            json.put("price", stoProduct.getMinPrice() == null ? "" : stoProduct.getMinPrice());
            json.put("views", stoProduct.getViews() == null ? 0 : stoProduct.getViews());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, products.total), HttpStatus.OK);
    }


    /**
     * @Description: 门店商品详情
     * @Author: Chen.zm
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/storeProductDetail", method = {RequestMethod.POST})
    public ResponseEntity storeProductDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        StoProduct stoProduct = storeHomeService.findStoreProductDetail(CurrentUser.get().getStoreId(), form.id);
        //访问详情页面时给浏览量加1
            storeHomeService.addView(form.id,stoProduct.getViews() == null ? 0 : stoProduct.getViews());
        JSONObject data = new JSONObject();
        data.put("id", stoProduct.getId());
        data.put("name", stoProduct.getName());
        data.put("price", stoProduct.getMinPrice());
        data.put("vedioUrl", OSSClientUtil.getObjectUrl(stoProduct.getVedioUrl()));
        //2017.12.07 图文详情字段隔离
//        data.put("description", stoProduct.getDescription());
//        data.put("description", storeHomeService.getProductDescription(stoProduct.getId()));

        data.put("categoryName", stoProduct.getPlaProductCategory() == null ? "" : stoProduct.getPlaProductCategory().getName());
        data.put("code", stoProduct.getPlaProduct().getProductCode());
        JSONArray imgList = new JSONArray();
        for (StoProductPicture picture : stoProduct.getStoProductPictureList()) {
            JSONObject json = new JSONObject();
            json.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            json.put("mainpic", picture.getMainpic());
            imgList.add(json);
        }
        data.put("imgList", imgList);
        JSONArray specList = new JSONArray();
        for (StoProductPrice picture : stoProduct.getStoProductPriceList()) {
            JSONObject json = new JSONObject();
            json.put("id", picture.getId());
            json.put("offlinePrice", picture.getOfflinePrice()); //注：若修改该字段，需要统一修改添加购物车以及下单接口的
            json.put("color", picture.getColor());
            json.put("size", picture.getSize());
            specList.add(json);
        }
        data.put("specList", specList);
         for(String key : data.keySet()){
            if(data.get(key).equals("null")) data.put(key,"");
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 单独取得图文详情
     * @Author: steven
     * @Date: 2017/12/11
     */
    @RequestMapping(value = "/getProductDescription", method = {RequestMethod.POST})
    public ResponseEntity getProductDescription(@RequestBody IdForm form) throws Exception {
        JSONObject json = new JSONObject();
        json.put("description", storeHomeService.getProductDescription(form.id));
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


}
