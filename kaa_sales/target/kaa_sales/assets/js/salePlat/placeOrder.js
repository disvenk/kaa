var url = window.location.href;
var id = url.split('=')[1];
// 省市区
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
//搜索
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});
//分类
// $.ajax({
//     type: 'POST',
//     url: '../storeProductManage/storeProductCategoryList',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         for(var i = 0 ;i < res.data[0].RelateCategoryName.length; i++){
//             var secCatego = '';
//             secCatego += '<a class="'+res.data[0].RelateCategoryName[i].RelateCategoryId+'">'+res.data[0].RelateCategoryName[i].RelateCategoryName+'</a>';
//             $('.content_header img').after(secCatego);
//         }
//         $('.content_header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('class')+'';
//         });
//     },
//     error: function (err) {
//         layer.alert(err.message);
//     }
// });
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
        for(var i = 0 ;i < res.data.length; i++){
            var address = '';
            address += '<label class="diy_label" style="color: #999" onclick="collapse()"><input style="margin-right: 6px" name="address" type="radio" value="1" /><span style="margin-right: 5px" class="'+res.data[i].provinceId+'">'+res.data[i].provinceName+'</span>'+
                '<span style="margin-right: 5px" class="'+ res.data[i].cityId+'">'+ res.data[i].cityName+'</span><span style="margin-right: 5px" class="'+ res.data[i].zoneId +'">'+ res.data[i].zoneName +'</span>'+
                '<span style="margin-right: 5px">'+res.data[i].address+'</span>(<span style="margin-right: 5px">'+res.data[i].receiver +'</span>收）<span>'+ res.data[i].mobile +'</span></label></br>';
            $('#address_container').after(address);
            if(res.data[i].Default){
                $('.diy_label').eq(i).find('input').eq(0).attr('checked', true)
            }
        }
function collapse() {
    $('#collapseExample').collapse('hide')
}
    },
    error: function (err) {
        layer.alert(err.message);
    }
});
//获取选择的商品
$.ajax({
    type: 'POST',
    url: '../session/getData',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        var dd = JSON.parse(res.data)
        for(var i = 0; i < dd.length; i++){
            var tr = '';
            tr += '<tr class="'+dd[i].pid+'"><td><div class="order-info-wrap">' +
                '<img style="width: 100px" class="order-info-img"  src="' + dd[i].href + '" alt=""><div class="sales-order-info">' +
                '<div class="order-info-name">名称：<span>' + dd[i].name + '</div>' +
                '<div class="order-info-desc"><span>' + dd[i].productCode + '</span> </div></div></div>' +
                '</td><td><div>颜色： <span>'+dd[i].color+'</span></div><div>尺寸： <span>'+dd[i].size+'</span></div></td>' +
                '<td>'+dd[i].price+'元</td><td>'+dd[i].nums+'</td><td style="color: #3c9ed8;">'+dd[i].price * dd[i].nums+'元</td></tr>';
            $('#customer').append(tr);
            var td = '';
            td += '<tr class="'+dd[i].pid+'"><td>'+ dd[i].name +'</td><td>'+ dd[i].productCode +'</td>' +
                '<td>'+dd[i].categoryName+'</td><td>'+ dd[i].color +'</td><td>'+ dd[i].size +'</td>' +
                '<td><input type="number"></td><td><input type="number"></td><td><input type="number"></td><td><input type="number">' +
                '</td><td><input type="number"></td><td><input type="number"></td><td><input type="number"></td><td><input type="text"></td></tr>';
            $('#xiadanTbody').append(td);
        }
        $('#totalNums').html(dd[0].totalNums + '件');
        $('#totalPrice').html(dd[0].totalPrice)
    },
    error: function (err) {
        layer.alert(err.message);
    }
});
$('#submitOrders').click(function () {
     var orderDetail = [];
     for(var i = 0; i < $('#customer tr').length; i++){
         var obj = {};
         obj = {
             pid: $('#customer tr').eq(i).attr('class'),
             num: $('#customer tr').eq(i).find('td').eq(3).html(),
             color: $('#xiadanTbody tr').eq(i).find('td').eq(3).html(),
             size: $('#xiadanTbody tr').eq(i).find('td').eq(4).html(),
             shoulder: $('#xiadanTbody tr').eq(i).find('td').eq(6).find('input').val(),
             bust: $('#xiadanTbody tr').eq(i).find('td').eq(7).find('input').val(),
             waist: $('#xiadanTbody tr').eq(i).find('td').eq(8).find('input').val(),
             hipline: $('#xiadanTbody tr').eq(i).find('td').eq(9).find('input').val(),
             height: $('#xiadanTbody tr').eq(i).find('td').eq(10).find('input').val(),
             weight: $('#xiadanTbody tr').eq(i).find('td').eq(11).find('input').val(),
             throatheight: $('#xiadanTbody tr').eq(i).find('td').eq(5).find('input').val(),
             other: $('#xiadanTbody tr').eq(i).find('td').eq(12).find('input').val()
         };
         orderDetail.push(obj);
    }
    var province;
    var provinceName
    var city;
    var cityName
    var zone;
    var zoneName
    var receiver;
    var provinceName
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
    }else if(diy_val == 2){
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
                layer.alert(err.message);
            }
        });
    }
    var data = {
        province: province,
        provinceName: provinceName,
        city: city,
        cityName: cityName,
        zone: zone,
        zoneName: zoneName,
        receiver: receiver,
        mobile: mobile,
        address: address,
        remarks: $('#remark').val(),
        orderDetail: orderDetail
    };
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/submitOrderSales',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                //调取支付接口
                location.href = '../payOrder/buyOrderPayHtml?id='+res.data.orderId;
            } else {
                layer.alert(res.message);
            }
        },
        error: function (err) {
            layer.alert(err.message);
        }
    });
})