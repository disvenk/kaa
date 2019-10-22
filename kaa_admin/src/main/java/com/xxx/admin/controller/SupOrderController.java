package com.xxx.admin.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.SupOrderService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.user.utils.SessionUtils;
import com.xxx.utils.*;
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
import java.util.*;

/**
 * 工单管理
 */
@Controller
@RequestMapping("/supOrder")
public class SupOrderController {

    @Autowired
    private SupOrderService supOrderService;

    /**
     * @Description: word导出 —— 平台
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWordOnline", method = {RequestMethod.GET})
    public void exportWordOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("supOrderId") == null) return;
        SupOrder supOrder = supOrderService.getSupOrder(Integer.parseInt(request.getParameter("supOrderId")));
        Map<String, Object> map = supOrderService.exportWordOnline(supOrder);
        String fileName = "合一智造工单详情" + supOrder.getOrderNo();
        WordUtils.exportMillCertificateWord(request,response, fileName, map);
    }

    /**
     * @Description: word批量导出 —— 平台
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWordListOnline", method = {RequestMethod.GET})
    public void exportWordListOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("ids") == null) return;
        List<Integer> ids = new ArrayList<>();
        for (String id : request.getParameter("ids").split(",")) {
            ids.add(Integer.parseInt(id));
        }
        List<Map> mapList = supOrderService.exportWordListOnline(ids);
        String fileName = "合一智造工单详情" + DateTimeUtils.parseStr(new Date(), "yyyyMMddHHmmss");
        WordUtils.exportMillCertificateWordList(request, response, fileName, mapList);
    }


    /**
     * @Description: word导出 —— 供应商
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWordOffline", method = {RequestMethod.GET})
    public void exportWordOffline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("supOrderId") == null) return;
        SupOrder supOrder = supOrderService.getSupOrder(Integer.parseInt(request.getParameter("supOrderId")));
        Map<String, Object> map = supOrderService.exportWordOffline(supOrder);
        String fileName = "供应商工单详情" + supOrder.getOrderNo();
        WordUtils.exportMillCertificateWord(request,response, fileName, map);
    }

    /**
     * @Description: word批量导出 —— 供应商
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWordListOffline", method = {RequestMethod.GET})
    public void exportWordListOffline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("ids") == null) return;
        List<Integer> ids = new ArrayList<>();
        for (String id : request.getParameter("ids").split(",")) {
            ids.add(Integer.parseInt(id));
        }
        List<Map> mapList = supOrderService.exportWordListOffline(ids);
        String fileName = "供应商工单详情" + DateTimeUtils.parseStr(new Date(), "yyyyMMddHHmmss");
        WordUtils.exportMillCertificateWordList(request, response, fileName, mapList);
    }



    /**
     * @Description:供应商本地订单的批量导出
     * @Author: hanchao
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportExcelOffline", method = {RequestMethod.GET})
    public void exportExcelOffline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        SupOrderListOfflineForm form = new SupOrderListOfflineForm();
        if (request.getParameter("producedStatus") == null)  return;
        form.supOrderNo = request.getParameter("supOrderNo");
        form.customerId = request.getParameter("customerId");
        form.pno = request.getParameter("pno");
        form.producedStatus = Integer.parseInt(request.getParameter("producedStatus"));
        form.startTime = request.getParameter("startTime");
        form.endTime = request.getParameter("endTime");
        form.deliveryDateStart = request.getParameter("deliveryDateStart");
        form.deliveryDateEnd = request.getParameter("deliveryDateEnd");
        PageList<SupOrder> supOrders = supOrderService.supOrderListOffline(pageQuery, form);
        //导出文件的标题
        String title = "平台工单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号", "工单号","内部编号", "供应商产品编号","客户","分类", "颜色", "尺寸","件数","价格","下单日期","交货日期","快递公司","快递编号","备注","供应商","状态"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (SupOrder supOrder : supOrders) {//循环每一条数据
            //处理异常数据
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            objs = new Object[headers.length];
            objs[1] = supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo();//工单号
            objs[2] = supOrder.getInsideOrderNo() == null ? "" : supOrder.getInsideOrderNo();//内部编号
            objs[3] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPno();
            objs[4] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getCustomer();
            objs[5] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName();//分类
            objs[6] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor();//颜色
            objs[7] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize();//尺寸
            objs[8] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty();//件数
            objs[9] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getOutputPrice();//价格
            objs[10] = DateTimeUtils.parseStr(supOrder.getCreatedDate() == null ? "" : supOrder.getCreatedDate(), "yyyy-MM-dd");//下单日期
            objs[11] = DateTimeUtils.parseStr(supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd");//交货日期
            objs[12] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryCompanyName();//快递公司
            objs[13] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryNo();//快递信息
            objs[14] = supOrder.getRemarks() == null ? "" : supOrder.getRemarks();//备注
            objs[15] = supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName();//供应商名称
            objs[16] = Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus());//状态
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList);
    }
    /**
     * @Description:平台工单的批量导出
     * @Author: hanchao
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportExcelOnline", method = {RequestMethod.GET})
    public void exportExcelOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        SupOrderListOnlineForm form = new SupOrderListOnlineForm();
        if (request.getParameter("producedStatus") == null)  return;
        form.supOrderNo = request.getParameter("supOrderNo");
        form.supplierOrderNo = request.getParameter("supplierOrderNo");
        form.productCode = request.getParameter("productCode");
        form.pno = request.getParameter("pno");
        form.producedStatus = Integer.parseInt(request.getParameter("producedStatus"));
        form.startTime = request.getParameter("startTime");
        form.endTime = request.getParameter("endTime");
        form.deliveryDateStart = request.getParameter("deliveryDateStart");
        form.deliveryDateEnd = request.getParameter("deliveryDateEnd");
        PageList<SupOrder> supOrders = supOrderService.supOrderListOnline(pageQuery,form);
        //导出文件的标题
        String title = "平台工单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号","采购订单号", "工单号","商品ID", "供应商产品编号","供应商","分类", "颜色", "尺寸","客户备注","其他","件数","价格","下单日期","交货日期","期望发货日期","快递信息","备注","状态"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (SupOrder supOrder : supOrders) {//循环每一条数据
            //处理异常数据
            if (supOrder.getStoSuplierOrder() == null) supOrder.setStoSuplierOrder(new StoSuplierOrder());
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());
            objs = new Object[headers.length];
            objs[1] = supOrder.getOrderNo() == null ? "" : supOrder.getStoSuplierOrder().getOrderNo();//采购订单号
            objs[2] = supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo();//工单号
            objs[3] = supOrder.getSupOrderDetail().getPlaProduct().getProductCode();//商品ID
            objs[4] = supOrder.getSupOrderDetail().getPlaProduct().getPno();//供应商产品编号
            objs[5] = supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName();//供应商名称
            objs[6] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName();//分类
            objs[7] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor();//颜色
            objs[8] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize();//尺寸
            objs[9] = supOrder.getStoSuplierOrder().getRemarks();//客户备注
            objs[10] = supOrder.getSupOrderDetail().getOther();//其他
            objs[11] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty();//件数
            objs[12] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getOutputPrice();//价格
            objs[13] = DateTimeUtils.parseStr(supOrder.getCreatedDate() == null ? "" : supOrder.getCreatedDate(), "yyyy-MM-dd");//下单日期
            objs[14] = DateTimeUtils.parseStr(supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd");//交货日期
            objs[15] = DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getExpectsendDate(), "yyyy-MM-dd");//期望发货日期
            objs[16] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryNo();//快递信息
            objs[17] = supOrder.getRemarks() == null ? "" : supOrder.getRemarks();//备注
            objs[18] = Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus());//状态
            //数据添加到excel表格
            dataList.add(objs);
        }
        ExportExcelUtil.exportExcel(request, response, title, headers, dataList);
    }

    /**
     * @Description: 平台工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/supOrderListOnline", method = {RequestMethod.POST})
    public ResponseEntity supOrderListOnline(@RequestBody SupOrderListOnlineForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrders = supOrderService.supOrderListOnline(pageQuery, form);
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            //处理异常数据
            if (supOrder.getStoSuplierOrder() == null) supOrder.setStoSuplierOrder(new StoSuplierOrder());
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());

            JSONObject json = new JSONObject();
            json.put("supOrderId", supOrder.getId());
            json.put("supplierOrderNo", supOrder.getStoSuplierOrder().getOrderNo());
            json.put("supOrderNo", supOrder.getOrderNo());
            json.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
            json.put("pno", supOrder.getSupOrderDetail().getPlaProduct().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("supplierName", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());
            json.put("categoryName", supOrder.getSupOrderDetail().getCategoryName());
            json.put("color", supOrder.getSupOrderDetail().getColor());
            json.put("size", supOrder.getSupOrderDetail().getSize());
            json.put("supplierOrderRemarks", supOrder.getStoSuplierOrder().getRemarks());
            json.put("other", supOrder.getSupOrderDetail().getOther());
            json.put("qty", supOrder.getSupOrderDetail().getQty());
            json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());
            json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
            json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
            json.put("expectsendDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
            json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
            json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());
            json.put("remarks", supOrder.getRemarks());
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus()));

            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }

            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", data, pageQuery.page, pageQuery.limit, supOrders.total), HttpStatus.OK);
    }

    /**
     * @Description: 平台工单详情
     * @Author: Chen.zm
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/supOrderOnline", method = {RequestMethod.POST})
    public ResponseEntity supOrderOnline(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = supOrderService.getSupOrder(form.id);
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());
        JSONObject json = new JSONObject();
        json.put("supOrderId", supOrder.getId());
        json.put("supplierOrderNo", supOrder.getStoSuplierOrder() == null ? "" : supOrder.getStoSuplierOrder().getOrderNo());
        json.put("supOrderNo", supOrder.getOrderNo());
        json.put("supplierName", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());
        json.put("supplierPhone", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getContactPhone());
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("expectsendDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getExpectsendDate(), "yyyy-MM-dd"));
        json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
        json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());

        json.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
        json.put("pno", supOrder.getSupOrderDetail().getPlaProduct().getPno());
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
        json.put("other", supOrder.getSupOrderDetail().getOther());
        json.put("qty", supOrder.getSupOrderDetail().getQty());
        json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());

        json.put("material", supOrder.getSupOrderDetail().getPlaProduct().getMaterial());
        json.put("technics", supOrder.getSupOrderDetail().getPlaProduct().getTechnics());
        json.put("supplierOrderRemarks", supOrder.getStoSuplierOrder() == null ? "" : supOrder.getStoSuplierOrder().getRemarks());
        json.put("description", supOrder.getDescription());

        JSONArray imgList = new JSONArray();
        for (PlaProductPicture picture: supOrder.getSupOrderDetail().getPlaProduct().getPlaProductPictureList()) {
            JSONObject img = new JSONObject();
            img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            imgList.add(img);
        }
        json.put("imgList", imgList);

        for (String key : json.keySet()) {
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 供应商工单列表
     * @Author: Chen.zm
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/supOrderListOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderListOffline(@RequestBody SupOrderListOfflineForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrders = supOrderService.supOrderListOffline(pageQuery, form);
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            //处理异常数据
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());

            JSONObject json = new JSONObject();
            json.put("supOrderId", supOrder.getId());
            json.put("supOrderNo", supOrder.getOrderNo());
            json.put("insideOrderNo", supOrder.getInsideOrderNo());
            json.put("pno", supOrder.getSupOrderDetail().getPlaProduct().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("customer", supOrder.getSupOrderDelivery().getCustomer());
            json.put("categoryName", supOrder.getSupOrderDetail().getCategoryName());
            json.put("color", supOrder.getSupOrderDetail().getColor());
            json.put("size", supOrder.getSupOrderDetail().getSize());
            json.put("other", supOrder.getSupOrderDetail().getOther());
            json.put("qty", supOrder.getSupOrderDetail().getQty());
            json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());
            json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
            json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
            json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
            json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());
            json.put("remarks", supOrder.getRemarks());
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));
            json.put("supplierName", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());

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
     * @Date: 2017/12/5 0005
     */
    @RequestMapping(value = "/supOrderOffline", method = {RequestMethod.POST})
    public ResponseEntity supOrderOffline(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = supOrderService.getSupOrder(form.id);
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);
        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        JSONObject json = new JSONObject();
        json.put("supOrderId", supOrder.getId());
        json.put("supOrderNo", supOrder.getOrderNo());
//        json.put("supplierName", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());
//        json.put("supplierPhone", supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getContactPhone());
        json.put("customer", supOrder.getSupOrderDelivery().getCustomer());
        json.put("customerPhone", supOrder.getSupOrderDelivery().getCustomerPhone());
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
        json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());
        json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));

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
        json.put("other", supOrder.getSupOrderDetail().getOther());
        json.put("qty", supOrder.getSupOrderDetail().getQty());
        json.put("outputPrice", supOrder.getSupOrderDetail().getOutputPrice());

        json.put("material", supOrder.getSupOrderDetail().getMaterial());
        json.put("technics", supOrder.getSupOrderDetail().getTechnics());
        json.put("description", supOrder.getDescription());
        JSONArray imgList = new JSONArray();
        for (SupOrderDetailPicture picture: supOrder.getSupOrderDetail().getSupOrderDetailPictureList()) {
            JSONObject img = new JSONObject();
            img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
            imgList.add(img);
        }
        json.put("imgList", imgList);

        for (String key : json.keySet()) {
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }


   /**
    * @Description: 工单记录
    * @Author: Chen.zm
    * @Date: 2017/12/5 0005
    */
    @RequestMapping(value = "/supOrderDeliveryLogList", method = {RequestMethod.POST})
    public ResponseEntity supOrderDeliveryLogList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        List<SupOrderDeliveryLog> logs = supOrderService.findSupOrderDeliveryLogList(form.id);
        JSONArray data = new JSONArray();
        for (SupOrderDeliveryLog log : logs) {
            JSONObject json = new JSONObject();
            json.put("createTime", DateTimeUtils.parseStr(log.getCreatedDate()));
            json.put("instruction", log.getInstruction() == null ? "" : log.getInstruction());
            json.put("personnel", log.getPersonnel() == null ? "" : log.getPersonnel());
            json.put("action", log.getAction() == null ? "" : log.getAction());
            for (String key : json.keySet()){
                if (json.get(key) == null) json.put(key, "");
            }
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
        if (StringUtils.isBlank(form.remarks))
            return new ResponseEntity(new RestResponseEntity(110, "备注不能为空", null), HttpStatus.OK);
        if (form.supOrderIds.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "修改数量不能为空", null), HttpStatus.OK);
        supOrderService.updateSupOrderRemarks(form.remarks, form.supOrderIds);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 获取工单可分配的供应商
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/produceSupplier", method = {RequestMethod.POST})
    public ResponseEntity produceSupplier(@RequestBody IdForm form) throws Exception {
        PlaProduct product = supOrderService.produceSupplier(form.id).getSupOrderDetail().getPlaProduct();
        JSONObject data = new JSONObject();
        data.put("supplierName", product.getSupSuplier().getName());
        data.put("suplierPrice", product.getSuplierPrice());
        data.put("deliveryTime", product.getSuplierDay() == null ? "" :
                DateTimeUtils.parseStr(DateTimeUtils.getDateByAddDay(new Date(), product.getSuplierDay().intValue() - 1), "yyyy-MM-dd"));
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 分配供应商
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/saveSupOrderSupplier", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderSupplier(@RequestBody SaveSupOrderSupplierForm form) throws Exception {
        if (StringUtils.isBlank(form.deliveryTime))
            return new ResponseEntity(new RestResponseEntity(120, "交付日期不能为空", null), HttpStatus.OK);
        Date deliveryDate = DateTimeUtils.parseDate(form.deliveryTime, "yyyy-MM-dd");
        if (deliveryDate == null)
            return new ResponseEntity(new RestResponseEntity(130, "交付日期格式不正确", null), HttpStatus.OK);
        supOrderService.saveSupOrderSupplier(form.supOrderId, deliveryDate, form.description);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 批量分配供应商
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/saveSupOrderSupplierList", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderSupplierList(@RequestBody IdsForm form) throws Exception {
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "分配数量不能为空", null), HttpStatus.OK);
        supOrderService.saveSupOrderSupplierList(form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }



    /**
     * @Description: 修改供应商商品编号
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/productPnoModify", method = {RequestMethod.POST})
    public ResponseEntity productPnoModify(@RequestBody ProductModifyForm form) throws Exception {
        if (StringUtils.isBlank(form.productCode))
            return new ResponseEntity(new RestResponseEntity(110, "商品ID不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.pno))
            return new ResponseEntity(new RestResponseEntity(120, "产品编号不能为空", null), HttpStatus.OK);
        supOrderService.saveProductPnoModify(form.productCode, form.pno);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 取消工单及批量取消
     * @Author: Chen.zm
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderCancel", method = {RequestMethod.POST})
    public ResponseEntity supOrderCancel(@RequestBody SupOrderCancelForm form) throws Exception {
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        supOrderService.saveSupOrderCancel(form.instruction, CurrentUser.get().userName, form.ids);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }


    /**
     * @Description: 平台工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/producedStatusCountOnline", method = {RequestMethod.POST})
    public ResponseEntity producedStatusCountOnline() throws Exception {
        JSONArray data = supOrderService.producedStatusCountOnline();
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 供应商工单各状态及数量
     * @Author: Chen.zm
     * @Date: 2017/12/7 0007
     */
    @RequestMapping(value = "/producedStatusCountOffline", method = {RequestMethod.POST})
    public ResponseEntity producedStatusCountOffline() throws Exception {
        JSONArray data = supOrderService.producedStatusCountOffline();
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description:供应商工单客户名称获取
     * @Author: hanchao
     * @Date: 2017/12/11 0011
     */
    @RequestMapping(value = "/producedCustomerOffline", method = {RequestMethod.POST})
    public ResponseEntity producedCustomerOffline() throws Exception {
        List<SupOrderDelivery> sup = supOrderService.producedCustomerOffline();
        JSONArray jsonArray = new JSONArray();
        for (SupOrderDelivery supOrderDelivery : sup){
            if (StringUtils.isBlank(supOrderDelivery.getCustomer())) continue;
            JSONObject json = new JSONObject();
            json.put("customerId", supOrderDelivery.getId());
            json.put("customerName", supOrderDelivery.getCustomer());
            jsonArray.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",jsonArray), HttpStatus.OK);
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
        List<SupOrderDeliveryRecord> logs = supOrderService.findSupOrderDeliveryRecordList(form.id);
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
        List<SupOrderRemarkLog> logs = supOrderService.findSupOrderRemarkLogList(form.id);
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
