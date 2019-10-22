package com.xxx.sales.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.PubUserAddress;
import com.xxx.model.business.SalesTeacher;
import com.xxx.model.business.SalesVedioInfo;
import com.xxx.sales.form.IdForm;
import com.xxx.sales.form.PubUserAddressForm;
import com.xxx.sales.form.SalesTeacherAddForm;
import com.xxx.sales.service.SalesVedioInfoService;
import com.xxx.sales.service.UserAddressService;
import com.xxx.user.security.CurrentUser;
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
import java.util.List;


@Controller
@RequestMapping("/video")
public class SalesVedioInfoController {

    @Autowired
    private SalesVedioInfoService salesVedioInfoService;


    @RequestMapping(value = "/videoListHtml", method = {RequestMethod.GET})
    public String videoListHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/videoList";
    }

    @RequestMapping(value = "/experienceUserHtml", method = {RequestMethod.GET})
    public String experienceUserHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/experienceUser";
    }

    @RequestMapping(value = "/experienceUserDetailHtml", method = {RequestMethod.GET})
    public String experienceUserDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/experienceUserDetail";
    }

    @RequestMapping(value = "/videoDetailHtml", method = {RequestMethod.GET})
    public String videoDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/videoDetail";
    }

    /**
     * @Description: 按类型展示视频，显示前面4条
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/findHomePageVedioInfoList", method = {RequestMethod.POST})
    public ResponseEntity findHomePageVedioInfoList(@RequestBody IdForm form) throws Exception {
        PageQuery pageQuery = new PageQuery(0, 4);
        PageList<SalesVedioInfo> list = salesVedioInfoService.findHomePageVedioInfoList(pageQuery, form.id);
        JSONArray data = new JSONArray();
        for (SalesVedioInfo salesVedioInfo : list) {
            JSONObject json = new JSONObject();
            json.put("id", salesVedioInfo.getId());
            json.put("title", salesVedioInfo.getTitle());
            json.put("vedioType", salesVedioInfo.getVedioType());
            json.put("vedioUrl", salesVedioInfo.getVedioUrl());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 按类型显示视频列表
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/findVedioInfoList", method = {RequestMethod.POST})
    public ResponseEntity findVedioInfoList(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "类型不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(0, 100);
        PageList<SalesVedioInfo> list = salesVedioInfoService.findVedioInfoList(pageQuery, form.id);
        JSONArray data = new JSONArray();
        for (SalesVedioInfo salesVedioInfo : list) {
            JSONObject json = new JSONObject();
            json.put("id", salesVedioInfo.getId());
            json.put("pictureUrl", OSSClientUtil.getObjectUrl(salesVedioInfo.getPictureUrl()));
            json.put("title", salesVedioInfo.getTitle());
//            json.put("vedioUrl", OSSClientUtil.getObjectUrl(salesVedioInfo.getVedioUrl()));
            json.put("views", salesVedioInfo.getViews());
            json.put("shortDescription", salesVedioInfo.getShortDescription());
//            json.put("description", salesVedioInfo.getDescription());
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 更新浏览次数
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/updateSalesVedioViews", method = {RequestMethod.POST})
    public ResponseEntity updateSalesVedioViews(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        salesVedioInfoService.updateSalesVedioViews(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description: 取得视频的详情
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/getSalesVedioInfo", method = {RequestMethod.POST})
    public ResponseEntity getSalesVedioInfo(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "ID不能为空", null), HttpStatus.OK);
        SalesVedioInfo salesVedioInfo = salesVedioInfoService.getSalesVedioInfo(form.id);
        if (salesVedioInfo == null)
            return new ResponseEntity(new RestResponseEntity(120, "视频不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", salesVedioInfo.getId());
        json.put("title", salesVedioInfo.getTitle());
        json.put("pictureUrl",OSSClientUtil.getObjectUrl(salesVedioInfo.getPictureUrl()));
        json.put("shortDescription", salesVedioInfo.getShortDescription());
        json.put("views", salesVedioInfo.getViews());
        json.put("vedioUrl",OSSClientUtil.getObjectUrl( salesVedioInfo.getVedioUrl()));
        json.put("description", salesVedioInfo.getDescription());

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 根据某一条视频，显示相关视频列表
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/findRelatedVedioInfoList", method = {RequestMethod.POST})
    public ResponseEntity findRelatedVedioInfoList(@RequestBody IdForm form) throws Exception {
        PageQuery pageQuery = new PageQuery(0, 4);
        PageList<SalesVedioInfo> list = salesVedioInfoService.findRelatedVedioInfoList(pageQuery, form.id);
        JSONArray data = new JSONArray();
        for (SalesVedioInfo salesVedioInfo : list) {
            JSONObject json = new JSONObject();
            json.put("id", salesVedioInfo.getId());
            json.put("title", salesVedioInfo.getTitle());
            json.put("view", salesVedioInfo.getViews());
            json.put("vedioUrl", OSSClientUtil.getObjectUrl(salesVedioInfo.getVedioUrl()));
            json.put("pictureUrl", OSSClientUtil.getObjectUrl(salesVedioInfo.getPictureUrl()));
            data.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

    /**
     * @Description: 体验师信息收集
     * @Author: Steven.Xiao
     * @Date: 2017/12/23
     */
    @RequestMapping(value = "/AddSalesTeacher", method = {RequestMethod.POST})
    public ResponseEntity AddSalesTeacher(@RequestBody SalesTeacherAddForm form) throws Exception {
        salesVedioInfoService.AddSalesTeacher(form.name, form.mobile, form.age);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
