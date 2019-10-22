var url = window.location.href;
var array = url.split('=');
var ids = array[1];
var id;
$.ajax({
    type: 'POST',
    url: '../boxHome/boxTypeList',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100){
            $('.pre-fee').html(res.data.needDeposit);
            for(var i = 0; i < res.data.typeList.length; i++){
                var length = '';
                length += ' <li class="vip-type-item" id="'+res.data.typeList[i].id+'" value="'+res.data.typeList[i].price+'" onclick="choose($(this), '+res.data.isOnePay+', '+res.data.needDeposit+')">'+res.data.typeList[i].name+'</li>';
                $('.vip-type-items').append(length);
            }
            if(!res.data.isOnePay){
                id = $('.vip-type-items li').eq(1).attr('id');
                $('.vip-type-items li').eq(0).css('color', '#999');
                $('.vip-type-items li').eq(1).addClass('active');
                $('.vip-fee').html($('.vip-type-items li').eq(1).attr('value') - 0);
                $('.price').html($('.vip-type-items li').eq(1).attr('value') - 0 + res.data.needDeposit);
            }else {
                id = $('.vip-type-items li').eq(0).attr('id');
                $('.vip-type-items li').eq(0).addClass('active');
                $('.vip-fee').html($('.vip-type-items li').eq(0).attr('value') - 0);
                $('.price').html($('.vip-type-items li').eq(0).attr('value') - 0 + res.data.needDeposit);
            }
        }else{
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});
function choose(it,is,price) {
    if(!is){
        if($('.vip-type-items li').eq(0).html() == it.html()){
            $('.vip-type-items li').eq(0).css('color', '#999');
            it.hover(function () {
                it.css('cursor', 'not-allowed');
            })
        }else{
            $('.vip-type-items li').removeClass('active');
            it.addClass('active');
            $('.vip-fee').html(it.attr('value'));
            $('.price').html(it.attr('value') - 0 + price);
            id = it.attr('id');
        }
    }else{
        $('.vip-type-items li').removeClass('active');
        it.addClass('active');
        $('.vip-fee').html(it.attr('value'));
        $('.price').html(it.attr('value') - 0 + price);
        id = it.attr('id');
    }
}
$('.order-btn').click(function () {
    $.ajax({
        type: 'POST',
        url: '../boxHome/saveBoxPayOrder',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
var data = {
    channel: 'WECHAT',
    orderId: res.data.orderId,
    orderType: 2,
    totalFee: ($('.price').html() - 0) * 100,
    productId: '',
    tradeType: 'JSAPI',
    outTradeNo: '',
    openid: sessionStorage.getItem('openId'),
    body: '合一盒子' + res.data.boxName + "-" + res.data.mobile,
    subject: '合一盒子' + res.data.boxName + "-" + res.data.mobile
  };
$.ajax({
    type: 'POST',
    url: '../pay/union_entrance',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify(data),
    success: function(res){
        if(res.stateCode == 100){
            function onBridgeReady(){
                WeixinJSBridge.invoke(
                    'getBrandWCPayRequest', {
                        "appId":res.data.appId,     //公众号名称，由商户传入
                        "timeStamp":res.data.timeStamp,       //时间戳，自1970年以来的秒数
                        "nonceStr":res.data.nonceStr, //随机串
                        "package":res.data.package,
                        "signType":"MD5",         //微信签名方式：
                        "paySign":res.data.paySign //微信签名
                    },
                    function(res){
                        if(res.err_msg == "get_brand_wcpay_request:ok" ) {
                            location.href='../boxMobile/mobileOrderConfirmHtml?boxIds='+ids;
                        }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
                    }
                );
            }
            onBridgeReady();
            if (typeof WeixinJSBridge == "undefined"){
                if( document.addEventListener ){
                    document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                }else if (document.attachEvent){
                    document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                }
            }else{
                onBridgeReady();
            }
        }else{
            layer.msg(res.message);
        }
    },
    error:function (err) {
        layer.msg(err.message);
         }
         })
      }else{
         layer.msg(res.message)
       }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })

});