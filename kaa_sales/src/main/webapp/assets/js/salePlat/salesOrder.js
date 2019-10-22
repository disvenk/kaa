/**
 * Created by 12452 on 2017/10/20.
 */
var receiver = '';
var mobile = '';
var orderno = '';
var orderno_suplier = '';
var data={
    pageNum:1,
};
var urlle = sessionStorage.getItem('urllen');
var productIds = '';
$.ajax({
    type: 'POST',
    url: '../account/loginInfo',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100) {
            if (res.data.userStatus != '0') {
                layer.alert('请先完成入驻', {icon:0}, function () {
                    window.open('../salesHome/joinHtml?type='+res.data.userStatus);
                });
            }
        }else {
//                    alert(res.message);
        }
    },
    error: function (err) {
//                alert(err.message);
    }
});
function post() {
    $.ajax({
        type: 'POST',
        url:  '../storeSalesOrder/findStoreSalesOrderList',
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
            if(res.data.length<1){
                $('.tcdPageCode').html('');
            }
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    data.pageNum = $(".current").html();
                    ajax();
                }
            });
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }

    });
}

post();

//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    receiver = $('#orderBuyer').val();
    mobile = $('#orderBuyerPhone').val();
    orderno = $('#orderInputNo').val();
    suplierOrderNo = $('#orderBuyNo').val();
    data = {
        receiver:receiver,
        mobile:mobile,
        orderNo:orderno,
        suplierOrderNo:suplierOrderNo,
        pageNum:1
    };
    // console.log(data);
    post();
});
//重置
$('#orderReset').click(function () {
    $('#orderBuyer').val('');
    $('#orderBuyerPhone').val('');
    $('#orderInputNo').val('');
    $('#orderBuyNo').val('');
    receiver = $('#orderBuyer').val('');
    mobile = $('#orderBuyerPhone').val('');
    orderno = $('#orderInputNo').val('');
    orderno_suplier = $('#orderBuyNo').val('');
    ajax();
});

