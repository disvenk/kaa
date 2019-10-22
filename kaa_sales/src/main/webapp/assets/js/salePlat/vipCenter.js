var url = window.location.href;
var id = url.split('=')[1];
var data = {
    pageNum: 1,
};
// 加载头部会员信息
$.ajax({
    type: 'POST',
    url: '../account/loginInfo',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        // console.log(res);
        $('.vip-head img').attr('src', res.data.icon);
        $('.vip-name').html(res.data.name);
        myOrderList();
        $('#toMyAddress').click(function(){
            var userCode=res.data.userCode;
            location.href= "../userCenter/myAddressHtml?id=" + userCode;

        })
    }
})

// 加载列表

function myOrderList() {
    $.ajax({
        type: 'POST',
        url: '../userCenter/findSuplierOrderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('#tbody').html('');
            for (var i = 0; i < res.data.length; i++) {
                var table = '';
                // console.log(res.data[i])
                for (var j = 0; j < res.data[i].detailList.length; j++) {
                    var detail = res.data[i].detailList[j];
                    table += '<tr class="'+res.data[i].orderId+'">' +
                        '<td class="' + detail.id + '" style="text-align: left;padding-left: 30px">' +
                        '<div class="order-info-wrap">' +
                        '<img class="order-info-img"  src="' + detail.productPicture + '" alt="">' +
                        '<div class="sales-order-info">' +
                        '<div class="order-info-name">名称：<span>' + detail.productName + '</div>' +
                        '<div class="order-info-desc">颜色：<span>' + detail.color + '</span>' +
                        ' &nbsp;&nbsp;尺寸：<span>' + detail.size + '</span></div>' +
                        '</div></div></td>' +
                        '<td>￥' + detail.price + '</td><td>X' + detail.qty + '</td>' +
                        '<td><button class="hello-btn" onclick="orderDetail('+res.data[i].orderId+')">查看订单</button></td>' +
                        '</tr>';
                }
                $('#tbody').append(table);
                //合并单元格
                for (var k = 0;k<$('#tbody tr').length;k++) {
                    if($('#tbody tr').eq(k).attr('class')==$('#tbody tr').eq(k+1).attr('class')){
                        $('#tbody tr').eq(k+1).find('td').eq(3).remove();
                        $('#tbody tr').eq(k).find('td').eq(3).attr('rowspan',$('.'+$('#tbody tr').eq(k).attr('class')+'').length);
                    }
                }
            }

        }
    })
}

function orderDetail(id) {
    location.href= "../storeSuplierOrder/buyOrderDetailHtml?id=" + id;
}
//我的资料
$('#toMyInformation').click(function () {
    location.href = '../userCenter/myInfoHtml';
});



