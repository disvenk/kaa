package com.xxx.user.controller;

import com.xxx.core.response.RestResponseEntity;
import com.xxx.model.business.SysDict;
import com.xxx.user.cache.VerifyCache;
import com.xxx.user.form.VerifyCodeForm;
import com.xxx.user.notice.SmsUtils;
import com.xxx.user.service.VerifyService;
import com.xxx.utils.JavaValidate;
import com.xxx.utils.RandomUtils;
import com.xxx.utils.date.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 验证管理
 */
@RestController
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private VerifyService verifyService;

    /**
     * 获取手机验证码
     * @param request
     * @param verifyCodeForm
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/verificationCode", method = {RequestMethod.POST})
    public ResponseEntity verificationCode(HttpServletRequest request, @RequestBody VerifyCodeForm verifyCodeForm) throws Exception {
        if (verifyCodeForm == null || StringUtils.isBlank(verifyCodeForm.phonenumber))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(verifyCodeForm.phonenumber))
            return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
        Date vtime = VerifyCache.getVerificationCodeTime(verifyCodeForm.phonenumber);
        if (vtime != null && DateTimeUtil.getTimeInterval(vtime, new Date()) / 1000d <= 60)
            return new ResponseEntity(new RestResponseEntity(130, "操作太频繁，请于1分钟后重试", null), HttpStatus.OK);
        String validateCode = RandomUtils.randomFixedLength(4);
        VerifyCache.putVerificationCode(verifyCodeForm.phonenumber, validateCode);
        String result = SmsUtils.sendResetPasswordValidateCode(verifyCodeForm.phonenumber, validateCode);
        if (result.equals("SUCCESS")) {
            return new ResponseEntity(new RestResponseEntity(100, "发送成功", null), HttpStatus.OK);
        } else {
            return new ResponseEntity(new RestResponseEntity(140, "短信发送频率较高，请于24小时后再试", null), HttpStatus.OK);
        }
    }

    /**
     * @Description: 校验验证码是否正确
     * @Author: Chen.zm
     * @Date: 2017/11/13 0013
     */
    @RequestMapping(value = "/checkVerificationCode", method = {RequestMethod.POST})
    public ResponseEntity checkVerificationCode(@RequestBody VerifyCodeForm form) throws Exception {
        if (form == null || StringUtils.isBlank(form.phonenumber))
            return new ResponseEntity(new RestResponseEntity(110, "手机号不能为空", null), HttpStatus.OK);
        if (!JavaValidate.isMobileNO(form.phonenumber))
            return new ResponseEntity(new RestResponseEntity(120, "手机号格式不正确", null), HttpStatus.OK);
        verifyService.checkVerificationCode(form.phonenumber, form.verificationCode);
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }
    
    /**
     * @Description:用于销售平台用户注册成功时的短信通知
     * @Author: hanchao
     * @Date: 2017/12/19 0019
     */
    @RequestMapping(value = "/sendMessage", method = {RequestMethod.POST})
    public ResponseEntity sendMessage(@RequestBody VerifyCodeForm form) throws Exception {
        String content1 = "销售平台有新的采购订单"+""+"，请及时确认分配！";
        String content2 = "销售平台有新的客户"+form.name+"入驻，请及时确认审核！";
        String content3 = "供应商平台有新的供应商"+form.name+"入驻，请及时确认审核！";
        String content4 = "尊敬的会员！恭喜您！您刚才提交的入驻审核已通过，欢迎加入。现可点击s.heyizhizao.com前往合一智造门店管理，打造您的专属云店铺。";
//        String content5 = "尊敬的会员！恭喜您！您刚才提交的入驻审核已通过，欢迎加入。现可点击f.heyizhizao.com前往合一智造生产智能云系统。";
        String content5 = "尊敬的会员！恭喜您！您刚才提交的入驻审核已通过，欢迎加入！现可点击f.heyizhizao.com前往合一智造生产智能云系统。";
        SysDict sd = verifyService.get2(SysDict.class,"keyName","PHONE_NUMBER");
        if(form.status != null){
            switch (form.status){
                case 1:SmsUtils.sendMessage(sd.getKeyValue(),content1);break;//下单成功平台短信提示
                case 2:SmsUtils.sendMessage(sd.getKeyValue(),content2);break;//门店入驻平台短信提示
                case 3:SmsUtils.sendMessage(sd.getKeyValue(),content3);break;//供应商入驻平台短信提示
                case 4:SmsUtils.sendMessage(form.phonenumber,content4);break;//门店入驻用户短信提示
                case 5:SmsUtils.sendMessage(form.phonenumber,content5);break;//供应商入驻用户短信提示
            }
        }
        return new ResponseEntity(new RestResponseEntity(100, "成功", null), HttpStatus.OK);
    }

}
