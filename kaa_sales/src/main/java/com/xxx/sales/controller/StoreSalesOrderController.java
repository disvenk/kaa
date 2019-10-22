package com.xxx.sales.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.*;
import com.xxx.sales.service.StoreSalesOrderService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.PlaProductBase;
import com.xxx.model.business.StoSalesOrder;
import com.xxx.model.business.StoSalesOrderDelivery;
import com.xxx.model.business.StoSalesOrderDetail;
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
@RequestMapping("/storeSalesOrder")
public class StoreSalesOrderController {

    @Autowired
    private StoreSalesOrderService stoSalesOrderService;

    /**
     * @Description: 订单详情页
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/salesDetailHtml", method = {RequestMethod.GET})
    public String salesDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "salesOrder/orderDetail";
    }

    /**
     * @Description: 订单编辑页面
     * @Author: Steven.Xiao
     * @Date: 2017/11/6
     */
    @RequestMapping(value = "/orderDetailEditHtml", method = {RequestMethod.GET})
    public String orderDetailEditHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("id");
        modelMap.put("id", id);
        return "salesOrder/orderDetailEdit";
    }

    /**
     * @Description: 跳转到门店端的销售管理平台
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/salesOrderHtml", method = {RequestMethod.GET})
    public String salesOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "salesOrder/salesOrder";
    }

   /**
    * @Description: 根据条件查询销售订单列表
    * @Author: Steven.Xiao
    * @Date: 2017/10/26
    */
    @RequestMapping(value = "/findStoreSalesOrderList", method = {RequestMethod.POST})
    public ResponseEntity findStoreSalesOrderList(@RequestBody StoreSalesOrderQueryForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        Integer storeId=CurrentUser.get().getStoreId();
       // Integer storeId=1;
        /*分页*/
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<StoSalesOrder> orderList = stoSalesOrderService.findStoreSalesOrderList(pageQuery,storeId,form.receiver,form.mobile,form.orderNo,form.suplierOrderNo);

        JSONArray jsonArray = new JSONArray();
        for (StoSalesOrder order : orderList) {
            JSONObject json = new JSONObject();
            /*订单ID*/
            json.put("orderId", order.getId());
            /* 订单号*/
            json.put("orderNo", order.getOrderNo());
            /* 订单日期*/
            json.put("orderDate", DateTimeUtils.parseStr(order.getOrderDate()));
            //销售订单状态
            json.put("orderStatus", Commo.parseSalesOrderStatus(order.getStatus()));
            //采购订单Id
            json.put("supplierOrderId", order.getStoSuplierOrder()==null?"":order.getStoSuplierOrder().getId());

            //采购订单状态
            json.put("orderSuplierStatus",Commo.parseSuplierOrderStatus(order.getOrderstatusSuplier()));
            /*订单总金额*/
            json.put("ordertotal",order.getTotal());

            String productIds="";

            /*订单明细*/
            JSONArray JsonDetailList = new JSONArray();
            for (StoSalesOrderDetail detail : order.getStoSalesOrderDetailList()) {
                JSONObject detailjson = new JSONObject();
                /*商品ID*/
                detailjson.put("productId",detail.getPid());

                productIds+=detail.getPid().toString()+",";

                /* 商品主图*/
                detailjson.put("productPicture", OSSClientUtil.getObjectUrl(detail.getHref()));
                /*商品名称*/
                detailjson.put("productName",detail.getProductName());
                //颜色
                detailjson.put("color",detail.getColor());
                //尺寸
                detailjson.put("size",detail.getSize());
                /*单价*/
                detailjson.put("price",detail.getPrice());
                /*数量*/
                detailjson.put("qty",detail.getQty());

                JsonDetailList.add(detailjson);
            }
            /*明细Json数据加入主订单Json中返回*/
            json.put("detailList",JsonDetailList);
            //商品的Ids
            if(productIds!="") {
                json.put("productIds", productIds.substring(0, productIds.length() - 1));
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
        /*取得订单对象*/
        StoSalesOrder order = stoSalesOrderService.getStoSalesOrderDetail(form.id);
        if (order == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单不存在", null), HttpStatus.OK);
        /*订单对象取值*/
        JSONObject json = new JSONObject();
        //订单Id
        json.put("orderId", order.getId());
        //订单号
        json.put("orderNo", order.getOrderNo());
        //下单日期
        json.put("orderDate",DateTimeUtils.parseStr(order.getOrderDate()));
        //期望发货日期
        json.put("expectsendDate", order.getStoSalesOrderDelivery()==null ? "":DateTimeUtils.parseStr(order.getStoSalesOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
        //销售订单状态
        json.put("orderStatus", Commo.parseSalesOrderStatus(order.getStatus()));
        //采购订单编号
        json.put("orderNoSuplier",order.getOrdernoSuplier());
        //采购订单状态
        json.put("orderSuplierStatus",Commo.parseSuplierOrderStatus(order.getOrderstatusSuplier()));

        //收件信息
        //收件人
        json.put("receiver",order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getReceiver());
        //收件人电话
        json.put("mobile", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getMobile());
        //收件人地址
        //省id
        json.put("provinceId", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getProvince());
        //省名称
        json.put("ProvinceName", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getProvinceName());
        //市id
        json.put("cityId", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getCity());
        //市名称
        json.put("cityName", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getCityName());
        //区id
        json.put("Zone", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getZone());
        //区名称
        json.put("ZoneName", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getZoneName());
        //详细地址
        json.put("address", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getAddress());
        //快递单号
        json.put("deliveryNo", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getDeliveryNo());
        //快递公司id
        json.put("deliveryCompanyId", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getDeliveryCompany());
        //快递公司名称
        json.put("deliveryCompanyName", order.getStoSalesOrderDelivery()== null ? "": order.getStoSalesOrderDelivery().getDeliveryCompanyName());

        //订单明细信息
        //总件数
        Integer totalQty=0;
        //总价格
        Double totalPrice=0.0;

        //备注
        json.put("remarks",order.getRemarks());

        JSONArray JsonDetailList = new JSONArray();
        for (StoSalesOrderDetail detail : order.getStoSalesOrderDetailList()) {
            JSONObject detailjson = new JSONObject();
                /*商品ID*/
            detailjson.put("productId",detail.getPid());
                /* 商品主图*/
            detailjson.put("productPictureUrl",OSSClientUtil.getObjectUrl(detail.getHref()));
                /*商品名称*/
            detailjson.put("productName",detail.getProductName());
            //分类
            detailjson.put("categoryName",detail.getCategoryName());
            //颜色
            detailjson.put("color",detail.getColor());
            //尺寸
            detailjson.put("size",detail.getSize());
            //肩宽
            detailjson.put("shoulder",detail.getShoulder());
            //胸围
            detailjson.put("bust",detail.getBust());
            //腰围
            detailjson.put("waist",detail.getWaist());
            //臀围
            detailjson.put("hipline",detail.getHipline());
            //身高
            detailjson.put("height",detail.getHeight());
            //体重
            detailjson.put("weight",detail.getWeight());
            //喉到地
            detailjson.put("throatheight",detail.getThroatheight());
            //其它
            detailjson.put("other",detail.getOther());
            //件数
            detailjson.put("qty",detail.getQty());

            if(detail.getQty()!=null) {
                totalQty += detail.getQty();
            }
            //单价
            detailjson.put("price",detail.getPrice());
            //总价
            detailjson.put("subtotal",detail.getSubtotal());
            //订单总价
            if(detail.getSubtotal()!=null)
            {
                totalPrice+=detail.getSubtotal();
            }

            JsonDetailList.add(detailjson);
        }

        json.put("totalQty",totalQty);
        json.put("totalPrice",totalPrice);

        json.put("stoSalesOrderDetailList",JsonDetailList);

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 销售订单取消
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @RequestMapping(value = "/removeStoSalesOrder", method = {RequestMethod.POST})
    public ResponseEntity removeStoSalesOrder(@RequestBody IdForm form) throws Exception {
        stoSalesOrderService.removeStoSalesOrder(form.id, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 销售订单中，快递单号和快递公司保存；
     * @Author: Steven.Xiao
     * @Date: 2017/10/27
     */
    @RequestMapping(value = "/saveStoSalesOrderDelivery", method = {RequestMethod.POST})
    public ResponseEntity saveStoSalesOrderDelivery(@RequestBody StoreSalesOrderDeliverNoSaveForm form) throws Exception {
        stoSalesOrderService.saveStoSalesOrderDelivery(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    
    /**
     * @Description: 销售订单,编辑后，保存
     * @Author: Steven.Xiao
     * @Date: 2017/10/31
     */
    @RequestMapping(value = "/updateStoSalesOrder", method = {RequestMethod.POST})
    public ResponseEntity updateStoSalesOrder(@RequestBody StoreSalesOrderEditForm form) throws Exception {
        stoSalesOrderService.updateStoSalesOrder(form, CurrentUser.get().getStoreId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 根据销售订单Id,取得快递单号和快递公司名称
     * @Author: Steven.Xiao
     * @Date: 2017/11/10
     */
    @RequestMapping(value = "/getStoSalesOrderDeliveryById", method = {RequestMethod.POST})
    public ResponseEntity getStoSalesOrderDeliveryById(@RequestBody IdForm form) throws Exception {
       StoSalesOrderDelivery stoSalesOrderDelivery= stoSalesOrderService.getStoSalesOrderDeliveryById(form.id);
       if(stoSalesOrderDelivery==null)
           return new ResponseEntity(new RestResponseEntity(120,"没有快递信息",null), HttpStatus.OK);
        JSONObject json=new JSONObject();
        json.put("deliveryNo",stoSalesOrderDelivery.getDeliveryNo());
        json.put("deliveryCompanyName",stoSalesOrderDelivery.getDeliveryCompanyName());
        PlaProductBase base = stoSalesOrderService.getPlaProductBaseById(stoSalesOrderDelivery.getDeliveryCompany());
        json.put("deliveryCode",base == null ? "" : base.getDescription());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

}
