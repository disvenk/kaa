var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
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
}
pro();
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({parameterType: 5}),
    success: function (res) {
        if (res.stateCode == 100) {
            for (var i = 0; i < res.data.length; i++) {
                var option = '';
                option += '<option id="' + res.data[i].id + '" value=' + res.data[i].name + '>' + res.data[i].name + '</option>';
                $('#deliveryCompanyName').append(option);
            }
        }
    },
    error: function (err) {
    alert(err.message)
    }
})
$.ajax({
    type: 'POST',
    url:  urlle + 'orderManage/suplierOrderDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        $('#salesOrderNo').html(res.data.salesOrderNo);
        $('#receiver').val(res.data.receiver);
        $('#mobile').val(res.data.mobile);
        $('#address').val(res.data.address);
        $('#remarks').val(res.data.remarks);
        $('#payTypeName').html(res.data.payTypeName);
        $('#actualPay').html(res.data.actualPay);
        $('#payTime').html(res.data.payTime);
        $('#orderDate').html(res.data.orderDate);
        $('#expectsendDate').html(res.data.expectsendDate);
        $('#suplierName').html(res.data.suplierName);
        $('#producedStatusName').val(res.data.producedStatusName);
        $('#statusName').val(res.data.statusName);
        for(var i = 0; i < $('#statusName option').length; i++){
            if($('#statusName option').eq(i).text() == res.data.statusName){
                $('#statusName option').eq(i).attr('selected', 'selected')
            }
        }
        $('#suplierOrderNo').html(res.data.suplierOrderNo);
        $('#channelName').html(res.data.channelName);
        $('#deliveryCompanyName').val(res.data.deliveryCompanyName);
        $('#deliveryNo').val(res.data.deliveryNo);
        $('#qtySum').html(res.data.qtySum + '件');
        $('#subtotalSum').html(res.data.subtotalSum + '元');
        for(var i = 0; i < res.data.productList.length; i++){
            var tr = '';
            tr += ' <tr class="'+res.data.productList[i].pid+'"><td>'+res.data.productList[i].name+'</td><td>'+res.data.productList[i].pno+'</td><td><img style="width: 80px" src="'+res.data.productList[i].href+'" alt=""></td><td>'+res.data.productList[i].categoryName+'</td>' +
                '<td class="supplier">'+res.data.productList[i].suplierName+'</td><td><select name="">' +
                '<option value="0">无</option><option value="1">待接单</option><option value="2">生产中</option><option value="3">生产完成</option>' +
                '<option value="4">质检通过</option><option value="5">质检未通过</option><option value="6">已完成</option></select></td>'+
                '<td class="p_color">'+res.data.productList[i].color+'</td><td class="p_size">'+res.data.productList[i].size+'</td><td>'+res.data.productList[i].throatheight+'</td><td>'+res.data.productList[i].shoulder+'</td>' +
                '<td>'+res.data.productList[i].bust+'</td><td>'+res.data.productList[i].waist+'</td><td>'+res.data.productList[i].hipline+'</td><td>'+res.data.productList[i].height+'</td>' +
                '<td>'+res.data.productList[i].weight+'</td><td>'+res.data.productList[i].qty+'</td><td>'+res.data.productList[i].price+'</td><td>'+res.data.productList[i].subtotal+'</td></tr>';
            $('#tbody').append(tr);
            //生产状态
            for(var j = 0; j < 7; j++){
                if($('#tbody select').eq(i).find('option').eq(j).text() == res.data.productList[i].producedStatusName){
                    $('#tbody select').eq(i).find('option').eq(j).attr('selected', 'selected');
                }
            }
        }
        //地址
        var provin = res.data.provinceName;
        var areaCit = res.data.cityName;
        var areaZoom = res.data.zoneName;
        var areaCitId = res.data.city;
        var proSelect = $('#cmbProvince option');
        for(var i = 0; i < proSelect.length; i++){
            if(proSelect.eq(i).text() == provin){
                proSelect.eq(i).attr('selected', 'selected');
            }
        }
        var code = res.data.province;
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
                for(var i = 0;i < res.data.length; i++){
                    var cmbCity = '';
                    cmbCity += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                    $('#cmbCity').append(cmbCity)
                }
                var citySelect = $('#cmbCity').find('option');
                for(var i = 0; i < citySelect.length; i++){
                    if(citySelect.eq(i).text() == areaCit){
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
                        for(var i = 0;i < res.data.length; i++){
                            var cmbArea = '';
                            cmbArea += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                            $('#cmbArea').append(cmbArea)
                        }
                        var areaSelect = $('#cmbArea').find('option');
                        for(var i = 0; i < areaSelect.length; i++){
                            if(areaSelect.eq(i).text() == areaZoom){
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
    },
    error: function (err) {
        $('.tcdPageCode').html('');
        alert(err.message);
    }
});
//提交
$('#save').click(function () {
    var pids = [];
    var array = $('#tbody tr');
    for(var i = 0; i < array.length; i++){
        var c = array.eq(i).find(".supplier").html();
        if (c != null && c != '') {
            var obj = {
                pid: array.eq(i).attr('class'),
                color: array.eq(i).find(".p_color").html(),
                size: array.eq(i).find(".p_size").html(),
                producedStatus: array.eq(i).find('option:selected').val()
            }
            pids[i] = obj;
        }
    }
    var data = {
        remarks: $('#remarks').val(),
        receiver: $('#receiver').val(),
        mobile: $('#mobile').val(),
        province: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        city: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zone: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text(),
        address: $('#address').val(),
        id: id,
        deliveryCompany: $('#deliveryCompanyName').find('option:selected').attr('id'),
        deliveryCompanyName: $('#deliveryCompanyName').find('option:selected').text(),
        deliveryNo: $('#deliveryNo').val(),
        status: $('#statusName').find('option:selected').val(),
        pids: pids
    };
    $('.spinner-wrap').show();
    $.ajax({
        type: 'POST',
        url: '../orderManage/suplierOrderUpdate',
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
                $('#myModal').modal('show');
                $('#myModal #sure').click(function () {
                    location.href = '../orderManage/orderManageHtml';
                })
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    })
});