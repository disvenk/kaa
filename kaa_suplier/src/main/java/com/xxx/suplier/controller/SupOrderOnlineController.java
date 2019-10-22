package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SupOrderOnlineService;
import com.xxx.suplier.service.SuplierOrderService;
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
 * @Description: 平台工单
 * @Author: Chen.zm
 * @Date: 2017/12/6 0006
 */
@Controller
@RequestMapping("/supOrderOnline")
public class SupOrderOnlineController {

    @Autowired
    private SuplierOrderService suplierOrderService;
    @Autowired
    private SupOrderOnlineService supOrderOnlineService;

    /**
     * @Description:平台工单的批量导出
     * @Author: hanchao
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportExcel", method = {RequestMethod.GET})
    public void getExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageQuery pageQuery = new PageQuery(1);
        pageQuery.limit = 9999;
        SupOrderListOnlineForm form = new SupOrderListOnlineForm();
        form.supOrderNo = request.getParameter("supOrderNo");
        form.pno = request.getParameter("pno");
        form.producedStatus = Integer.parseInt(request.getParameter("producedStatus"));
        form.startTime = request.getParameter("startTime");
        form.endTime = request.getParameter("endTime");
        form.deliveryDateStart = request.getParameter("deliveryDateStart");
        form.deliveryDateEnd = request.getParameter("deliveryDateEnd");
        if (SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        PageList<SupOrder> supOrders = supOrderOnlineService.supOrderListOnline(pageQuery, form,(int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID));
        //导出文件的标题
        String title = "平台工单列表"+DateTimeUtils.parseStr(new Date(),"yyyy-MM-dd")+".xls";
        //设置表格标题行
        String[] headers = new String[] {"编号", "工单号","商品ID", "供应商产品编号","分类", "颜色", "尺寸","件数","价格","下单日期","交货日期","快递信息","备注","状态"};
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        for (SupOrder supOrder : supOrders) {//循环每一条数据
            //处理异常数据
            objs = new Object[headers.length];
            objs[1] = supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo();//工单号
            objs[2] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct().getProductCode();//商品ID
            objs[3] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct().getPno() ;
            objs[4] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName();//分类
            objs[5] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor();//颜色
            objs[6] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize();//尺寸
            objs[7] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty();//件数
            objs[8] = supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getOutputPrice();//价格
            objs[9] = DateTimeUtils.parseStr(supOrder.getCreatedDate() == null ? "" : supOrder.getCreatedDate(), "yyyy-MM-dd");//下单日期
            objs[10] = DateTimeUtils.parseStr(supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd");//交货日期
            objs[11] = supOrder.getSupOrderDelivery() == null ? "" : supOrder.getSupOrderDelivery().getDeliveryNo();//快递信息
            objs[12] = supOrder.getRemarks() == null ? "" : supOrder.getRemarks();//备注
            objs[13] = Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus());//状态
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
    public ResponseEntity supOrderList(@RequestBody SupOrderListOnlineForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrders = supOrderOnlineService.supOrderListOnline(pageQuery, form, CurrentUser.get().getSuplierId());
        JSONArray data = new JSONArray();
        for (SupOrder supOrder : supOrders) {
            //处理异常数据
            if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
            if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
            if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());

            JSONObject json = new JSONObject();
            json.put("supOrderId", supOrder.getId());
            json.put("supOrderNo", supOrder.getOrderNo());
            json.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
            json.put("pno", supOrder.getSupOrderDetail().getPno());
            json.put("href", OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
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
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/supOrderOnline", method = {RequestMethod.POST})
    public ResponseEntity supOrderOnline(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(form.id, CurrentUser.get().getSuplierId());
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "订单不存在", null), HttpStatus.OK);

        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        if (supOrder.getSupOrderDetail().getPlaProduct() == null) supOrder.getSupOrderDetail().setPlaProduct(new PlaProduct());

        JSONObject json = new JSONObject();
        json.put("supOrderId", supOrder.getId());
        json.put("supOrderNo", supOrder.getOrderNo());
//        json.put("customer", supOrder.getSupOrderDelivery().getCustomer());
//        json.put("customerPhone", supOrder.getSupOrderDelivery().getCustomerPhone());
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
//        json.put("deliveryCompanyName", supOrder.getSupOrderDelivery().getDeliveryCompanyName());
//        json.put("deliveryNo", supOrder.getSupOrderDelivery().getDeliveryNo());

        json.put("productCode", supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
//        json.put("pno", supOrder.getSupOrderDetail().getPlaProduct().getPno());
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

        json.put("material", supOrder.getSupOrderDetail().getPlaProduct().getMaterial());
        json.put("technics", supOrder.getSupOrderDetail().getPlaProduct().getTechnics());
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
     * @Description: word导出
     * @Author: Chen.zm
     * @Date: 2017/12/8 0008
     */
    @RequestMapping(value = "/exportWord", method = {RequestMethod.GET})
    public void exportWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("supOrderId") == null || SessionUtils.getSession(request, SessionUtils.SUPPLIERID) == null) return;
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(Integer.parseInt(request.getParameter("supOrderId")), (int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID));
        Map<String, Object> map = supOrderOnlineService.exportWord(supOrder);
        String fileName = "合一智造工单详情" + supOrder.getOrderNo();
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
        List<Map> mapList = supOrderOnlineService.exportWordList((int)SessionUtils.getSession(request, SessionUtils.SUPPLIERID), ids);
        String fileName = "合一智造工单详情" + DateTimeUtils.parseStr(new Date(), "yyyyMMddHHmmss");
        WordUtils.exportMillCertificateWordList(request, response, fileName, mapList);
    }


}
