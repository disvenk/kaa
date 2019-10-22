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

$.ajax({
    type: 'POST',
    url: '../suplier/suplierEditDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        var aaa = res.data.icon;
        convertImgToBase64(aaa, function (Base64Img) {
            aaa = Base64Img;
            var cc = '';
            cc = '<div class="img_container1"><img src="' + aaa + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('.img_container').append(cc);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        });
        var bbb = res.data.qualifications;
        convertImgToBase64(bbb, function (Base64Img) {
            bbb = Base64Img;
            var img = '';
            img += '<div style="height: 170px" class="img_container3"><img style="height: 60%" src="' + bbb + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('#img3 .img_container3').append('' + img + '');
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        });
        $('.supplier-name').val(res.data.name);
        $('#supplierCode').val(res.data.code);

        // $('.supplier-company').val(res.data.companyName);
        $('.supplier-address').val(res.data.address);
        $('.supplier-years').val(res.data.openYears);
        $('.chegong').val(res.data.smith);
        $('.caijian').val(res.data.sewer);
        $('.bxs').val(res.data.editer);
        if(res.data.modelSet != null){
            var modelSet = res.data.modelSet.split(",");
            for (var i = 0; i < modelSet.length; i++) {
                $.each($('input:checkbox'), function () {
                    if ($(this).val().trim() == modelSet[i].trim()) {
                        $(this).attr('checked', true)
                    }
                });
            }
        }

        /*if (res.data.modelSet == 0) {
            res.data.modelSet = '无'
        } else if (res.data.modelSet == 1) {
            res.data.modelSet = '外模'
        } else {
            res.data.modelSet = '内模'
        }
        $('#model input[value=' + res.data.modelSet + ']').attr('checked', true);*/
        $('.order-textarea').val(res.data.description);

        $('.supplier-yyzz').attr('src', res.data.qualifications);
        $('#approveStatus').val(res.data.approveStatus);
        $('.contact-name').val(res.data.contact);
        if (res.data.sex == 1) {
            res.data.sex = '男'
        } else {
            res.data.sex = '女'
        }
        $('#sex input[value=' + res.data.sex + ']').attr('checked', true);

        $('.id-num').val(res.data.personID);
        $('.phone-num').val(res.data.mobile);


        var scope = res.data.scopeName.split(",");
        for (var i = 0; i < scope.length; i++) {
            $.each($('input:checkbox'), function () {
                if ($(this).val().trim() == scope[i].trim()) {
                    $(this).attr('checked', true)
                } else {
                    $("#rests").val(scope[scope.length - 1]);
                }
            });
        }
    }


});


//本地上传图片
document.getElementById("upLoad").addEventListener("change", function (e) {
    var log = $('#img .img_container > div').length;
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
            $('.img_container').append(image);
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
    var code = $('#supplierCode').val();
    var supplierPhoto = '';
    var qualifications = '';
    var scope = '';
    $.each($('input:checkbox[name="necessary"]:checked'), function () {
        scope += $(this).val() + ',';
    });
    scope += $("#rests").val();

    var supplierName = $('.supplier-name').val();
    supplierPhoto = $('.img_container > div img').attr('src');
    qualifications = $('.img_container3 > div img').attr('src');
    // var supplierCompany = $('.supplier-company').val();
    var supplierAddress = $('.supplier-address').val();
    var supplierYears = $('.supplier-years').val();
    var chegong = $('.chegong').val();
    var caijian = $('.caijian').val();
    var bxs = $('.bxs').val();
    var model = '';
    $.each($('input:checkbox[name="model"]:checked'), function () {
        model += $(this).val() + ',';
    });

    /*var model = $('#model input:radio:checked').val();
    if (model == '无') {
        model = '0';
    } else if (model == '外模') {
        model = '1';
    } else {
        model = '2';
    }*/
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
    // console.log(approveStatus)
    var data = {
        id: id,
        code: code,
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
        modelSet: model,
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
    } else if (phoneNum == '') {
        layer.alert('请输入联系人电话!', {icon: 0});
    } else if ($('.error-phone').css('display') == 'inline') {
        layer.alert('请输入正确的手机号!', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../suplier/updateSuplier',
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