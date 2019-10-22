
//获取验证码
$("#verifyCode").click(function () {
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
                layer.msg("发送成功");
            } else {
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })
})

//找回密码
$("#nextStep").click(function () {
    if ($("#password").val() != $("#password2").val()) {
        layer.msg("密码不一致");
        return
    }
    data = {
        phonenumber: $("#phone").val(),
        verificationCode: $("#code").val(),
        password: $("#password").val(),
        loginType: 3
    }
    $.ajax({
        type: 'POST',
        url: '../account/resetPassword',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.msg("修改成功");
                location.href = "../suplierHome/loginHtml";
            } else {
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })
})