// ajax加载列表
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../storeSalesOrder/findStoreSalesOrderList',
        dataType: 'json',

        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res)
        }
    })
};
//加载数据
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var table1 = '';
        table1 += '<tr id="'+res.data[i].orderId+'" style="background-color:rgb(243,242,244);height: 50px;">' +
            '<td style="border: none"><span class="color-blue">下单日期：</span>' + res.data[i].orderDate + '</td>' +
            '<td style="border: none"><span class="color-blue">订单号：</span>' + res.data[i].orderNo + '</td>' +
            '<td style="border: none"><span class="color-blue">交易状态：</span>' + res.data[i].orderStatus + '</td>' +
            '<td style="border: none"><span class="color-blue">合一智造采购状态：</span>' + res.data[i].orderSuplierStatus + '</td>' +
            '<td style="border: none" class="null"></td></tr>';

        for (var j = 0; j < res.data[i].detailList.length; j++) {
            var detailListArr = res.data[i].detailList[j];
            table1 += '<tr class="' + res.data[i].orderId + '">' +
                '<td class="'+res.data[i].supplierOrderId+'">' +
                '<div class="order-info-wrap">' +
                '<img class="order-info-img"  src="' + detailListArr.productPicture + '" alt=""><div class="sales-order-info">' +
                '<div class="order-info-name">名称：<span>' + detailListArr.productName + '</div>' +
                '<div class="order-info-desc">颜色：<span>' + detailListArr.color + '</span> &nbsp;&nbsp;尺寸：<span>' + detailListArr.size + '</span></div>' +
                '</div></div></td>' +
                '<td>￥' + detailListArr.price + '</td><td>X' + detailListArr.qty + '</td>' +
                '<td class="'+res.data[i].productIds+'">￥' + detailListArr.price * detailListArr.qty + '</td>' +
                '<td>'
            if (res.data[i].orderStatus == '待发货' && res.data[i].orderSuplierStatus == '未采购') {
                table1 += '<div class="sales-order-operate send">直接发货</div>' +
                    '<div class="sales-order-operate createSupplierOrder">采购货品</div>' +
                    '<div class="sales-order-operate orderDetail">订单详情</div>' +
                    '<div class="sales-order-operate orderEdit">编辑</div>' +
                    '<div class="sales-order-operate salesCancel" data-toggle="modal" data-target="#myModal">删除</div>' +
                    '</td>' +
                    '</tr>';
            } else if (res.data[i].orderStatus == '待发货' && res.data[i].orderSuplierStatus == '采购中') {
                table1 += '<div class="sales-order-operate supplierOrderDetail">采购订单详情</div>' +
                    '<div class="sales-order-operate orderDetail">订单详情</div>' +
                    '</td>' +
                    '</tr>';
            }
            else if (res.data[i].orderStatus == '已发货' && res.data[i].orderSuplierStatus == '已完成') {
                table1 += '<div class="sales-order-operate deliveryNo">查看快递单号</div>' +
                    '<div class="sales-order-operate supplierOrderDetail">采购订单详情</div>' +
                    '<div class="sales-order-operate orderDetail">订单详情</div>' +
                    '</td>' +
                    '</tr>';
            }
            else if (res.data[i].orderStatus == '已发货' && res.data[i].orderSuplierStatus == '未采购') {
                table1 += '<div class="sales-order-operate deliveryNo">查看快递单号</div>' +
                    '<div class="sales-order-operate orderDetail">订单详情</div>' +
                    '</td>' +
                    '</tr>';
            }
            else if (res.data[i].orderStatus == '待发货' && res.data[i].orderSuplierStatus == '已完成') {
                table1 += '<div class="sales-order-operate send">直接发货</div>' +
                    '<div class="sales-order-operate supplierOrderDetail">采购订单详情</div>' +
                    '<div class="sales-order-operate orderDetail">订单详情</div>' +
                    '</td>' +
                    '</tr>';
            }
        }
        $('#tbody').append(table1);
    }

    //合并列
    var tr = $('#tbody tr');
    for(var i = 0; i < tr.length; i++) {
        if (tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')) {
            tr.eq(i + 1).find('td').eq(4).remove()
            tr.eq(i).find('td').eq(4).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);
        }
    }

    $('.send').click(function () {
        $('#sendNow').modal('show');
        var orderId = $(this).parent().parent().attr('class');
        //快递公司
        $.ajax({
            type: 'POST',
            url: urlle + '/base/getBaseDataList',
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
                    $('#sendN').click(function () {
                        var param = {
                            orderId: orderId,
                            deliveryCompanyName: $('#deliveryCompany').val(),
                            deliveryNo: $('#deliveryNo').val(),
                            deliverCompanyId: $('#deliveryCompany').find('option:checked').attr('id')
                        };
                        if($('#deliveryNo').val() == ''){
                            alert('请填写单号')
                        }else{
                            $.ajax({
                                url: urlle + 'storeSalesOrder/saveStoSalesOrderDelivery',
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
                        }
                    })

                } else {
                    alert('网络异常，请稍后再试...')
                }
            },
            error: function (err) {
                alert(err.message)
            }
        });

    });
    //物流信息
    $('.deliveryNo').click(function () {
        $('#sendInformation').modal('show');
        $.ajax({
            type: 'POST',
            url: '../storeSalesOrder/getStoSalesOrderDeliveryById',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: $(this).parent().parent().attr('class')}),
            success: function (res) {
                var com = res.data.deliveryCode;
                var nu = res.data.deliveryNo;
                $('#deliveryNum').html(nu);
                $('#deliveryCom').html(res.data.deliveryCompanyName);
                $.ajax({
                    type: 'POST',
                    url: '../express/searchExpress',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({com: com, nu: nu}),
                    success: function (res) {
                        $('#iframe').attr('src', res.data.result)
                    },
                    error: function (err) {
                        alert(err.message);
                    }
                });
            },
            error: function (err) {
                alert(err.message);
            }
        });
    });
    // 删除
    $('.salesCancel').click(function(){
        $('#myNodal').modal('show');
        var pid = $(this).parent().parent().attr('class');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: '../storeSalesOrder/removeStoSalesOrder',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: pid}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    ajax();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })

    })

    // 订单详情
    $('.orderDetail').click(function () {
        orderId = $(this).parent().parent().attr('class');
        location.href = '../storeSalesOrder/salesDetailHtml?id='+orderId+'';
    })

    //采购订单详情
    $('.supplierOrderDetail').click(function () {
        supplierOrderId = $(this).parent().parent().find('td').eq(0).attr('class');
        location.href = '../storeSuplierOrder/buyOrderDetailHtml?id='+supplierOrderId+'';
    })

    //采购货品
    $('.createSupplierOrder').click(function () {
        orderId = $(this).parent().parent().attr('class');
        productIds = $(this).parent().prev().attr('class');
        location.href='../storeSuplierOrder/buyOrderBuyHtml?ids='+productIds + "&salesOrderId=" + orderId;
    })

    // 编辑
    $('.orderEdit').click(function(){
        orderId = $(this).parent().parent().attr('class');
        location.href = '../storeSalesOrder/orderDetailEditHtml?id='+orderId+'';
    })
}

function detail(orderno) {
    location.href = 'orderDetail.html?id=' + orderno;
};

function edit(orderno) {
    location.href = 'orderDetailEdit.html?id=' + orderno;
}