package com.xxx.suplier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/homePage")
public class HomePageController {

    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String WOlocalHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/login";
    }

    @RequestMapping(value = "/registerHtml", method = {RequestMethod.GET})
    public String registerHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/register";
    }

    @RequestMapping(value = "/forgetPasswordHtml", method = {RequestMethod.GET})
    public String forgetPasswordHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/forgetPassword";
    }
    @RequestMapping(value = "/joinInHtml", method = {RequestMethod.GET})
    public String joinInHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/joinIn";
    }
    @RequestMapping(value = "/reviewHtml", method = {RequestMethod.GET})
    public String reviewHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/review";
    }
    @RequestMapping(value = "/reviewDefaultHtml", method = {RequestMethod.GET})
    public String reviewDefaultHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/reviewDefault";
    }
    @RequestMapping(value = "/welcomeHtml", method = {RequestMethod.GET})
    public String welcomeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/welcome";
    }

    @RequestMapping(value = "/aboutUsHtml", method = {RequestMethod.GET})
    public String aboutUsHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/aboutUs";
    }

    @RequestMapping(value = "/privacyHtml", method = {RequestMethod.GET})
    public String privacyHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/privacy";
    }

    @RequestMapping(value = "/agreementHtml", method = {RequestMethod.GET})
    public String agreementHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "web/homePage/agreement";
    }

}