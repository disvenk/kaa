package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.BoxOperateLogform;
import com.xxx.admin.form.IdForm;
import com.xxx.admin.service.HeYiBoxService;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.BoxInfo;
import com.xxx.model.business.BoxOperateLog;
import com.xxx.model.business.PubUserLogin;
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
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("heYiBox")
public class HeYiBoxController {

    @Autowired
    private HeYiBoxService heYiBoxService;

    /**
     * @Description: 和一盒子页面
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @RequestMapping(value="heYiBoxHtml",method = {RequestMethod.GET})
    public String heYiBoxHtml(HttpServletRequest request, ModelMap modelMap){
        return "/";
    }

    /**
     * @Description: 查询盒子详情
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @RequestMapping(value="findUserStoreBox",method = {RequestMethod.POST})
    public ResponseEntity findUserStoreBox(@RequestBody IdForm form){
        if(form.id == null){
            return new ResponseEntity(new RestResponseEntity(110, "用户id不能为空", null), HttpStatus.OK);
        }
      PubUserLogin pubUserLogin = heYiBoxService.getPubUserLogin(form.id);
        JSONObject json = new JSONObject();
        json.put("userName",pubUserLogin.getUserCode());
        json.put("id",pubUserLogin.getId());
        json.put("mobile",pubUserLogin.getPubUserBase().getMobile());
        BoxInfo boxInfo = heYiBoxService.getStoStoreInfo(pubUserLogin.getId());
        if(boxInfo==null){
            json.put("isBox",false);
            return  new ResponseEntity(new RestResponseEntity(110,"暂未开通盒子",json), HttpStatus.OK);
        }
        json.put("boxId",boxInfo.getId());
        json.put("isBox",true);
        json.put("storeId",boxInfo.getStoreId());
        json.put("yaJin",boxInfo.getDeposit());
        json.put("count",boxInfo.getCount());
        json.put("termTime", new SimpleDateFormat("yyyy-MM-dd").format(boxInfo.getTermTime()));

        return  new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 查询盒子操作详情
     * @Author: disvenk.dai
     * @Date: 2017/12/22
     */
    @RequestMapping(value="findBoxOperateLog",method = {RequestMethod.POST})
    public ResponseEntity getBoxDetail(@RequestBody BoxOperateLogform form){
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"门店id不能为空",null),HttpStatus.OK);
        }
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.pageSize=5;
        PageList<BoxOperateLog> list = heYiBoxService.getBoxOperateLog(form, pageQuery);
        JSONArray data = new JSONArray();
        for (BoxOperateLog boxOperateLog : list) {
        JSONObject json = new JSONObject();
        json.put("createdDate",DateTimeUtils.parseStr(boxOperateLog.getCreatedDate()));
        json.put("name",boxOperateLog.getName());
        json.put("price",boxOperateLog.getPrice());
        json.put("deposit",boxOperateLog.getDeposit());
        json.put("count",boxOperateLog.getCount());
        json.put("termTime",new SimpleDateFormat("yyyy-MM-dd").format(boxOperateLog.getTermTime()));
        data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit, list.total), HttpStatus.OK);
    }

    /**
     * @Description: 退押金
     * @Author: disvenk.dai
     * @Date: 2017/12/23
     */
    @RequestMapping(value="returnYaJin",method = {RequestMethod.POST})
    public ResponseEntity returnYaJin(@RequestBody IdForm form) throws Exception{
        if(form.id==null){
            return  new ResponseEntity(new RestResponseEntity(110,"门店id不能为空",null),HttpStatus.OK);
        }
        heYiBoxService.returnYaJin(form.id);
        return  new ResponseEntity(new RestResponseEntity(100,"押金退还成功",null),HttpStatus.OK);
    }
}
