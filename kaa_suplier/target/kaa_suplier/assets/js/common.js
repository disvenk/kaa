$(function () {
    var url = window.location.href.split('#')[0];
    var encodeURI = encodeURIComponent(url);
    $.ajax({
        type: 'GET',
        url: '../suplierHome/getshareinfo?url='+encodeURI,
        dataType: 'json',
        async: false,
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            'Authorization': 'Basic '
        },
        success: function(res){
            wx.config({
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端layer.msg出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: res.data.appid, // 必填，公众号的唯一标识
                timestamp: res.data.timestamp, // 必填，生成签名的时间戳
                nonceStr: res.data.noncestr, // 必填，生成签名的随机串
                signature: res.data.signature,// 必填，签名
                jsApiList: ['chooseImage','uploadImage','downloadImage', 'getLocalImgData'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
            });
            wx.ready(function(){
                layer.msg("wx.config success.");

            });
            wx.error(function(res){
                layer.msg("wx.config failed.");
                //layer.msg(res);
                // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，
                // 也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
            });
        },
        error:function (err) {
            layer.msg('配置错误');
        }
    })
})
