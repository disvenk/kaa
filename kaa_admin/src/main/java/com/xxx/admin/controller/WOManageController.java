package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.*;
import com.xxx.admin.service.SuplierOrderService;
import com.xxx.core.query.MybatisPageQuery;
import com.xxx.core.query.PageList;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.Commo;
import com.xxx.user.service.PaymentService;
import com.xxx.utils.Arith;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import com.xxx.model.business.*;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/WOManage")
public class WOManageController {
    @RequestMapping(value = "/WOlocalHtml", method = {RequestMethod.GET})
    public String WOlocalHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOlocal";
    }

    @RequestMapping(value = "/WOkaaHtml", method = {RequestMethod.GET})
    public String WOkaaHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOkaa";
    }

    @RequestMapping(value = "/WOoperateHtml", method = {RequestMethod.GET})
    public String WOoperateHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOoperate";
    }

    @RequestMapping(value = "/WOrecordHtml", method = {RequestMethod.GET})
    public String WOrecordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOrecord";
    }

    @RequestMapping(value = "/localDetailHtml", method = {RequestMethod.GET})
    public String localDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOlocalDetail";
    }

    @RequestMapping(value = "/kaaDetailHtml", method = {RequestMethod.GET})
    public String kaaDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOkaaDetail";
    }

    @RequestMapping(value = "/WOsendHtml", method = {RequestMethod.GET})
    public String WOsendHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOsend";
    }

    @RequestMapping(value = "/WOcheckHtml", method = {RequestMethod.GET})
    public String WOcheckHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOcheck";
    }

    @RequestMapping(value = "/WOinHtml", method = {RequestMethod.GET})
    public String WOinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOin";
    }

    @RequestMapping(value = "/WOorderlocalHtml", method = {RequestMethod.GET})
    public String WOorderlocalHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        System.out.println("keyile");
        return "woManage/WOorderlocal";
    }

    @RequestMapping(value = "/orderlocalDetailHtml", method = {RequestMethod.GET})
    public String orderlocalDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOorderlocalDetail";
    }

    @RequestMapping(value = "/WOproductHtml", method = {RequestMethod.GET})
    public String WOproductHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "woManage/WOproduct";
    }

}
