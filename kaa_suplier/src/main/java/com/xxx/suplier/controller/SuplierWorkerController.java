package com.xxx.suplier.controller;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.*;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SuplierWorkerService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
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
@RequestMapping("/suplierWorker")
public class SuplierWorkerController {

    @Autowired
    private SuplierWorkerService suplierWorkerService;
    

    /**
     * @Description:供应商工人信息新增
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/addSuplierWorker", method = {RequestMethod.POST})
    public ResponseEntity addSuplierWorker(@RequestBody SuplierWorkerEditForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        suplierWorkerService.addSuplierWorker(form,CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:工厂工人信息编辑
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/saveSuplierWorker", method = {RequestMethod.POST})
    public ResponseEntity saveSuplierWorker(@RequestBody SuplierWorkerEditForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        suplierWorkerService.saveSuplierWorker(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100,"成功", null), HttpStatus.OK);
    }

    /**
     * @Description:供应商工人编辑详情
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/suplierWorkerDetail", method = {RequestMethod.POST})
    public ResponseEntity productDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "供应商工人id不能为空", null), HttpStatus.OK);
        SupWorker supWorker = suplierWorkerService.findSuplierWorkerDateil(form.id);
        if (supWorker == null)
            return new ResponseEntity(new RestResponseEntity(120, "供应商工人信息不存在", null), HttpStatus.OK);

        JSONObject json = new JSONObject();
        json.put("code", supWorker.getCode());
//        json.put("suplierId",supWorker.getSuplierId());
        json.put("name", supWorker.getName());
        json.put("phone", supWorker.getPhone());
        json.put("remark", supWorker.getRemarks());
        json.put("updateDate", DateTimeUtils.parseStr(supWorker.getUpdateDate()));
        JSONArray stationTypeList = new JSONArray();
        for (SupWorkerStation supWorkerStation : supWorker.getSupWorkerStationList()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("stationType", supWorkerStation.getProcedureId());
            stationTypeList.add(jsonObject);
        }
        json.put("stationTypeList", stationTypeList);
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:获取工厂工人维护列表
     * @Author: hanchao
     * @Date: 2017/12/4 0004
     */
    @RequestMapping(value = "/workerList", method = {RequestMethod.POST})
    public ResponseEntity productList(@RequestBody SuplierWorkerForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupWorker> suplierWorkerList = suplierWorkerService.findSuplierWorkerList(pageQuery,form.code,form.name,form.phone,form.stationType, CurrentUser.get().getSuplierId());
        JSONArray jsonArray = new JSONArray();
        JSONObject json1 =new JSONObject();
//        json1.put("suplierId",CurrentUser.get().getSuplierId());

        for (SupWorker supWorker : suplierWorkerList) {
            JSONObject json = new JSONObject();
            json.put("id", supWorker.getId());
            json.put("code", supWorker.getCode());
            json.put("name",supWorker.getName());
            json.put("phone",supWorker.getPhone());
            json.put("updateDate", DateTimeUtils.parseStr(supWorker.getUpdateDate()));
            json.put("remark",supWorker.getRemarks());
            JSONArray stationTypeList = new JSONArray();
            for (SupWorkerStation supWorkerStation : supWorker.getSupWorkerStationList()){
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("stationType", supWorkerStation.getStationType());
                jsonObject.put("stationType", supWorkerStation.getProcedureId());
                jsonObject.put("stationName", supWorkerStation.getSupProcedure() == null ? "" : supWorkerStation.getSupProcedure().getName());
                stationTypeList.add(jsonObject);
            }
            json.put("stationTypeList",stationTypeList);
            jsonArray.add(json);
        }

        json1.put("worklist",jsonArray);

        return new ResponseEntity(new PageResponseEntity(100, "成功", json1, pageQuery.page, pageQuery.limit, suplierWorkerList.total), HttpStatus.OK);
    }

   /**
    * @Description:工厂工人信息逻辑删除
    * @Author: hanchao
    * @Date: 2017/12/4 0004
    */
    @RequestMapping(value = "/removeSuplierWorker", method = {RequestMethod.POST})
    public ResponseEntity removeSuplierWorker(@RequestBody IdForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "工人信息id不能为空", null), HttpStatus.OK);
        suplierWorkerService.removeSuplierWorker(form.id, CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 重置密码
     * @Author: Chen.zm
     * @Date: 2017/12/19 0019
     */
    @RequestMapping(value = "/resetPassword", method = {RequestMethod.POST})
    public ResponseEntity resetPassword(@RequestBody IdForm form) throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "工人信息id不能为空", null), HttpStatus.OK);
        suplierWorkerService.resetPassword(form.id, CurrentUser.get().getSuplierId());
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }


    /**
     * @Description: 工人列表
     * @Author: Chen.zm
     * @Date: 2018/1/11 0011
     */
    @RequestMapping(value = "/workerListAll", method = {RequestMethod.POST})
    public ResponseEntity workerListAll() throws Exception {
        if (CurrentUser.get().loginType == 4)
            return new ResponseEntity(new RestResponseEntity(300, "权限不足", null), HttpStatus.OK);
        List<SupWorker> list = suplierWorkerService.supOrderList(CurrentUser.get().getSuplierId());
        JSONArray data = new JSONArray();
        for (SupWorker worker : list) {
            JSONObject json = new JSONObject();
            json.put("id", worker.getId());
            json.put("name", worker.getName());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }


}
