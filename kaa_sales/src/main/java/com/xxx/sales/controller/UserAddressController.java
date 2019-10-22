package com.xxx.sales.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.PubUserAddress;
import com.xxx.sales.form.IdForm;
import com.xxx.sales.form.PubUserAddressForm;
import com.xxx.sales.service.UserAddressService;
import com.xxx.user.security.CurrentUser;
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
@RequestMapping("/userAddress")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * @Description: 跳转到会员中心会员地址页面
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/userAddress", method = {RequestMethod.GET})
    public String data_html(HttpServletRequest request, ModelMap modelMap) throws Exception {
        return "userAddress";
    }

    /**
     * @Description: 会员收件地址新增
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/saveUserAddress", method = {RequestMethod.POST})
    public ResponseEntity saveUserAddress(@RequestBody PubUserAddressForm form) throws Exception {
        if(form==null)
            return new ResponseEntity(new RestResponseEntity(109, "收件参数创建错误", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.address))
            return new ResponseEntity(new RestResponseEntity(110, "收件地址不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.mobile))
            return new ResponseEntity(new RestResponseEntity(111, "手机号不能为空", null), HttpStatus.OK);

        userAddressService.saveUserAddress(CurrentUser.get().userId,form.receiver,form.mobile,form.tel,form.province, form.provinceName,form.city,form.cityName, form.zone,form.zoneName, form.address,form.isDefault);

        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description:根据会员ID查询会员地址信息列表
     * @Author: hanchao
     * @Date: 2017/12/14 0014
     */
    @RequestMapping(value = "/findUserAddress", method = {RequestMethod.POST})
    public ResponseEntity findUserAddress() throws Exception {
        List<PubUserAddress> list = userAddressService.findUserAddressList(CurrentUser.get().userId);
        JSONArray data = new JSONArray();
        JSONArray data1 = new JSONArray();
        for (PubUserAddress pal : list) {
            if(pal.getDefault() == true){
                JSONObject json = new JSONObject();
                json.put("id", pal.getId());
                json.put("receiver", pal.getReceiver());
                json.put("mobile", pal.getMobile());
                json.put("provinceId", pal.getProvince());
                json.put("provinceName", pal.getProvinceName());
                json.put("cityId", pal.getCity());
                json.put("cityName", pal.getCityName());
                json.put("zoneId", pal.getZone());
                json.put("zoneName", pal.getZoneName());
                json.put("address", pal.getAddress());
                json.put("Default", pal.getDefault());
                data.add(json);
                break;
            }
            JSONObject json = new JSONObject();
            json.put("id", pal.getId());
            json.put("receiver", pal.getReceiver());
            json.put("mobile", pal.getMobile());
            json.put("provinceId", pal.getProvince());
            json.put("provinceName", pal.getProvinceName());
            json.put("cityId", pal.getCity());
            json.put("cityName", pal.getCityName());
            json.put("zoneId", pal.getZone());
            json.put("zoneName", pal.getZoneName());
            json.put("address", pal.getAddress());
            json.put("Default", pal.getDefault());
            data1.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100,"成功",data.size() == 0 ? data1 : data), HttpStatus.OK);
    }

    /**
     * @Description: 根据会员ID查询会员地址信息列表
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/findUserAddressList", method = {RequestMethod.POST})
    public ResponseEntity findUserAddressList() throws Exception {
        List<PubUserAddress> list = userAddressService.findUserAddressList(CurrentUser.get().userId);
        JSONArray data = new JSONArray();
        for (PubUserAddress pal : list) {
            JSONObject json = new JSONObject();
            json.put("id", pal.getId());
            json.put("receiver", pal.getReceiver());
            json.put("mobile", pal.getMobile());
            json.put("provinceId", pal.getProvince());
            json.put("provinceName", pal.getProvinceName());
            json.put("cityId", pal.getCity());
            json.put("cityName", pal.getCityName());
            json.put("zoneId", pal.getZone());
            json.put("zoneName", pal.getZoneName());
            json.put("address", pal.getAddress());
            json.put("Default", pal.getDefault());
            data.add(json);
        }

        return new ResponseEntity(new RestResponseEntity(100,"成功",data), HttpStatus.OK);
    }

    /**
     * @Description: 根据地址记录ID，取得该笔地址信息详情
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/userAddressDetail", method = {RequestMethod.POST})
    public ResponseEntity userAddressDetail(@RequestBody IdForm form) throws Exception {
        PubUserAddress pal = userAddressService.getUserAddress(form.id);
        if (pal == null)
            return new ResponseEntity(new RestResponseEntity(113, "地址不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("id", pal.getId());
        json.put("receiver", pal.getReceiver());
        json.put("mobile", pal.getMobile());
        json.put("provinceId", pal.getProvince());
        json.put("provinceName", pal.getProvinceName());
        json.put("cityId", pal.getCity());
        json.put("cityName", pal.getCityName());
        json.put("zoneId", pal.getZone());
        json.put("zoneName", pal.getZoneName());
        json.put("address", pal.getAddress());
        json.put("Default", pal.getDefault());
        return new ResponseEntity(new RestResponseEntity(100,"成功",json), HttpStatus.OK);
    }

    /**
     * @Description: 会员收件地址删除
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/removeUserAddress", method = {RequestMethod.POST})
    public ResponseEntity removeUserAddress(@RequestBody IdForm form) throws Exception {
        userAddressService.removeUserAddress(form.id);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 会员收件地址编辑保存
     * @Author: Steven.Xiao
     * @Date: 2017/10/26
     */
    @RequestMapping(value = "/updateUserAddress", method = {RequestMethod.POST})
    public ResponseEntity updateUserAddress(@RequestBody PubUserAddressForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(114, "会员地址id不能为空", null), HttpStatus.OK);
        userAddressService.updateUserAddress(form, CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }

    /**
     * @Description: 设置为默认地址
     * @Author: Steven.Xiao
     * @Date: 2017/11/21
     */
    @RequestMapping(value = "/setDefaultAddress", method = {RequestMethod.POST})
    public ResponseEntity setDefaultAddress(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(114, "会员地址id不能为空", null), HttpStatus.OK);
        userAddressService.setDefaultAddress(form.id, CurrentUser.get().userId);
        return new ResponseEntity(new RestResponseEntity(100,"成功",null), HttpStatus.OK);
    }
}
