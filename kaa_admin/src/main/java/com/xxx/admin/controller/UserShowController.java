package com.xxx.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.admin.form.UserListForm;
import com.xxx.admin.form.UserSaveForm;
import com.xxx.admin.service.RoleService;
import com.xxx.admin.service.UserService;
import com.xxx.core.exceptions.UpsertException;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.PubUserLogin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("user")
public class UserShowController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    /**
     * @Description:获取用户列表信息
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @RequestMapping(value = "/getUserListAllByUser", method = {RequestMethod.POST})
    public ResponseEntity getRoleListAll(@RequestBody UserListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        pageQuery.limit = 999;
        JSONArray data = new JSONArray();
        PageList<PubUserLogin> list = userService.getUserListAll(pageQuery);
        for (PubUserLogin pubUserLogin : list) {
            JSONObject json = new JSONObject();
            json.put("id",pubUserLogin.getId());
            json.put("tel",pubUserLogin.getPubUserBase()==null? "" : pubUserLogin.getPubUserBase().getMobile());
            json.put("userName",pubUserLogin.getUserCode());
            json.put("relName",pubUserLogin.getPubUserBase()==null?"":pubUserLogin.getPubUserBase().getName());
            data.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功",data, pageQuery.page, pageQuery.limit,list.total) , HttpStatus.OK);
    }

    /**
     * @Description:新增用户与编辑
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @RequestMapping(value="/newUserAddManage",method = {RequestMethod.POST})
    public ResponseEntity newUserddManage(@RequestBody UserSaveForm form) throws UpsertException {
        String mobileRegex = "^1(3|4|5|7|8)\\d{9}$";
        String tel = form.tel.replace(" ","");
        if(form.tel==null || StringUtils.isBlank(form.tel)) return new ResponseEntity(new RestResponseEntity(110,"手机号必填",null),HttpStatus.OK);
        if(!tel.matches(mobileRegex)) return new ResponseEntity(new RestResponseEntity(110,"手机格式不正确",null),HttpStatus.OK);
        if(form.userName==null || StringUtils.isBlank(form.userName)) return new ResponseEntity(new RestResponseEntity(110,"用户名必填",null),HttpStatus.OK);
        if(form.id==null){
            if(form.password==null || StringUtils.isBlank(form.password)) return new ResponseEntity(new RestResponseEntity(110,"密码必填",null),HttpStatus.OK);
        }

        return userService.newUserddManage(form);
    }

    /**
     * @Description:用户编辑详情
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @RequestMapping(value = "/userCheckDetail", method = {RequestMethod.POST})
    public ResponseEntity roleEditDetail(@RequestBody UserSaveForm form) throws Exception {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        }
        JSONObject json = userService.checkUser(form.id);

        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description:用户删除
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @RequestMapping(value="/deleteUser",method = {RequestMethod.POST})
    public ResponseEntity deleteUser(@RequestBody UserSaveForm form){
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        }
        userService.deleteUser(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:密码重置
     * @Author: disvenk.dai
     * @Date: 2018/1/6
     */
    @RequestMapping(value="/resetUserPass",method = {RequestMethod.POST})
    public ResponseEntity resetUserPass(@RequestBody UserSaveForm form) throws UpsertException {
        if(form.id==null){
            return new ResponseEntity(new RestResponseEntity(110,"id不能为空",null), HttpStatus.OK);
        }
        userService.resetUserPass(form);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

}
