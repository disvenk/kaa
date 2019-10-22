window.sessionStorage.removeItem('token');
//获取验证码
var waitTime = 60;
$("#findVerifyCode").click(function () {
    $.ajax({
        type: 'POST',
        url: '../verify/verificationCode',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({phonenumber: $("#phone").val()}),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.alert("发送成功",{icon:1});
                var time = setInterval(function () {
                    waitTime--;
                    $('#findVerifyCode').text(waitTime + '秒');
                    if(waitTime < 1){
                        clearInterval(time);
                        waitTime = 60;
                        $('#findVerifyCode').text('获取验证码');
                    }
                }, 1000)
            } else {
                layer.alert(res.message,{icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    })
})

//注册
//不点击协议处理
$('#checkbox').bind('change', function () {
    if($("input[type='checkbox']").is(':checked')) {
        $('#nextStep').css('background-color', '#3c9ed8');
        $("#nextStep").click(function () {
            if ($("#password").val() != $("#password2").val()) {
                layer.alert("密码不一致",{icon:0});
                return
            }
            data = {
                userCode: $("#userCode").val(),
                phoneNumber: $("#phone").val(),
                verificationCode: $("#verifyCode").val(),
                password: $("#password").val(),
                loginType: 1, //门店平台
                userType:$('#select').find('option:selected').val()
            }
            $.ajax({
                type: 'POST',
                url: '../account/register',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(data),
                success: function (res) {
                    if (res.stateCode == 100) {
                        sessionStorage.setItem("token",res.data.authToken);
                        location.href='../salesHome/regSuccessHtml?phonenumber='+data.phoneNumber;
                    } else {
                        layer.alert(res.message,{icon:0});
                    }
                },
                error: function (err) {
                    layer.alert(err.message,{icon:0})
                }
            })
        });
    }else{
        $("#nextStep").unbind('click');
        $('#nextStep').css('background-color', 'grey');
    }
    });