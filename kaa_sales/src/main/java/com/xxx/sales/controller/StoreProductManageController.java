package com.xxx.sales.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.service.StoreProductManageService;
import com.xxx.sales.form.*;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.*;
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
@RequestMapping("/storeProductManage")
public class StoreProductManageController {

    @Autowired
    private StoreProductManageService storeProductManageService;

    private static final String LOCATION_URL = "LOCATION_URL";


    @RequestMapping(value = "/showGoodsHtml", method = {RequestMethod.GET})
    public String data1_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/showGoods/showGoods";
    }

    @RequestMapping(value = "/goodsDetailHtml", method = {RequestMethod.GET})
    public String data2_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/showGoods/goodsDetail";
    }

    @RequestMapping(value = "/editShowGoodsHtml", method = {RequestMethod.GET})
    public String data3_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "/showGoods/editShowGoods";
    }

    @RequestMapping(value = "/goodsManageFromStoreHtml", method = {RequestMethod.GET})
    public String data4_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/showGoods/goodsManageFromStore";
    }

    /**
     * @Description: 获取前端展示地址
     * @Author: Chen.zm
     * @Date: 2017/11/23 0023
     */
    @RequestMapping(value = "/storeLocation", method = {RequestMethod.POST})
    public ResponseEntity submitOrderSales() throws Exception {
        JSONObject json = new JSONObject();
        SysDict dict = storeProductManageService.get2(SysDict.class, "keyName", LOCATION_URL);
        String url = dict == null ? "" : dict.getKeyValue() + "/storeHome/indexHtml?store=" + CurrentUser.get().userId + "-" + CurrentUser.get().getStoreId();
        json.put("url", url);
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }


    /**
     * @Description:获取分类
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @RequestMapping(value = "/storeProductCategoryList", method = {RequestMethod.POST})
    public ResponseEntity productCategoryList() throws Exception {
        List<PlaProductCategory> plaProductCategoryList = storeProductManageService.findProductCategoryList();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for (PlaProductCategory categoryName : plaProductCategoryList) {
            if (categoryName.getParentId() == null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("categoryName", categoryName.getName());
                jsonObject.put("categoryId", categoryName.getId());
                jsonArray1.add(jsonObject);
            } else {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("RelateCategoryName", categoryName.getName());
                jsonObject1.put("RelateCategoryId", categoryName.getId());
                jsonArray2.add(jsonObject1);
            }
        }
        jsonObject2.put("categoryName", jsonArray1);
        jsonObject2.put("RelateCategoryName", jsonArray2);
        jsonArray.add(jsonObject2);

        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);
    }


    /**
     * @Description:商品库删除商品
     * @Author: hanchao
     * @Date: 2017/10/27 0027
     */
    @RequestMapping(value = "/deleteProductManage", method = {RequestMethod.POST})
    public ResponseEntity deleteProductManage(@RequestBody IdForm form) throws UpsertException,ResponseEntityException {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        storeProductManageService.updateStoreProductId(form.id, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:商品库商品添加到门店展示商品(门店)
     * @Author: hanchao
     * @Date: 2017/10/31 0031
     */
    @RequestMapping(value = "/insertStoreProductManage", method = {RequestMethod.POST})
    public ResponseEntity insertStoreProductManage(@RequestBody IdForm form) throws Exception {
        if (form.id ==null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        storeProductManageService.insertStoreProductManage(form, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    
    /**
     * @Description:商品详情页
     * @Author: hanchao
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/storeProducManagetDetail", method = {RequestMethod.POST})
    public ResponseEntity storeProducManagetDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        PlaProduct pla = storeProductManageService.getPlaProduct(form.id);
        if (pla == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("name", pla.getName());
        json.put("productCode",pla.getProductCode());
        //2017.12.07 图文详情字段隔离
//        json.put("description",pla.getDescription());
        json.put("description",storeProductManageService.getPlatProductDescription(form.id));

        json.put("vedioUrl",OSSClientUtil.getObjectUrl(pla.getVedioUrl()));
        json.put("pno",pla.getPno());
        json.put("remarks",pla.getRemarks());
        json.put("colligate",pla.getColligate());//排序
        json.put("categoryName",pla.getPlaProductCategory() == null ? "" : pla.getPlaProductCategory().getName());
        json.put("sales",pla.getSales());//销量
        json.put("views",pla.getViews());//浏览量

        json.put("brand",pla.getBrand());//
        json.put("suplierDay",pla.getSuplierDay());//
        json.put("updateDate", DateTimeUtils.parseStr(pla.getUpdateDate()));//更新日期
        json.put("price",pla.getPrice());


        StoProduct sto = storeProductManageService.getplatProductIdStatus(form.id, CurrentUser.get().getStoreId());
        if (sto!=null){
            json.put("platProductIdStatus",1);//门店里面还没有从商品库添加数据,有,1,没有,0
        }else{
            json.put("platProductIdStatus",0);
        }

        JSONArray picture=new JSONArray();
        for (PlaProductPicture plaProductPicture : pla.getPlaProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("href",OSSClientUtil.getObjectUrl(plaProductPicture.getHref()));
            json.put("isMainpic",plaProductPicture.getMainpic());
            picture.add(jsonObject);
        }
        json.put("picture",picture);

        JSONArray jsonSupplier=new JSONArray();
        for (PlaProductPrice plaProductPrice: pla.getPlaProductPriceList()){
            JSONObject jsonObject=new JSONObject();
//            jsonObject.put("onlinePrice",plaProductPrice.getOnlinePrice());
            jsonObject.put("offlinePrice",plaProductPrice.getOfflinePrice());//线下指导价
            jsonObject.put("color", plaProductPrice.getColor());
            jsonObject.put("size", plaProductPrice.getSize());
            jsonObject.put("stock", plaProductPrice.getStock());
            jsonSupplier.add(jsonObject);
        }

        json.put("jsonSupplier",jsonSupplier);

        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:商品管理新增(列表筛选显示)
     * @Author: hanchao
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/storeAddProductManageList", method = {RequestMethod.POST})
    public ResponseEntity storeAddProductManageList(@RequestBody StoreProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 12;
        List<Integer> ids = new ArrayList<>();
        for (StoreProductCategoryIdListForm id : form.plaProductCategory) {
            ids.add(id.categoryId);
            ids.add(id.relateCategoryId);
        }
        StringBuffer str = new StringBuffer();
        for (StoreProductLabelIdListForm id : form.plaProductLabelId) {
            str.append(id.labelId + ",");
        }
        if (str.length() > 0) {
            str = new StringBuffer(str.substring(0, str.length() - 1));
        }

        //遍历已添加的商品集合
        Set<Integer> stoIds = new HashSet<>();
        List<StoProduct> sto = storeProductManageService.findStoProductList(CurrentUser.get().getStoreId());
        for (StoProduct stoProduct : sto) {
            stoIds.add(stoProduct.getPlatProductId());
        }
        PageList<PlaProduct> productList = storeProductManageService.findProductList(pageQuery, ids, str.toString(), form.sortType, form.startPrice, form.endPrice,form.productCode);
        JSONArray jsonArray = new JSONArray();
        for (PlaProduct pla : productList) {
            JSONObject json = new JSONObject();
            json.put("id", pla.getId());
            json.put("name", pla.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(pla.getHref()));//主图地址
            json.put("price", pla.getMinPrice());
            json.put("suplierDay", pla.getSuplierDay());

            if (stoIds.contains(pla.getId())) {
                json.put("platProductIdStatus", 1);//门店里面还没有从商品库添加数据,有,1,没有,0
            } else {
                json.put("platProductIdStatus", 0);
            }

            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, productList.total), HttpStatus.OK);
    }

    /**
     * @Description:商品编辑详情提交
     * @Author: hanchao
     * @Date: 2017/10/30 0030
     */
    @RequestMapping(value = "/updateStoreProductManage", method = {RequestMethod.POST})
    public ResponseEntity updateStoreProductManage(@RequestBody StoreProductForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品id不能为空", null), HttpStatus.OK);
        storeProductManageService.updateStoreProduct(form, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:商品编辑详情
     * @Author: hanchao
     * @Date: 2017/10/26 0026
     */
    @RequestMapping(value = "/storeProductManageEditDetail", method = {RequestMethod.POST})
    public ResponseEntity storeProductManageEditDetail(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        StoProduct sto=storeProductManageService.getStoProduct(form.id, CurrentUser.get().getStoreId());
        if (sto == null)
            return new ResponseEntity(new RestResponseEntity(110, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("name", sto.getName());
        json.put("platProductId",sto.getPlatProductId());
        json.put("productCode",sto.getPlaProduct() ==  null ? "" : sto.getPlaProduct().getProductCode());
        json.put("vedioKey", sto.getVedioUrl());
        json.put("vedioUrl", OSSClientUtil.getObjectUrl(sto.getVedioUrl()));
        //2017.12.07 图文详情字段隔离
//        json.put("description",sto.getDescription());
        json.put("description",storeProductManageService.getProductDescription(sto.getId()));

        json.put("pno",sto.getPlaProduct() == null ? "" : sto.getPlaProduct().getPno());
        json.put("remarks",sto.getRemarks());
        json.put("colligate",sto.getColligate());//综合排序
        json.put("sales",sto.getSales());//销量
        json.put("views",sto.getViews() );//浏览量
        json.put("price",sto.getPrice());//建议零售价
        json.put("categoryName",sto.getPlaProductCategory() == null ? "" : sto.getPlaProductCategory().getName());
        json.put("categoryId",sto.getPlaProductCategory() == null ? "" : sto.getPlaProductCategory().getId());
        json.put("brand",sto.getBrand());
        //json.put("status",sto.getStatus());//是否上架
        switch (sto.getStatus()){
            case 0:
                json.put("status","未上架");
                break;
            case 1:
                json.put("status","已上架");
        }

        JSONArray picture=new JSONArray();
        for (StoProductPicture stoProductPicture : sto.getStoProductPictureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",stoProductPicture.getHref());
            jsonObject.put("href",OSSClientUtil.getObjectUrl(stoProductPicture.getHref()));
            picture.add(jsonObject);
        }
        json.put("picture",picture);


        JSONArray jsonSupplier=new JSONArray();
        for (StoProductPrice stoProductPrice: sto.getStoProductPriceList()){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("color", stoProductPrice.getColor());
            jsonObject.put("size", stoProductPrice.getSize());
            jsonObject.put("offlinePrice",stoProductPrice.getOfflinePrice());//门店销售价格
            jsonObject.put("stock", stoProductPrice.getStock());
            jsonSupplier.add(jsonObject);
        }

        json.put("jsonSupplier",jsonSupplier);
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
    }


    /**
     * @Description:获取门店商品列表
     * @Author: hanchao
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/storeProducManagetList", method = {RequestMethod.POST})
    public ResponseEntity storeProducManagetList(@RequestBody StoreProductManageForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 24;
        PageList<StoProduct> productList = storeProductManageService.findProductList(CurrentUser.get().getStoreId(), pageQuery, form.name, form.categoryId);
        JSONArray jsonArray = new JSONArray();
        for (StoProduct stoProduct : productList) {
            JSONObject json = new JSONObject();
            json.put("id",stoProduct.getId());
            json.put("productCode",stoProduct.getPlaProduct() == null ? "" : stoProduct.getPlaProduct().getProductCode());
            json.put("name", stoProduct.getName());
            json.put("mainpicHref", OSSClientUtil.getObjectUrl(stoProduct.getHref()));//主图地址
            json.put("pno", stoProduct.getPlaProduct() == null ? "" : stoProduct.getPlaProduct().getPno());
            json.put("categoryName",stoProduct.getPlaProductCategory() == null ? "" : stoProduct.getPlaProductCategory().getName());

//            json.put("price", stoProduct.getMinPrice()+"~"+stoProduct.getMaxPrice()); //价格，取区间

            json.put("price", stoProduct.getMinPrice()); //价格，取区间

            json.put("buyprice", stoProduct.getPlaProduct() == null ? "" : stoProduct.getPlaProduct().getMinPrice()); //进货价

            json.put("colligate",stoProduct.getColligate() == null ? "" : stoProduct.getColligate());//排序
            json.put("updateDate", DateTimeUtils.parseStr(stoProduct.getUpdateDate()));//更新日期
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, productList.total), HttpStatus.OK);
    }

}
