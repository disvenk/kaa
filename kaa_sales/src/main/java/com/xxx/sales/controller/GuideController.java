package com.xxx.sales.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/guideHome")
public class GuideController {

    @RequestMapping(value = "/listHtml", method = {RequestMethod.GET})
    public String listHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/list";
    }

    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String loginHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/login";
    }

    @RequestMapping(value = "/pickHtml", method = {RequestMethod.GET})
    public String pickHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/pickBuy";
    }

    @RequestMapping(value = "/buyStepHtml", method = {RequestMethod.GET})
    public String buyStepHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/buyStep";
    }

    @RequestMapping(value = "/uploadHtml", method = {RequestMethod.GET})
    public String uploadHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/uploadTmall";
    }

    @RequestMapping(value = "/offLineHtml", method = {RequestMethod.GET})
    public String offLineHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/offLineCenter";
    }

    @RequestMapping(value = "/contactHtml", method = {RequestMethod.GET})
    public String contactHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/contact";
    }

    @RequestMapping(value = "/insuranceHtml", method = {RequestMethod.GET})
    public String insurancetHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/insurance";
    }

    @RequestMapping(value = "/guidingHtml", method = {RequestMethod.GET})
    public String guidingHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/guiding";
    }

    @RequestMapping(value = "/servantHtml", method = {RequestMethod.GET})
    public String servantHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "servant";
    }

    @RequestMapping(value = "/aboutUsHtml", method = {RequestMethod.GET})
    public String aboutUsHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/aboutUs";
    }

    @RequestMapping(value = "/privacyHtml", method = {RequestMethod.GET})
    public String privacyHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/privacy";
    }

    @RequestMapping(value = "/treatmentHtml", method = {RequestMethod.GET})
    public String treatmentHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/treatment";
    }



}
