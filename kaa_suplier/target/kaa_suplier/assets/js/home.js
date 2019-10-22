$(function(){
    window.onload = function()
    {
        var $li = $('#tabItems li');
        var $ul = $('.tab-cont');

        $li.mouseover(function(){
            var $this = $(this);
            var $t = $this.index();
            $li.removeClass();
            $this.addClass('current');
            $ul.css('display','none');
            $ul.eq($t).css('display','block');
        })

    }
});

//加载首页商品数据
$.ajax({
    type: 'POST',
    url: '../suplierHome/homePageInfo',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({}),
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
       $("#orderSum").html(res.data.orderSum)
       $("#countSum").html(res.data.countSum)
       $("#categorySum").html(res.data.categorySum)
       $("#avgSum").html(res.data.avgSum)
    },
    error: function (err) {
        layer.msg(err)
    }
});

//订单统计数据加载
orderCount(1);
function orderCount(type) {
    if (type == 1) {
        $("#day").css("color","red");
        $("#month").css("color","");
        $("#year").css("color","");
    } else if (type == 2) {
        $("#month").css("color","red");
        $("#day").css("color","");
        $("#year").css("color","");
    } else if (type == 3) {
        $("#year").css("color","red");
        $("#day").css("color","");
        $("#month").css("color","");
    }
    $.ajax({
        type: 'POST',
        url: '../suplierHome/orderCount',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({type:type}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $("#orderCount").html(res.data.countSum)
            $("#totalSum").html(res.data.totalSum)
            $("#avgTotal").html(res.data.avgTotal)
        },
        error: function (err) {
            layer.msg(err)
        }
    });
}

$('#supplierShow').click(function () {
    location.href='../suplierHome/supplierShowHtml';
})


var waitingOrderStatus = 1;
var productionOrderStatus = 2;
//默认加载
homeOrderList(waitingOrderStatus)
homeOrderList(productionOrderStatus)
$('#waitingOrder').click(function () {
    homeOrderList(waitingOrderStatus)
})
$('#productionOrder').click(function () {
    homeOrderList(productionOrderStatus)
})
//获取首页订单信息
function homeOrderList (status) {
    $.ajax({
        type: 'POST',
        url: '../suplierOrder/homeOrderList',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({status:status}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            var div = '';
            for(var i = 0; i < res.data.length; i++){
                div += ' <div class="order-wrap" id="'+res.data[i].id+'">\n' +
                    '                        <div class="order-info-topic">订单号：<span>'+ res.data[i].orderNo +'</span></div>\n' +
                    '                        <div class="order-infos clearfix">\n' +
                    '                            <img class="order-infos-model" src="'+res.data[i].href+'" alt="">\n' +
                    '                            <div class="order-infos-item">\n' +
                    '                                <span>货号<span>'+res.data[i].pno+'</span></span><br>\n' +
                    '                                <span class="order-infos-span">单价：<span>￥'+res.data[i].price+'</span>&nbsp;&nbsp;数量：<span>'+res.data[i].qty+'</span></span><br>\n' +
                    '                                <span class="order-infos-span">合计：<span>￥'+res.data[i].subtotal+'</span></span><br>\n' +
                    '                                <span class="order-infos-span">期望发货日期：<span>'+res.data[i].expectsendDate+'</span></span>\n' +
                    '                            </div>\n'
                if (status == waitingOrderStatus) {
                    div +='                     <div class="make-order" onclick="orderDetail('+res.data[i].id+')">确认<br>订单</div>\n'
                } else if (status == productionOrderStatus) {
                    div +='                     <div class="make-order" onclick="orderDetail('+res.data[i].id+')">生产<br>完成</div>\n'
                }
                div +=
                    '                        </div>\n' +
                    '    </div>'


            }
            if (status == waitingOrderStatus) {
                $('#waitingContent').html(div);
            } else if (status == productionOrderStatus) {
                $('#productionContent').html(div);
            }

        },
        error: function (err) {
            layer.msg(err)
        }
    });
}

function orderDetail(orderId) {
    location.href = '../suplierOrder/orderDetailHtml?id='+orderId;
}