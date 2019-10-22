
$.ajax({
    type: 'POST',
    url: '../suplierOrder/orderList',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({pageNum:1}),
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0; i < res.data.length; i++){
                var div = ""
                div += '<div class="order-wrap clearfix">\n' +
                    '        <div class="order-info-topic">订单号：<span>'+res.data[i].orderNo+'</span><span class="pruduct-condition">'+res.data[i].producedStatusName+'</span></div>\n' +
                    '        <div id="toMakingDetail" class="order-infos clearfix">\n' +
                    '            <img class="order-infos-model" src="'+res.data[i].href+'" alt="">\n' +
                    '            <div class="order-infos-item">\n' +
                    '                <span>货号<span>'+res.data[i].pno+'</span></span><br>\n' +
                    '                <span class="order-infos-span">单价：<span>￥'+res.data[i].price+'</span>&nbsp;&nbsp;数量：<span>'+res.data[i].qty+'</span></span><br>\n' +
                    '                <span class="order-infos-span">合计：<span>￥'+res.data[i].subtotal+'</span></span><br>\n' +
                    '                <span class="order-infos-span">期望发货日期：<span>'+res.data[i].expectsendDate+'</span></span>\n' +
                    '            </div>\n' +
                    '        </div>\n'
                if (res.data[i].producedStatus == 1) {
                    div += '        <div class="order-confirm-btn" onclick="orderDetail('+res.data[i].id+')">确认订单</div>'
                } else if(res.data[i].producedStatus == 2) {
                    div += '        <div class="order-confirm-btn" onclick="orderDetail('+res.data[i].id+')">生产完成</div>'
                } else if(res.data[i].producedStatus == 5) {
                    div += '        <div class="order-confirm-btn" onclick="delivery('+res.data[i].id+')">交付记录</div><div class="order-confirm-btn" onclick="orderDetail('+res.data[i].id+')">修改完成</div>'
                } else {
                    div += '        <div class="order-confirm-btn" onclick="delivery('+res.data[i].id+')">交付记录</div><div class="order-confirm-btn" onclick="orderDetail('+res.data[i].id+')">查看详情</div>'
                }

                $('#quanbu').append(div);
            }
        }else{
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message)
    }
})

function orderDetail(orderId) {
    location.href = '../suplierOrder/orderDetailHtml?id='+orderId;
}


