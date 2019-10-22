var url = window.location.href;
var array = url.split('=');
var ids = array[1].split(',');
var params = [];
var data = {};
for(var i = 0; i < ids.length; i++){
    var obj = {
        id: ''
    };
    obj.id = ids[i];
    params[i] = obj
}
// 省市区
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

//获取收货地址
$.ajax({
    type: 'POST',
    url: '../userAddress/findUserAddressList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0 ;i < res.data.length; i++){
                var address = '';
                address += '<label class="diy_label" style="color: #999" onclick="$(\'#collapseExample\').collapse(\'hide\')"><input style="margin-right: 6px" name="address" type="radio" value="1" /><span style="margin-right: 5px" class="'+res.data[i].provinceId+'">'+res.data[i].provinceName+'</span>'+
                    '<span style="margin-right: 5px" class="'+ res.data[i].cityId+'">'+ res.data[i].cityName+'</span><span style="margin-right: 5px" class="'+ res.data[i].zoneId +'">'+ res.data[i].zoneName +'</span>'+
                    '<span style="margin-right: 5px">'+res.data[i].address+'</span>(<span style="margin-right: 5px">'+res.data[i].receiver +'</span>收）<span>'+ res.data[i].mobile +'</span></label></br>';
                $('#address_container').after(address);
                if(res.data[i].Default){
                    $('.diy_label').eq(i).find('input').eq(0).attr('checked', true)
                }
            }
            if(res.data.length == 0){
                $('#add').attr('checked', true);
            }
        }else{
            layer.alert(res.message, {icon:0});
        }
    },
    error: function (err) {
        layer.alert(err.message,{icon:0});
    }
});
//商品信息
$.ajax({
    type: 'POST',
    url: '../boxHome/boxUseLogCheck',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({ids:params}),
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0 ;i < res.data.length; i++){
                var ul = '';
                ul += '  <ul class="order-list-items clearfix">\n' +
                    '                <li class="order-list-item w60">\n' +
                    '                    <img style="width: 120px;height: 120px;border-radius: 6px" class="order-list-img" src="'+res.data[i].href+'" alt="">\n' +
                    '                    <span class="order-list-name">'+res.data[i].name+'</span>\n' +
                    '                </li>\n' +
                    '                <li class="order-list-item w20">￥'+res.data[i].price+'</li>\n' +
                    '                <li class="order-list-item w20">'+res.data[i].count+'件</li>\n' +
                    '            </ul>';
                $('.order-list').append(ul);
            }
        }else{
            layer.alert(res.message, {icon:0});
            IntervalName = setInterval(function () {
                history.go(-1);
            },2700)
        }
    },
    error: function (err) {
        layer.alert(err.message,{icon:0});
    }
});
//确认
$('.confirm-btn').click(function () {
    var index = layer.load(2, {shade: [0.6,'#000']});
    var province;
    var provinceName;
    var city;
    var cityName;
    var zone;
    var zoneName;
    var receiver;
    var mobile;
    var address;
    var diy_val = $('input[name=address]:checked').val();
    if(diy_val == 1){
        var addre = $('input[name=address]:checked').parent();
        province = addre.find('span').eq(0).attr('class');
        provinceName = addre.find('span').eq(0).html();
        city = addre.find('span').eq(1).attr('class');
        cityName = addre.find('span').eq(1).html();
        zone = addre.find('span').eq(2).attr('class');
        zoneName = addre.find('span').eq(2).html();
        receiver = addre.find('span').eq(4).html();
        mobile = addre.find('span').eq(5).html();
        address = addre.find('span').eq(3).html();
        data = {
            province: province,
            provinceName: provinceName,
            city: city,
            cityName: cityName,
            zone: zone,
            zoneName: zoneName,
            receiver: receiver,
            mobile: mobile,
            address: address,
            ids:params
        };
        $.ajax({
            type: 'POST',
            url: '../boxHome/boxUseLogSave',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    location.href='../salesHome/boxUseSuccessHtml';
                }else{
                    layer.close(index);
                    layer.alert(res.message, {icon:0});
                }
            },
            error: function (err) {
                layer.close(index);
                layer.alert(err.message,{icon:0});
            }
        });
    }else if(diy_val == 2){
        if($('#receive').val() == ''){
            layer.alert('收件人不能为空', {icon:0})
        }else if($('#textarea').val() == ''){
            layer.alert('详细地址不能为空', {icon:0})
        }else if(!(/^1[3|4|5|8][0-9]\d{4,8}$/.test($('#receivedTel').val()))){
            layer.alert('手机号不正确', {icon:0})
        }else{
            province = $('#cmbProvince').find('option:selected').val();
            provinceName = $('#cmbProvince').find('option:selected').text();
            city = $('#cmbCity').find('option:selected').val();
            cityName = $('#cmbCity').find('option:selected').text();
            zone = $('#cmbArea').find('option:selected').val();
            zoneName = $('#cmbArea').find('option:selected').text();
            receiver = $('#receive').val();
            mobile = $('#receivedTel').val();
            address = $('#textarea').val();
            var param = {
                province :$('#cmbProvince').find('option:selected').val(),
                provinceName : $('#cmbProvince').find('option:selected').text(),
                city : $('#cmbCity').find('option:selected').val(),
                cityName : $('#cmbCity').find('option:selected').text(),
                zone : $('#cmbArea').find('option:selected').val(),
                zoneName : $('#cmbArea').find('option:selected').text(),
                receiver : $('#receive').val(),
                mobile : $('#receivedTel').val(),
                address : $('#textarea').val(),
                isDefault: false
            };
            $.ajax({
                type: 'POST',
                url: '../userAddress/saveUserAddress',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(param),
                success: function (res) {
                },
                error: function (err) {
                    // layer.alert(err.message);
                }
            });
            data = {
                province: province,
                provinceName: provinceName,
                city: city,
                cityName: cityName,
                zone: zone,
                zoneName: zoneName,
                receiver: receiver,
                mobile: mobile,
                address: address,
                ids:params
            };
            $.ajax({
                type: 'POST',
                url: '../boxHome/boxUseLogSave',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(data),
                success: function (res) {
                    if(res.stateCode == 100){
                        location.href='../salesHome/boxUseSuccessHtml';
                    }else{
                        layer.close(index);
                        layer.alert(res.message, {icon:0});
                    }
                },
                error: function (err) {
                    layer.close(index);
                    layer.alert(err.message,{icon:0});
                }
            });
        }
    }
});