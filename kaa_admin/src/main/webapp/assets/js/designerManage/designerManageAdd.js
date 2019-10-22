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
        alert('最多上传1张')
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
            var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="$(this).parent().remove()"></span></div>';
            $('.img_container1').append(image);
            $("#isIcon").val(true);
        }
    }
});


var url = window.location.href;
var id = url.split('=')[1];


//省市区
var code = 110000;
$.ajax({
    type: 'POST',
    url: '../area/findProvince',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0;i < res.data.length; i++){
            var province = '';
            province += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
            $('#cmbProvince').append(province);
        }
        code = $('#cmbProvince option').eq(0).val();
        provinceCity();
        $("#cmbProvince").change(function(){
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
                    for(var i = 0;i < res.data.length; i++){
                        var cmbCity = '';
                        cmbCity += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                        $('#cmbCity').append(cmbCity)
                    }
                    //区
                    code = $('#cmbCity option').eq(0).val();
                    area();
                    $("#cmbCity").change(function(){
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
                                for(var i = 0;i < res.data.length; i++){
                                    var cmbArea = '';
                                    cmbArea += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                                    $('#cmbArea').append(cmbArea)
                                }
                            },
                            error: function (err) {
                                layer.alert(err.message)
                            }
                        })
                    }
                },
                error: function (err) {
                    layer.alert(err.message)
                }
            })
        }
    },
    error: function (err) {
        layer.alert(err.message)
    }
});


//获取详情
if (id) {
    $.ajax({
        type: 'POST',
        url: '../designer/designerDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            if(res.stateCode == 100){
                var image = '<div class="img_container1"><img src="'+res.data.icon+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="$(this).parent().remove()"></span></div>';
                $('.img_container1').append(image);
                $('#phone').val(res.data.phone);
                $('#name').val(res.data.name);
                $('#sex input[value='+res.data.sex+']').attr('checked',true);
                $("#type").val(res.data.type);
                $("#resume").val(res.data.resume);
                $("#remarks").val(res.data.remarks);
                $("#isShow").val(res.data.isShow ? 1 : 0);
                $("#editor1").html(res.data.description);
                $("#address").html(res.data.address);

                var province = res.data.province;
                var city = res.data.city;
                var zone = res.data.zone;
                $("#cmbProvince").val(province);
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: province}),
                    success: function (res) {
                        $('#cmbCity').html('');
                        for(var i = 0;i < res.data.length; i++){
                            var cmbCity = '';
                            cmbCity += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        $("#cmbCity").val(city);

                        $.ajax({
                            type: 'POST',
                            url: '../area/findCity',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({code: city}),
                            success: function (res) {
                                $('#cmbArea').html('');
                                for(var i = 0;i < res.data.length; i++){
                                    var cmbArea = '';
                                    cmbArea += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                                    $('#cmbArea').append(cmbArea)
                                }
                                $('#cmbArea').val(zone)
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
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//提交
$("#confirm").click(function () {
    data = {
        id: id,
        icon: $("#isIcon").val() == "true" ? $('.img_container1 > div img').attr('src') : "",
        receiver: $("#name").val(),
        mobile: $("#phone").val(),
        sex: $('input:radio[name="sex"]:checked').val(),
        type: $('#type option:selected').attr('value'),
        isShow: $('#isShow option:selected').attr('value') == 1 ? true : false ,
        resume: $("#resume").val(),
        remarks: $("#remarks").val(),
        address: $("#address").val(),
        description: $("#editor1").html(),
        province: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        city: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zone: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text()
    }

    if ($('#img1 .img_container1 div').length == 0) {
        layer.alert('请上传设计师头像！', {icon: 0});
    } else if ($("#phone").val()=='' || $('.order-detail .error-info').css('display') == 'inline') {
        layer.alert('请输入正确的手机号！', {icon: 0});
    } else if ($("#name").val()=='') {
        layer.alert('请输入真实姓名！', {icon: 0});
    } else if ($("#address").val()=='') {
        layer.alert('请输入详细地址！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../designer/designerSave',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').hide();
                if(res.stateCode == 100){
                    layer.alert('新增成功！', {icon: 0});
                    history.go('-1')
                }else{
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    }


})

// 验证手机号码
function checkMobile() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('phone').value;
    if(!(pattern.test(phone))){
        $('.error-phone').show();
        return false;
    } else {
        $('.error-phone').hide();
        return false;
    }
}

