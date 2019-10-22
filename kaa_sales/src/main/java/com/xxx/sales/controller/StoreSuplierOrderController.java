package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.*;
import com.xxx.sales.service.StoreSuplierOrderService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.service.PaymentService;
import com.xxx.user.utils.GenerateNumberUtil;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
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
import java.util.*;


@Controller
@RequestMapping("/storeSuplierOrder")
public class StoreSuplierOrderController {

    @Autowired
    private StoreSuplierOrderService storeSuplierOrderService;
    @Autowired
    private PaymentService paymentService;

    /**
     * @Description: 采购订单列表
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyOrderHtml", method = {RequestMethod.GET})
    public String buyOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "buyOrder/buyOrder";
    }

    /**
     * @Description:根据采购订单id获取订单快递信息
     * @Author: hanchao
     * @Date: 2017/12/19 0019
     */
    @RequestMapping(value = "/buyOrderDeliveryMessage", method = {RequestMethod.POST})
    public ResponseEntity buyOrderDeliveryMessage(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        StoSuplierOrderDelivery sto = storeSuplierOrderService.buyOrderDeliveryMessage(form.id);
        if(sto == null) {
            return new ResponseEntity(new RestResponseEntity(120, "该订单信息不存在", null), HttpStatus.OK);
        }
        JSONObject json = new JSONObject();
        json.put("createTime",DateTimeUtils.parseStr(sto.getCreatedDate()));
        json.put("deliveryCompanyName", sto.getDeliveryCompanyName() == null ? "" : sto.getDeliveryCompanyName());
        json.put("deliveryNo",sto.getDeliveryNo());
        PlaProductBase base = sto.getDeliveryCompany() == null ? null :
                        (PlaProductBase) storeSuplierOrderService.get2(PlaProductBase.class, sto.getDeliveryCompany());
        json.put("deliveryCom", base == null ? "" : base.getDescription());
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 采购订单详情
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyOrderDetailHtml", method = {RequestMethod.GET})
    public String buyOrderDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id))
            return "redirect: buyOrderBuyHtml";
        StoSuplierOrder suplierOrder = storeSuplierOrderService.findStoSuplierOrder(null, Integer.parseInt(id));
        JSONObject data = new JSONObject();
        data.put("suplierOrderNo", suplierOrder.getOrderNo());
        data.put("orderId", suplierOrder.getId());
        data.put("salesOrderNo", suplierOrder.getStoSalesOrder() == null ? "" : suplierOrder.getStoSalesOrder().getOrderNo());
        data.put("orderDate", DateTimeUtils.parseStr(suplierOrder.getOrderDate()));
        data.put("statusName", Commo.parseStoreSuplierOrderStatus(suplierOrder.getStatus()));
        data.put("remarks", suplierOrder.getRemarks());
        data.put("channelName", suplierOrder.getChannelName());
        data.put("receiver", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getReceiver());
        data.put("mobile", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getMobile());
        data.put("provinceName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getProvinceName());
        data.put("cityName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getCityName());
        data.put("zoneName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getZoneName());
        data.put("address", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getAddress());
        data.put("deliveryCompanyName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompanyName());
        data.put("deliveryNo", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryNo());
        data.put("expectsendDate", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : DateTimeUtils.parseStr(suplierOrder.getStoSuplierOrderDelivery().getExpectsendDate()));
        Integer qtySum = 0;
        Double subtotalSum = 0.0;
        JSONArray productList = new JSONArray();
        for (StoSuplierOrderDetail suplierOrderDetail : suplierOrder.getStoSuplierOrderDetailList()) {
            JSONObject json = new JSONObject();
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
            json.put("qty", suplierOrderDetail.getQty());
            json.put("price", suplierOrderDetail.getPrice());
            json.put("subtotal", suplierOrderDetail.getSubtotal());

            qtySum += suplierOrderDetail.getQty() == null ? 0 : suplierOrderDetail.getQty();
            subtotalSum = Arith.add(subtotalSum, suplierOrderDetail.getSubtotal() == null ? 0.0 : suplierOrderDetail.getSubtotal());
            // 过滤value == null 的数据    使其为空
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            productList.add(json);
        }
        data.put("productList", productList);
        data.put("qtySum", qtySum);
        data.put("subtotalSum", subtotalSum);
        //获取支付成功记录
        Payment pay = paymentService.findPayment(Integer.parseInt(id), 1);
        data.put("payTypeName", pay == null ? "" : Commo.parsePaymentChannelName(pay.getChannel()));
        data.put("payTime", pay == null ? "" : DateTimeUtils.parseStr(pay.getFinishTime()));
        data.put("actualPay", pay == null ? "" : pay.getPrice());
        // 过滤value == null 的数据    使其为空
        for (String key : data.keySet()) {
            if (data.get(key) == null) data.put(key, "");
        }
        modelMap.put("data", data);
        return "buyOrder/buyOrderDetail";
    }

    /**
     * @Description: 采购订单下单
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyOrderBuyHtml", method = {RequestMethod.GET})
    public String buyOrderBuyHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String ids = request.getParameter("ids");
        String salesOrderId = request.getParameter("salesOrderId");
        String storePid = request.getParameter("storePid");

        modelMap.put("orderNo", GenerateNumberUtil.generateStoreSuplierNumber());
        modelMap.put("salesOrderId", StringUtils.isNotBlank(salesOrderId) ? salesOrderId : "");
        modelMap.put("storePid", StringUtils.isNotBlank(storePid) ? storePid : "");
        if (StringUtils.isNotBlank(storePid)) {
            StoProduct product = storeSuplierOrderService.productDetail(Integer.parseInt(storePid));
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("name", product.getName());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("price", product.getPlaProduct().getPlaProductPriceList() == null ? "" : product.getPlaProduct().getPlaProductPriceList().size() == 0 ? "" : product.getPlaProduct().getPlaProductPriceList().get(0).getOfflinePrice());
            json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            JSONArray priceList = new JSONArray();
            Set<String> colors = new HashSet<>();
            Set<String> sizes = new HashSet<>();
            for (PlaProductPrice price : product.getPlaProduct().getPlaProductPriceList()) {
                colors.add(price.getColor());
                sizes.add(price.getSize());
                JSONObject js = new JSONObject();
                js.put("price", price.getOfflinePrice() == null ? "" : price.getOfflinePrice());
//                js.put("stock", price.getStock() == null ? "" : price.getStock());
                js.put("color", price.getColor() == null ? "" : price.getColor());
                js.put("size", price.getSize() == null ? "" : price.getSize());
                priceList.add(js);
            }
            json.put("colors", colors);
            json.put("sizes", sizes);
            json.put("priceList", priceList);
            modelMap.put("storeProduct", json);

            return "buyOrder/buyOrderBuy"; //目前商品采购时 不存在订单信息
        }
        JSONArray data = new JSONArray();
        if (StringUtils.isNotBlank(ids)) {
            //返回附带的商品数据
//            String[] proIds = ids.split(",");
//            for (String proId : proIds) {
//                StoProduct product = storeSuplierOrderService.productDetail(Integer.parseInt(proId));
//                JSONObject json = new JSONObject();
//                json.put("id", product.getId());
//                json.put("name", product.getName());
//                json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
//                json.put("price", product.getStoProductPriceList() == null ? "" : product.getStoProductPriceList().size() == 0 ? "" : product.getStoProductPriceList().get(0).getOfflinePrice());
//                json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
//                data.add(json);
//            }
        }
        if (StringUtils.isNotBlank(salesOrderId)) {
            List<StoSalesOrderDetail> list = storeSuplierOrderService.getStoSalesOrderDetailList(Integer.parseInt(salesOrderId));
            for (StoSalesOrderDetail product : list) {
                JSONObject json = new JSONObject();
                json.put("id", product.getPid());
                json.put("name", product.getProductName());
                json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
                json.put("categoryName", product.getCategoryName());
                json.put("color", product.getColor());
                json.put("size", product.getSize());
                json.put("throatheight", product.getThroatheight());
                json.put("shoulder", product.getShoulder());
                json.put("bust", product.getBust());
                json.put("waist", product.getWaist());
                json.put("hipline", product.getHipline());
                json.put("height", product.getHeight());
                json.put("weight", product.getWeight());
                json.put("other", product.getOther());
                json.put("qty", product.getQty());
                PlaProductPrice plaProductPrice = storeSuplierOrderService.get2(PlaProductPrice.class, "pid", product.getStoProduct().getPlatProductId(),
                        "color", product.getColor(), "size", product.getSize());
                if (plaProductPrice == null)
                    throw new ResponseEntityException(130, "平台商品不存在");
                json.put("price", plaProductPrice.getOfflinePrice());
                json.put("subtotal", Arith.mul(plaProductPrice.getOfflinePrice(), product.getQty()));
                // 过滤value == null 的数据    使其为空
                for (String key : json.keySet()) {
                    if (json.get(key) == null) json.put(key, "");
                }
                data.add(json);
            }
        }
        modelMap.put("data", data);
        //获取销售订单的收货信息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("receiver", "");
        jsonObject.put("mobile", "");
        jsonObject.put("address", "");
        if (StringUtils.isNotBlank(salesOrderId)) {
            StoSalesOrderDelivery salesOrderDelivery = storeSuplierOrderService.get2(StoSalesOrderDelivery.class, "orderId", Integer.parseInt(salesOrderId));
            if (salesOrderDelivery != null) {
                jsonObject.put("receiver", salesOrderDelivery.getReceiver());
                jsonObject.put("mobile", salesOrderDelivery.getMobile());
                jsonObject.put("address", salesOrderDelivery.getAddress());
                jsonObject.put("province", salesOrderDelivery.getProvince());
                jsonObject.put("provinceName", salesOrderDelivery.getProvinceName());
                jsonObject.put("city", salesOrderDelivery.getCity());
                jsonObject.put("cityName", salesOrderDelivery.getCityName());
                jsonObject.put("zone", salesOrderDelivery.getZone());
                jsonObject.put("zoneName", salesOrderDelivery.getZoneName());
                jsonObject.put("expectsendDate", DateTimeUtils.parseStr(salesOrderDelivery.getExpectsendDate(), "yyyy-MM-dd"));
            }

        }
        modelMap.put("salesOrderDelivery", jsonObject);
        return "buyOrder/buyOrderBuy";
    }

    /**
     * @Description: 采购订单下单添加商品
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyOrderBuyAddProduct", method = {RequestMethod.POST})
    public ResponseEntity buyOrderBuyAddProduct(@RequestBody PnoForm form) throws Exception {
        if (StringUtils.isBlank(form.pno))
            return new ResponseEntity(new RestResponseEntity(110, "货号不能为空", null), HttpStatus.OK);
        StoProduct product = storeSuplierOrderService.getProductByPno(form.pno);
        if (product == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", product.getId());
        json.put("name", product.getName());
        json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
        json.put("price", product.getStoProductPriceList() == null ? "" : product.getStoProductPriceList().size() == 0 ? "" : product.getStoProductPriceList().get(0).getOfflinePrice());
        json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 商品库
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyOrderStorageHtml", method = {RequestMethod.GET})
    public String buyOrderStorageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "buyOrder/buyOrderStorage";
    }

    /**
     * @Description: 商品库详情
     * @Author: Chen.zm
     * @Date: 2017/11/6 0006
     */
    @RequestMapping(value = "/buyGoodsDetailHtml", method = {RequestMethod.GET})
    public String buyGoodsDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        StoProduct product = storeSuplierOrderService.productDetail(Integer.parseInt(id));
        JSONObject data = new JSONObject();
        data.put("id", product.getId());
        data.put("name", product.getName());
        data.put("pno", product.getPlaProduct() == null ? "" : product.getPlaProduct().getPno());
        data.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
        data.put("updateDate", DateTimeUtils.parseStr(product.getUpdateDate()));
        data.put("remarks", product.getRemarks());
        data.put("vedioUrl", product.getVedioUrl());
        //2017.12.07 图文详情字段隔离
//        data.put("description", product.getDescription());
        data.put("description",storeSuplierOrderService.getProductDescription(product.getId()));

        data.put("suplierDay", product.getSuplierDay());
        data.put("brand", product.getBrand());
        JSONArray pictureList = new JSONArray();
        for (StoProductPicture picture : product.getStoProductPictureList()) {
            JSONObject json = new JSONObject();
            json.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            json.put("isMainpic", picture.getMainpic());
            pictureList.add(json);
        }
        data.put("pictureList", pictureList);
        JSONArray supplierList = new JSONArray();
        for (StoProductPrice supplier : product.getStoProductPriceList()) {
            JSONObject json = new JSONObject();
            json.put("price", supplier.getOfflinePrice() == null ? "" : supplier.getOfflinePrice());
            json.put("stock", supplier.getStock() == null ? "" : supplier.getStock());
            json.put("color", supplier.getColor() == null ? "" : supplier.getColor());
            json.put("size", supplier.getSize() == null ? "" : supplier.getSize());
            supplierList.add(json);
        }
        data.put("supplierList", supplierList);
        // 过滤value == null 的数据    使其为空
        for (String key : data.keySet()) {
            if (data.get(key) == null) data.put(key, "");
        }
        modelMap.put("data", data);
        return "buyOrder/buyGoodsDetail";
    }


    /**
    * @Description: 门店采购订单列表
    * @Author: Chen.zm
    * @Date: 2017/10/31 0031
    */
    @RequestMapping(value = "/findStoreSuplierOrderList", method = {RequestMethod.POST})
    public ResponseEntity findStoreSuplierOrderList(@RequestBody StoreSuplierOrderForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        MybatisPageQuery pageQuery = new MybatisPageQuery(form.pageNum);
        pageQuery.getParams().put("storeId", CurrentUser.get().getStoreId());
        pageQuery.getParams().put("productName", form.productName);
        pageQuery.getParams().put("suplierOrderNo", form.suplierOrderNo);
        pageQuery.getParams().put("salesOrderNo", form.salesOrderNo);
        pageQuery.getParams().put("orderStatus", form.orderStatus);
        pageQuery.getParams().put("startTime", form.startTime);
        pageQuery.getParams().put("endTime", form.endTime);
        PageList<JSONObject> suplierOrderList = storeSuplierOrderService.findStoreSuplierOrderList(pageQuery);
        //处理数据格式
        Map<Integer, JSONObject> map = new LinkedHashMap<>();
        for (JSONObject suplierOrder : suplierOrderList) {
            Integer groupId = suplierOrder.getInteger("id");
            if (map.get(groupId) == null) { //添加分类数据
                JSONObject json = new JSONObject();
                json.put("id", suplierOrder.get("id"));
                json.put("suplierOrderNo", suplierOrder.get("suplierOrderNo"));
                json.put("orderDate", DateTimeUtils.parseStr(suplierOrder.get("orderDate")));
                json.put("status", suplierOrder.get("status"));
                json.put("statusName", Commo.parseStoreSuplierOrderStatus(suplierOrder.getInteger("status")));
                json.put("salesOrderNo", suplierOrder.get("salesOrderNo"));
                json.put("channelName", suplierOrder.get("channelName") == null ? "" : suplierOrder.get("channelName"));
                json.put("productList", new JSONArray());
                map.put(groupId, json);
            }
            //添加项目数据
            JSONObject json = new JSONObject();
            json.put("href", suplierOrder.get("href") == null ? "" : OSSClientUtil.getObjectUrl(suplierOrder.get("href").toString()));
            json.put("productName", suplierOrder.get("productName"));
            json.put("color", suplierOrder.get("color"));
            json.put("size", suplierOrder.get("size"));
            json.put("qty", suplierOrder.get("qty"));
            json.put("price", suplierOrder.get("price"));
            json.put("subtotal", suplierOrder.get("subtotal"));
            ((JSONArray) map.get(groupId).get("productList")).add(json);
        }
        //封装数据
        JSONArray data = new JSONArray();
        for (JSONObject json : map.values()) {
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.getOffset(), pageQuery.getLimit(), suplierOrderList.total), HttpStatus.OK);
    }


    /**
     * @Description: 门店采购订单详情 （未用） 【*注： 使用时请注明
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @RequestMapping(value = "/storeSuplierOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity storeSuplierOrderDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = storeSuplierOrderService.findStoSuplierOrder(CurrentUser.get().getStoreId(), form.id);
        if (suplierOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("suplierOrderNo", suplierOrder.getOrderNo());
        data.put("salesOrderNo", suplierOrder.getStoSalesOrder() == null ? "" : suplierOrder.getStoSalesOrder().getOrderNo());
        data.put("orderDate", suplierOrder.getOrderDate());
        data.put("statusName", Commo.parseStoreSuplierOrderStatus(suplierOrder.getStatus()));
        data.put("remarks", suplierOrder.getRemarks());
        data.put("channelName", suplierOrder.getChannelName());
        data.put("receiver", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getReceiver());
        data.put("mobile", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getMobile());
        data.put("address", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getAddress());
        data.put("deliveryCompanyName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompanyName());
        data.put("deliveryNo", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryNo());
        data.put("expectsendDate", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getExpectsendDate());
        JSONArray productList = new JSONArray();
        for (StoSuplierOrderDetail suplierOrderDetail : suplierOrder.getStoSuplierOrderDetailList()) {
            JSONObject json = new JSONObject();
            json.put("name", suplierOrderDetail.getProductName());
            json.put("href", suplierOrderDetail.getHref());
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
            json.put("qty", suplierOrderDetail.getQty());
            json.put("price", suplierOrderDetail.getPrice());
            json.put("subtotal", suplierOrderDetail.getSubtotal());
            productList.add(json);
        }
        data.put("productList", productList);
        Payment pay = paymentService.findPayment(suplierOrder.getId(), 1);
        data.put("payTypeName", pay == null ? "" : Commo.parsePaymentChannelName(pay.getChannel()));
        data.put("payTime", pay == null ? "" : DateTimeUtils.parseStr(pay.getFinishTime()));
        data.put("actualPay", pay.getPrice());
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 商品库商品列表
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @RequestMapping(value = "/productList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody PlaProductListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoProduct> productList = storeSuplierOrderService.findProductList(pageQuery, form.name, form.categoryId,form.pno);
        JSONArray data = new JSONArray();
        for (StoProduct product : productList) {
            JSONObject json = new JSONObject();
            json.put("id", product.getId());
            json.put("name", product.getName());
            json.put("pno", product.getPlaProduct() == null ? "" : product.getPlaProduct().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(product.getHref()));
            json.put("price", product.getStoProductPriceList() == null ? "" : product.getStoProductPriceList().size() == 0 ? "" : product.getStoProductPriceList().get(0).getOfflinePrice());
            json.put("categoryName", product.getPlaProductCategory() == null ? "" : product.getPlaProductCategory().getName());
            json.put("updateDate", DateTimeUtils.parseStr(product.getUpdateDate()));
            json.put("suplierDay", product.getSuplierDay());
            json.put("brand", product.getBrand());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, productList.total), HttpStatus.OK);
    }


    /**
     * @Description: 门店采购订单下单： 门店商品下单，订单采购
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @RequestMapping(value = "/submitOrder", method = {RequestMethod.POST})
    public ResponseEntity submitOrder(@RequestBody SubmitOrderForm form) throws Exception {
        if (form.orderDetail.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "订单商品不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = storeSuplierOrderService.saveStoSuplierOrder(form, CurrentUser.get().getStoreId());
        JSONObject json = new JSONObject();
        json.put("orderId", suplierOrder.getId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }


    /**
     * @Description:  销售商品下单
     * @Author: Chen.zm
     * @Date: 2017/11/20 0020
     */
    @RequestMapping(value = "/submitOrderSales", method = {RequestMethod.POST})
    public ResponseEntity submitOrderSales(@RequestBody SubmitOrderForm form) throws Exception {
        if (form.orderDetail.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "订单商品不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.receiver))
            return new ResponseEntity(new RestResponseEntity(120, "收件人不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.mobile))
            return new ResponseEntity(new RestResponseEntity(130, "收件电话不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.address))
            return new ResponseEntity(new RestResponseEntity(140, "收件地址不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = storeSuplierOrderService.saveStoSuplierOrderSales(form, CurrentUser.get().getStoreId());
        JSONObject json = new JSONObject();
        json.put("orderId", suplierOrder.getId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 取消订单
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderCancel", method = {RequestMethod.POST})
    public ResponseEntity cancelSuplierOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        storeSuplierOrderService.updateSuplierOrderSuplierOrderCancel(form.id, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 删除订单
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderRemove", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderRemove(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        storeSuplierOrderService.removeSuplierOrderSuplierOrder(form.id, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 确认收货
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderReceive", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderReceive(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        storeSuplierOrderService.updateSuplierOrderSuplierOrderReceive(form.id, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }



    /**
     * @Description: 门店订单数量
     * @Author: Chen.zm
     * @Date: 2017/11/24 0024
     */
    @RequestMapping(value = "/orderCount", method = {RequestMethod.POST})
    public ResponseEntity orderCount() throws Exception {
        List<JSONObject> json = storeSuplierOrderService.orderCount(CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", json), HttpStatus.OK);
    }


}
