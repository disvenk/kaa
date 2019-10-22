var url = window.location.href;
var id = url.split('=')[1];

//图片转化
function convertImgToBase64(url, callback, outputFormat) {
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}

//本地上传图片
document.getElementById("upLoad1").addEventListener("change", function (e) {
    var log = $('#img1 .img_container1 > div').length;
    if (log > 0) {
        alert('最多上传1张')
    } else {
        var files = this.files;
        var img = new Image();
        var reader = new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload = function (e) {
            var dx = (e.total / 1024) / 1024;
            if (dx >= 2) {
                alert("文件大小大于2M");
                return;
            }
            img.src = this.result;
            img.style.width = '138px';
            img.style.height = "93px";
            var image = '<div class="img_container1"><img src="' + this.result + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="$(this).parent().remove()"></span></div>';
            $('.img_container1').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});

//本地上传图片
document.getElementById("upLoad3").addEventListener("change", function (e) {
    var log = $('#img3 .img_container3 > div').length;
    if (log > 0) {
        alert('最多只能上传1张图片')
    } else {
        var files = this.files;
        var img = new Image();
        var reader = new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload = function (e) {
            var dx = (e.total / 1024) / 1024;
            if (dx >= 2) {
                alert("文件大小大于2M");
                return;
            }
            img.src = this.result;
            img.style.width = '138px';
            img.style.height = "93px";
            var image = '<div class="img_container1"><img src="' + this.result + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('#img3 .img_container3').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});


// 提交编辑信息

$('#submit').click(function () {
    var scope = '';
    $.each($('input:checkbox:checked'), function () {
        scope += $(this).val() + ',';
    });

    scope += $("#rests").val();

    var code = $('#supplier-code').val();
    var supplierName = $('.supplier-name').val();
    var supplierPhoto = $('.img_container1 > div img').attr('src');
    var qualifications = $('.img_container3 > div img').attr('src');
    // var supplierCompany = $('.supplier-company').val();
    var supplierAddress = $('.supplier-address').val();
    var supplierYears = $('.supplier-years').val();
    var chegong = $('.chegong').val();
    var caijian = $('.caijian').val();
    var bxs = $('.bxs').val();
    var model = $('#model input:radio:checked').val();
    if (model == '无') {
        model = '0';
    } else if (model == '外模') {
        model = '1';
    } else {
        model = '2';
    }
    var sex = $('#sex input:radio:checked').val();
    if (sex == '男') {
        sex = '1';
    } else {
        sex = '0';
    }
    var orderText = $('.order-textarea').val();
    var supplierYyzz = $('.supplier-yyzz').attr('src');
    var contactName = $('.contact-name').val();
    var idNum = $('.id-num').val();
    var phoneNum = $('.phone-num').val();
    var sexName = $('#sex input:radio:checked').val();
    var approveStatus = $('#approveStatus').find('option:selected').val()
    var data = {
        id: id,
        code:code,
        name: supplierName,
        icon: supplierPhoto,
        // companyName: supplierCompany,
        address: supplierAddress,
        openYears: supplierYears,
        smith: chegong,
        sewer: caijian,
        editer: bxs,
        description: $('.order-textarea').val(),
        qualifications: qualifications,
        contact: contactName,
        personID: idNum,
        model: model,
        sex: sex,
        mobile: phoneNum,
        approveStatus: approveStatus,
        scope: scope
    };
    if (supplierName == '') {
        layer.alert('供应商名称为必填！', {icon: 0});
    } else if (supplierAddress == '') {
        layer.alert('工厂地址必填！', {icon: 0});
    } else if (scope == '') {
        layer.alert('请选择主营业务!', {icon: 0});
    } else if (chegong == '') {
        layer.alert('请输入车工工人人数!', {icon: 0});
    } else if (caijian == '') {
        layer.alert('请输入裁剪工人人数!', {icon: 0});
    } else if (contactName == '') {
        layer.alert('请输入联系人姓名!', {icon: 0});
    }else if (phoneNum == '') {
        layer.alert('请输入联系人电话!', {icon: 0});
    }else if ($('.error-phone').css('display') == 'inline') {
        layer.alert('请输入正确的手机号!', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../suplier/insertSuplier',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').hide();
                if (res.stateCode == 100) {
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        setTimeout(function () {
                            location.href = '../suplier/suplierHtml';
                        }, 1500)
                    })
                } else {
                    // alert(res.message)
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                // alert(err.message)
                layer.alert(err.message, {icon: 0});
            }
        })
    }


})