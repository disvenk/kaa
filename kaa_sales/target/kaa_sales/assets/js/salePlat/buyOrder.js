/**
 * Created by 12452 on 2017/10/24.
 */
$('#orderToBuy').click(function(){
    location.href='../storeSuplierOrder/buyOrderBuyHtml';
})
var url = window.location.href;
var type = url.split('=')[1];
var data = {
    pageNum: 1,
    orderStatus: type ? type : ''
};
for(var i = 0; i < $('#orderStatus option').length; i++){
    if($('#orderStatus option').eq(i).attr('value') == type){
        $('#orderStatus option').eq(i).attr('selected', 'selected');
    }
}
//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    var proName = $('#proName').val();
    var suplierOrderNo = $('#suplierOrderNo').val();
    var salesOrderNo = $('#salesOrderNo').val();
    var orderStatus = $('#orderStatus').find("option:checked").attr("value");

    data = {
        productName: proName,
        suplierOrderNo: suplierOrderNo,
        salesOrderNo: salesOrderNo,
        orderStatus: orderStatus,
        startTime: $('#datepickerStart').val(),
        endTime: $('#datepickerEnd').val(),
        pageNum: 1
    };
    list()
});
//重置
$('#resert').click(function () {
    $('#proName').val("");
    $('#suplierOrderNo').val("");
    $('#salesOrderNo').val("");
    $('#datepickerStart').val("");
    $('#datepickerEnd').val("");
    $('#orderStatus').val("");
    data = {
        productName: '',
        suplierOrderNo:  '',
        salesOrderNo:  '',
        orderStatus:  '',
        startTime:  '',
        endTime:  '',
        pageNum: 1
    };
    list()
});
//列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/findStoreSuplierOrderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum=pageNum;
                    ajax();
                }
            });
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/findStoreSuplierOrderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
//加载数据
function load(res) {
    $('.spinner').hide();
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += '<tr class="sales-order-state" style="background-color:rgb(243,242,244);height: 50px;">\n' +
            '                                            <td style="border: none"><span class="color-blue">下单日期：</span>'+res.data[i].orderDate+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">订单号：</span>'+res.data[i].suplierOrderNo+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">交易状态：</span>'+res.data[i].statusName+'</td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                        </tr>\n';
        for (var j = 0; j < res.data[i].productList.length; j++) {
            var product = res.data[i].productList[j]
            tr += '                                        <tr class="'+res.data[i].id+'">\n' +
                '                                            <td>\n' +
                '                                                <div class="order-info-wrap">\n' +
                '                                                    <img class="order-info-img" src="'+product.href+'" alt="">\n' +
                '                                                    <div class="sales-order-info">\n' +
                '                                                        <div class="order-info-name">'+product.productName+'</div>\n' +
                '                                                        <div class="order-info-desc">颜色：<span>'+product.color+'</span>  尺寸：<span>'+product.size+'</span></div>\n' +
                '                                                    </div>\n' +
                '                                                </div>\n' +
                '                                            </td>\n' +
                '                                            <td>￥'+product.price+'</td>\n' +
                '                                            <td>X'+product.qty+'</td>\n' +
                '                                            <td>￥'+product.subtotal+'</td>\n' +
                '                                            <td>' +
                '                                                 <div>'+ res.data[i].channelName +'</div>';
            if (res.data[i].salesOrderNo != null) {
                tr += '   <div>订单号</div>' +
                    '   <div>'+ res.data[i].salesOrderNo +'</div>';
            }
            tr +=  '                                            </td>\n' +
                '                                            <td>\n';
            //状态  0：待支付   1:待发货   2：已完成  3：已取消  4：待收货
            if (res.data[i].status == 0) {
                tr += '                                          <div class="sales-order-operate"><a class="buy-pay" href="../payOrder/buyOrderPayHtml?id='+res.data[i].id+'&suc=1">立即支付</a></div>\n';
                tr += '                                          <div class="sales-order-operate mt4" onclick="suplierOrderCancel('+res.data[i].id+')">取消订单</div>\n' ;
            } else if (res.data[i].status == 4) {
                tr += '                                          <div class="sales-order-operate"><a class="buy-pay" onclick="suplierOrderReceive('+res.data[i].id+')">确认收货</a></div>\n';
            }
            tr += '                                              <div class="sales-order-operate mt4"><a href="../storeSuplierOrder/buyOrderDetailHtml?id='+res.data[i].id+'">订单详情</a></div>\n';
            if (res.data[i].status == 3) {
                tr += '                                          <div class="sales-order-operate mt4" onclick="suplierOrderRemove('+res.data[i].id+')">删除</div>\n';
            }
            tr += '                                           </td>\n' +
                '                                        </tr>';

        }
    }
    $('#tbody').append(tr);

    //合并列
    var tr = $('#tbody tr');
    for(var i = 0; i < tr.length; i++) {
        if (tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')) {
            tr.eq(i + 1).find('td').eq(5).remove()
            tr.eq(i).find('td').eq(5).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);

            tr.eq(i + 1).find('td').eq(4).remove()
            tr.eq(i).find('td').eq(4).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);
        }
    }
}

//取消订单
function suplierOrderCancel(id) {
    $('#myModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../storeSuplierOrder/suplierOrderCancel',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#myModal').modal('hide');
                    ajax();
                }else{
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
}
//删除订单
function suplierOrderRemove(id) {
    $('#myModal1').modal('show');
    $('#sure1').click(function () {
        $.ajax({
            type: 'POST',
            url: '../storeSuplierOrder/suplierOrderRemove',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#myModal1').modal('hide');
                    ajax();
                }else{
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
}
//确认收货
function suplierOrderReceive(id) {
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/suplierOrderReceive',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                ajax();
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}
