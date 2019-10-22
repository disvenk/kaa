$('#toRegister').click(function () {
    location.href='../suplierHome/registerHtml';
});

$('#forgetPassword').click(function () {
    location.href='../suplierHome/forgetPasswordHtml';
})

$('.login').click(function () {
    var data = {
        usercode: $('#userName').val(),
        password: $('#password').val(),
        loginType: 4
    };
    $.ajax({
        type: 'POST',
        url: '../account/login',
        cache: false,
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                sessionStorage.setItem("token",res.data.authToken);
                //判断审核状态  跳转至不同页面
                if (res.data.userStatus == 0) {
                    location.href = '../suplierHome/workerHomeHtml';
                } else if (res.data.userStatus == 1) {
                    location.href='../suplierHome/joinHtml'
                } else if (res.data.userStatus == 2){
                    location.href='../suplierHome/auditHtml';
                }else if (res.data.userStatus == 3) {
                    location.href='../suplierHome/joinHtml'
                }
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })
})