window.sessionStorage.removeItem('token');
window.sessionStorage.removeItem('loginType');
$('.forget > span').click(function () {
    location.href='../homePage/forgetPasswordHtml';
})

$('.login').click(function () {
    var data = {
        usercode: $('#account').val(),
        password: $('#password').val(),
        loginType: 3
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
                sessionStorage.setItem('loginType', res.data.loginType);
                //判断审核状态  跳转至不同页面
                if (res.data.userStatus == 0) {
                    location.href = '../suplierHome/listHtml';
                } else if (res.data.userStatus == 1) {
                    location.href='../homePage/joinInHtml'
                } else if (res.data.userStatus == 2){
                    location.href='../homePage/reviewHtml';
                }else if (res.data.userStatus == 3){
                    location.href='../homePage/reviewDefaultHtml';
                }
            }else{
                layer.alert(res.message,{icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    })
});
function keyLogin(){
    if (event.keyCode==13)  //回车键的键值为13
        $('.login').click(); //调用登录按钮的登录事件
}