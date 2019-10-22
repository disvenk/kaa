package com.xxx.admin.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.xxx.admin.form.*;
import com.xxx.admin.service.SupOrderOperationService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
@RequestMapping("/supOrderOperation")
public class SupOrderOperationController {

    @Autowired
    private SupOrderOperationService supOrderOperationService;

    /**
     * @Description:入库通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/saveSupOrderStorage", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderStorage(@RequestBody SupOrderOperationForm form) throws Exception {
        if(form.orderNoList.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110,"工单号不能为空",null), HttpStatus.OK);
        supOrderOperationService.saveSupOrderStorage(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:获取入库列表页面
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderStorageList", method = {RequestMethod.POST})
    public ResponseEntity supOrderStorageList(@RequestBody SupOrderStorageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrderStorageLog> supOrderStorageList = supOrderOperationService.findSupOrderStorageList(pageQuery,form.orderNo,form.name,form.startTime,form.endTime);
        JSONArray jsonArray = new JSONArray();
        for (SupOrderStorageLog supOrderStorageLog : supOrderStorageList) {
            JSONObject json = new JSONObject();
            json.put("id", supOrderStorageLog.getId());
            json.put("orderNo", supOrderStorageLog.getOrderNo());
            json.put("name",supOrderStorageLog.getName());
            json.put("updateDate", DateTimeUtils.parseStr(supOrderStorageLog.getCreatedDate()));
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supOrderStorageList.total), HttpStatus.OK);
    }

    /**
     * @Description:质检通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/saveOrderTest", method = {RequestMethod.POST})
    public ResponseEntity saveOrderTest(@RequestBody SupOrderOperationForm form) throws Exception {
        if(form.orderNoList.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110,"工单号不能为空",null), HttpStatus.OK);
        supOrderOperationService.saveOrderTest(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
    /**
     * @Description:质检不通过操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/saveOrderTestFail", method = {RequestMethod.POST})
    public ResponseEntity saveOrderTestFail(@RequestBody SupOrderOperationForm form) throws Exception {
        if(form.orderNoList.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110,"工单号不能为空",null), HttpStatus.OK);
        supOrderOperationService.saveOrderTestFail(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description:通过质检条码获取工单详情和入库详情
     * @Author: hanchao
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/selectOrderTestDetail", method = {RequestMethod.POST})
    public ResponseEntity selectOrderTestDetail(@RequestBody OrderNoListForm form) throws Exception {
        if (form.orderNo == null)
            return new ResponseEntity(new RestResponseEntity(110, "工单号不能为空", null), HttpStatus.OK);
        JSONArray jsonArray = new JSONArray();
        SupOrder supOrder = supOrderOperationService.selectWorkOrder(form.orderNo);
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "工单不存在", null), HttpStatus.OK);
        if (supOrder.getProducedStatus() != 5 && supOrder.getProducedStatus() != 7)
            return new ResponseEntity(new RestResponseEntity(130, "该工单不能被录入", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("suplierOrderNo",supOrder.getStoSuplierOrder() == null ? "" : supOrder.getStoSuplierOrder().getOrderNo());
        json.put("orderNo",supOrder.getOrderNo());
        json.put("suplierName",supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());
        json.put("productCode",supOrder.getSupOrderDetail() == null ? "" :
                supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                        supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
        json.put("supplierProductCode", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPno());
        /*下单时间*/
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd HH:mm:ss"));
        /*交货日期*/
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("color", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor());
        json.put("size", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize());
        json.put("throatheight",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getThroatheight());
        //胸围
        json.put("bust",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getBust());
        //身高
        json.put("height",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getHeight());
        //臀围
        json.put("hipline",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getHipline());
        //肩宽
        json.put("shoulder",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getShoulder());
        //腰围
        json.put("waist",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getWaist());
        json.put("height",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getHeight());
        json.put("weight",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getWeight());
        //重要说明
        json.put("description",supOrder.getDescription());
        //材料说明
        json.put("material",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct().getMaterial());
        //工艺说明
        json.put("technics",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct().getTechnics());
//        SupOrderDetail supOrderDetail = supOrder.getSupOrderDetail();
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());
        JSONArray pictureList = new JSONArray();
            for (PlaProductPicture picture: supOrder.getSupOrderDetail().getPlaProduct().getPlaProductPictureList()) {
                JSONObject img = new JSONObject();
                img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
                pictureList.add(img);
            }
        for (String key : json.keySet()) {
            if (json.get(key) == null || json.get(key) == "") json.put(key, "无");
        }
        json.put("pictureList",pictureList);
        jsonArray.add(json);
        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:获取质检列表页面
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderTestList", method = {RequestMethod.POST})
    public ResponseEntity supOrderTestList(@RequestBody SupOrderTestListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrderTestingLog> supOrderTestList = supOrderOperationService.findSupOrderTestList(pageQuery,form.orderNo,form.name,form.startTime,form.endTime);
        JSONArray jsonArray = new JSONArray();
        for (SupOrderTestingLog supOrderTestingLog : supOrderTestList) {
            JSONObject json = new JSONObject();
            json.put("id", supOrderTestingLog.getId());
            json.put("orderNo", supOrderTestingLog.getOrderNo());
            json.put("name",supOrderTestingLog.getName());
            json.put("updateDate", DateTimeUtils.parseStr(supOrderTestingLog.getCreatedDate()));
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supOrderTestList.total), HttpStatus.OK);
    }

    /**
     * @Description:工单收货保存操作
     * @Author: hanchao
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/saveWorkOrder", method = {RequestMethod.POST})
    public ResponseEntity saveWorkOrder(@RequestBody SupOrderOperationForm form) throws Exception {
        supOrderOperationService.saveWorkOrder(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:工单号扫码条件
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/selectWorkOrder", method = {RequestMethod.POST})
    public ResponseEntity selectWorkOrder(@RequestBody OrderNoListForm form) throws Exception {
        if (form.orderNo == null)
            return new ResponseEntity(new RestResponseEntity(110, "工单号不能为空", null), HttpStatus.OK);
        SupOrder supOrder = supOrderOperationService.selectWorkOrder(form.orderNo);
        if (supOrder == null || supOrder.getProducedStatus() != 4)
            return new ResponseEntity(new RestResponseEntity(120, "该工单不能被录入", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("orderNo",supOrder.getOrderNo());
        json.put("orderId",supOrder.getOrderId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }


    /**
     * @Description:获取工单收货列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/workOrderList", method = {RequestMethod.POST})
    public ResponseEntity workOrderList(@RequestBody WorkOrderListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrderReceiptLog> supOrderReceiptList = supOrderOperationService.findSupOrderReceiptLogList(pageQuery,form.orderNo,form.name,form.startTime,form.endTime);
        JSONArray jsonArray = new JSONArray();
        for (SupOrderReceiptLog supOrderReceiptLog : supOrderReceiptList) {
            JSONObject json = new JSONObject();
            json.put("id", supOrderReceiptLog.getId());
            json.put("orderId", supOrderReceiptLog.getOrderId());
            json.put("orderNo", supOrderReceiptLog.getOrderNo());
            json.put("name",supOrderReceiptLog.getName());
            json.put("updateDate", DateTimeUtils.parseStr(supOrderReceiptLog.getCreatedDate()));
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supOrderReceiptList.total), HttpStatus.OK);
    }
}
