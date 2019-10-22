package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SuplierOrderService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
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
@RequestMapping("/suplierOrder")
public class SuplierOrderController {

    @Autowired
    private SuplierOrderService suplierOrderService;

    /**
     * @Description: 首页订单的数据
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/homeOrderList", method = {RequestMethod.POST})
    public ResponseEntity homeOrderList(@RequestBody StatusForm form) throws Exception {
        if (form.status == null)
            return new ResponseEntity(new RestResponseEntity(110, "状态不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 3;
        PageList<SupOrder> supOrders = suplierOrderService.findSupOrderByProducedStatus(pageQuery, CurrentUser.get().getSuplierId(), form.status);
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            JSONObject json = new JSONObject();
            json.put("id", supOrder.getId());
            json.put("orderNo", supOrder.getOrderNo());
            json.put("href", supOrder.getSupOrderDetail() == null ? "" : OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("price", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getOutputPrice());
            json.put("qty", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty());
            json.put("subtotal", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSubtotal());
            json.put("pno", supOrder.getSupOrderDetail() == null ? "" :
                            supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                                    supOrder.getSupOrderDetail().getPlaProduct().getPno());
            json.put("expectsendDate", supOrder.getStoSuplierOrder() == null ? "" :
                    supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery() == null ? "" :
                            DateTimeUtils.parseStr(supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, supOrders.total), HttpStatus.OK);
    }


    /**
     * @Description: 获取供应商订单列表
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/orderList", method = {RequestMethod.POST})
    public ResponseEntity orderList(@RequestBody StatusForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 9999;
        PageList<SupOrder> supOrders = suplierOrderService.findSupOrderByProducedStatus(pageQuery, CurrentUser.get().getSuplierId(), form.status);
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            JSONObject json = new JSONObject();
            json.put("id", supOrder.getId());
            json.put("orderNo", supOrder.getOrderNo());
            json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("price", supOrder.getSupOrderDetail().getOutputPrice());
            json.put("qty", supOrder.getSupOrderDetail().getQty());
            json.put("subtotal", supOrder.getSupOrderDetail().getSubtotal());
            json.put("pno", supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                            supOrder.getSupOrderDetail().getPlaProduct().getPno());
            json.put("expectsendDate", DateTimeUtils.parseStr(supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus()));
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, supOrders.total), HttpStatus.OK);
    }


    /**
     * @Description: 订单详情
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/orderDetailHtml", method = {RequestMethod.GET})
    public String orderDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        Integer id = request.getParameter("id") == null ? null : Integer.parseInt(request.getParameter("id"));
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(id, null);
        if (supOrder != null) {
            modelMap.put("id", supOrder.getId());
            modelMap.put("receiver", supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getReceiver());
            modelMap.put("mobile", supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getMobile());
            modelMap.put("address", supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getAddress());
            modelMap.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            modelMap.put("name", supOrder.getSupOrderDetail().getProductName());
            modelMap.put("color", supOrder.getSupOrderDetail().getColor());
            modelMap.put("size", supOrder.getSupOrderDetail().getSize());
            modelMap.put("pno", supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                        supOrder.getSupOrderDetail().getPlaProduct().getPno());
            modelMap.put("qty", supOrder.getSupOrderDetail().getQty());
            modelMap.put("price", supOrder.getSupOrderDetail().getOutputPrice());
            modelMap.put("subtotal", supOrder.getSupOrderDetail().getSubtotal());
            modelMap.put("shoulder", supOrder.getSupOrderDetail().getShoulder());
            modelMap.put("bust", supOrder.getSupOrderDetail().getBust());
            modelMap.put("waist", supOrder.getSupOrderDetail().getWaist());
            modelMap.put("hipline", supOrder.getSupOrderDetail().getHipline());
            modelMap.put("height", supOrder.getSupOrderDetail().getHeight());
            modelMap.put("weight", supOrder.getSupOrderDetail().getWeight());
            modelMap.put("throatheight", supOrder.getSupOrderDetail().getThroatheight());
            modelMap.put("other", supOrder.getSupOrderDetail().getOther());
            modelMap.put("expectsendDate", DateTimeUtils.parseStr(supOrder.getStoSuplierOrder().getStoSuplierOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
            modelMap.put("orderNo", supOrder.getOrderNo());
            modelMap.put("createDate", DateTimeUtils.parseStr(supOrder.getCreatedDate()));
            modelMap.put("producedStatus", supOrder.getProducedStatus());
            modelMap.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus()));
            // 过滤value == null 的数据    使其为空
            for (String key : modelMap.keySet()) {
                if (modelMap.get(key) == null) modelMap.put(key, "");
            }
        }
        return "mobile/orderDetail";
    }

    /**
     * @Description: 修改供应商生产状态
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/updateProducedStatus", method = {RequestMethod.POST})
    public ResponseEntity updateProducedStatus(@RequestBody ProducedStatusForm form) throws Exception {
        return new ResponseEntity(new RestResponseEntity(110, "暂未开放", null), HttpStatus.OK);
//        if (form.orderId == null)
//            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
//        if (form.producedStatus == null)
//            return new ResponseEntity(new RestResponseEntity(120, "状态不能为空", null), HttpStatus.OK);
//        if ((form.producedStatus == 2 || form.producedStatus == 5) && StringUtils.isBlank(form.instruction))
//            return new ResponseEntity(new RestResponseEntity(130, "说明不能为空", null), HttpStatus.OK);
//        suplierOrderService.updateProducedStatus(CurrentUser.get().getSuplierId(), form.orderId, form.producedStatus, form.instruction);
//        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 接单及批量接单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderTaking", method = {RequestMethod.POST})
    public ResponseEntity supOrderTaking(@RequestBody IdsForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderTaking(CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 发货及批量发货
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderDeliver", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliver(@RequestBody SupOrderDeliverForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderDeliver(form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo,
                CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 修改完成
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderModify", method = {RequestMethod.POST})
    public ResponseEntity supOrderModify(@RequestBody SupOrderModifyForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderModify(form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo, form.instruction,
                CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 确认取消
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderCancel", method = {RequestMethod.POST})
    public ResponseEntity supOrderCancel(@RequestBody IdsForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderCancel(CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 重新修改  —供应商工单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderModifyOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderModifyOffline(@RequestBody SupOrderModifyForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderModifyOffline(form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo, form.instruction,
                CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 完成工单  —供应商工单
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderFinishOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderFinishOffline(@RequestBody IdsForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        suplierOrderService.saveSupOrderFinishOffline(CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 获取订单交付记录
     * @Author: Chen.zm
     * @Date: 2017/11/4 0004
     */
    @RequestMapping(value = "/supOrderDeliveryLogList", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryLogList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        List<SupOrderDeliveryLog> logs = suplierOrderService.findSupOrderDeliveryLogList(form.id);
        JSONArray data = new JSONArray();
        for (SupOrderDeliveryLog log : logs) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("instruction", log.getInstruction());
            json.put("action", log.getAction());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }


