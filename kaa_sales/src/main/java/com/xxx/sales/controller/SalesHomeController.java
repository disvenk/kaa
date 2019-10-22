package com.xxx.sales.controller;


import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.FeedbackSubmitForm;
import com.xxx.sales.service.SalesHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/salesHome")
public class SalesHomeController {
    @Autowired
    private SalesHomeService salesHomeService;

    @RequestMapping(value = "/homePageHtml", method = {RequestMethod.GET})
    public String homePageHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage";
    }

    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String loginHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "login";
    }

    @RequestMapping(value = "/registerHtml", method = {RequestMethod.GET})
    public String registerHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "register";
    }

    @RequestMapping(value = "/forgetHtml", method = {RequestMethod.GET})
    public String forgetHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "forgetPwd";
    }

    @RequestMapping(value = "/serviceHtml", method = {RequestMethod.GET})
    public String serviceHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "servant";
    }

    @RequestMapping(value = "/indexHtml", method = {RequestMethod.GET})
    public String indexHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "index";
    }

    @RequestMapping(value = "/joinHtml", method = {RequestMethod.GET})
    public String joinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "storeJoin";
    }

    @RequestMapping(value = "/searchHtml", method = {RequestMethod.GET})
    public String searchHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/search";
    }

    @RequestMapping(value = "/detailHtml", method = {RequestMethod.GET})
    public String detailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/detail";
    }

    @RequestMapping(value = "/placeOrderHtml", method = {RequestMethod.GET})
    public String placeOrderHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/placeOrder";
    }

    @RequestMapping(value = "/defaultHtml", method = {RequestMethod.GET})
    public String defaultHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/default";
    }

    @RequestMapping(value = "/designHtml", method = {RequestMethod.GET})
    public String designHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/design";
    }

    @RequestMapping(value = "/bannerHtml", method = {RequestMethod.GET})
    public String bannerHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/banner";
    }

    @RequestMapping(value = "/uploadHtml", method = {RequestMethod.GET})
    public String uploadHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "upLoad";
    }

    @RequestMapping(value = "/privacyHtml", method = {RequestMethod.GET})
    public String privacyHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/privacy";
    }

    @RequestMapping(value = "/treatmentHtml", method = {RequestMethod.GET})
    public String treatmentHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/treatment";
    }

    @RequestMapping(value = "/aboutHtml", method = {RequestMethod.GET})
    public String aboutHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "guide/aboutUs";
    }

    @RequestMapping(value = "/regSuccessHtml", method = {RequestMethod.GET})
    public String regSuccessHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/regSuccess";
    }

    @RequestMapping(value = "/boxUseSuccessHtml", method = {RequestMethod.GET})
    public String paySuccessHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/boxUseSuccess";
    }

    @RequestMapping(value = "/boxHtml", method = {RequestMethod.GET})
    public String boxHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/box";
    }

    @RequestMapping(value = "/boxJoinHtml", method = {RequestMethod.GET})
    public String boxJoinHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/boxJoin";
    }

    @RequestMapping(value = "/orderConfirmHtml", method = {RequestMethod.GET})
    public String orderConfirmHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "homePage/orderConfirm";
    }

    @RequestMapping(value = "/testHtml", method = {RequestMethod.GET})
    public String testHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "/test";
    }


    /**
     * @Description: 收集反馈意见
     * @Author: Steven.Xiao
     * @Date: 2017/11/29
     */
    @RequestMapping(value = "/saveFeedback", method = {RequestMethod.POST})
    public ResponseEntity saveFeedback(@RequestBody FeedbackSubmitForm form) throws Exception {
        salesHomeService.saveFeedback(form.name,form.telephone,form.description);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

//    首页轮播通知跳转详情
    @RequestMapping(value = "/noticeInfoHtml", method = {RequestMethod.GET})
    public String noticeInfoHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "noticeInfo";
    }
}
