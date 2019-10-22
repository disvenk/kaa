package com.xxx.suplier.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SuplierOrderService;
import com.xxx.suplier.service.SuplierSalesOrderService;
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
import org.springframework.ui.ModelMap;
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
     * @Description: Excel导出
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request, HttpServletResponse response)  throws Exception {
        if (SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        MybatisPageQuery pageQuery = new MybatisPageQuery(0, 9999);
        pageQuery.getParams().put("suplierId", SessionUtils.getSession(request, SessionUtils.SUPPLIERID));
        pageQuery.getParams().put("orderNo",  request.getParameter("orderNo"));
        pageQuery.getParams().put("pno", request.getParameter("pno"));
        pageQuery.getParams().put("customerId", request.getParameter("customerId"));
        pageQuery.getParams().put("producedStatus", StringUtils.trimToEmpty(request.getParameter("producedStatus")));
        pageQuery.getParams().put("createdDateSta", request.getParameter("createdDateSta"));
        pageQuery.getParams().put("createdDateEnd", request.getParameter("createdDateEnd"));
        pageQuery.getParams().put("deliveryDateStr", request.getParameter("deliveryDateStr"));
        pageQuery.getParams().put("deliveryDateEnd", request.getParameter("deliveryDateEnd"));
        PageList<JSONObject> list = suplierSalesOrderService.findList(pageQuery);
        //导出文件的标题
        String title = "订单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号", "订单号","内部编号", "供应商产品编号","客户","分类", "颜色", "尺寸","件数","价格","下单日期","交货日期","快递公司","快递单号","备注","状态"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs;
        for (JSONObject jso : list) {
            objs = new Object[headers.length];

            objs[1] = StringUtils.trimToEmpty(jso.getString("orderNo"));
            objs[2] = StringUtils.trimToEmpty(jso.getString("insideOrderNo"));
            objs[3] = StringUtils.trimToEmpty(jso.getString("pno"));
            objs[4] = StringUtils.trimToEmpty(jso.getString("customer"));
            objs[5] = StringUtils.trimToEmpty(jso.getString("categoryName"));
            objs[6] = StringUtils.trimToEmpty(jso.getString("color"));
            objs[7] = StringUtils.trimToEmpty(jso.getString("size"));
            objs[8] = StringUtils.trimToEmpty(jso.getString("qty"));
            objs[9] = StringUtils.trimToEmpty(jso.getString("price"));
            objs[10] = StringUtils.trimToEmpty(jso.getString("createdDate"));
            objs[11] = StringUtils.trimToEmpty(jso.getString("deliveryDate"));
            objs[12] = StringUtils.trimToEmpty(jso.getString("deliveryCompanyName"));
            objs[13] = StringUtils.trimToEmpty(jso.getString("deliveryNo"));
            objs[14] = StringUtils.trimToEmpty(jso.getString("remarks"));
            objs[15] = jso.get("producedStatus") == null ? "" : Commo.parseSuplierOrderProducedStatusOffline((Integer)jso.get("producedStatus"));

            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList);
    }

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
        pageQuery.getParams().put("suplierId", CurrentUser.get().getSuplierId());
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
        JSONArray data = suplierSalesOrderService.findOrderCount(CurrentUser.get().getSuplierId());
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
        SupSalesOrder salesOrder = suplierSalesOrderService.getSupSalesOrderDetail(form.id, CurrentUser.get().getSuplierId());
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

    /**
     * @Description: 保存订单
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/saveOrder", method = {RequestMethod.POST})
    public ResponseEntity saveOrder(@RequestBody SuplierSalesOrderForm form) throws Exception {
        suplierSalesOrderService.saveOrder(form, CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 删除订单
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/removeOrder", method = {RequestMethod.POST})
    public ResponseEntity removeOrder(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.removeOrder(form.id, CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 批量修改备注
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/orderRemarksUpdate", method = {RequestMethod.POST})
    public ResponseEntity orderRemarksUpdate(@RequestBody SupOrderRemarkUpdateForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.remarks))
            return new ResponseEntity(new RestResponseEntity(110, "备注不能为空", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "修改数量不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.updateOrderRemarks(form.remarks, CurrentUser.get().userName, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 生成工单  ——单笔
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/saveSupOrderOne", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderOne(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.saveSupOrder(form.id, CurrentUser.get().getSuplierId(), 1, CurrentUser.get().userName);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 生成工单  ——多笔
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/saveSupOrderMany", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderMany(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.saveSupOrder(form.id, CurrentUser.get().getSuplierId(), 2, CurrentUser.get().userName);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 订单发货
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/orderDeliver", method = {RequestMethod.POST})
    public ResponseEntity orderDeliver(@RequestBody OrderDeliverForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.orderDeliver(form.deliveryCompany, form.deliveryCompanyName, form.deliveryNo, CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 完成订单
     * @Author: Chen.zm
     * @Date: 2018/1/10 0010
     */
    @RequestMapping(value = "/orderFinish", method = {RequestMethod.POST})
    public ResponseEntity orderFinish(@RequestBody IdForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        suplierSalesOrderService.orderFinish(CurrentUser.get().getSuplierId(), CurrentUser.get().userName, form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 物流信息
     * @Author: Chen.zm
     * @Date: 2018/1/13 0013
     */
    @RequestMapping(value = "/orderDeliveryRecordList", method = {RequestMethod.POST})
    public ResponseEntity orderDeliveryRecordList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        SupSalesOrderDelivery delivery = suplierSalesOrderService.get2(SupSalesOrderDelivery.class, "orderId", form.id);
        if (delivery == null || delivery.getDeliveryCompany() == null)
            return new ResponseEntity(new RestResponseEntity(120, "物流信息不存在", null), HttpStatus.OK);
        PlaProductBase base = suplierSalesOrderService.get2(PlaProductBase.class, "id", delivery.getDeliveryCompany());
        JSONArray data = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("createTime", DateTimeUtils.parseStr(delivery.getDeliveryActualDate()));
        json.put("deliveryCompanyName", delivery.getDeliveryCompanyName());
        json.put("deliveryNo", delivery.getDeliveryNo());
        json.put("deliveryCom", base == null ? "" : base.getDescription());
        data.add(json);
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }


    /**
     * @Description: 获取工单备注记录
     * @Author: Chen.zm
     * @Date: 2017/12/12 0012
     */
    @RequestMapping(value = "/orderRemarkLogList", method = {RequestMethod.POST})
    public ResponseEntity orderRemarkLogList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        SupSalesOrder salesOrder = suplierSalesOrderService.getSupSalesOrderDetail(form.id, CurrentUser.get().getSuplierId());
        JSONArray data = new JSONArray();
        for (SupSalesOrderRemarkLog log : salesOrder.getSupSalesOrderRemarkLogList()) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("name", log.getName());
            json.put("remarks", log.getRemarks());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

}
