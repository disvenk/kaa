package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.SuplierOrderService;
import com.xxx.core.exceptions.ResponseEntityException;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.Commo;
import com.xxx.user.service.PaymentService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/orderManage")
public class OrderManageController {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private PaymentService paymentService;


    @RequestMapping(value = "/orderManageHtml", method = {RequestMethod.GET})
    public String orderManage(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "orderManage/orderManage";
    }

    @RequestMapping(value = "/orderManageDetailHtml", method = {RequestMethod.GET})
    public String orderManageDetail(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "orderManage/orderManageDetail";
    }

    @RequestMapping(value = "/orderManageEditHtml", method = {RequestMethod.GET})
    public String orderManageEdit(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "orderManage/orderManageEdit";
    }
    @RequestMapping(value = "/orderManageStatusHtml", method = {RequestMethod.GET})
    public String orderManageStatus(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String suplierOrderNo = request.getParameter("suplierOrderNo");
        modelMap.put("suplierOrderNo", suplierOrderNo);
        return "orderManage/orderManageStatus";
    }


   /**
    * @Description: 采购订单列表
    * @Author: Chen.zm
    * @Date: 2017/10/31 0031
    */
    @RequestMapping(value = "/findSuplierOrderList", method = {RequestMethod.POST})
    public ResponseEntity findSuplierOrderList(@RequestBody SuplierOrderForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        MybatisPageQuery pageQuery = new MybatisPageQuery(form.pageNum);
        pageQuery.getParams().put("productName", form.productName);
        pageQuery.getParams().put("nameMobile", form.nameMobile);
        pageQuery.getParams().put("suplierOrderNo", form.suplierOrderNo);
        pageQuery.getParams().put("orderStatus", form.orderStatus);
        pageQuery.getParams().put("startTime", form.startTime);
        pageQuery.getParams().put("endTime", form.endTime);
        pageQuery.getParams().put("producedStatus", form.producedStatus);
        pageQuery.getParams().put("channelType", form.channelType);
        pageQuery.getParams().put("channelName", form.channelName);
        PageList<JSONObject> suplierOrderList = suplierOrderService.findSuplierOrderList(pageQuery);
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
                json.put("statusName", Commo.parseStoreSuplierOrderStatus(suplierOrder.getInteger("status")));//订单状态
                json.put("channelName", suplierOrder.get("channelName") == null ? "" : suplierOrder.get("channelName"));
                json.put("channelType", suplierOrder.get("channelType") == null ? "" : Commo.parseChannelType(suplierOrder.get("channelType").toString())); //渠道来源
                json.put("salesOrderNo", suplierOrder.get("salesOrderNo"));
                json.put("name", suplierOrder.get("name"));
                json.put("mobile", suplierOrder.get("mobile"));
                json.put("productList", new JSONArray());
                //去除null值
                for(String key : json.keySet()){
                    if(json.get(key) == null) json.put(key,"");
                }
                map.put(groupId, json);
            }
            //添加项目数据
            JSONObject json = new JSONObject();
            json.put("pid", suplierOrder.get("pid"));
            json.put("productCode", suplierOrder.get("productCode"));
            json.put("detId", suplierOrder.get("detId"));
            json.put("href", suplierOrder.get("href") == null ? "" : OSSClientUtil.getObjectUrl(suplierOrder.get("href").toString()));
            json.put("productName", suplierOrder.get("productName"));
            json.put("color", suplierOrder.get("color"));
            json.put("size", suplierOrder.get("size"));
            json.put("qty", suplierOrder.get("qty"));
            json.put("price", suplierOrder.get("price"));
            json.put("subtotal", suplierOrder.get("subtotal"));
//            json.put("producedStatus", suplierOrder.get("producedStatus"));//生产状态
//            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(suplierOrder.getInteger("producedStatus")));//生产状态
//            json.put("supOrderNo", suplierOrder.get("supOrderNo"));
//            json.put("suplierId", suplierOrder.get("suplierId"));
//            json.put("suplierName", suplierOrder.get("suplierName"));
            //去除null值
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
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
     * @Description: 采购订单详情
     * @Author: Chen.zm
     * @Date: 2017/10/31 0031
     */
    @RequestMapping(value = "/suplierOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = suplierOrderService.findStoSuplierOrder(form.id);
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
        data.put("provinceName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getProvinceName());
        data.put("province", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getProvince());
        data.put("cityName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getCityName());
        data.put("city", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getCity());
        data.put("zoneName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getZoneName());
        data.put("zone", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getZone());
        data.put("address", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getAddress());
        data.put("deliveryCompanyName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompanyName());
        data.put("deliveryNo", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryNo());
        data.put("expectsendDate", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : DateTimeUtils.parseStr(suplierOrder.getStoSuplierOrderDelivery().getExpectsendDate()));
        Integer qtySum = 0;
        Double subtotalSum = 0.0;
        JSONArray productList = new JSONArray();
        for (StoSuplierOrderDetail suplierOrderDetail : suplierOrder.getStoSuplierOrderDetailList()) {
            JSONObject json = new JSONObject();
            json.put("pid", suplierOrderDetail.getPid());
            json.put("name", suplierOrderDetail.getProductName());
            json.put("pno", suplierOrderDetail.getPlaProduct() == null ? "" :
                    suplierOrderDetail.getPlaProduct().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(suplierOrderDetail.getHref()));
            json.put("categoryName", suplierOrderDetail.getCategoryName());
            json.put("color", suplierOrderDetail.getColor());
            json.put("size", suplierOrderDetail.getSize());
            json.put("shoulder", suplierOrderDetail.getShoulder() == null ? "" : suplierOrderDetail.getShoulder());
            json.put("bust", suplierOrderDetail.getBust() == null ? "" : suplierOrderDetail.getBust());
            json.put("waist", suplierOrderDetail.getWaist() == null ? "" : suplierOrderDetail.getWaist());
            json.put("hipline", suplierOrderDetail.getHipline() == null ? "" : suplierOrderDetail.getHipline());
            json.put("height", suplierOrderDetail.getHeight() == null ? "" : suplierOrderDetail.getHeight());
            json.put("weight", suplierOrderDetail.getWeight() == null ? "" : suplierOrderDetail.getWeight());
            json.put("throatheight", suplierOrderDetail.getThroatheight() == null ? "" : suplierOrderDetail.getThroatheight());
            json.put("other", suplierOrderDetail.getOther() == null ? "" : suplierOrderDetail.getOther());
            json.put("qty", suplierOrderDetail.getQty());
            json.put("price", suplierOrderDetail.getPrice());
            json.put("subtotal", suplierOrderDetail.getSubtotal());

            qtySum += json.getInteger("qty");
            subtotalSum = Arith.add(subtotalSum, json.getDouble("subtotal"));

            //获取供应商订单信息  2018.1.8 因供应商工单结构调整，取供应商自己的商品库，颜色，尺寸，暂屏蔽对接合一智造平台
//            SupOrderDetail supOrderDetail = suplierOrderService.findSupOrderDetailByOrderIdAndPid(suplierOrder.getId(), suplierOrderDetail.getPid(), suplierOrderDetail.getColor(), suplierOrderDetail.getSize());
//            json.put("producedStatus", supOrderDetail == null ? "" : supOrderDetail.getSupOrder() == null ? "" : supOrderDetail.getSupOrder().getProducedStatus());
//            json.put("producedStatusName", supOrderDetail == null ? "" : supOrderDetail.getSupOrder() == null ? "" :
//                    Commo.parseSuplierOrderProducedStatus(supOrderDetail.getSupOrder().getProducedStatus()));
//            json.put("suplierName", supOrderDetail == null ? "" : supOrderDetail.getSupOrder() == null ? "" :
//                    supOrderDetail.getSupOrder().getSupSuplier() == null ? "" :  supOrderDetail.getSupOrder().getSupSuplier().getName());
//            for (String key : json.keySet()){
//                if (json.get(key) == null) json.put(key, "");
//            }

            //2018.1.8  因供应商工单结构调整，取供应商自己的商品库，颜色，尺寸，暂屏蔽对接合一智造平台
            json.put("producedStatus","");
            json.put("producedStatusName","");
            json.put("suplierName","");

            productList.add(json);
        }
        data.put("productList", productList);
        data.put("qtySum", qtySum);
        data.put("subtotalSum", subtotalSum);
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
     * @Description: 编辑采购订单信息
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderUpdate", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderUpdate(@RequestBody UpdateSuplierOrderForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        suplierOrderService.updateSuplierOrder(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 发货
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderShipping", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderShipping(@RequestBody ShippingSuplierOrderForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        suplierOrderService.updateSuplierOrderShipping(form.id, form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 物流信息
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierOrderLogistics", method = {RequestMethod.POST})
    public ResponseEntity suplierOrderLogistics(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        StoSuplierOrder suplierOrder = suplierOrderService.findStoSuplierOrder(form.id);
        if (suplierOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
        JSONObject data = new JSONObject();
        data.put("deliveryCompanyName", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompanyName());
        data.put("deliveryNo", suplierOrder.getStoSuplierOrderDelivery() == null ? "" : suplierOrder.getStoSuplierOrderDelivery().getDeliveryNo());
        PlaProductBase base = suplierOrder.getStoSuplierOrderDelivery() == null ? null :
                suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompany() == null ? null :
                        (PlaProductBase) suplierOrderService.get2(PlaProductBase.class, suplierOrder.getStoSuplierOrderDelivery().getDeliveryCompany());
        data.put("deliveryCode",base == null ? "" : base.getDescription());
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }


    /**
     * @Description: 供应商列表
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierList", method = {RequestMethod.POST})
    public ResponseEntity suplierList(@RequestBody SuplierListForm form) throws Exception {
        List<SupSuplier> supSupliers = suplierOrderService.findSuplierList(form.name, form.size, form.scope);
        JSONArray data = new JSONArray();
        for (SupSuplier suplier : supSupliers) {
            JSONObject json = new JSONObject();
            json.put("id", suplier.getId());
            json.put("name", suplier.getName());
            json.put("contact", suplier.getContact());
            json.put("contactPhone", suplier.getContactPhone());
            json.put("address", suplier.getAddress());
            json.put("openYears", suplier.getOpenYears());
            json.put("scope", suplier.getScope());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 分配供应商
     * @Author: Chen.zm
     * @Date: 2017/11/1 0001
     */
    @RequestMapping(value = "/suplierAllotment", method = {RequestMethod.POST})
    public ResponseEntity suplierAllotment(@RequestBody SuplierAllotmentForm form) throws Exception {
        if (form.orderId == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        if (form.detailId == null)
            return new ResponseEntity(new RestResponseEntity(120, "详情id不能为空", null), HttpStatus.OK);
        if (form.suplierId == null)
            return new ResponseEntity(new RestResponseEntity(130, "供应商id不能为空", null), HttpStatus.OK);
        if (form.outputPrice == null)
            return new ResponseEntity(new RestResponseEntity(140, "生产价格不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrder(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 质检
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/updateProducedStatus", method = {RequestMethod.POST})
    public ResponseEntity updateProducedStatus(@RequestBody ProducedStatusForm form) throws Exception {
        if (form.orderId == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        if (form.pid == null)
            return new ResponseEntity(new RestResponseEntity(170, "商品id不能为空", null), HttpStatus.OK);
        if (form.producedStatus == null)
            return new ResponseEntity(new RestResponseEntity(120, "状态不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.instruction))
            return new ResponseEntity(new RestResponseEntity(130, "说明不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.personnel))
            return new ResponseEntity(new RestResponseEntity(160, "人员不能为空", null), HttpStatus.OK);
        suplierOrderService.updateProducedStatus(form.orderId, form.pid, form.producedStatus, form.instruction, form.personnel, form.color, form.size);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 获取订单交付记录
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/supOrderDeliveryLogList", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryLogList(@RequestBody DeliveryLogListForm form) throws Exception {
        if (form.orderId == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        if (form.pid == null)
            return new ResponseEntity(new RestResponseEntity(120, "商品id不能为空", null), HttpStatus.OK);
        SupOrderDetail detail = suplierOrderService.findSupOrderDetailByOrderIdAndPid(form.orderId, form.pid, form.color, form.size);
        if (detail == null || detail.getSupOrder() == null)
            return new ResponseEntity(new RestResponseEntity(130, "采购订单不存在", null), HttpStatus.OK);
        List<SupOrderDeliveryLog> logs = suplierOrderService.findSupOrderDeliveryLogList(detail.getSupOrder().getId());
        JSONArray data = new JSONArray();
        for (SupOrderDeliveryLog log : logs) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("instruction", log.getInstruction() == null ? "" : log.getInstruction());
            json.put("personnel", log.getPersonnel() == null ? "" : log.getPersonnel());
            json.put("action", log.getAction() == null ? "" : log.getAction());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }
    
    /**
     * @Description:工单状态
     * @Author: hanchao
     * @Date: 2017/12/13 0013
     */
    @RequestMapping(value = "/supOrderStatusList", method = {RequestMethod.POST})
    public ResponseEntity supOrderStatusList(@RequestBody SuplierOrderForm form) throws ResponseEntityException {

        if(StringUtils.isBlank(form.suplierOrderNo)){
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        }
        List<SupOrder> supOrders = suplierOrderService.findSupOrderStatusList(form.suplierOrderNo);
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders){
            JSONObject json = new JSONObject();
            json.put("orderNo",supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo());
            json.put("orderId",supOrder.getId()== null ? "" : supOrder.getId());
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus()));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

}