    /**
     * @Description: 批量修改备注
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderRemarksUpdate", method = {RequestMethod.POST})
    public ResponseEntity supOrderRemarksUpdate(@RequestBody SupOrderRemarkUpdateForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.remarks))
            return new ResponseEntity(new RestResponseEntity(110, "备注不能为空", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "修改数量不能为空", null), HttpStatus.OK);
        suplierOrderService.updateSupOrderRemarks(form.remarks, CurrentUser.get().userName, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 修改供应商商品编号
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/productPnoModify", method = {RequestMethod.POST})
    public ResponseEntity productPnoModify(@RequestBody ProductModifyForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.productCode))
            return new ResponseEntity(new RestResponseEntity(110, "商品ID不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.pno))
            return new ResponseEntity(new RestResponseEntity(120, "产品编号不能为空", null), HttpStatus.OK);
        suplierOrderService.saveProductPnoModify(form.productCode, form.pno);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 获取工单物流记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @RequestMapping(value = "/supOrderDeliveryRecordList", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryRecordList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        List<SupOrderDeliveryRecord> logs = suplierOrderService.findSupOrderDeliveryRecordList(form.id);
        JSONArray data = new JSONArray();
        for (SupOrderDeliveryRecord log : logs) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("deliveryCompanyName", log.getDeliveryCompanyName());
            json.put("deliveryNo", log.getDeliveryNo());
            json.put("deliveryCom", log.getPlaProductBase() == null ? "" : log.getPlaProductBase().getDescription());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }


    /**
     * @Description: 获取工单备注记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @RequestMapping(value = "/supOrderRemarkLogList", method = {RequestMethod.POST})
    public ResponseEntity supOrderRemarkLogList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        List<SupOrderRemarkLog> logs = suplierOrderService.findSupOrderRemarkLogList(form.id);
        JSONArray data = new JSONArray();
        for (SupOrderRemarkLog log : logs) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("name", log.getName());
            json.put("remarks", log.getRemarks());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }
}
