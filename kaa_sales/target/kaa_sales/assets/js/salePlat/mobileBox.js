var nums = 0;
var ids = [];
var token = sessionStorage.getItem('token');
//图片列表
$.ajax({
    type: 'POST',
    url:  '../boxHome/mobileBoxProductList',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    headers: {
        'Accept': 'application/json; charset=utf-8'
    },
    data: JSON.stringify({pageNum: 1}),
    success: function (res) {
        if(res.stateCode == 100){
            if(res.data.length == 0){
            }else{
                for (var i = 0; i < res.data.length; i++) {
                    var list = '';
                    list += '  <div class="product-list-item">'+
                        '<div class="product-info-wrap">'+
                        '<img onclick="detail('+res.data[i].id+')" class="product-img" src="'+res.data[i].href+'" alt="">'+
                        '<div class="product-name">'+res.data[i].name+'</div>'+
                        '</div><div class="pic-operate" onclick="select($(this))" value="'+res.data[i].id+'">放入盒子</div> </div>';
                    $('.product-list-items').append(list);
                }
            }
        }else{
            layer.msg(res.message)
        }
    },
    error: function (err) {
        layer.msg(err.message)
    }
});
function select(it) {
    if(it.html() == '移出盒子'){
        it.css('background-color', '#2b3646');
        it.css('color', '#bd946e');
        it.removeClass('addBox');
        it.html('放入盒子');
        nums--;
    }else{
        if(nums > 2){
            layer.msg('最多选择3个商品');
        }else{
            $.ajax({
                type: 'POST',
                url: '../boxHome/boxOrderCount',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                headers: {
                    'Accept': 'application/json; charset=utf-8'
                },
                data: JSON.stringify({id: it.attr('value')}),
                success: function (res) {
                    if(res.stateCode == 100){
                        it.css('background-color', '#999');
                        it.css('color', '#fff');
                        it.addClass('addBox');
                        it.html('移出盒子');
                        nums++;
                    }else {
                        layer.msg(res.message);
                    }
                },
                error: function (err) {
                    layer.msg(err.message);
                }
            });
        }
    }
}
function detail(id) {
    location.href='../boxMobile/mobileDetailHtml?id='+id+'';
}
$('.box-discount').click(function () {
    //限购盒子数量
    $.ajax({
        type: 'POST',
        url:  '../boxHome/boxCount',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8'
        },
        success: function (res) {
            if(res.stateCode == 100){
                if(res.data.count > 0){
                    if(nums == 0){
                        layer.msg('请选择商品');
                    }else{
                        if(!token){
                            layer.open({
                                title: ['手机号绑定']
                                ,skin: 'demo-class'
                                ,anim: 'up'
                                ,btn: ['确认']
                                ,yes:function () {
                                    //短信验证
                                    $.ajax({
                                        type: 'POST',
                                        url:  '../account/verifyLogin',
                                        contentType: 'application/json; charset=utf-8',
                                        dataType: 'json',
                                        headers: {
                                            'Accept': 'application/json; charset=utf-8'
                                        },
                                        data: JSON.stringify({phoneNumber: $(".mobile-phone-num").val(), verificationCode: $(".mobile-phone-code").val(), loginType: 1}),
                                        success: function (res) {
                                            if(res.stateCode == 100){
                                                sessionStorage.setItem('token', res.data.authToken);
                                                $.ajax({
                                                    type: 'POST',
                                                    url:  '../boxHome/boxInfo',
                                                    contentType: 'application/json; charset=utf-8',
                                                    dataType: 'json',
                                                    headers: {
                                                        'Accept': 'application/json; charset=utf-8',
                                                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                                    },
                                                    success: function (res) {
                                                        if(res.stateCode == 100){
                                                            var ids = [];
                                                            for (var i = 0; i < $('.addBox').length; i++) {
                                                                ids.push($('.addBox').eq(i).attr('value'));
                                                            }
                                                            if(res.data.boxCheck){
                                                                location.href = '../boxMobile/mobileOrderConfirmHtml?boxIds='+ids;
                                                            }else{
                                                                location.href='../boxMobile/mobileBuyHtml?boxIds='+ids;
                                                            }
                                                        }else{
                                                            layer.msg(res.message)
                                                        }
                                                    },
                                                    error: function (err) {
                                                        layer.msg(err.message)
                                                    }
                                                });
                                            }else{
                                                layer.msg(res.message)
                                            }
                                        },
                                        error: function (err) {
                                            layer.msg(err.message)
                                        }
                                    });
                                }
                                ,closeBtn: 0
                                ,area: ['320px', '285px']
                                ,content: '<div class="mobile-phone bg1">\n' +
                                '        <input class="mobile-phone-num" type="tel" placeholder="请输入手机号码">\n' +
                                '    </div>\n' +
                                '    <div class="mobile-phone bg2">\n' +
                                '        <input class="mobile-phone-code" type="number" placeholder="请输入验证码">\n' +
                                '        <span class="send-code" onclick="send()">发送验证码</span>\n' +
                                '    </div>'
                            });
                        }else{
                            $.ajax({
                                type: 'POST',
                                url:  '../boxHome/boxInfo',
                                contentType: 'application/json; charset=utf-8',
                                dataType: 'json',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                success: function (res) {
                                    if(res.stateCode == 100){
                                        var ids = [];
                                        for (var i = 0; i < $('.addBox').length; i++) {
                                            ids.push($('.addBox').eq(i).attr('value'));
                                        }
                                        if(res.data.boxCheck){
                                            location.href = '../boxMobile/mobileOrderConfirmHtml?boxIds='+ids;
                                        }else{
                                            location.href='../boxMobile/mobileBuyHtml?boxIds='+ids;
                                        }
                                    }else{
                                        layer.msg(res.message)
                                    }
                                },
                                error: function (err) {
                                    layer.msg(err.message)
                                }
                            });
                        }
                    }
                }else{
                    layer.msg('已抢完，请下次再来');
                }
            }else{
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    });
});
//发送验证码
function send() {
    var waitTime = 60;
        $.ajax({
            type: 'POST',
            url: '../verify/verificationCode',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({phonenumber: $(".mobile-phone-num").val()}),
            success: function (res) {
                if (res.stateCode == 100) {
                    var time = setInterval(function () {
                        waitTime--;
                        $('.send-code').text(waitTime + '秒');
                        if(waitTime < 1){
                            clearInterval(time);
                            waitTime = 60;
                            $('.send-code').text('获取验证码');
                        }
                    }, 1000)
                } else {
                    // layer.msg(res.message);
                }
            },
            error: function (err) {
                // layer.msg(err.message)
            }
        })
}