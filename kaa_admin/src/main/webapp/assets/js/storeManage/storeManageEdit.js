var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
var proSelect;
//时间戳转换
function timetrans(date) {
    var date = new Date(date);//如果date为10位不需要乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y + M + D + h + m + s;
}

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

function pro() {
    $.ajax({
        type: 'POST',
        url: '../area/findProvince',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for (var i = 0; i < res.data.length; i++) {
                var province = '';
                province += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                $('#cmbProvince').append(province);
                proSelect = $('#cmbProvince').find('option');
            }
            code = $('#cmbProvince option').eq(0).val();
            provinceCity();
            $("#cmbProvince").change(function () {
                $('#cmbCity').html('');
                $('#cmbArea').html('');
                code = $("#cmbProvince").val();
                provinceCity()
            });

            function provinceCity() {
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: code}),
                    success: function (res) {
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity = '';
                            cmbCity += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        //区
                        code = $('#cmbCity option').eq(0).val();
                        area();
                        $("#cmbCity").change(function () {
                            code = $("#cmbCity").val();
                            area();
                        });

                        function area() {
                            $('#cmbArea').html('');
                            $.ajax({
                                type: 'POST',
                                url: '../area/findCity',
                                contentType: 'application/json; charset=utf-8',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                data: JSON.stringify({code: code}),
                                success: function (res) {
                                    $('#cmbArea').html('');
                                    for (var i = 0; i < res.data.length; i++) {
                                        var cmbArea = '';
                                        cmbArea += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                                        $('#cmbArea').append(cmbArea)
                                    }
                                },
                                error: function (err) {
                                    alert(err.message)
                                }
                            })
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}

pro();

//用户身份
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },

    data: JSON.stringify({parameterType: 4}),
    success: function (res) {
        for (var i = 0; i < res.data.length; i++) {
            var userType = '';
            userType += '<option id="' + (1 + i) + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            $('#userType').append(userType);
        }
    },

    error: function (err) {
        alert(err.message)
    }
});

