var url = window.location.href;
var id = url.split('=')[1];
showDetail();

//显示门店商品明细
function showDetail() {
    $.ajax({
        type: 'POST',
        url: '../store/getStoSalesOrderDetail',
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
                //商品名称
                $('#orderNo').html(''+res.data.orderNo+'');
                $('#orderDate').html(''+res.data.orderDate+'');
                $('#orderStatus').html(''+res.data.orderStatus+'');
                $('#ordernoSuplier').html(''+res.data.ordernoSuplier+'');
                $('#orderSuplierStatus').html(''+res.data.orderSuplierStatus+'');
                //收件信息
                $('#receiver').html(''+res.data.receiver+'');
                $('#mobile').html(''+res.data.mobile+'');
                $('#address').html(res.data.provinceName+res.data.cityName+res.data.zoneName+res.data.address);

                $('#deliveryNo').html(''+res.data.deliveryNo+'');
                $('#deliveryCompanyName').html(''+res.data.deliveryCompanyName+'');
                $('#remarks').html(''+res.data.remarks+'');

                //订单明细
                var tr = '';
                for(var i = 0; i < res.data.stoSalesOrderDetailList.length; i++){
                    tr += '<tr>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].productCode+'</td>'+
                        '<td><div class="detail-goods-name">'+res.data.stoSalesOrderDetailList[i].productName+'</div></td>'+
                        '<td>'+res.data.stoSalesOrderDetailList[i].pno+'</td>'+
                        '<td><img style="width: 120px;height: 120px" src="'+res.data.stoSalesOrderDetailList[i].productPictureUrl+'" alt=""></td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].categoryName+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].color+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].size+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].shoulder+'</td>'+
                        '<td>'+res.data.stoSalesOrderDetailList[i].bust+'</td>'+
                        '<td>'+res.data.stoSalesOrderDetailList[i].waist+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].hipline+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].height+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].weight+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].throatheight+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].other+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].qty+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].price+'</td>' +
                        '<td>'+res.data.stoSalesOrderDetailList[i].subtotal+'</td>' +
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