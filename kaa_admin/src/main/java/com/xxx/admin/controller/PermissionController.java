package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.xxx.admin.service.PermissionService;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * @Description: 菜单管理页面
     * @Author: disvenk.dai
     * @Date: 2017/12/27
     */
    @RequestMapping(value="/menuManagerHtml",method = {RequestMethod.GET})
    public String menuManageHtml(HttpServletRequest request, ModelMap modelMap){
        return "/permissionManage/menuManage";
    }

    /**
     * @Description: 角色管理页面
     * @Author: disvenk.dai
     * @Date: 2017/12/27
     */
    @RequestMapping(value="/roleManagerHtml",method = {RequestMethod.GET})
    public String roleManageHtml(HttpServletRequest request, ModelMap modelMap){
        return "/permissionManage/roleManage";
    }

    /**
     * @Description: 用户管理页面
     * @Author: disvenk.dai
     * @Date: 2017/1/3
     */
    @RequestMapping(value="/userManagerHtml",method = {RequestMethod.GET})
    public String userManageHtml(HttpServletRequest request, ModelMap modelMap){
        System.out.println("ddd");
        return "/permissionManage/userManage";
    }

    /**
     * @Description: 根据用户查询菜单
     * @Author: disvenk.dai
     * @Date: 2017/12/27
     */
    @RequestMapping(value="/findMenuByUser",method = {RequestMethod.POST})
    public ResponseEntity findMenuByUser(){
       JSONArray data = permissionService.findMenuByUser(CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100, "成功", data), HttpStatus.OK);
    }

}
