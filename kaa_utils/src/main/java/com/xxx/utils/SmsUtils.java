package com.xxx.utils;

import com.xxx.utils.config.SmsConfigs;
import com.xxx.utils.contract.SimpleResult;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.StringReader;

/**
 * Created by Chen.zm on 17/11/8.
 */
public class SmsUtils {
    /**
     * @param receiverPhone 短信接收者的手机号
     * @param content        内容,请查阅互亿无线文档中心页面:"http://www.ihuyi.com/api/sms.html"
     * @return
     */
    public static String send(String receiverPhone, String content) {

        //******************************注释****************************************************************
        //*调用发送短信的接口发送短信                                                                      *
        //*参数顺序说明：                                                                                  *
        //**************************************************************************************************
        try {
            HttpClient client = new HttpClient();
            String url = "http://106.ihuyi.com/webservice/sms.php?method=Submit" ;
            String body = "account=" + SmsConfigs.getApiId()
                    + "&password=" + SmsConfigs.getApiKey()
                    + "&mobile=" + receiverPhone
                    + "&content=" + content;
            SimpleResult result = client.request(HttpClient.Method.POST, url, body, null);
            SAXReader saxreader = new SAXReader();
            Document doc = saxreader.read(new StringReader(result.getMessage()));
            String returnMsg = doc.selectSingleNode("/").getStringValue();
            String[] returnMsgs = returnMsg.split("\n");
            if (returnMsgs.length < 2) returnMsgs = new String[2];
            if ("2".equals(returnMsgs[1])) {
                return "SUCCESS";
            } else {
                //异常返回输出错误码和错误信息
                System.out.println("错误码=" +returnMsgs[1] + " 错误信息= " + returnMsgs[2]);
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public static void main(String[] args) {
         SmsUtils.send("17715580265", "您的验证码是：1234。请不要把验证码泄露给其他人。");
    }
}
