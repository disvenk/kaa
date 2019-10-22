package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.form.SuplierSalesOrderListForm;
import com.xxx.admin.service.SuplierSalesOrderService;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.ExportExcelUtil;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/suplierSalesOrder")
public class SuplierSalesOrderController {


    @Autowired
    private SuplierSalesOrderService suplierSalesOrderService;


    /**
     * @Description: 订单列表
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/orderList", method = {RequestMethod.POST})
    public ResponseEntity orderList(@RequestBody SuplierSalesOrderListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        MybatisPageQuery pageQuery = new MybatisPageQuery(form.pageNum);
        pageQuery.getParams().put("suplierId", form.suplierId);
        pageQuery.getParams().put("orderNo", form.orderNo);
        pageQuery.getParams().put("pno", form.pno);
        pageQuery.getParams().put("customerId", form.customerId);
        pageQuery.getParams().put("producedStatus", form.producedStatus);
        pageQuery.getParams().put("createdDateSta", form.createdDateSta);
        pageQuery.getParams().put("createdDateEnd", form.createdDateEnd);
        pageQuery.getParams().put("deliveryDateStr", form.deliveryDateStr);
        pageQuery.getParams().put("deliveryDateEnd", form.deliveryDateEnd);
        PageList<JSONObject> list = suplierSalesOrderService.findList(pageQuery);
        JSONArray data = new JSONArray();
        for (JSONObject jso : list) {
            JSONObject json = new JSONObject();
            json.put("id", jso.get("id"));
            json.put("orderNo", jso.get("orderNo"));
            json.put("insideOrderNo", jso.get("insideOrderNo"));
            json.put("pno", jso.get("pno"));
            json.put("href", jso.get("href") == null ? "" : OSSClientUtil.getObjectUrl(jso.get("href").toString()));
            json.put("customer", jso.get("customer"));
            json.put("categoryName", jso.get("categoryName"));
            json.put("color", jso.get("color"));
            json.put("size", jso.get("size"));
            json.put("qty", jso.get("qty"));
            json.put("price", jso.get("price"));
            json.put("createdDate", jso.get("createdDate"));
            json.put("deliveryDate", jso.get("deliveryDate"));
            json.put("deliveryCompanyName", jso.get("deliveryCompanyName"));
            json.put("deliveryNo", jso.get("deliveryNo"));
            json.put("remarks", jso.get("remarks"));
            json.put("suplierName", jso.get("suplierName"));
            json.put("producedStatus", jso.get("producedStatus"));
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline((Integer)jso.get("producedStatus")));
            Integer subTime = jso.getInteger("subTime");
            json.put("subTime", subTime == null ? "" : subTime > 0 ? "距交期" + (((int) subTime / (3600 * 24)) + 1) + "天" : "过交期" + (((int) -subTime / (3600 * 24)) + 0) + "天");
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.getOffset(), pageQuery.getLimit(), list.total), HttpStatus.OK);
    }

    /**
     * @Description: 获取订单状态各数量
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/findOrderCount", method = {RequestMethod.POST})
    public ResponseEntity findOrderCount() throws Exception {
        JSONArray data = suplierSalesOrderService.findOrderCount(null);
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 订单详情
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/salesOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity salesOrderDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        SupSalesOrder salesOrder = suplierSalesOrderService.getSupSalesOrderDetail(form.id);
        if (salesOrder.getSupSalesOrderDelivery() == null) salesOrder.setSupSalesOrderDelivery(new SupSalesOrderDelivery());
        if (salesOrder.getSupSalesOrderDetail() == null) salesOrder.setSupSalesOrderDetail(new SupSalesOrderDetail());

        JSONObject json = new JSONObject();
        json.put("id", salesOrder.getId());
        json.put("orderNo", salesOrder.getOrderNo());
        json.put("insideOrderNo", salesOrder.getInsideOrderNo());
        json.put("customerId", salesOrder.getSupSalesOrderDelivery().getCustomerId());
        json.put("customer", salesOrder.getSupSalesOrderDelivery().getContact());
        json.put("customerPhone", salesOrder.getSupSalesOrderDelivery().getCustomerPhone());
        json.put("customerAddressId", salesOrder.getSupSalesOrderDelivery().getCustomerAddressId());
        json.put("remarks", salesOrder.getRemarks());
        json.put("createTime", DateTimeUtils.parseStr(salesOrder.getCreatedDate()));
        json.put("deliveryTime", DateTimeUtils.parseStr(salesOrder.getSupSalesOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("producedStatus", salesOrder.getProducedStatus());
        json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(salesOrder.getProducedStatus()));

        json.put("pid", salesOrder.getSupSalesOrderDetail().getPid());
        json.put("pno", salesOrder.getSupSalesOrderDetail().getPno());
        json.put("categoryName", salesOrder.getSupSalesOrderDetail().getCategoryName());
        json.put("color", salesOrder.getSupSalesOrderDetail().getColor());
        json.put("size", salesOrder.getSupSalesOrderDetail().getSize());
        json.put("throatheight", salesOrder.getSupSalesOrderDetail().getThroatheight());
        json.put("shoulder", salesOrder.getSupSalesOrderDetail().getShoulder());
        json.put("bust", salesOrder.getSupSalesOrderDetail().getBust());
        json.put("waist", salesOrder.getSupSalesOrderDetail().getWaist());
        json.put("hipline", salesOrder.getSupSalesOrderDetail().getHipline());
        json.put("height", salesOrder.getSupSalesOrderDetail().getHeight());
        json.put("weight", salesOrder.getSupSalesOrderDetail().getWeight());
        json.put("qty", salesOrder.getSupSalesOrderDetail().getQty());
        json.put("price", salesOrder.getSupSalesOrderDetail().getPrice());
        json.put("subtotal", salesOrder.getSupSalesOrderDetail().getSubtotal());

        JSONArray pictures = new JSONArray();
        if (salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailPictureList() != null) {
            for (SupSalesOrderDetailPicture picture : salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailPictureList()) {
                JSONObject jso = new JSONObject();
                jso.put("key", (picture.getHref()));
                jso.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
                pictures.add(jso);
            }
        }
        json.put("pictures", pictures);

        JSONArray materials = new JSONArray();
        if (salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailMaterialList() != null) {
            for (SupSalesOrderDetailMaterial material : salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailMaterialList()) {
                JSONObject jso = new JSONObject();
                jso.put("materialId", material.getMaterialId());
                jso.put("materialName", material.getMaterialName());
                jso.put("price", material.getPrice());
                jso.put("count", material.getCount());
                jso.put("unit", material.getUnit());
                materials.add(jso);
            }
        }
        json.put("materials", materials);

        JSONArray procedures = new JSONArray();
        if (salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList() != null) {
            for (SupSalesOrderDetailProcedure procedure : salesOrder.getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList()) {
                JSONObject jso = new JSONObject();
                jso.put("procedureId", procedure.getProcedureId());
                jso.put("procedureName", procedure.getProcedureName());
                jso.put("price", procedure.getPrice());
                procedures.add(jso);
            }
        }
        json.put("procedures", procedures);

        json.put("material", salesOrder.getSupSalesOrderDetail().getMaterial());
        json.put("technics", salesOrder.getSupSalesOrderDetail().getTechnics());
        json.put("description", salesOrder.getDescription());

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }


}
