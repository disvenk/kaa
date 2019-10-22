// 验证手机号码
function checkTel() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('telephone').value;
    if(!(pattern.test(phone))){
        $('.error-phone').show();
        return false;
    } else {
        $('.error-phone').hide();
        return false;
    }
}

// 验证用户名

function checkUerName() {
    var pattern = /^.{0,20}$/;
    var userName=document.getElementById('storeName').value;
    if(!(pattern.test(userName))){
        $('.error-user').show();
        return false;
    } else {
        $('.error-user').hide();
        return false;
    }
}