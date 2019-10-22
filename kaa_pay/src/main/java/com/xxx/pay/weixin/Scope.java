package com.xxx.pay.weixin;

/**
 * Created by wangh on 2016/7/1.
 */
public enum  Scope {
    snsapi_base, snsapi_userinfo

     /* snsapi_base与snsapi_userinfo属于微信网页授权获取用户信息的两种作用域

            snsapi_base只能获取access_token和openID

    snsapi_userinfo可以获取更详细的用户资料，比如头像、昵称、性别等


    首先，这里的access_token与基础access_token(比如自定义菜单用到的)是不一样的。两者区别如下：

    网页授权的access_token在每次获取openID时一起更新，在接口调用频次限制中为“无上限”

    基础access_token一般限制为2000次/日，需要自己保存起来并定时更新


    其次，当作用域为snsapi_userinfo时，根据官方文档中的说明，需要用户进行点击授权，但是我发现，有一个办法可以静默获得而不需要点击授权。方法如下：

            1、通过snsapi_base，同时获得access_token和openid

2、把这里的access_token和openid用于https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN 接口中

            3、请求成功之后，就静默获取到用户详细资料了。

    以上方法的前提是同一个公众号内，用户关注了。如果是借用其他的公众号的，是需要用户点击授权的。



            1、第一步，获取code，https://open.weixin.qq.com/connect/oauth2/authorize?appid='.APPID.'&redirect_uri='.urldecode(URL).'login.php&response_type=code&scope=snsapi_base&state=start#wechat_redirect


            2、第二步，获取openID,https://api.weixin.qq.com/sns/oauth2/access_token?appid='.APPID.'&secret='.APPSECRET.'&code='.CODE.'&grant_type=authorization_code


            3、第三步，结合openID和access_token获取用户详细信息，https://api.weixin.qq.com/cgi-bin/user/info?access_token='.$access_token.'&openid='.$openId.'&lang=zh_CN*/
}
