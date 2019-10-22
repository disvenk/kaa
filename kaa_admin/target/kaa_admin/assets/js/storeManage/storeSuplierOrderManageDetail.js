var url = window.location.href;
var id = url.split('=')[1];
showDetail();

//显示门店商品明细
function showDetail() {
    $.ajax({
        type: 'POST',
        url: '../store/getSuplierOrderDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            //console.log(res)
            if(res.stateCode == 100){
                //订单信息
                $('#suplierOrderNo').html(''+res.data.suplierOrderNo+'');
                //收件信息
                $('#receiver').html(''+res.data.receiver+'');
                $('#mobile').html(''+res.data.mobile+'');
                $('#address').html(res.data.address);
                $('#remarks').html(''+res.data.remarks+'');
                $('#totalQty').html(''+res.data.totalQty+'');
                $('#payTypeName').html(''+res.data.payTypeName+'');
                $('#actualPay').html(''+res.data.actualPay+'');
                $('#payTime').html(''+res.data.payTime+'');
                $('#orderDate').html(''+res.data.orderDate+'');
                $('#expectsendDate').html(''+res.data.expectsendDate+'');
                $('#statusName').html(''+res.data.statusName+'');
                $('#salesOrderNo').html(''+res.data.salesOrderNo+'');
                $('#channelName').html(''+res.data.channelName+'');
                $('#deliveryCompanyName').html(''+res.data.deliveryCompanyName+'');
                $('#deliveryNo').html(''+res.data.deliveryNo+'');

                //订单明细
                var tr = '';
                for(var i = 0; i < res.data.productList.length; i++){
                    tr += '<tr>' +
                        '<td>'+res.data.productList[i].productCode+'</td>'+
                        '<td><div class="detail-goods-name">'+res.data.productList[i].name+'</div></td>'+
                        '<td>'+res.data.productList[i].pno+'</td>'+
                        '<td><img style="width: 120px;height: 120px" src="'+res.data.productList[i].href+'" alt=""></td>' +
                        '<td>'+res.data.productList[i].categoryName+'</td>' +
                        '<td>'+res.data.productList[i].color+'</td>' +
                        '<td>'+res.data.productList[i].size+'</td>' +
                        '<td>'+res.data.productList[i].shoulder+'</td>'+
                        '<td>'+res.data.productList[i].bust+'</td>'+
                        '<td>'+res.data.productList[i].waist+'</td>' +
                        '<td>'+res.data.productList[i].hipline+'</td>' +
                        '<td>'+res.data.productList[i].height+'</td>' +
                        '<td>'+res.data.productList[i].weight+'</td>' +
                        '<td>'+res.data.productList[i].throatheight+'</td>' +
                        '<td>'+res.data.productList[i].other+'</td>' +
                        '<td>'+res.data.productList[i].qty+'</td>' +
                        '<td>'+res.data.productList[i].price+'</td>' +
                        '<td>'+res.data.productList[i].subtotal+'</td>' +
                        '<td>'+res.data.productList[i].suplierName+'</td>' +
                        '<td>'+res.data.productList[i].producedStatusName+'</td>' +
                        '</tr>';
                }
                $('#tbody').append(tr);
            }
        },
        error: function (err) {
            alert(err)
        }
    });
}

$('#back').click(function () {
    location.href = '../store/storeSalesOrderManageHtml';
});