function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: urlle + 'store/getStoreDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('.spinner').hide();
            if (res.stateCode == 100) {
                var aaa = res.data.icon;
                convertImgToBase64(aaa, function (Base64Img) {
                    aaa = Base64Img;
                    var img = '';
                    img += '<div style="height: 170px" class="img_container1"><img style="height: 60%" src="' + aaa + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                    $('#img1 .img_container1').append('' + img + '');
                    $('.delete_img').click(function () {
                        $(this).parent().remove();
                    })
                });

                for (var i = 0; i < res.data.storePictureList.length; i++) {
                    var aaa = res.data.storePictureList[i].picId;
                    convertImgToBase64(aaa, function (Base64Img) {
                        aaa = Base64Img;
                        var img = '';
                        img += '<div style="height: 170px" class="img_container2"><img style="height: 60%" src="' + aaa + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                        $('#img2').append('' + img + '');
                        $('.delete_img').click(function () {
                            $(this).parent().remove();
                        })
                    });
                }

                var bbb = res.data.Credentials;
                convertImgToBase64(bbb, function (Base64Img) {
                    bbb = Base64Img;
                    var img = '';
                    img += '<div style="height: 170px" class="img_container3"><img style="height: 60%" src="' + bbb + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                    $('#img3 .img_container3').append('' + img + '');
                    $('.delete_img').click(function () {
                        $(this).parent().remove();
                    })
                });

                //详情信息
                //销售商品
                var scope = res.data.storeScope.split(",");

                for (var i=0; i<scope.length; i++) {
                    $.each($('input:checkbox'),function(){
                        if($(this).val().trim() == scope[i].trim())
                            $(this).attr('checked', true)
                    });
                }
                $('#userCode').val('' + res.data.userCode + '');
                $('#mobile').val('' + res.data.mobile + '');
                $('#userName').val('' + res.data.userName + '');
                if (res.data.sex == 1) {
                    res.data.sex = '男'
                } else {
                    res.data.sex = '女'
                }
                $('#sex input[value=' + res.data.sex + ']').attr('checked', true);
                $('#finalLogindate').val('' + res.data.finalLogindate + '');

                // $('#userType').find('option:checked').attr('id');
                for (var i = 0; i < $('#userType').find('option').length; i++) {
                    if ($('#userType').find('option').eq(i).attr('id') == res.data.userType) {
                        $('#userType').find('option').eq(i).attr('selected', 'selected')
                    }
                }
                $('#remarks').val('' + res.data.remarks + '');
                $('#storeName').val('' + res.data.storeName + '');
                $('#address').val('' + res.data.storeAddress + '');
                $('#storeContact').val('' + res.data.storeContact + '');
                $('#storeContactPhone').val('' + res.data.storeContactPhone + '');

                // $('#approveStatus').find('option:checked').attr('id');
                for (var i = 0; i < $('#approveStatus').find('option').length; i++) {
                    if ($('#approveStatus').find('option').eq(i).attr('id') == res.data.storeStatus) {
                        $('#approveStatus').find('option').eq(i).attr('selected', 'selected')
                    }
                }
                //分类
                // if(res.data.storeSalesCategoryList.length == 0){
                // }else {
                //     for(var i = 0; i < res.data.storeSalesCategoryList.length; i++){
                //         $('#salesGoods input[value='+res.data.storeSalesCategoryList[i].categoryName+']').attr('checked',true);
                //     }
                // }
                $('#salesGoods').append(res.data.storeScope);

                //地址
                var provin = res.data.provinceName;
                var areaCit = res.data.cityName;
                var areaZoom = res.data.zoneName;
                var areaCitId = res.data.cityId;
                for (var i = 0; i < proSelect.length; i++) {
                    if (proSelect.eq(i).text() == provin) {
                        proSelect.eq(i).attr('selected', 'selected');
                    }
                }
                var code = res.data.provinceId;
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: code}),
                    success: function (res) {
                        $('#cmbCity').html('');
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity = '';
                            cmbCity += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        var citySelect = $('#cmbCity').find('option');
                        for (var i = 0; i < citySelect.length; i++) {
                            if (citySelect.eq(i).text() == areaCit) {
                                citySelect.eq(i).attr('selected', 'selected')
                            }
                        }
                        code = areaCitId;
                        $('#cmbArea').html('');
                        $.ajax({
                            type: 'POST',
                            url: '../area/findCity',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({code: code}),
                            success: function (res) {
                                for (var i = 0; i < res.data.length; i++) {
                                    var cmbArea = '';
                                    cmbArea += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                                    $('#cmbArea').append(cmbArea)
                                }
                                var areaSelect = $('#cmbArea').find('option');
                                for (var i = 0; i < areaSelect.length; i++) {
                                    if (areaSelect.eq(i).text() == areaZoom) {
                                        areaSelect.eq(i).attr('selected', 'selected')
                                    }
                                }
                            },
                            error: function (err) {
                                alert(err.message)
                            }
                        })
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })

            } else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

detail();
//本地上传图片
document.getElementById("upLoad1").addEventListener("change", function (e) {
    var log = $('#img1 .img_container1 > div').length;
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
            $('#img1 .img_container1').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});

