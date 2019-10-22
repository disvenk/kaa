package com.xxx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.xxx.core.response.RestResponseEntity;
import com.xxx.user.form.ExpressForm;
import com.xxx.utils.KuaiDi100;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 快递
 */
@RestController
@RequestMapping("/express")
public class ExpressController {

    /**
     * @Description: 获取快递信息
     * @Author: Chen.zm
     * @Date: 2017/11/9 0009
     */
    @RequestMapping(value = "/searchExpress", method = {RequestMethod.POST})
    public ResponseEntity searchExpress(@RequestBody ExpressForm form) throws Exception {
        if (StringUtils.isBlank(form.com))
            return new ResponseEntity(new RestResponseEntity(110, "快递公司不能为空", null), HttpStatus.OK);
        if (StringUtils.isBlank(form.nu))
            return new ResponseEntity(new RestResponseEntity(120, "快递单号不能为空", null), HttpStatus.OK);
        String result = KuaiDi100.searchkuaiDiInfo(form.com, form.nu);
        JSONObject data = new JSONObject();
        data.put("result", result);
        return new ResponseEntity(new RestResponseEntity(100,"成功", data), HttpStatus.OK);
    }


}
