window.sessionStorage.removeItem('token');
//获取验证码
var waitTime=60;
$("#getCode").click(function () {
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
                layer.alert("发送成功", {icon: 1});
                var time = setInterval(function () {
                    waitTime--;
                    $('#getCode').text(waitTime + '秒');
                    if(waitTime < 1){
                        clearInterval(time);
                        waitTime = 60;
                        $('#getCode').text('获取验证码');
                    }
                }, 1000)
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
});
//协议
$('#reader').click(function () {
  $('#myModal').modal('show');
})
//注册
//不点击协议处理
$('#checkbox').bind('change', function () {
    if($("input[type='checkbox']").is(':checked')){
        $('.login').css('background-color', 'rgb(46,140,251)');
        $(".login").click(function () {
            //不点击协议处理
            if ($("#password").val() != $("#surePassword").val()) {
                layer.alert("密码不一致", {icon: 0});
                return
            }
            data = {
                phoneNumber: $("#phone").val(),
                verificationCode: $("#code").val(),
                password: $("#password").val(),
                loginType:3
            }
            $.ajax({
                type: 'POST',
                url: '../account/register',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data),
                success: function (res) {
                    if (res.stateCode == 100) {
                        sessionStorage.setItem("token",res.data.authToken);
                        location.href='../homePage/joinInHtml';
                    } else {
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.alert(err.message, {icon: 0})
                }
            })
        })
    } else{
        $('.login').css('background-color', 'rgb(189,189,189)');
        $("#nextStep").unbind('click');
    }
});
function keyLogin(){
    if (event.keyCode==13)  //回车键的键值为13
        $('.login').click(); //调用登录按钮的登录事件
}
$('#container > .logo img').click(function () {
    location.href = '../homePage/loginHtml';
});