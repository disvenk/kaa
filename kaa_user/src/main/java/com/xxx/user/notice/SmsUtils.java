package com.xxx.user.notice;

/**
 * Created by wanghua on 17/2/24.
 */
public class SmsUtils {
    /**
     * 发送用于重置密码的验证码
     */
    public static String sendResetPasswordValidateCode(String phone, String validateCode) {
        String content = "您的验证码是："+ validateCode
                +"。请不要把验证码泄露给其他人。";
        String result= com.xxx.utils.SmsUtils.send(phone, content);
        return result;
    }

    /**
     * 发送用于注册通知短信
     */
    public static String sendRegisterMessage(String phone, String userCode, String password) {
        String content = "尊敬的会员！恭喜您！您已通过合一盒子成功注册。你的账号："+ userCode
                +"；密码："+ password +"，您可前往s.heyizhizao.com查看商品的物流信息。";
        String result= com.xxx.utils.SmsUtils.send(phone, content);
        return result;
    }


    /**
     * 用于各平台短信通知
     */
    public static String sendMessage(String phone,String content) {
        String result = com.xxx.utils.SmsUtils.send(phone, content);
        return result;
    }



    public static void main(String[] args){
        System.out.println(sendRegisterMessage("15737927872", "233", "6666"));;
    }
}
