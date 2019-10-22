package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SuplierHomeService;
import com.xxx.suplier.service.SuplierOrderService;
import com.xxx.suplier.service.WeChatService;
import com.xxx.suplier.service.WorkerHomeService;
import com.xxx.user.Commo;
import com.xxx.user.form.IconForm;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/workerHome")
public class WorkerHomeController {

    @Autowired
    private WorkerHomeService workerHomeService;
    @Autowired
    private SuplierOrderService suplierOrderService;

    /**
     * @Description: 获取工人订单量统计
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/findWorkerOrder", method = {RequestMethod.POST})
    public ResponseEntity findWorkerOrder(@RequestBody DateStrEndForm form) throws Exception {
        JSONObject json = workerHomeService.findWorkerOrder(CurrentUser.get().getWorkerId(), form.dateStr, form.dateEnd);
        if (json == null) {
            json = new JSONObject();
            json.put("orderCount", 0);
            json.put("priceSum", 0);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }


    /**
     * @Description: 工人每月总工价统计
     * @Author: Chen.zm
     * @Date: 2017/11/3 0003
     */
    @RequestMapping(value = "/findWorkerMonthOrder", method = {RequestMethod.POST})
    public ResponseEntity findWorkerMonthOrder(@RequestBody DateStrEndListForm form) throws Exception {
        List<JSONObject> list = workerHomeService.findWorkerMonthOrder(CurrentUser.get().getWorkerId(), null, null);
        JSONArray data = new JSONArray();
        for (DateForm f : form.dateList) {
            boolean isDate = false;
            for (JSONObject js : list) {
                if (js.get("date") == null) continue;
                if (js.get("date").equals(f.date)) {
                    JSONObject json = new JSONObject();
                    json.put("date", js.get("date"));
                    json.put("priceSum", js.get("priceSum"));
                    data.add(json);
                    isDate = true;
                    continue;
                }
            }
            if (!isDate) {
                JSONObject json = new JSONObject();
                json.put("date", f.date);
                json.put("priceSum", 0.0);
                data.add(json);
            }
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 工人扫码工单列表
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    @RequestMapping(value = "/findWorkerSupOrder", method = {RequestMethod.POST})
    public ResponseEntity findWorkerSupOrder(@RequestBody DateStrEndForm form) throws Exception {
        List<JSONObject> list = workerHomeService.findWorkerSupOrder(form.workerId != null ? form.workerId : CurrentUser.get().getWorkerId(), form.dateStr, form.dateEnd);
        JSONArray data = new JSONArray();
        data.addAll(list);
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }



    /**
     * @Description: 工人扫码后的工单详情工单详情
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    @RequestMapping(value = "/supOrderDetail", method = {RequestMethod.POST})
    public ResponseEntity supOrderDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(form.id, CurrentUser.get().getSuplierId());
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "工单不存在", null), HttpStatus.OK);
//        if (supOrder.getProducedStatus() != 3)
//            return new ResponseEntity(new RestResponseEntity(130, "工单已发货或已完成", null), HttpStatus.OK);
        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());
        JSONObject json = new JSONObject();
        //获取当前工人名称，可操作的工序
        json.put("userName", CurrentUser.get().userName);
        SupWorker worker = suplierOrderService.get2(SupWorker.class, CurrentUser.get().getWorkerId());
        if (worker == null)
            return new ResponseEntity(new RestResponseEntity(140, "请重新登录", null), HttpStatus.OK);


        //可用工序列表
        Map<Integer, String > procedureMap = new HashMap<>();
        //获取工单所有工序
        if (!(supOrder.getSupSalesOrder() == null || supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList() == null
                || supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList().size() == 0)) {
            for (SupSalesOrderDetailProcedure procedure : supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList()) {
                procedureMap.put(procedure.getProcedureId(), procedure.getProcedureName());
            }
        }

//        //获取工单生产记录 （已领取的工序）
//        if (supOrder.getSupOrderProductionLogList() != null) {
//            for (SupOrderProductionLog log : supOrder.getSupOrderProductionLogList()) {
//                if (log.getSupSalesOrderDetailProcedure() == null) continue;
//                //移除已领取的工序
//                procedureMap.remove(log.getSupSalesOrderDetailProcedure().getProcedureId());
//            }
//        }

        //获取当前工人可操作的工序
        JSONArray procedures = new JSONArray();
        if ((worker.getSupWorkerStationList() != null && worker.getSupWorkerStationList().size() != 0)) {
            for (SupWorkerStation station : worker.getSupWorkerStationList()) {
                //校验工人是否拥有该工序
                if (procedureMap.get(station.getProcedureId()) != null) {
                    JSONObject jso = new JSONObject();
                    jso.put("procedureId", station.getProcedureId());
                    jso.put("procedureName", procedureMap.get(station.getProcedureId()));
                    procedures.add(jso);
                }
            }
        }
        json.put("procedures", procedures);

        json.put("supOrderNo", supOrder.getOrderNo());
        json.put("insideOrderNo", supOrder.getInsideOrderNo());
        json.put("supOrderDate", DateTimeUtils.parseStr(supOrder.getCreatedDate(), "yyyy-MM-dd"));
        json.put("deliveryDate", DateTimeUtils.parseStr(supOrder.getSupOrderDelivery().getDeliveryDate(), "yyyy-MM-dd"));
        json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));

        JSONArray imgList = new JSONArray();
        if (supOrder.getSupOrderDetail().getSupOrderDetailPictureList() != null) {
            for (SupOrderDetailPicture picture: supOrder.getSupOrderDetail().getSupOrderDetailPictureList()) {
                JSONObject img = new JSONObject();
                img.put("key", picture.getHref());
                img.put("href", OSSClientUtil.getObjectUrl(picture.getHref()));
                imgList.add(img);
            }
        }
        json.put("imgList", imgList);

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);

    }



    /**
     * @Description: 工人领取工序
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    @RequestMapping(value = "/saveSupOrderProductionLog", method = {RequestMethod.POST})
    public ResponseEntity saveSupOrderProductionLog(@RequestBody SaveSupOrderProductionLogForm form) throws Exception {
        if (form.orderId == null)
            return new ResponseEntity(new RestResponseEntity(110, "订单id不能为空", null), HttpStatus.OK);
        if (form.procedureId == null)
            return new ResponseEntity(new RestResponseEntity(120, "请选择工序", null), HttpStatus.OK);
        SupOrder supOrder = suplierOrderService.findSupOrderDateil(form.orderId, CurrentUser.get().getSuplierId());
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "工单不存在", null), HttpStatus.OK);
        if (supOrder.getProducedStatus() != 3)
            return new ResponseEntity(new RestResponseEntity(130, "工单已发货或已完成", null), HttpStatus.OK);
        if (supOrder.getSupOrderDelivery() == null) supOrder.setSupOrderDelivery(new SupOrderDelivery());
        if (supOrder.getSupOrderDetail() == null) supOrder.setSupOrderDetail(new SupOrderDetail());

        //判断工单是否有该工序
        SupSalesOrderDetailProcedure procedure =
                suplierOrderService.get2(SupSalesOrderDetailProcedure.class, "detailId", supOrder.getSupSalesOrder().getSupSalesOrderDetail().getId(), "procedureId", form.procedureId);
        if (procedure == null)
            return new ResponseEntity(new RestResponseEntity(140, "工单不存在当前工序", null), HttpStatus.OK);

        //判断该工序是否已生产
        SupOrderProductionLog productionLog =
                suplierOrderService.get2(SupOrderProductionLog.class, "orderId", supOrder.getId(), "detailProcedureId", procedure.getId());
        if (productionLog != null)
            return new ResponseEntity(new RestResponseEntity(140, "当前工序已被领取，如需修改请联系管理员", null), HttpStatus.OK);

        //判断工人是否可生产该工序
        SupWorkerStation station = suplierOrderService.get2(SupWorkerStation.class, "workerId", CurrentUser.get().getWorkerId(), "procedureId", form.procedureId);
        if (station == null)
            return new ResponseEntity(new RestResponseEntity(150, "您无法领取当前工序", null), HttpStatus.OK);

        //创建生产记录
        workerHomeService.saveSupOrderProductionLog(CurrentUser.get().getWorkerId(), supOrder.getId(), procedure.getId(), procedure.getProcedureName(), CurrentUser.get().userName);

        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
