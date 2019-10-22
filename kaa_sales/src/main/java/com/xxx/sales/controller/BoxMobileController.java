package com.xxx.sales.controller;


import com.xxx.core.config.Configs;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.sales.form.FeedbackSubmitForm;
import com.xxx.sales.service.SalesHomeService;
import com.xxx.sales.service.WeChatService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Description: 微信——盒子
 * @Author: Chen.zm
 * @Date: 2017/12/25 0025
 */
@Controller
@RequestMapping("/boxMobile")
public class BoxMobileController {

    @Autowired
    private WeChatService weChatService;

    /**
     * 获取微信签名
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getshareinfo", method = {RequestMethod.GET})
    public ResponseEntity getShareInfo(HttpServletRequest request) throws Exception {
        Map map = weChatService.gettick(request);
        return new ResponseEntity(new RestResponseEntity(100, "成功", map), HttpStatus.OK);
    }


    @RequestMapping(value = "/weixinAuthorizationHtml", method = {RequestMethod.GET})
    public String weixinAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirectUrl = "http%3a%2f%2f" + Configs.openIdUrl + "%2fboxMobile%2fmobileHomeHtml";
        if (request.getParameter("type") != null && request.getParameter("type").equals("2")) {
            redirectUrl = "http%3a%2f%2f" + Configs.openIdUrl + "%2fboxMobile%2fmobilePayHtml%3forderId%3d" + request.getParameter("orderId") +
                    "%26orderNo%3d" + request.getParameter("orderNo");
        }
        String id = request.getParameter("id") == null ? "" : request.getParameter("id");
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + Configs.payAppid +
//                "&redirect_uri=http%3a%2f%2f" + Configs.openIdUrl + "%2fboxMobile%2fmobileHomeHtml" + //【需用encode编码
                "&redirect_uri=" + redirectUrl + //【需用encode编码
                "&response_type=code" +
                "&scope=snsapi_base" + //snsapi_base（默认）   snsapi_userinfo （需授权）
                "&state=" + id +
                "#wechat_redirect";
        return "redirect:" + new String(url.getBytes("UTF-8"));
    }


    //盒子购买
    @RequestMapping(value = "/mobileHomeHtml", method = {RequestMethod.GET})
    public String mobileHomeHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String openId = null;
        String code = request.getParameter("code");//微信跳转携带的code  用于获取openId
        if (StringUtils.isNotBlank(code)) {
            openId = weChatService.findOpenId(request, code);
        }
        modelMap.put("openId", openId);
        return "mobileBox/homePage";
    }

    /**
     * @Description: 盒子商品购买
     * @Author: Chen.zm
     * @Date: 2017/12/25 0025
     */
    @RequestMapping(value = "/mobilePayHtml", method = {RequestMethod.GET})
    public String mobilePayHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        String id = request.getParameter("state") == null ? "" : request.getParameter("state");
        String openId = null;
        String code = request.getParameter("code");//微信跳转携带的code  用于获取openId
        if (StringUtils.isNotBlank(code)) {
            openId = weChatService.findOpenId(request, code);
        }
        modelMap.put("openId", openId);
        modelMap.put("id", id);
        modelMap.put("orderId", request.getParameter("orderId") == null ? "" : request.getParameter("orderId"));
        modelMap.put("orderNo", request.getParameter("orderNo") == null ? "" : request.getParameter("orderNo"));
        return "mobileBox/mobilePay";
    }


    @RequestMapping(value = "/mobileDetailHtml", method = {RequestMethod.GET})
    public String mobileDetailHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobileBox/mobileDetail";
    }

    @RequestMapping(value = "/mobileOrderConfirmHtml", method = {RequestMethod.GET})
    public String mobileOrderConfirmHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobileBox/mobileOrderConfirm";
    }

    @RequestMapping(value = "/mobileBuyHtml", method = {RequestMethod.GET})
    public String mobileBuyHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobileBox/mobileBuy";
    }

    @RequestMapping(value = "/mobileSuccessHtml", method = {RequestMethod.GET})
    public String mobileSuccessHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "mobileBox/mobileSuccess";
    }




}
