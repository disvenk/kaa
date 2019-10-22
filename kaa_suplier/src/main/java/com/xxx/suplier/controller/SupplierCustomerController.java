package com.xxx.suplier.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxx.core.query.PageList;
import com.xxx.core.query.PageQuery;
import com.xxx.core.response.PageResponseEntity;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SupCustomer;
import com.xxx.model.business.SupCustomerAddress;
import com.xxx.suplier.form.*;
import com.xxx.suplier.service.SupplierCustomerService;
import com.xxx.user.security.CurrentUser;
import com.xxx.utils.DateTimeUtils;
import com.xxx.utils.OSSClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@Controller
@RequestMapping("/supplierCustomer")
public class SupplierCustomerController {

    @Autowired
    private SupplierCustomerService supplierCustomerService;

    /**
     * @Description:本地工单管理里面的客户模糊查询
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/selectSupplierCustomer", method = {RequestMethod.POST})
    public ResponseEntity selectSupplierCustomer(@RequestBody SupplierCustomerEditForm form) throws Exception {
        List<SupCustomer> supCustomers = supplierCustomerService.selectSupplierCustomer(CurrentUser.get().getSuplierId(), form.customer);
        JSONArray jsonArray = new JSONArray();
        for (SupCustomer sup : supCustomers) {
            JSONObject json = new JSONObject();
            json.put("customer", sup.getCustomer());
            json.put("customerPhone", sup.getCustomerPhone());
            json.put("id", sup.getId());
            jsonArray.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);
    }

    /**
     * @Description:客户管理地址新增
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/addSupplierCustomerAddress", method = {RequestMethod.POST})
    public ResponseEntity addSupplierCustomerAddress(@RequestBody SupplierCustomerAddressEditForm form) throws Exception {
        supplierCustomerService.addSupplierCustomerAddress(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:客户管理地址保存
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/saveSupplierCustomerAddress", method = {RequestMethod.POST})
    public ResponseEntity saveSupplierCustomerAddress(@RequestBody SupplierCustomerAddressEditForm form) throws Exception {
        supplierCustomerService.saveSupplierCustomerAddress(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:客户管理地址详情
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/supplierCustomerAddressDetail", method = {RequestMethod.POST})
    public ResponseEntity supplierCustomerAddressDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不能为空", null), HttpStatus.OK);
        SupCustomerAddress supCustomerAddress = supplierCustomerService.get2(SupCustomerAddress.class, "id", form.id);
        if (supCustomerAddress == null)
            return new ResponseEntity(new RestResponseEntity(120, "客户地址信息不存在", null), HttpStatus.OK);
        JSONObject json = new JSONObject();
        json.put("receiver", supCustomerAddress.getReceiver());
        json.put("mobile", supCustomerAddress.getMobile());
        json.put("provinceId", supCustomerAddress.getProvince());
        json.put("provinceName", supCustomerAddress.getProvinceName());
        json.put("cityId", supCustomerAddress.getCity());
        json.put("cityName", supCustomerAddress.getCityName());
        json.put("zone", supCustomerAddress.getZone());
        json.put("zoneName", supCustomerAddress.getZoneName());
        json.put("address", supCustomerAddress.getAddress());
        //去除null值
        for (String key : json.keySet()) {
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description:工厂端地址列表删除
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/removeSupplierCustomerAddress", method = {RequestMethod.POST})
    public ResponseEntity removeSupplierCustomerAddress(@RequestBody IdForm form) throws Exception {
        supplierCustomerService.removeSupplierCustomerAddress(form.id);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:客户管理地址列表
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/supplierCustomerAddressList", method = {RequestMethod.POST})
    public ResponseEntity supplierCustomerAddressList(@RequestBody IdForm form) throws Exception {
        JSONArray jsonArray = new JSONArray();
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不能为空", jsonArray), HttpStatus.OK);
        List<SupCustomerAddress> supCustomerAddressList = supplierCustomerService.getSupplierCustomerAddressList(form.id);
        if (supCustomerAddressList.size() == 0) {
            return new ResponseEntity(new RestResponseEntity(120, "客户地址信息不存在", null), HttpStatus.OK);
        }
        for (SupCustomerAddress sup : supCustomerAddressList) {
            JSONObject json = new JSONObject();
            json.put("id", sup.getId());
            json.put("receiver", sup.getReceiver());
            json.put("mobile", sup.getMobile());
            json.put("province", sup.getProvince());
            json.put("provinceName", sup.getProvinceName());
            json.put("city", sup.getCity());
            json.put("cityName", sup.getCityName());
            json.put("zone", sup.getZone());
            json.put("zoneName", sup.getZoneName());
            json.put("address1", sup.getAddress());
            json.put("address", (sup.getProvinceName() == null ? "" : sup.getProvinceName()) +
                    (sup.getCityName() == null ? "" : sup.getCityName()) + (sup.getZoneName() == null ? "" : sup.getZoneName()) +
                    (sup.getAddress() == null ? "" : sup.getAddress()));
            //去除null值
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", jsonArray), HttpStatus.OK);
    }


    /**
     * @Description:工厂端客户信息删除
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/removeSupplierCustomer", method = {RequestMethod.POST})
    public ResponseEntity removeSupplierCustomer(@RequestBody SupplierCustomerEditForm form) throws Exception {
        supplierCustomerService.removeSupplierCustomer(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:新增工厂端客户管理信息
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/addSupplierCustomer", method = {RequestMethod.POST})
    public ResponseEntity addSupplierCustomer(@RequestBody SupplierCustomerEditForm form) throws Exception {
        if (StringUtils.isBlank(form.customer)) {
            return new ResponseEntity(new RestResponseEntity(110, "客户名称不能为空", null), HttpStatus.OK);
        }
        supplierCustomerService.addSupplierCustomer(form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:保存工厂端客户管理信息
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/saveSupplierCustomerEdit", method = {RequestMethod.POST})
    public ResponseEntity saveSupplierCustomerEdit(@RequestBody SupplierCustomerEditForm form) throws Exception {
        if (StringUtils.isBlank(form.customer)) {
            return new ResponseEntity(new RestResponseEntity(110, "客户名称不能为空", null), HttpStatus.OK);
        }
        supplierCustomerService.saveSupplierCustomerEdit(CurrentUser.get().getSuplierId(), form);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

    /**
     * @Description:工厂端客户管理详情
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/supplierCustomerDetail", method = {RequestMethod.POST})
    public ResponseEntity supplierCustomerDetail(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户id不能为空", null), HttpStatus.OK);
        SupCustomer supCustomer = supplierCustomerService.get2(SupCustomer.class, "id", form.id);
        if (supCustomer == null)
            return new ResponseEntity(new RestResponseEntity(120, "客户信息不存在", null), HttpStatus.OK);

        JSONObject json = new JSONObject();
        json.put("customer", supCustomer.getCustomer());
        json.put("customerInit", supCustomer.getCustomerInit());
        json.put("customerPhone", supCustomer.getCustomerPhone());
        json.put("key", supCustomer.getIcon());
        json.put("icon", OSSClientUtil.getObjectUrl(supCustomer.getIcon()));
        json.put("remark", supCustomer.getRemarks());
        //去除null值
        for (String key : json.keySet()) {
            if (json.get(key) == null) json.put(key, "");
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description:工厂端客户维护列表
     * @Author: hanchao
     * @Date: 2017/12/20 0020
     */
    @RequestMapping(value = "/supplierCustomerList", method = {RequestMethod.POST})
    public ResponseEntity supplierCustomerList(@RequestBody SupplierCustomerListForm form) throws Exception {
        if (form.pageNum == null)
            return new ResponseEntity(new RestResponseEntity(110, "页码不能为空", null), HttpStatus.OK);
        PageQuery pageQuery = new PageQuery(form.pageNum);
        PageList<SupCustomer> supCustomerList = supplierCustomerService.findSupplierCustomerList(pageQuery, form.customer, form.customerInit, form.customerPhone, CurrentUser.get().getSuplierId());
        JSONArray jsonArray = new JSONArray();
        for (SupCustomer supCustomer : supCustomerList) {
            JSONObject json = new JSONObject();
            json.put("id", supCustomer.getId());
            json.put("customer", supCustomer.getCustomer());
            json.put("customerInit", supCustomer.getCustomerInit());
            json.put("customerPhone", supCustomer.getCustomerPhone());
            json.put("key", supCustomer.getIcon());
            json.put("icon", OSSClientUtil.getObjectUrl(supCustomer.getIcon()));
            json.put("updateDate", DateTimeUtils.parseStr(supCustomer.getUpdateDate()));
            json.put("remark", supCustomer.getRemarks());
            //去除null值
            for (String key : json.keySet()) {
                if (json.get(key) == null) json.put(key, "");
            }
            jsonArray.add(json);
        }
        return new ResponseEntity(new PageResponseEntity(100, "成功", jsonArray, pageQuery.page, pageQuery.limit, supCustomerList.total), HttpStatus.OK);
    }

