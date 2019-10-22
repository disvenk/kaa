/**
 * Created by 12452 on 2017/10/24.
 */
$('#toGoodsStorage').click(function(){
    location.href='../storeSuplierOrder/buyOrderStorageHtml';
})

// var url = window.location.href;
// var storePid = url.split('storePid=')[1];
// console.log(storePid)
//省市区
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

//根据货号添加商品
$("#add").click(function () {
    var pno = $("#pno").val();
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/buyOrderBuyAddProduct',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({pno: pno.trim()}),
        success: function (res) {
            if(res.stateCode == 100){
                var tr = "";
                tr += '<tr id="'+res.data.id+'">\n' +
                    '                                    <td><div class="detail-edit-topic">'+res.data.name+'</div></td>\n' +
                    '                                    <td><img class="order-info-img" src="'+res.data.href+'" alt=""></td>\n' +
                    '                                    <td><div >'+res.data.categoryName+'</div></td>\n' +
                    '                                    <td><select class="color">\n' +
                    '                                            <option value="">请选择</option>\n' +
                    '                                        </select>\n' +
                    '                                    </td>\n' +
                    '                                    <td><select class="size">\n' +
                    '                                            <option value="">请选择</option>\n' +
                    '                                        </select>\n' +
                    '                                    </td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text"></td>\n' +
                    '                                    <td><input type="text" value="1"></td>\n' +
                    // '                                    <td><input type="text" value="'+res.data.price+'"></td>\n' +
                    // '                                    <td><input type="text" value="'+res.data.price+'"></td>\n' +
                    '                                    <td><div>'+res.data.price+'</div></td>\n' +
                    '                                    <td><div>'+res.data.price+'</div></td>\n' +
                    '                                </tr>';
                $("#proList").append(tr);
                getBaseDataList();

            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
})

//加载颜色尺寸  ../base/getBaseDataList   parameterType-2/3
function getBaseDataList() {
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
            if(res.stateCode == 100){
                var color = '';
                for (var i=0; i<res.data.length; i++) {
                    color += '<option value="'+res.data[i].id+'">'+res.data[i].name+'</option>'
                }
                $(".color").append(color);
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
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
            if(res.stateCode == 100){
                var size = '';
                for (var i=0; i<res.data.length; i++) {
                    size += '<option value="'+res.data[i].id+'">'+res.data[i].name+'</option>'
                }
                $(".size").append(size);
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });

}
getBaseDataList();


//提交采购订单
$("#submitOrder").click(function () {

    var trList = $("#proList").children("tr")
    var arr = [];
    for (var i=0;i<trList.length;i++) {
        var tdArr = trList.eq(i).find("td");
        var obj = {
            pid: trList.eq(i).attr("id"),
            color: tdArr.eq(3).find('select').find("option:selected").text(),
            size: tdArr.eq(4).find('select').find("option:selected").text(),
            num: tdArr.eq(tdArr.length-3).find('input').val(),
            throatheight: tdArr.eq(tdArr.length-11).find('input').val(),
            shoulder:tdArr.eq(tdArr.length-10).find('input').val(),
            bust: tdArr.eq(tdArr.length-9).find('input').val(),
            waist: tdArr.eq(tdArr.length-8).find('input').val(),
            hipline: tdArr.eq(tdArr.length-7).find('input').val(),
            height: tdArr.eq(tdArr.length-6).find('input').val(),
            weight: tdArr.eq(tdArr.length-5).find('input').val(),
            other: tdArr.eq(tdArr.length-4).find('input').val()
        }
        arr.push(obj)
    }
    var data = {
        province: $("#cmbProvince").val(),
        provinceName: $("#cmbProvince").find("option:selected").text(),
        city: $("#cmbCity").val(),
        cityName: $("#cmbCity").find("option:selected").text(),
        zone: $("#cmbArea").val(),
        zoneName: $("#cmbArea").find("option:selected").text(),
        receiver: $('#receive').val(),
        mobile: $('#receivedTel').val(),
        address: $('#textarea').val(),
        remarks: $('#remark').val(),
        expectsendDate: $('#datepickerStart').val(),

        orderNo: $('#orderNo').html(), //订单号
        salesOrderId: $('#salesOrderId').html(), //销售订单号

        orderDetail: arr
    };

    $.ajax({
        type: 'POST',
        //提交订单，返回订单id
        url: '../storeSuplierOrder/submitOrder',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                //调取支付接口
                location.href = '../payOrder/buyOrderPayHtml?id='+res.data.orderId + "&suc=2";
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    })


})

//返回
function buyOrder() {
    // location.href = '../storeSuplierOrder/buyOrderHtml';
    window.history.back();
}

$.ajax({
    type: 'POST',
    url: '../userAddress/findUserAddress',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
       if(res.stateCode == 100){
           $('#receive').val(res.data[0].receiver);
           $('#receivedTel').val(res.data[0].mobile);
           $('#textarea').val(res.data[0].address);
           //地址
           var provin = res.data[0].provinceName;
           var areaCit = res.data[0].cityName;
           var areaZoom = res.data[0].zoneName;
           var areaCitId = res.data[0].cityId;
           var proSelect = $('#cmbProvince').find('option');
           for(var i = 0; i < proSelect.length; i++){
               if(proSelect.eq(i).text() == provin){
                   proSelect.eq(i).attr('selected', 'selected');
               }
           }
           var code = res.data[0].provinceId;
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
                           $('#cmbArea').html('');
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
       }
    },
    error: function (err) {
        alert(err.message)
    }
})



