package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.StoreService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.Commo;
import com.xxx.user.service.PaymentService;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.MD5Utils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.*;
import com.xxx.utils.date.DateTimeUtil;
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
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private PaymentService paymentService;



    /**
     * @Description: 打开门店管理列表页
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/indexHtml", method = {RequestMethod.GET})
    public String data_Indexhtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "index";
    }

    /**
     * @Description: 进入编辑页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/storeManageEditHtml", method = {RequestMethod.GET})
    public String data_edithtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "storeManage/storeManageEdit";
    }

    /**
     * @Description: 跳转到门店管理页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */

    @RequestMapping(value = "/storeHtml", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeManage";
    }

    /**
     * @Description: 门店商品管理页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */

    @RequestMapping(value = "/storeGoodsManageHtml", method = {RequestMethod.GET})
    public String data_html1(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeGoodsManage";
    }
//    进入合一盒子
    @RequestMapping(value = "/storeToBoxHtml", method = {RequestMethod.GET})
    public String storeToBoxHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeToBox";
    }

    /**
     * @Description: 门店商品的详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/8
     */

    @RequestMapping(value = "/storeGoodsManageDetailHtml", method = {RequestMethod.GET})
    public String data_html3(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeGoodsManageDetail";
    }

    /**
     * @Description: 门店销售订单列表页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */

    @RequestMapping(value = "/storeSalesOrderManageHtml", method = {RequestMethod.GET})
    public String data_html2(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeSalesOrderManage";
    }

    /**
     * @Description: 门店销售订单详情页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */

    @RequestMapping(value = "/storeSalesOrderManageDetailHtml", method = {RequestMethod.GET})
    public String data_html4(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeSalesOrderManageDetail";
    }

    /**
     * @Description: 门店采购订单详情页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/9
     */

    @RequestMapping(value = "/storeSuplierOrderManageDetailHtml", method = {RequestMethod.GET})
    public String data_html5(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeManage/storeSuplierOrderManageDetail";
    }

    /**
     * @Description: 门店管理列表查询
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/findStoreList", method = {RequestMethod.POST})
    public ResponseEntity findStoreList(@RequestBody StoreListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoStoreInfo> storeList = storeService.findStoreList(pageQuery, form.userCode, form.mobile);
        JSONArray jsonArray = new JSONArray();
        for (StoStoreInfo store : storeList) {
            JSONObject json = new JSONObject();
            json.put("id", store.getId());
            json.put("userId",store.getUserId());
            if (store.getPubUserLogin() == null) store.setPubUserLogin(new PubUserLogin());
            PubUserBase pubUserBase = store.getPubUserLogin().getPubUserBase();
            if (pubUserBase == null) pubUserBase = new PubUserBase();
            json.put("icon", OSSClientUtil.getObjectUrl(pubUserBase.getIcon()));
            json.put("userCode", store.getPubUserLogin().getUserCode());
            json.put("userName", pubUserBase.getName());
            json.put("sex", Commo.parseSex(pubUserBase.getSex()));
            json.put("mobile", pubUserBase.getMobile());
            json.put("storeName", store.getStoreName());
//            json.put("storeAddress", store.getAddress());
//            json.put("storePicture", OSSClientUtil.getObjectUrl(store.getStorepicture()));
//            json.put("credentials", OSSClientUtil.getObjectUrl(store.getCredentials()));
            json.put("storeSalesCategory", store.getScope());
            json.put("storeContact", store.getContact());
            json.put("storeContactPhone", store.getContactPhone());
            json.put("userType", Commo.parseUserType(store.getPubUserLogin().getUserType()));
            //2018.1.4 增加注册时间，购买盒子：是/否
            json.put("createdDate",DateTimeUtils.parseStr(store.getCreatedDate()));
            json.put("haveBuy", storeService.haveBuyHeyiService(store.getId()));
            json.put("finalLogindate", DateTimeUtils.parseStr(store.getPubUserLogin().getFinalLogindate()));
            json.put("storeStatus", store.getStoreStatus());
            json.put("storeStatusName", Commo.parseStoreStatus(store.getStoreStatus()));
            // 过滤value == null 的数据    使其为空
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, storeList.total), HttpStatus.OK);
    }

    /**
     * @Description: 编辑取得门店详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/getStoreDetail", method = {RequestMethod.POST})
    public ResponseEntity getStoreDetail(@RequestBody IdForm form) throws Exception {
        StoStoreInfo store = storeService.getStoreDetail(form.id);
        if (store == null)
            return new ResponseEntity(new RestResponseEntity(110, "门店不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", store.getId());
        PubUserBase pubUserBase = store.getPubUserLogin().getPubUserBase();
        json.put("icon", OSSClientUtil.getObjectUrl(pubUserBase.getIcon()));
        json.put("userCode", store.getPubUserLogin().getUserCode());
        json.put("mobile", pubUserBase.getMobile());
        json.put("userName", pubUserBase.getName());
        json.put("sex", pubUserBase.getSex());
        json.put("finalLogindate", DateTimeUtils.parseStr(store.getPubUserLogin().getFinalLogindate()==null?"":store.getPubUserLogin().getFinalLogindate().getTime() / 1000, "yyyy-MM-dd hh:mm:ss"));
        json.put("userType", store.getPubUserLogin().getUserType());
        json.put("remarks",store.getPubUserLogin().getPubUserBase().getRemarks());
        json.put("storeName", store.getStoreName());
        json.put("provinceId", store.getProvince());
        json.put("provinceName", store.getProvinceName());
        json.put("cityId", store.getCity());
        json.put("cityName", store.getCityName());
        json.put("zoneId", store.getZone());
        json.put("zoneName", store.getZoneName());
        json.put("storeAddress", store.getAddress());
        json.put("storeMainPicture", OSSClientUtil.getObjectUrl(store.getStorepicture()));
        JSONArray jsonArray = new JSONArray();
        for (StoStorePicture picture : store.getStoStorePictureList()) {
            JSONObject picturelist = new JSONObject();
            picturelist.put("picId", OSSClientUtil.getObjectUrl(picture.getHref()));
            picturelist.put("isMainPic", picture.getMainpic());
            jsonArray.add(picturelist);
        }
        json.put("storePictureList", jsonArray);
        json.put("Credentials", OSSClientUtil.getObjectUrl(store.getCredentials()));
        json.put("storeScope", store.getScope());
        json.put("storeContact", store.getContact());
        json.put("storeContactPhone", store.getContactPhone());
        json.put("storeStatus",store.getStoreStatus());
        for (String key : json.keySet()){
            if(json.get(key) == null) json.put(key,"");
        }

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 门店信息编辑后保存
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/updateStore", method = {RequestMethod.POST})
    public ResponseEntity updateStore(@RequestBody StoreEditForm form) throws Exception {
        if (StringUtils.isBlank(form.storeName))
            return new ResponseEntity(new RestResponseEntity(110, "名称不能为空", null), HttpStatus.OK);
        storeService.updateStoStoreInfo(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 门店密码重置
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/updateStorePassword", method = {RequestMethod.POST})
    public ResponseEntity updateStorePassword(@RequestBody IdForm form) throws Exception {
        storeService.updateStorePassword(form.id, MD5Utils.md5Hex("888888"));
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 门店删除
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/removeStore", method = {RequestMethod.POST})
    public ResponseEntity removeStore(@RequestBody IdForm form) throws Exception {
        storeService.removeStoStoreInfo(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 门店审核
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/updateStoreStatus", method = {RequestMethod.POST})
    public ResponseEntity updateStoreStatus(@RequestBody StoreApproveStatusUpdateForm form) throws Exception {
        storeService.updateStoreStatus(form.id, form.statusId);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 查询门店商品
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/findStoreProductList", method = {RequestMethod.POST})
    public ResponseEntity storeProductList(@RequestBody StoreQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoProduct> storeProductList = storeService.findStoreProductList(pageQuery, form.productName, form.categoryId, form.storeId);
        JSONArray jsonArray = new JSONArray();
        for (StoProduct product : storeProductList) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("productName", product.getName());
            json.put("productPicture", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("productCode", product.getPlaProduct() == null ? "" : product.getPlaProduct().getProductCode());
            json.put("pno", product.getPlaProduct() == null ? "" : product.getPlaProduct().getPno() == null ? "" : product.getPlaProduct().getPno());
            json.put("productCategory", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            json.put("salesPrice", product.getPrice() == null ? "" : product.getPrice());
            json.put("views", product.getViews() == null ? "" : product.getViews());
            json.put("updateDate", DateTimeUtils.parseStr(product.getUpdateDate()));
            json.put("storeName", product.getStoStoreInfo() == null ? "" : product.getStoStoreInfo().getStoreName() == null ? "" : product.getStoStoreInfo().getStoreName());
            json.put("sort", product.getSort() == null ? "" : product.getSort());
            //去除null
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, storeProductList.total), HttpStatus.OK);
    }

    /**
     * @Description: 查看门店的商品详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/1
     */
    @RequestMapping(value = "/storeProductDetail", method = {RequestMethod.POST})
    public ResponseEntity storeProductDetail(@RequestBody IdForm form) throws Exception {
        //取得门店商品对象
        StoProduct product = storeService.getStoProductDetail(form.id);
        if (product == null)
            return new ResponseEntity(new RestResponseEntity(150, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("productCode", product.getPlaProduct()==null ?"":product.getPlaProduct().getProductCode());
        json.put("productName", product.getName());
        JSONArray jsonArray = new JSONArray();
        for (StoProductPicture picture : product.getStoProductPictureList()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("pictureUrl", OSSClientUtil.getObjectUrl(picture.getHref()));
                jsonObject.put("isMainPicture", picture.getMainpic());
                jsonObject.put("sort", picture.getSort());
                jsonArray.add(jsonObject);
        }
        json.put("productPictureList", jsonArray);
        json.put("brand", product.getBrand());
        json.put("productCategory", product.getPlaProductCategory()==null?"":product.getPlaProductCategory().getName());
        json.put("salesPrice", product.getPrice());
        json.put("pno", product.getPlaProduct()==null ? "" : product.getPlaProduct().getPno());
        json.put("suplierDay", product.getSuplierDay() == null? 0 : product.getSuplierDay());
        json.put("updateDate", DateTimeUtils.parseStr(product.getUpdateDate()));
        json.put("storeName", product.getStoStoreInfo() == null ? "" : product.getStoStoreInfo().getStoreName());
        JSONArray jsonArraySpec = new JSONArray();
        for (StoProductPrice price : product.getStoProductPriceList()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("color", price.getColor());
            jsonObject.put("size", price.getSize());
            jsonObject.put("price", price.getOfflinePrice());
            jsonObject.put("stock", price.getStock());
            jsonArraySpec.add(jsonObject);
        }
        json.put("productSpecList", jsonArraySpec);
        //2017.12.07 图文详情字段隔离
//        json.put("description", product.getDescription());
        json.put("description", storeService.getProductDescription(product.getId()));
        for (String key : json.keySet()){
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 门店的销售订单查询列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/2
     */
    @RequestMapping(value = "/findStoreSalesOrderList", method = {RequestMethod.POST})
    public ResponseEntity findStoreSalesOrderList(@RequestBody StoreSalesOrderQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoSalesOrder> orderList = storeService.findStoreSalesOderList(pageQuery, form.receiver, form.mobile, form.orderNo, form.ordernoSuplier, form.storeId);
        JSONArray jsonArray = new JSONArray();
        for (StoSalesOrder order : orderList) {
            JSONObject json = new JSONObject();
            json.put("id", order.getId());
            json.put("orderNo", order.getOrderNo());
            json.put("orderDate", DateTimeUtils.parseStr(order.getOrderDate()));
            json.put("storeName", order.getStoStoreInfo()==null?"":order.getStoStoreInfo().getStoreName());
            json.put("orderStatus", order.getStatus());
            json.put("orderStatusName", Commo.parseSalesOrderStatus(order.getStatus()));
            json.put("orderSuplierStatus", order.getOrderstatusSuplier());
            json.put("orderSuplierStatusName", Commo.parseSuplierOrderStatus(order.getOrderstatusSuplier()));
            json.put("orderIdSuplier", order.getStoSuplierOrder()==null?"":order.getStoSuplierOrder().getId());
            json.put("ordertotal", order.getTotal());

            JSONArray JsonDetailList = new JSONArray();
            for (StoSalesOrderDetail detail : order.getStoSalesOrderDetailList()) {
                JSONObject detailjson = new JSONObject();
                detailjson.put("productId",detail.getPid());
                detailjson.put("productCode", detail.getStoProduct() == null ? "" : detail.getStoProduct().getPlaProduct() == null ? "" :
                        detail.getStoProduct().getPlaProduct().getProductCode());
                detailjson.put("href", OSSClientUtil.getObjectUrl(detail.getHref()));
                detailjson.put("productName", detail.getProductName());
                detailjson.put("color", detail.getColor());
                detailjson.put("size", detail.getSize());
                detailjson.put("price", detail.getPrice());
                detailjson.put("qty", detail.getQty());
                detailjson.put("subtotal", detail.getSubtotal());
                for (String key : detailjson.keySet()){
                    if (detailjson.get(key) == null) detailjson.put(key, "");
                }
                JsonDetailList.add(detailjson);
            }
            json.put("productList", JsonDetailList);
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }

        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, orderList.total), HttpStatus.OK);
    }

    /**
     * @Description: 门店销售订单详情
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @RequestMapping(value = "/getStoSalesOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity getStoSalesOrderDetail(@RequestBody IdForm form) throws Exception {
        StoSalesOrder order = storeService.getStoSalesOrderDetail(form.id);
        if (order == null)
            return new ResponseEntity(new RestResponseEntity(130, "订单不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("orderNo", order.getOrderNo());
        json.put("orderDate",DateTimeUtils.parseStr(order.getOrderDate()));
        json.put("expectsendDate", order.getStoSalesOrderDelivery().getExpectsendDate());
        json.put("orderStatus", Commo.parseSalesOrderStatus(order.getStatus()));
        json.put("ordernoSuplier", order.getOrdernoSuplier());
        json.put("orderSuplierStatus", Commo.parseSuplierOrderStatus(order.getOrderstatusSuplier()));
        json.put("receiver", order.getStoSalesOrderDelivery().getReceiver());
        json.put("mobile", order.getStoSalesOrderDelivery().getMobile());
        json.put("provinceId", order.getStoSalesOrderDelivery().getProvince());
        json.put("provinceName", order.getStoSalesOrderDelivery().getProvinceName());
        json.put("cityId", order.getStoSalesOrderDelivery().getCity());
        json.put("cityName", order.getStoSalesOrderDelivery().getCityName());
        json.put("zone", order.getStoSalesOrderDelivery().getZone());
        json.put("zoneName", order.getStoSalesOrderDelivery().getZoneName());
        json.put("address", order.getStoSalesOrderDelivery().getAddress());
        json.put("deliveryNo", order.getStoSalesOrderDelivery().getDeliveryNo());
        json.put("deliveryCompanyId", order.getStoSalesOrderDelivery().getDeliveryCompany());
        json.put("deliveryCompanyName", order.getStoSalesOrderDelivery().getDeliveryCompanyName());

        JSONArray JsonDetailList = new JSONArray();
        for (StoSalesOrderDetail detail : order.getStoSalesOrderDetailList()) {
            JSONObject detailjson = new JSONObject();
            detailjson.put("productId", detail.getPid());
            detailjson.put("productPictureUrl", OSSClientUtil.getObjectUrl(detail.getHref()));
            detailjson.put("productName", detail.getProductName());
            detailjson.put("pno", detail.getStoProduct().getPlaProduct().getProductCode());
            detailjson.put("productCode", detail.getStoProduct().getPlaProduct().getPno());
            detailjson.put("categoryName", detail.getCategoryName());
            detailjson.put("color", detail.getColor());
            detailjson.put("size", detail.getSize());
            detailjson.put("shoulder", detail.getShoulder());
            detailjson.put("bust", detail.getBust());
            detailjson.put("waist", detail.getWaist());
            detailjson.put("hipline", detail.getHipline());
            detailjson.put("height", detail.getHeight());
            detailjson.put("weight", detail.getWeight());
            detailjson.put("throatheight", detail.getThroatheight());
            detailjson.put("other", detail.getOther());
            detailjson.put("qty", detail.getQty());
            detailjson.put("price", detail.getPrice());
            detailjson.put("subtotal", detail.getSubtotal());
            for (String key : detailjson.keySet()){
                if (detailjson.get(key) == null) detailjson.put(key, "");
            }
            JsonDetailList.add(detailjson);
        }
        json.put("stoSalesOrderDetailList", JsonDetailList);
        json.put("remarks", order.getRemarks());
        for (String key : json.keySet()){
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 门店采购订单详情
     * @Author: Steven.Xiao
     * @Date: 2017/11/02
     */
    @RequestMapping(value = "/getSuplierOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity getSuplierOrderDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(140, "id不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = storeService.getStoreSuplierOrderDetail(form.id);
        if (suplierOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("suplierOrderNo", suplierOrder.getOrderNo());
        data.put("salesOrderNo", suplierOrder.getStoSalesOrder() == null ? "" : suplierOrder.getStoSalesOrder().getOrderNo());
        data.put("channelName", suplierOrder.getChannelName());
        data.put("orderDate", DateTimeUtils.parseStr(suplierOrder.getOrderDate()));
        data.put("statusName", Commo.parseStoreSuplierOrderStatus(suplierOrder.getStatus()));
        data.put("remarks", suplierOrder.getRemarks());
        data.put("receiver", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getReceiver());
        data.put("mobile", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getMobile());
        data.put("address", suplierOrder.getStoSuplierOrderDelivery() == null ? "" :
                        suplierOrder.getStoSuplierOrderDelivery().getProvinceName()+
                                suplierOrder.getStoSuplierOrderDelivery().getCityName()+
                                suplierOrder.getStoSuplierOrderDelivery().getZoneName()+
                        suplierOrder.getStoSuplierOrderDelivery().getAddress());
        data.put("deliveryCompanyName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompanyName());
        data.put("deliveryNo", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryNo());
        data.put("expectsendDate", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : DateTimeUtils.parseStr(suplierOrder.getStoSuplierOrderDelivery().getExpectsendDate()));

        Integer totalQty=0;
        double totalPrice=0.0;

        JSONArray productList = new JSONArray();
        for (StoSuplierOrderDetail suplierOrderDetail : suplierOrder.getStoSuplierOrderDetailList()) {
            JSONObject json = new JSONObject();
            json.put("productCode", suplierOrderDetail.getPlaProduct() == null ? "" :
                    suplierOrderDetail.getPlaProduct().getProductCode());
            json.put("name", suplierOrderDetail.getProductName());
            json.put("pno", suplierOrderDetail.getPlaProduct() == null ? "" :
                    suplierOrderDetail.getPlaProduct().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(suplierOrderDetail.getHref()));
            json.put("categoryName", suplierOrderDetail.getCategoryName());
            json.put("color", suplierOrderDetail.getColor());
            json.put("size", suplierOrderDetail.getSize());
            json.put("shoulder", suplierOrderDetail.getShoulder());
            json.put("bust", suplierOrderDetail.getBust());
            json.put("waist", suplierOrderDetail.getWaist());
            json.put("hipline", suplierOrderDetail.getHipline());
            json.put("height", suplierOrderDetail.getHeight());
            json.put("weight", suplierOrderDetail.getWeight());
            json.put("throatheight", suplierOrderDetail.getThroatheight());
            json.put("other", suplierOrderDetail.getOther());
            json.put("qty", "");
            if(suplierOrderDetail.getQty() !=null ) {
                json.put("qty", suplierOrderDetail.getQty());
                totalQty += suplierOrderDetail.getQty();
            }
            json.put("price", suplierOrderDetail.getPrice());
            json.put("subtotal", "");
            if(suplierOrderDetail.getSubtotal()!=null) {
                json.put("subtotal", suplierOrderDetail.getSubtotal());
                totalPrice+=suplierOrderDetail.getSubtotal();
            }

            //获取供应商订单信息
            SupOrderDetail supOrderDetail = storeService.findSupOrderDetailByOrderIdAndPid(suplierOrder.getId(), suplierOrderDetail.getPid());
            json.put("producedStatusName", supOrderDetail == null ? "" : supOrderDetail.getSupOrder() == null ? "" :
                    Commo.parseSuplierOrderProducedStatus(supOrderDetail.getSupOrder().getProducedStatus()));
            json.put("suplierName", supOrderDetail == null ? "" : supOrderDetail.getSupOrder() == null ? "" :
                    supOrderDetail.getSupOrder().getSupSuplier() == null ? "" : supOrderDetail.getSupOrder().getSupSuplier().getName());
            //去除null值
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            productList.add(json);
        }

        data.put("productList", productList);
        //总件数
        data.put("totalQty", totalQty);
        //总金额
        data.put("totalPrice", totalPrice);

        Payment pay = paymentService.findPayment(suplierOrder.getId(), 1);
        data.put("payTypeName", pay == null ? "" : Commo.parsePaymentChannelName(pay.getChannel()));
        data.put("payTime", pay == null ? "" : DateTimeUtils.parseStr(pay.getFinishTime()));
        data.put("actualPay", pay == null ? "" : pay.getPrice());
        for (String key : data.keySet()){
            if (data.get(key) == null) data.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 根据门店Id取得该门店的审核记录
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/findStoreApproveLog", method = {RequestMethod.POST})
    public ResponseEntity findStoreApproveLog(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(140, "id不能为空", null), HttpStatus.OK);
        List<StoStoreApproveLog> list = storeService.findStoreApproveLog(form.id);
        JSONArray array = new JSONArray();
        for (StoStoreApproveLog log : list) {
            JSONObject json = new JSONObject();
            json.put("id", log.getId());
            json.put("storeId", log.getStoreId());
            json.put("approveDate",DateTimeUtils.parseStr(log.getApproveDate()));
            json.put("description", log.getDescription());
            json.put("operate", log.getOperate());
            array.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", array), HttpStatus.OK);
    }

    /**
     * @Description: 门店审核操作
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/upateStoreApproveLog", method = {RequestMethod.POST})
    public ResponseEntity upateStoreApproveLog(@RequestBody StoreApproveLogForm form) throws Exception {
        if (form.storeId == null)
            return new ResponseEntity(new RestResponseEntity(140, "门店Id不能为空", null), HttpStatus.OK);

        //更新门店审核状态
        storeService.updateStoreStatus(form.storeId, form.approveStatus);

        //更新门店的审核记录
        storeService.upateStoreApproveLog(form.storeId, form.description,  new Date(), form.operate);

        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 取得门店下拉列表
     * @Author: Steven.Xiao
     * @Date: 2017/11/7
     */
    @RequestMapping(value = "/getStoStoreInfoList", method = {RequestMethod.POST})
    public ResponseEntity getStoStoreInfoList() throws Exception {
        List<StoStoreInfo> list = storeService.getStoStoreInfoList();
        JSONArray data = new JSONArray();
        for (StoStoreInfo base : list) {
            JSONObject json = new JSONObject();
            json.put("id", base.getId());
            json.put("name", base.getStoreName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 根据销售订单Id,取得快递单号和快递公司名称
     * @Author: Steven.Xiao
     * @Date: 2017/11/10
     */
    @RequestMapping(value = "/getStoSalesOrderDeliveryById", method = {RequestMethod.POST})
    public ResponseEntity getStoSalesOrderDeliveryById(@RequestBody IdForm form) throws Exception {
        StoSalesOrderDelivery stoSalesOrderDelivery= storeService.getStoSalesOrderDeliveryById(form.id);
        if(stoSalesOrderDelivery==null)
            return new ResponseEntity(new RestResponseEntity(120,"没有快递信息",null), HttpStatus.OK);
        JSONObject json=new JSONObject();
        json.put("deliveryNo",stoSalesOrderDelivery.getDeliveryNo());
        json.put("deliveryCompanyName",stoSalesOrderDelivery.getDeliveryCompanyName());
        PlaProductBase base = storeService.getPlaProductBaseById(stoSalesOrderDelivery.getDeliveryCompany());
        json.put("deliveryCode",base == null ? "" : base.getDescription());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

}
