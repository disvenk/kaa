package com.xxx.suplier.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.ProduceRecordService;
import com.xxx.suplier.service.ProductProcedureService;
import com.xxx.user.Commo;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.utils.date.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;


@Controller
@RequestMapping("/produceRecord")
public class ProduceRecordController {

    @Autowired
    private ProduceRecordService produceRecordService;
    @Autowired
    private ProductProcedureService productProcedureService;

    /**
     * @Description:批量修改供应商产品编号
     * @Author: hanchao
     * @Date: 2017/12/14 0014
     */
    @RequestMapping(value = "/updateSupOrderPno", method = {RequestMethod.POST})
    public ResponseEntity updateSupOrderPno(@RequestBody IdsSupOrderForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.ids.size() == 0)
            return new ResponseEntity(new RestResponseEntity(120, "工单数量不能为空", null), HttpStatus.OK);
        produceRecordService.updateSupOrderPno(CurrentUser.get().getSuplierId(), form.ids,form.pno);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:保存工厂工人生产记录
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/saveproduceRecord", method = {RequestMethod.POST})
    public ResponseEntity saveproduceRecord(@RequestBody ProduceRecordEditForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        produceRecordService.saveproduceRecord(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:获取所有工人名称
     * @Author: hanchao
     * @Date: 2017/12/6 0006
     */
    @RequestMapping(value = "/getAllWorkerName", method = {RequestMethod.POST})
    public ResponseEntity getAllWorkerName() throws Exception {
        List<SupWorker> supWorker = produceRecordService.getAllWorkerName(CurrentUser.get().getSuplierId());
        if (supWorker == null)
            return new ResponseEntity(new RestResponseEntity(120, "工人名称不存在", null), HttpStatus.OK);
        JSONArray json = new JSONArray();
        for (SupWorker sup : supWorker){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("workId",sup.getId());
            jsonObject.put("name",sup.getName());
            json.add(jsonObject);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description:工厂工人生产记录详情
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/produceRecordDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "工人生产信息id不能为空", null), HttpStatus.OK);
        SupOrder supOrder = produceRecordService.findProduceRecordDateil(form.id);
        if (supOrder == null)
            return new ResponseEntity(new RestResponseEntity(120, "工人生产记录不存在", null), HttpStatus.OK);

        JSONObject json = new JSONObject();
        json.put("orderNo", supOrder.getOrderNo());
        /*供应商产品编号*/
        json.put("pno", supOrder.getSupOrderDetail() == null ? "" :
                        supOrder.getSupOrderDetail().getPno());
        json.put("href", supOrder.getSupOrderDetail() == null ? "" : OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
        json.put("categoryName",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName());
        json.put("color", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor());
        json.put("qty", supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty());

        //填充工序列表
        Map<Integer, JSONObject> procedureMap = new HashMap<>();
        if (supOrder.getSupSalesOrder() != null && supOrder.getSupSalesOrder().getSupSalesOrderDetail() != null && supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList() != null)
        for(SupSalesOrderDetailProcedure procedure : supOrder.getSupSalesOrder().getSupSalesOrderDetail().getSupSalesOrderDetailProcedureList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("detailProcedureId", procedure.getId());
            jsonObject.put("procedureId", procedure.getProcedureId());
            jsonObject.put("procedureName", procedure.getProcedureName());
            jsonObject.put("workerName", "");
            jsonObject.put("productionDate", "");
            procedureMap.put(procedure.getProcedureId(), jsonObject);
        }

        for(SupOrderProductionLog sup : supOrder.getSupOrderProductionLogList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("detailProcedureId",sup.getDetailProcedureId());
            jsonObject.put("procedureId", sup.getSupSalesOrderDetailProcedure() == null ? "" : sup.getSupSalesOrderDetailProcedure().getProcedureId());
            jsonObject.put("procedureName", sup.getSupSalesOrderDetailProcedure() == null ? "" : sup.getSupSalesOrderDetailProcedure().getProcedureName());
            jsonObject.put("workerName",sup.getSupWorker() == null ? "" : sup.getSupWorker().getName());
            jsonObject.put("productionDate", DateTimeUtils.parseStr(sup.getProductionDate()));

            if (sup.getSupSalesOrderDetailProcedure() == null) continue;
            if (procedureMap.get(sup.getSupSalesOrderDetailProcedure().getProcedureId()) != null) {
                procedureMap.put(sup.getSupSalesOrderDetailProcedure().getProcedureId(), jsonObject);
            }
        }
        JSONArray jsonArray1 = new JSONArray();
        for(Integer key : procedureMap.keySet()){
            jsonArray1.add(procedureMap.get(key));
        }
        json.put("procedureTypeList",jsonArray1);

        /*生产状态*/
        json.put("producedStatus", supOrder.getProducedStatus());
        json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:获取工厂生产记录列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/produceRecordList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody ProduceRecordListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrderList = produceRecordService.findProduceRecordList(pageQuery,form.orderNo,form.supplierProductCode,form.procedureType,form.producedStatus,form.productCode,form.timeStatus, CurrentUser.get().getSuplierId());
        JSONArray jsonArray = new JSONArray();
        for (SupOrder supOrder : supOrderList) {
            JSONObject json = new JSONObject();
            json.put("id", supOrder.getId());
            json.put("orderNo", supOrder.getOrderNo());
            json.put("supplierProductCode",supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPno());
            json.put("productCode",supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                                supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
            json.put("href",supOrder.getSupOrderDetail() == null ? "" : OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("categoryName",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName());
            json.put("color",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor());
            json.put("size",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize());
            /*件数*/
            json.put("qty",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty());

            //填充工序列表
            List<SupProcedure> list = productProcedureService.findProcedureListCombox();
            Map<Integer, JSONObject> procedureMap = new HashMap<>();
            for(SupProcedure supProcedure : list){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("detailProcedureId", "");
                jsonObject.put("procedureId", "");
                jsonObject.put("workerName", "");
                jsonObject.put("productionDate", "");
                procedureMap.put(supProcedure.getId(), jsonObject);
            }

            for(SupOrderProductionLog sup : supOrder.getSupOrderProductionLogList()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("detailProcedureId",sup.getDetailProcedureId());
                jsonObject.put("procedureId", sup.getSupSalesOrderDetailProcedure() == null ? "" : sup.getSupSalesOrderDetailProcedure().getProcedureId());
                jsonObject.put("workerName",sup.getSupWorker() == null ? "" : sup.getSupWorker().getName());
                jsonObject.put("productionDate", DateTimeUtils.parseStr(sup.getProductionDate()));

                if (sup.getSupSalesOrderDetailProcedure() == null) continue;
                if (procedureMap.get(sup.getSupSalesOrderDetailProcedure().getProcedureId()) != null) {
                    procedureMap.put(sup.getSupSalesOrderDetailProcedure().getProcedureId(), jsonObject);
                }
            }
            JSONArray jsonArray1 = new JSONArray();
            for(Integer key : procedureMap.keySet()){
                jsonArray1.add(procedureMap.get(key));
            }
            json.put("procedureTypeList",jsonArray1);
            /*生产状态*/
            json.put("producedStatus", supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatusOffline(supOrder.getProducedStatus()));

            for(String key : json.keySet()){
                if(json.get(key) == null)json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supOrderList.total), HttpStatus.OK);
    }
}
