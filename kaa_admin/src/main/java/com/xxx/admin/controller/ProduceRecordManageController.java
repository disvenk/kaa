package com.xxx.admin.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.ProduceRecordManageListForm;
import com.xxx.admin.service.ProduceRecordManageService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SupOrder;
import com.xxx.model.business.SupOrderProductionLog;
import com.xxx.user.Commo;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/produceRecordManage")
public class ProduceRecordManageController {

    @Autowired
    private ProduceRecordManageService produceRecordService;


    /**
     * @Description:获取工厂生产记录列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/produceRecordList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody ProduceRecordManageListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupOrder> supOrderList = produceRecordService.findProduceRecordList(pageQuery,form.orderNo,form.supplierProductCode,form.procedureType,form.producedStatus,form.productCode,form.timeStatus,form.supplierName);
        JSONArray jsonArray = new JSONArray();
        for (SupOrder supOrder : supOrderList) {
            JSONObject json = new JSONObject();
            json.put("id", supOrder.getId());
            json.put("orderNo", supOrder.getOrderNo() == null ? "" : supOrder.getOrderNo());
            json.put("pno",supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPno());
            json.put("productCode",supOrder.getSupOrderDetail() == null ? "" :
                    supOrder.getSupOrderDetail().getPlaProduct() == null ? "" :
                            supOrder.getSupOrderDetail().getPlaProduct().getProductCode());
            json.put("href",supOrder.getSupOrderDetail() == null ? "" : OSSClientUtil.getObjectUrl(supOrder.getSupOrderDetail().getHref()));
            json.put("categoryName",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getCategoryName());
            json.put("color",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getColor());
            json.put("size",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getSize());
            json.put("suplierName",supOrder.getSupSuplier() == null ? "" : supOrder.getSupSuplier().getName());
            /*件数*/
            json.put("qty",supOrder.getSupOrderDetail() == null ? "" : supOrder.getSupOrderDetail().getQty());
            JSONArray jsonArray1 = new JSONArray();
            for (int i=0; i<5; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("procedureType","");
                jsonObject.put("workerName", "");
                jsonObject.put("productionDate", "");
                jsonArray1.add(jsonObject);
            }
            for(SupOrderProductionLog sup : supOrder.getSupOrderProductionLogList()){
                JSONObject jsonObject = new JSONObject();
                //jsonObject.put("procedureType",sup.getProcedureType());
                jsonObject.put("workerName",sup.getSupWorker() == null ? "" : sup.getSupWorker().getName());
                jsonObject.put("productionDate", DateTimeUtils.parseStr(sup.getProductionDate()));
               // jsonArray1.set(Integer.parseInt(sup.getProcedureType()) - 1,jsonObject);
            }

            json.put("procedureTypeList",jsonArray1);
            /*生产状态*/
            json.put("producedStatus", supOrder.getProducedStatus() == null ? "" : supOrder.getProducedStatus());
            json.put("producedStatusName", Commo.parseSuplierOrderProducedStatus(supOrder.getProducedStatus()));

            //去除null
            for(String key : json.keySet()){
                if(json.get(key) == null) json.put(key,"");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supOrderList.total), HttpStatus.OK);
    }
}
