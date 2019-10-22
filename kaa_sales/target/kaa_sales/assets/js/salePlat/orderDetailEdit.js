var date = new Date().getFullYear() +'-'+ (new Date().getMonth() + 1) + '-' + (new Date().getDate() + 1);
$('#datetimepicker').datetimepicker({
    format: 'yyyy-mm-dd',
    weekStart: 1,
    autoclose: true,
    startView: 2,
    minView: 2,
    forceParse: false,
    language: 'zh-CN',
    startDate: date
});
var url = window.location.href;
var id = url.split('=')[1];
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
    url: '../storeSalesOrder/getStoSalesOrderDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id:id}),
    success:function (res) {
        $('#orderNo').html(res.data.orderNo);
        $('#buyTime').html(res.data.orderDate);
        $('#status').html(res.data.orderStatus);
        $('#orderNoSuplier').html(res.data.orderNoSuplier);
        $('#orderSuplierStatus').html(res.data.orderSuplierStatus);
        $('#receiver').val(res.data.receiver);
        $('#mobile').val(res.data.mobile);
        // $('#cmbArea').find('option[value='+res.data.Zone+']').attr('selected', 'selected');
        // $('#cmbCity').find('option[value='+res.data.cityId+']').attr('selected', 'selected');
        // $('#cmbProvince').find('option[value='+res.data.provinceId+']').attr('selected', 'selected');
        $('#address').val(res.data.address);
        $('#datetimepicker').val(res.data.expectsendDate);
        $('#totalPrice').html(res.data.totalPrice);
        $('#totalQty').html(res.data.totalQty);
        $('#remarks').val(res.data.remarks);
        for(var i = 0; i < res.data.stoSalesOrderDetailList.length; i++){
            var tr = '';
            tr += '<tr id="'+res.data.stoSalesOrderDetailList[i].productId+'"><td><div class="detail-edit-topic">'+res.data.stoSalesOrderDetailList[i].productName+'</div></td>' +
                '<td><img style="width: 60px" src="'+res.data.stoSalesOrderDetailList[i].productPictureUrl+'" alt=""></td>' +
                '<td style="width: 80px">'+res.data.stoSalesOrderDetailList[i].categoryName+'</td>' +
                '<td><select style="width: 100px" name="" class="selectColor"></select></td>' +
                '<td><select style="width: 50px" name="" class="selectSize"></select></td>' +
                '<td><input class="shoulder" type="number" value="'+res.data.stoSalesOrderDetailList[i].shoulder+'"></td>' +
                '<td><input class="bust" type="number" value="'+res.data.stoSalesOrderDetailList[i].bust+'"></td>' +
                '<td><input class="waist" type="number" value="'+res.data.stoSalesOrderDetailList[i].waist+'"></td>' +
                '<td><input class="hipline" type="number" value="'+res.data.stoSalesOrderDetailList[i].hipline+'"></td>' +
                '<td><input class="height" type="number" value="'+res.data.stoSalesOrderDetailList[i].height+'"></td>' +
                '<td><input class="weight" type="number" value="'+res.data.stoSalesOrderDetailList[i].weight+'"></td>' +
                '<td><input class="throatheight" type="number" value="'+res.data.stoSalesOrderDetailList[i].throatheight+'"></td>' +
                '<td><input class="other" type="text" value="'+res.data.stoSalesOrderDetailList[i].other+'"></td>' +
                '<td><input class="qty" type="number" value="'+res.data.stoSalesOrderDetailList[i].qty+'"></td>' +
                '<td><input class="price" type="number" value="'+res.data.stoSalesOrderDetailList[i].price+'"></td>' +
                '<td class="subtotal">'+res.data.stoSalesOrderDetailList[i].subtotal+'</tr>';
            $('#tbody').append(tr);
        }
        $('#tbody > tr .qty').bind('input propertychange', function() {
                $(this).parent().next().next().html($(this).parent().next().children().val() * $(this).val());
                var totalQ = 0;
              for(var i = 0; i < $('#tbody tr .qty').length; i++){
                  totalQ = totalQ + ($('#tbody tr .qty').eq(i).val() - 0);
               }
            $('#totalQty').html(totalQ)
            var totalP = 0;
            for(var i = 0; i < $('#tbody tr .subtotal').length; i++){
                totalP = totalP + ($('#tbody tr .subtotal').eq(i).html() - 0);
            }
            $('#totalPrice').html(totalP)
        });
        $('#tbody > tr .qty').change(function () {
            if($(this).val() < 1){
                $(this).val('1');
                $(this).parent().next().next().html($(this).parent().next().children().val() * $(this).val());
                var totalQ = 0;
                for(var i = 0; i < $('#tbody tr .qty').length; i++){
                    totalQ = totalQ + ($('#tbody tr .qty').eq(i).val() - 0);
                }
                $('#totalQty').html(totalQ)
                var totalP = 0;
                for(var i = 0; i < $('#tbody tr .subtotal').length; i++){
                    totalP = totalP + ($('#tbody tr .subtotal').eq(i).html() - 0);
                }
                $('#totalPrice').html(totalP)
            }
        });
        $('#tbody > tr .price').bind('input propertychange', function() {
            $(this).parent().next().html($(this).parent().prev().children().val() * $(this).val());
            var totalP = 0;
            for(var i = 0; i < $('#tbody tr .subtotal').length; i++){
                totalP = totalP + ($('#tbody tr .subtotal').eq(i).html() - 0);
            }
            $('#totalPrice').html(totalP)
        });
        $('#tbody > tr .price').change(function () {
            if($(this).val() < 1){
                $(this).val('1');
                $(this).parent().next().html($(this).parent().prev().children().val() * $(this).val());
                var totalP = 0;
                for(var i = 0; i < $('#tbody tr .subtotal').length; i++){
                    totalP = totalP + ($('#tbody tr .subtotal').eq(i).html() - 0);
                }
                $('#totalPrice').html(totalP)
            }
        });
        //地址
        var provin = res.data.ProvinceName;
        var areaCit = res.data.cityName;
        var areaZoom = res.data.ZoneName;
        var areaCitId = res.data.cityId;
        var proSelect = $('#cmbProvince').find('option');
        for(var i = 0; i < proSelect.length; i++){
            if(proSelect.eq(i).text() == provin){
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
        //颜色尺寸下拉
        //颜色
        var sss = res.data.stoSalesOrderDetailList;
        $.ajax({
            type: 'POST',
            url: '../base/getBaseDataList',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({parameterType: 2}),
            success: function (res) {
                for(var i = 0; i < res.data.length; i++){
                    var color = '';
                    color += '<option id="'+res.data[i].id+'" name="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                    $('.selectColor').each(function () {
                        var me = $(this);
                        me.append(color);
                    })
                }
                //颜色尺寸添值
                for(var j = 0; j < $('.selectColor').length; j++){
                    for(var i = 0; i < sss.length; i++){
                        for(var k = 0; k < $('.selectColor').eq(j).find('option').length; k++){
                            if($('.selectColor').eq(j).find('option').eq(k).attr('name') == sss[i].color){
                                $('.selectColor').eq(j).find('option').eq(k).attr('selected', true);
                            }
                        }
                    }
                }
            },
            error: function (err) {
                alert(err.message)
            }
        });
        //尺寸
        $.ajax({
            type: 'POST',
            url: '../base/getBaseDataList',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({parameterType: 3}),
            success: function (res) {
                for(var i = 0; i < res.data.length; i++){
                    var size = '';
                    size += '<option id="'+res.data[i].id+'" value="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                    $('.selectSize').each(function () {
                        var my = $(this);
                        my.append(size);
                    })
                }
                for(var j = 0; j < $('.selectSize').length; j++){
                    for(var i = 0; i < sss.length; i++){
                        for(var k = 0; k < $('.selectSize').eq(j).find('option').length; k++){
                            $('.selectSize').eq(j).find('option[value = '+sss[i].size+']').attr('selected', true);
                        }
                    }
                }
            },
            error: function (err) {
                alert(err.message)
            }
        });
    },
    error: function (err) {
        alert(err.message)
    }
});
//保存
$('#save').click(function () {
    var orderDetail = [];
    for(var i = 0; i < $('#tbody tr').length; i++){
        var obj = {
        };
        obj = {
            pid: $('#tbody tr').eq(i).attr('id'),
            num: $('#tbody .qty').eq(i).val(),
            price: $('#tbody .price').eq(i).val(),
            color: $('#tbody .selectColor').eq(i).find('option:selected').text(),
            size: $('#tbody .selectSize').eq(i).find('option:selected').text(),
            shoulder: $('#tbody .shoulder').eq(i).val(),
            bust: $('#tbody .bust').eq(i).val(),
            waist: $('#tbody .waist').eq(i).val(),
            hipline: $('#tbody .hipline').eq(i).val(),
            height: $('#tbody .height').eq(i).val(),
            weight: $('#tbody .weight').eq(i).val(),
            throatheight: $('#tbody .throatheight').eq(i).val(),
            other: $('#tbody .other').eq(i).val()
    }
    orderDetail[i] = obj;
    }
    var data = {
        remarks: $('#remarks').val(),
        receiver: $('#receiver').val(),
        mobile: $('#mobile').val(),
        provinceId: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        cityId: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zoneId: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text(),
        address: $('#address').val(),
        expectDeliveryDate: $('#datetimepicker').val(),
        id: id,
        orderDetail: orderDetail,
        total: $('#totalPrice').html()
    };
    $.ajax({
        type: 'POST',
        url: '../storeSalesOrder/updateStoSalesOrder',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
           if(res.stateCode == 100){
               $('#mymodalSelf').modal('show');
               $('#mymodalSelf #sure').click(function () {
                   location.href = '../storeSalesOrder/salesOrderHtml';
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