    /**
     * @Description: 新增一个客户及新增相应的客户地址
     * @Author: Steven.Xiao
     * @Date: 2018/1/11
     */
    @RequestMapping(value = "/addCustomerAndAddress", method = {RequestMethod.POST})
    public ResponseEntity addCustomerAndAddress(@RequestBody AddCustomerAndAddress form) throws Exception {
        if (form.customerName == null || form.customerName == "")
            return new ResponseEntity(new RestResponseEntity(110, "客户名称不能为空", null), HttpStatus.OK);
        if (!supplierCustomerService.notExistCustomer(CurrentUser.get().getSuplierId(), form.customerName))
            return new ResponseEntity(new RestResponseEntity(120, "客户名称已存在,不能重复新增", null), HttpStatus.OK);

        SupCustomer supCustomer = supplierCustomerService.addCustomerAndAddress(CurrentUser.get().getSuplierId(), form);
        if (supCustomer == null) {
            return new ResponseEntity(new RestResponseEntity(130, "新增客户不成功", null), HttpStatus.OK);
        }
        JSONObject json = new JSONObject();
        json.put("id", supCustomer.getId());
        json.put("name", supCustomer.getCustomer());

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

    /**
     * @Description: 用一句话描述作用
     * @Author: Steven.Xiao
     * @Date: 2018/1/13
     */
    @RequestMapping(value = "/getCustomer", method = {RequestMethod.POST})
    public ResponseEntity getCustomer(@RequestBody IdForm form) throws Exception {
        if (form.id == null)
            return new ResponseEntity(new RestResponseEntity(110, "客户名称不能为空", null), HttpStatus.OK);
        SupCustomer supCustomer = supplierCustomerService.getCustomer(form.id);
        JSONObject json = new JSONObject();
        //联系人
        json.put("contact", supCustomer.getCustomerInit());
        //联系方式
        json.put("contactTel", supCustomer.getCustomerPhone());

        return new ResponseEntity(new RestResponseEntity(100, "成功", json), HttpStatus.OK);
    }

}
