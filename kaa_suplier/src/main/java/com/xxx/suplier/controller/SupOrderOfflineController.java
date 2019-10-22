package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SupOrderOfflineService;
import com.xxx.suplier.service.SuplierOrderService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.*;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @Description: 供应商工单
 * @Author: Chen.zm
 * @Date: 2017/12/6 0006
 */
@Controller
@RequestMapping("/supOrderOffline")
public class SupOrderOfflineController {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private SupOrderOfflineService supOrderOfflineService;

    /**
     * @Description:修改结款状态
     * @Author: hanchao
     * @Date: 2017/12/18 0018
     */
    @RequestMapping(value = "/supOrderPayStatusSave", method = {RequestMethod.POST})
    public ResponseEntity supOrderPayStatusSave(@RequestBody PayStatusForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if(form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(110, "工单数量不能为空", null), HttpStatus.OK);
        supOrderOfflineService.supOrderPayStatusSave(CurrentUser.get().getSuplierId(), form.ids,form.payStatus);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:供应商工单管理的批量导出
     * @Author: hanchao
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request, HttpServletResponse response)  throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        SupOrderListOfflineForm form = new SupOrderListOfflineForm();
        form.supOrderNo = request.getParameter("supOrderNo");
        form.pno = request.getParameter("pno");
        form.producedStatus = request.getParameter("producedStatus") == null ? null : Integer.parseInt(request.getParameter("producedStatus"));
        form.startTime = request.getParameter("startTime");
        form.endTime = request.getParameter("endTime");
        form.deliveryDateStart = request.getParameter("deliveryDateStart");
        form.deliveryDateEnd = request.getParameter("deliveryDateEnd");
        form.customer = request.getParameter("customer");
        form.payStatus = request.getParameter("payStatus");
        if (SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        PageList<SupOrder> supOrders = supOrderOfflineService.supOrderListOffline(pageQuery, form, (int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID));
        //导出文件的标题
        String title = "供应商工单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号", "紧急程度", "订单号","工单号","内部编号", "供应商产品编号","客户","分类", "颜色", "尺寸","件数","价格","下单日期","交货日期","快递公司","快递单号","备注","状态","是否结款"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (SupOrder supOrder : supOrders) {//循环每一条数据
            //处理异常数据
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            objs = new Object[headers.length];
            objs[1] = supOrder.getUrgent() == null || supOrder.getUrgent() == 1 ? "正常" : "紧急";
            objs[2] = supOrder.getSupSalesOrder() == null ? "" : supOrder.getSupSalesOrder().getOrderNo();
            objs[3] = supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo();//工单号
            objs[4] = supOrder.getInsideOrderNo() == null ? "" : supOrder.getInsideOrderNo();//内部编号
            objs[5] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPno();//
            objs[6] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getCustomer();//客户
            objs[7] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName();//分类
            objs[8] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor();//颜色
            objs[9] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize();//尺寸
            objs[10] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty();//件数
            objs[11] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getOutputPrice();//价格
            objs[12] = DateTimeUtils.parseStr(supOrder.getCreatedDate() == null ? "" : supOrder.getCreatedDate(), "yyyy-MM-dd");//下单日期
            objs[13] = DateTimeUtils.parseStr(supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd");//交货日期
            objs[14] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryCompanyName();//快递公司
            objs[15] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryNo();//快递单号
            objs[16] = supOrder.getRemarks() == null ? "" : supOrder.getRemarks();//备注
            objs[17] = Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus());//状态
            if(StringUtils.isNotBlank(supOrder.getPayStatus())){
                if(supOrder.getPayStatus().equals("1")){
                    supOrder.setPayStatus("已结款");
                }else if(supOrder.getPayStatus().equals("2")){
                    supOrder.setPayStatus("未结款");
                }else{
                    supOrder.setPayStatus("");
                }
            }
            objs[16] = supOrder.getPayStatus();//结算状态
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList);
    }

