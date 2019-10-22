package com.xxx.admin.controller;



import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/home")
public class HomeController {

    @RequestMapping(value = "/loginHtml", method = {RequestMethod.GET})
    public String loginHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "login";
    }

    @RequestMapping(value = "/testHtml", method = {RequestMethod.GET})
    public String testHtml(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "test";
    }
}