//本地上传图片
document.getElementById("upLoad2").addEventListener("change",function(e){
    var log = $('#img2 .img_container2').length;
    if(log > 4){
        alert('最多上传5张图片');
    }else{
        var files = document.getElementById("upLoad2").files;
        if(log + files.length > 5){
            alert('最多上传5张图片');
        }else{
            for(var i= 0; i < files.length; i++){
                var img = new Image();
                var reader =new FileReader();
                reader.readAsDataURL(files[i]);
                reader.onload =function(e){
                    var dx =(e.total/1024)/1024;
                    if(dx>=2){
                        alert("文件大小大于2M");
                        return;
                    }
                    img.src =this.result;
                    img.style.width = '138px';
                    img.style.height ="93px";
                    var image = '<div class="img_container2"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                    $('#img2').append(image);
                    $('.delete_img').click(function () {
                        $(this).parent().remove();
                    })
                }
            }
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

//提交编辑信息
$('#confirm').click(function () {
    var icon = $('.img_container1 > div img').attr('src');
    var credentials = $('.img_container3 > div img').attr('src');
    var userCode = $('#userCode').val();
    var mobile = $('#mobile').val();
    var userName = $('#userName').val();
    var sex = $('#sex input:radio:checked').val();
    if (sex == '男') {
        sex = '1';
    } else {
        sex = '0';
    }
    var finalLogindate = $('#finalLogindate').val();
    var userType = $('#userType option:selected').attr('id');
    var remarks = $('#remarks').val();
    var storeName = $('#storeName').val();
    var address = $('#address').val();
    var storeContact = $('#storeContact').val();
    var storeContactPhone = $('#storeContactPhone').val();

    var object = document.getElementsByName("categoryName");
    var check_val = [];
    for (var i = 0; i < object.length; i++) {
        if (object[i].checked) {
            check_val.push({categoryId: object[i].id, categoryName: object[i].value})
        }
    }
    var approveStatus = $('#approveStatus option:selected').attr('id');

    var img_con = $('#img2 .img_container2');
    var src = [];
    for (var i = 0; i < img_con.length; i++) {
        var imageSrc = {};
        imageSrc.href = img_con.eq(i).find('img').eq(0).attr('src');
        src[i] = imageSrc;
    }
    var scope = '';
    $.each($('input:checkbox:checked'),function(){
        scope += $(this).val() + ',';
    });

    var data = {

        storeId: id,
        userCode: userCode,
        scope: scope.substring(0,scope.lastIndexOf(",")),
        mobile: mobile,
        userName: userName,
        sex: sex,
        finalLogindate: finalLogindate,
        userType: userType,
        approveStatus: approveStatus,
        remarks: remarks,
        storeName: storeName,
        address: address,
        contact: storeContact,
        contactTelephone: storeContactPhone,
        provinceId: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        cityId: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zoneId: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text(),
        icon: icon,
        credentials: credentials,
        stoSalesCategoryList: check_val,
        stoStorePictureList: src,
        // productSupplierId: $('#productSupplierName option:selected').attr('id'),

    };
    if (userCode == '') {
        layer.alert('用户名必填！', {icon: 0});
    } else if (mobile == '') {
        layer.alert('手机号为必填！', {icon: 0});
    } else if (storeName == '') {
        layer.alert('门店名称为必填！', {icon: 0});
    } else if (address == '') {
        layer.alert('请填写详细的门店地址！', {icon: 0});
    } else if (storeContact == '') {
        layer.alert('请填写联系人！', {icon: 0});
    } else if (storeContactPhone == '') {
        layer.alert('请填写联系人电话！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'store/updateStore',
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
                            location.href = '../store/storeHtml';
                        }, 1500)
                    })
                } else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }

});


// 因为本页有重复的手机号和用户名，所以验证单独写在这里
// 验证用户手机号码
function checkStoreTel() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('mobile').value;
    if(!(pattern.test(phone))){
        $('.error-phone').show();
        return false;
    } else {
        $('.error-phone').hide();
        return false;
    }
}

function checkContacTel() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('storeContactPhone').value;
    if(!(pattern.test(phone))){
        $('.error-contact').show();
        return false;
    } else {
        $('.error-contact').hide();
        return false;
    }
}

// 验证联系人名字

function checkContactName() {
    var pattern = /^.{0,20}$/;
    var userName=$('.check-lxr').val();
    if(!(pattern.test(userName))){
        $('.error-lxr').show();
        return false;
    } else {
        $('.error-lxr').hide();
        return false;
    }
}
