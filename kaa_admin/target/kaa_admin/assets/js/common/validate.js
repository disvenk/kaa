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

// 验证手机号码
function checkMobile() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('mobile').value;
    if(!(pattern.test(phone))){
        $('.error-mobile').show();
        return false;
    } else {
        $('.error-mobile').hide();
        return false;
    }
}

// 验证用户名

function checkUerName() {
    var pattern = /^.{0,20}$/;
    var userName=$('.check-name').val();
    if(!(pattern.test(userName))){
        $('.error-user').show();
        return false;
    } else {
        $('.error-user').hide();
        return false;
    }
}

// 验证供应商名、商品名称名

function checkSupplierName() {
    var pattern = /^.{0,100}$/;
    var userName=$('.check-supplier').val();
    if(!(pattern.test(userName))){
        $('.error-supplier').show();
        return false;
    } else {
        $('.error-supplier').hide();
        return false;
    }
}

// 验证品牌名称

function checkBrandName() {
    var pattern = /^.{0,30}$/;
    var userName=$('.check-brand').val();
    if(!(pattern.test(userName))){
        $('.error-brand').show();
        return false;
    } else {
        $('.error-brand').hide();
        return false;
    }
}