var url = window.location.href;
var id = url.split('=')[1];
var productIds = '';
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
        success: function (res) {
          if(res.stateCode == 100){
              $('#receiver').html(res.data.receiver);
              $('#orderno').html(''+res.data.orderNo+'');
              $('#orderdate').html(''+res.data.orderDate+'');
              $('#expectsend_data').html(''+res.data.expectsendDate+'');
              $('#status').html(''+res.data.orderStatus+'');
              $('#orderno_suplier').html(''+res.data.orderNoSuplier+'');
              $('#orderstatus_suplier').html(''+res.data.orderSuplierStatus+'');
              $('#mobile').html(''+res.data.mobile+'');
              $('#address').html(res.data.ProvinceName + res.data.cityName + res.data.ZoneName + res.data.address);
              $('#delivery_no').html(''+res.data.deliveryNo+'');
              $('#delivery_company').html(''+res.data.deliveryCompanyName+'');
              $('#productNums').html(res.data.totalQty);
              $('#productTotal').html(res.data.totalPrice);
              $('#remarks').val(res.data.remarks);
              if(res.data.orderStatus == '已发货'){
                  $('#send').css('display', 'none');
              }else {}
              for(var i = 0; i < res.data.stoSalesOrderDetailList.length; i++){
                  var tr = '';
                  tr += '<tr><td><div class="detail-goods-name">'+res.data.stoSalesOrderDetailList[i].productName+'</div></td>'+
                      '<td><img style="width: 100px;height: 100px" src="'+res.data.stoSalesOrderDetailList[i].productPictureUrl+'" alt=""></td><td>'+res.data.stoSalesOrderDetailList[i].categoryName+'</td>' +
                      '<td>'+res.data.stoSalesOrderDetailList[i].color+'</td><td>'+res.data.stoSalesOrderDetailList[i].size+'</td><td>'+res.data.stoSalesOrderDetailList[i].shoulder+'</td><td>'+res.data.stoSalesOrderDetailList[i].bust+'' +
                      '</td><td>'+res.data.stoSalesOrderDetailList[i].waist+'</td><td>'+res.data.stoSalesOrderDetailList[i].hipline+'</td><td>'+res.data.stoSalesOrderDetailList[i].height+'</td><td>'+res.data.stoSalesOrderDetailList[i].weight+'</td>' +
                      '<td>'+res.data.stoSalesOrderDetailList[i].other+'</td><td>'+res.data.stoSalesOrderDetailList[i].qty+'</td><td>'+res.data.stoSalesOrderDetailList[i].price+'</td><td>'+res.data.stoSalesOrderDetailList[i].subtotal+'</td></tr>';

                  $('#tbody').append(tr);

                  productIds += res.data.stoSalesOrderDetailList[i].productId + ',';
              }

          }
        },
        error: function (err) {
            alert(err)
        }
    });
$('#send').click(function () {
    $('#sendNow').modal('show');
    //快递公司
    $.ajax({
        type: 'POST',
        url: '../base/getBaseDataList',
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
                    option += '<option id="'+ res.data[i].id +'" value=' + res.data[i].name + '>' + res.data[i].name + '</option>';
                    $('#deliveryCompany').append(option);
                }
                //直接发货
                $('#fahuo').click(function () {
                    var param = {
                        orderId: id,
                        deliveryCompanyName: $('#deliveryCompany').val(),
                        deliveryNo: $('#deliveryNo').val(),
                        deliverCompanyId: $('#deliveryCompany').find('option:checked').attr('id')
                    };
                    $.ajax({
                        url: '../storeSalesOrder/saveStoSalesOrderDelivery',
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(param),
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        success: function (res) {
                            if (res.stateCode == 100) {
                                $('#sendNow').modal('hide');
                                location.reload();
                            } else {
                                alert('网络异常，请稍后再试...')
                            }
                        }
                    })
                })

            } else {
                alert('网络异常，请稍后再试...')
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });

})

$("#supplierProduct").click(function () {
    location.href='../storeSuplierOrder/buyOrderBuyHtml?ids='+productIds.substr(0,productIds.length-1) + "&salesOrderId=" + id;
})