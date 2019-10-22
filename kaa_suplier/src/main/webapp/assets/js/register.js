
//获取验证码
var waitTime=60;
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
                var time = setInterval(function () {
                    waitTime--;
                    $('#verifyCode').text(waitTime + '秒');
                    if(waitTime < 1){
                        clearInterval(time);
                        waitTime = 60;
                        $('#verifyCode').text('获取验证码');
                    }
                }, 1000)
            } else {
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })
})
//注册
    //不点击协议处理
  $('#checkbox').bind('change', function () {
      if($("input[type='checkbox']").is(':checked')){
          $('.login').css('background-color', '#ffffff');
          $('.login').css('color', 'rgb(61,134,211)');
          $("#nextStep").click(function () {
              //不点击协议处理
              if ($("#password").val() != $("#password2").val()) {
                  layer.msg("密码不一致");
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
                  headers: {
                      'Accept': 'application/json; charset=utf-8',
                      'Authorization': 'Basic ' + sessionStorage.getItem('token')
                  },
                  data: JSON.stringify(data),
                  success: function (res) {
                      if (res.stateCode == 100) {
                          sessionStorage.setItem("token",res.data.authToken);
                          location.href='../suplierHome/joinHtml'
                      } else {
                          layer.msg(res.message);
                      }
                  },
                  error: function (err) {
                      layer.msg(err.message)
                  }
              })
          })
      } else{
          $('.login').css('background-color', 'rgb(171,171,171)');
          $('.login').css('color', '#ffffff');
          $("#nextStep").unbind('click');
      }
  });