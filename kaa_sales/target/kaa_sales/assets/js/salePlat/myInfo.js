
//图片转化
function convertImgToBase64(url, callback, outputFormat){
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function(){
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img,0,0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}

//本地上传图片
document.getElementById("upLoad1").addEventListener("change",function(e){
    var log = $('#img1 .img_container1 > div').length;
    if(log > 0){
        alert('最多只能上传1张图片')
    }else{
        var files =this.files;
        var img = new Image();
        var reader =new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload =function(e){
            var dx =(e.total/1024)/1024;
            if(dx>=2){
                alert("文件大小大于2M");
                return;
            }
            img.src =this.result;
            img.style.width = '138px';
            img.style.height ="93px";
            var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('#img1 .img_container1').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});

// //本地上传图片
// document.getElementById("upLoad2").addEventListener("change",function(e){
//     var log = $('#img2 .img_container2').length;
//     if(log > 4){
//         alert('最多只能上传5张图片')
//     }else{
//         var files =this.files;
//         var img = new Image();
//         var reader =new FileReader();
//         reader.readAsDataURL(files[0]);
//         reader.onload =function(e){
//             var dx =(e.total/1024)/1024;
//             if(dx>=2){
//                 alert("文件大小大于2M");
//                 return;
//             }
//             img.src =this.result;
//             img.style.width = '138px';
//             img.style.height ="93px";
//             var image = '<div class="img_container2"><img style="height: 102px;" src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
//             $('#img2').append(image);
//             $('.delete_img').click(function () {
//                 $(this).parent().remove();
//             })
//
//         }
//     }
// });



//获取验证码
$("#findVerifyCode").click(function () {
    $.ajax({
        type: 'POST',
        url: '../verify/verificationCode',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({phonenumber: $("#telephone").val()}),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.alert('发送成功！', {icon:0});
                var waitTime = 60;
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
                // alert(res.message);
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            layer.alert(res.message, {icon:0});
        }
    })
});

//加载会员资料
function loadUserInfo() {
    $.ajax({
        type: 'POST',
        url: '../account/loginInfo',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100) {
                $("#userCode").html(res.data.userCode);
                $("#name").val(res.data.name);
                $("#telephone").val(res.data.mobile);
                $('#sex input[value='+res.data.sex+']').attr('checked',true);
                if(res.data.userStatus=='0'){
                    $('.store-wrap').css('display','block');
                }
                var icon = res.data.icon;
                convertImgToBase64(icon, function (Base64Img) {
                    icon = Base64Img;
                    var image = '<div class="img_container1"><img src="'+icon+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                    $('#img1 .img_container1').append(image);
                    $('#img1').append('<span style="margin-left: 171px;color: red">您的头像将作为logo在您的云店展示，为了展示效果更佳，请上传2.1:1尺寸的图片</span>');
                    $('.delete_img').click(function () {
                        $(this).parent().remove();
                    })
                });

            } else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
loadUserInfo();

//保存会员资料
function updateUserBase() {
    var userName = $('#name').val();
    var mobile = $('#telephone').val();
    var verifyCode = $('#verifyCode').val();
    var data = {
        name:$("#name").val(),
        phone:$("#telephone").val(),
        icon:$('.img_container1 > div img').attr('src'),
        sex:$('#sex input:radio:checked').val(),
        verificationCode:$("#verifyCode").val(),
    };
    if(userName==''){
        layer.alert('请输入姓名！', {icon:0});
    }else if(mobile==''){
        layer.alert('请输入手机号！', {icon:0});
    }else if(verifyCode==''){
        layer.alert('请输入验证码！', {icon:0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../account/updateUserBase',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').hide();
                if (res.stateCode == 100) {
                    layer.alert('修改成功', {icon:0});
                } else {
                    layer.alert(res.message, {icon:0});
                }
            },
            error: function (err) {
                layer.alert(res.message, {icon:0});
            }
        })
    }

}


//门店信息
$.ajax({
    type: 'POST',
    url: '../userCenter/storeInfo',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100) {
            $('#storeName').html(res.data.storeName);
            $('#address').html(res.data.provinceName + res.data.cityName + res.data.zoneName + res.data.address);
            $('#storeContactPhone').html(res.data.contactPhone);
            $('#storeContact').html(res.data.contact);
           // for(var i = 0; i < res.data.categoryList.length; i++){
           //     var cateGory = '';
           //     cateGory += '<span style="padding-right: 9px">'+res.data.categoryList[i].categoryName+'</span>';
           //     $('#salesGoods').append(cateGory)
           // }
            $('#salesGoods').append(res.data.scope)
            $('.img_container3 img').attr('src', res.data.credentials);
            for(var i = 0; i < res.data.pictureList.length; i++){
                var img = '';
                img += '<img style="width: 100px;height: 100px;margin-right: 9px" src="'+res.data.pictureList[i].href+'">';
                $('#img2').append(img)

            }
        } else {
            layer.alert(res.message, {icon:0});
        }
    },
    error: function (err) {
        layer.alert(res.message, {icon:0});
    }
})