    /**
     * @Description: 供应商工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/supOrderListOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderList(@RequestBody SupOrderListOfflineForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrders = supOrderOfflineService.supOrderListOffline(pageQuery, form, CurrentUser.get().getSuplierId());
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            //处理异常数据
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());

            JSONObject json = new JSONObject();
            json.put("supOrderId", supOrder.getId());
            json.put("supOrderNo", supOrder.getOrderNo());
            json.put("insideOrderNo", supOrder.getInsideOrderNo());
            json.put("urgent", supOrder.getUrgent() == null ? 1 : supOrder.getUrgent());
            json.put("salesOrderNo", supOrder.getSupSalesOrder() == null ? "" : supOrder.getSupSalesOrder().getOrderNo());
            json.put("pno", supOrder.getSupOrderDetail().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("customer", supOrder.getSupOrderDelivery().getCustomer());
            json.put("categoryName", supOrder.getSupOrderDetail().getCategoryName());
            json.put("color", supOrder.getSupOrderDetail().getColor());
            json.put("size", supOrder.getSupOrderDetail().getSize());
//            json.put("other", supOrder.getSupOrderDetail().getOther());
            json.put("qty", supOrder.getSupOrderDetail().getQty());
            json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());
            json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
            json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
//            json.put("expectsendDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
            json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
            json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());
            json.put("remarks", supOrder.getRemarks());
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));
            json.put("payStatus",supOrder.getPayStatus());//结算状态

            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }

            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, supOrders.total), HttpStatus.OK);
    }


    /**
     * @Description: 供应商工单详情
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderOffline(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(form.id, CurrentUser.get().getSuplierId());
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "工单不存在", null), HttpStatus.OK);
        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        JSONObject json = new JSONObject();
        json.put("supOrderId", supOrder.getId());
        json.put("supOrderNo", supOrder.getOrderNo());
        json.put("insideOrderNo", supOrder.getInsideOrderNo());
        json.put("customerId", supOrder.getSupOrderDelivery().getCustomerId());
        json.put("customerAddressId", supOrder.getSupOrderDelivery().getCustomerAddressId());
        json.put("customer", supOrder.getSupOrderDelivery().getCustomer());
        json.put("customerPhone", supOrder.getSupOrderDelivery().getCustomerPhone());
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
        json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());
        json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));

        json.put("remarks", supOrder.getRemarks());
        json.put("receiver", supOrder.getSupOrderDelivery().getReceiver());
        json.put("mobile", supOrder.getSupOrderDelivery().getMobile());
        json.put("address", supOrder.getSupOrderDelivery().getAddress());
        json.put("province", supOrder.getSupOrderDelivery().getProvince());
        json.put("provinceName", supOrder.getSupOrderDelivery().getProvinceName());
        json.put("city", supOrder.getSupOrderDelivery().getCity());
        json.put("cityName", supOrder.getSupOrderDelivery().getCityName());
        json.put("zone", supOrder.getSupOrderDelivery().getZone());
        json.put("zoneName", supOrder.getSupOrderDelivery().getZoneName());

        json.put("pno", supOrder.getSupOrderDetail().getPno());
        json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
        json.put("categoryName", supOrder.getSupOrderDetail().getCategoryName());
        json.put("color", supOrder.getSupOrderDetail().getColor());
        json.put("size", supOrder.getSupOrderDetail().getSize());
        json.put("throatheight",supOrder.getSupOrderDetail().getThroatheight());
        json.put("shoulder",supOrder.getSupOrderDetail().getShoulder());
        json.put("bust",supOrder.getSupOrderDetail().getBust());
        json.put("waist",supOrder.getSupOrderDetail().getWaist());
        json.put("hipline",supOrder.getSupOrderDetail().getHipline());
        json.put("height",supOrder.getSupOrderDetail().getHeight());
        json.put("weight",supOrder.getSupOrderDetail().getWeight());
//        json.put("other", supOrder.getSupOrderDetail().getOther());
        json.put("qty", supOrder.getSupOrderDetail().getQty());
        json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());

        json.put("material", supOrder.getSupOrderDetail().getMaterial());
        json.put("technics", supOrder.getSupOrderDetail().getTechnics());
        json.put("description", supOrder.getDescription());
        JSONArray imgList = new JSONArray();
        if (supOrder.getSupOrderDetail().getSupOrderDetailPictureList() != null) {
            for (SupOrderDetailPicture picture : supOrder.getSupOrderDetail().getSupOrderDetailPictureList()) {
                JSONObject img = new JSONObject();
                img.put("key", picture.getHref());
                img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
                imgList.add(img);
            }
        }
        json.put("imgList", imgList);

        //获取原材料
        JSONArray materials = new JSONArray();
        if (supOrder.getSupSalesOrder() != null && supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailMaterialList() != null) {
            for (SupSalesOrderDetailMaterial material : supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailMaterialList()) {
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

        //获取工序
        JSONArray procedures = new JSONArray();
        if (supOrder.getSupSalesOrder() != null && supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList() != null) {
            for (SupSalesOrderDetailProcedure procedure : supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList()) {
                JSONObject jso = new JSONObject();
                jso.put("procedureId", procedure.getProcedureId());
                jso.put("procedureName", procedure.getProcedureName());
                jso.put("price", procedure.getPrice());
                procedures.add(jso);
            }
        }
        json.put("procedures", procedures);


        for (String key : json.keySet()) {
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


    /**
     * @Description: 供应商工单新增编辑
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderOfflineSave", method = {RequestMethod.POST})
    public ResponseEntity supOrderOfflineSave(@RequestBody SupOrderOfflineSaveForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        supOrderOfflineService.saveSupOrderOffline(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 供应商工单删除
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderOfflineRemove", method = {RequestMethod.POST})
    public ResponseEntity supOrderOfflineRemove(@RequestBody IdForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        supOrderOfflineService.removeSupOrderOffline(CurrentUser.get().getSuplierId(), form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: word导出
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWord", method = {RequestMethod.GET})
    public void exportWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("supOrderId") == null || SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(Integer.parseInt(request.getParameter("supOrderId")), (int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID));
        Map<String, Object> map = supOrderOfflineService.exportWord(supOrder);
        String fileName = "供应商工单详情" + supOrder.getOrderNo();
        WordUtils.exportMillCertificateWord(request,response, fileName, map);
    }

    /**
     * @Description: word批量导出
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWordList", method = {RequestMethod.GET})
    public void exportWordList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("ids") == null || SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        List<Integer> ids = new ArrayList<>();
        for (String id : request.getParameter("ids").split(",")) {
            ids.add(Integer.parseInt(id));
        }
        List<Map> mapList = supOrderOfflineService.exportWordList((int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID), ids);
        String fileName = "供应商工单详情" + DateTimeUtils.parseStr(new Date(), "yyyyMMddHHmmss");
        WordUtils.exportMillCertificateWordList(request, response, fileName, mapList);
    }

    /**
     * @Description: 修改紧急状态
     * @Author: Chen.zm
     * @Date: 2018/1/9 0009
     */
    @RequestMapping(value = "/updateUrgent", method = {RequestMethod.POST})
    public ResponseEntity updateUrgent(@RequestBody UrgentForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        supOrderOfflineService.updateUrgent(CurrentUser.get().getSuplierId(), form.id, form.urgent);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }
}
