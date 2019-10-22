$(function () {
    var  url = window.location.href.split('#')[0];
    var  encodeURI = encodeURIComponent(url);
    var title = '合一盒子|一元抢购 万元豪礼';
    var desc = '合一盒子是合一智造平台推出的针对销售商的企业会员订阅服务';
    var link = 'http://s.heyizhizao.com/boxMobile/weixinAuthorizationHtml';
    var imgUrl = 'http://kaa-qat.oss-cn-hangzhou.aliyuncs.com/box-join.jpg';
    $.ajax({
        type: 'GET',
        url: '../boxMobile/getshareinfo?url='+encodeURI,
        dataType: 'json',
        async: false,
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic "
        },
        success: function(res){
            if(res.stateCode == 100){
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: res.data.appid, // 必填，公众号的唯一标识
                    timestamp: res.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: res.data.noncestr, // 必填，生成签名的随机串
                    signature: res.data.signature,// 必填，签名，见附录1
                    jsApiList: ['onMenuShareAppMessage','onMenuShareTimeline'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
                wx.ready(function () {
                    wx.onMenuShareAppMessage({
                        title: title, // 分享标题
                        desc: desc, // 分享描述
                        link:  link, // 分享链接
                        imgUrl: imgUrl, // 分享图标
                        type: '', // 分享类型,music、video或link，不填默认为link
                        dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
                        success: function () {
                            // 用户确认分享后执行的回调函数
                        },
                        cancel: function () {
                            // 用户取消分享后执行的回调函数
                        }
                    });
                    // 朋友圈
                    wx.onMenuShareTimeline({
                        title: title, // 分享标题
                        link:  link,
                        desc: desc, // 分享描述// 分享链接
                        imgUrl: imgUrl, // 分享图标
                        success: function () {
                            // 用户确认分享后执行的回调函数
                        },
                        cancel: function () {
                            // 用户取消分享后执行的回调函数
                        }
                    });
                });
                wx.error(function(res){
                    layer.msg('配置错误')
                })
            }else{
                layer.msg(res.message);
            }
        },
        error:function (err) {
            layer.msg(err.message);
        }
    });
})