var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
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
        $('#receiver').html(res.data.receiver);
        $('#mobile').html(res.data.mobile);
        $('#address').html(res.data.provinceName + res.data.cityName + res.data.zoneName + res.data.address);
        $('#remarks').html(res.data.remarks);
        $('#payTypeName').html(res.data.payTypeName);
        $('#actualPay').html(res.data.actualPay);
        $('#payTime').html(res.data.payTime);
        $('#orderDate').html(res.data.orderDate);
        $('#expectsendDate').html(res.data.expectsendDate);
        $('#statusName').html(res.data.statusName);
        $('#suplierOrderNo').html(res.data.suplierOrderNo);
        $('#channelName').html(res.data.channelName);
        $('#deliveryCompanyName').html(res.data.deliveryCompanyName);
        $('#deliveryNo').html(res.data.deliveryNo);
        $('#qtySum').html(res.data.qtySum + '件');
        $('#subtotalSum').html(res.data.subtotalSum + '元');
       for(var i = 0; i < res.data.productList.length; i++){
           var tr = '';
           tr += ' <tr><td>'+res.data.productList[i].name+'</td><td>'+res.data.productList[i].pno+'</td><td><img style="width: 60px;height: 60px" src="'+res.data.productList[i].href+'" alt=""></td><td>'+res.data.productList[i].categoryName+'</td>' +
               '<td>'+res.data.productList[i].suplierName+'</td><td>'+res.data.productList[i].producedStatusName+'</td>'+
               '<td>'+res.data.productList[i].color+'</td><td>'+res.data.productList[i].size+'</td><td>'+res.data.productList[i].throatheight+'</td><td>'+res.data.productList[i].shoulder+'</td>' +
               '<td>'+res.data.productList[i].bust+'</td><td>'+res.data.productList[i].waist+'</td><td>'+res.data.productList[i].hipline+'</td><td>'+res.data.productList[i].height+'</td>' +
               '<td>'+res.data.productList[i].weight+'</td><td>'+res.data.productList[i].qty+'</td><td>'+res.data.productList[i].price+'</td><td>'+res.data.productList[i].subtotal+'</td></tr>';

           $('#tbody').append(tr);
       }
    },
    error: function (err) {
        $('.tcdPageCode').html('');
        alert(err.message);
    }
});