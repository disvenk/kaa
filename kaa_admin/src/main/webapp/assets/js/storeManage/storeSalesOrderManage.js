var urlle = sessionStorage.getItem('urllen');
var pageNum=1;
var data = {
    receiver:"",
    mobile: "",
    orderNo:"",
    ordernoSuplier:"",
    storeId: "",
    pageNum: pageNum
};

//初始化加载所有列表数据
initData(data);
//初始化门店
initStore();

function initData(data) {
    $.ajax({
        type: 'POST',
        url: '../store/findStoreSalesOrderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            //清空原表格内容
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
                return;
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
                    queryData();
                }
            });
        },

        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}

function queryData(data) {
    $.ajax({
        type: 'POST',
        url: '../store/findStoreSalesOrderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        }
    })
}

function load(res) {
    $('.spinner').hide();
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('.tcdPageCode').html('');
        return;
    }

    //绑定产生表格数据
    var tr = '';
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        tr += '<tr class="sales-order-state">\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"><span class="color-blue">订单号:</span>' + res.data[i].orderNo + '</td>\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"><span class="color-blue">下单日期：</span>' + res.data[i].orderDate + '</td>\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"><span class="color-blue">交易状态：</span>' + res.data[i].orderStatusName + '</td>\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"><span class="color-blue">合一智造采购状态：</span>' + res.data[i].orderSuplierStatusName + '</td>\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"><span class="color-blue">订单总金额：</span>' + res.data[i].ordertotal + '</td>\n' +
            '                                            <td style="border: 0;background-color: #F3F2F4"></td>\n' +
            '                                        </tr>\n';

        for (var j = 0; j < res.data[i].productList.length; j++) {
            var product = res.data[i].productList[j]
            tr += '                                        <tr class="'+res.data[i].id+'">\n' +
                '                                            <td class="'+res.data[i].orderIdSuplier+'">\n' +
                '                                                <div class="order-info-wrap">\n' +
                '                                                    <img class="order-info-img" src="' + product.href + '" alt="">\n' +
                '                                                    <div class="sales-order-info">\n' +
                '                                                        <div class="order-info-name">' + product.productName + '</div>\n' +
                '                                                        <div class="order-info-code" style="width: 215px;text-align: left">商品ID：<span>' + product.productCode + '</span></div>\n' +
                '                                                        <div class="order-info-desc">颜色：<span>' + product.color + '</span>  尺寸：<span>' + product.size + '</span></div>\n' +
                '                                                    </div>\n' +
                '                                                </div>\n' +
                '                                            </td>\n' +
                '                                            <td>￥' + product.price + '</td>\n' +
                '                                            <td>X' + product.qty + '</td>\n' +
                '                                            <td>￥' + product.subtotal + '</td>\n' +
                '                                            <td>' + res.data[i].storeName + '</td>\n' +
                '                                            <td>\n';
            //状态  0：待支付   1:待发货   2：已完成  3：已取消  4：待收货
            if (res.data[i].orderStatus == 1) {
                tr += '                                          <div class="sales-order-operate mt4" onclick="suplierOrderCancel(' + res.data[i].id + ')">查看快递单号</div>\n';
            }
            tr += '                                              <div  class="salesOrderDetail setting-btn">订单详情</div>\n';
            if (res.data[i].orderSuplierStatus != 0) {
                tr += '                                          <div class="suplierOrderDetail setting-btn">采购订单详情</div>\n';
            }
            tr += '                                           </td>\n' +
                '                                        </tr>';
        }
    }

    $('#tbody').append(tr);

    //查看功能绑定
    //销售订单详情页面
    $('.salesOrderDetail').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        location.href = '../store/storeSalesOrderManageDetailHtml?id='+id+''
    });

    //采购订单详情页面
    $('.suplierOrderDetail').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().find('td').eq(0).attr('class');
        location.href = '../store/storeSuplierOrderManageDetailHtml?id='+id+''
    });
}

//初始化门店资料
function initStore() {
    $.ajax({
        type: 'POST',
        url:  '../store/getStoStoreInfoList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $('.spinner').hide();
            $('#store').append('<option id=""></option>')
            for(var i = 0 ;i < res.data.length; i++){
                var options = '';
                options += '<option id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                $('#store').append(options);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//搜索
$('#btnQuery').click(function () {
    $('.spinner').show();
    var receiver = $('#txtReceiver').val();
    var mobile = $('#txtMobile').val();
    var orderNo = $('#txtOrderNo').val();
    var ordernoSuplier = $('#txtOrdernoSuplier').val();
    var storeId = $('#store').find("option:checked").attr("id");

    var data = {
        receiver:receiver,
        mobile: mobile,
        orderNo:orderNo,
        ordernoSuplier:ordernoSuplier,
        storeId: storeId,
        pageNum: pageNum
    };
    initData(data);
});

//重置
$('#btnReset').click(function () {
    $('#txtReceiver').val('');
    $('#txtMobile').val('');
    $('#txtOrderNo').val('');
    $('#txtOrdernoSuplier').val('');
    $('#store').val('');
    initData(data);
});

//全选框的全选功能
$("#checkAll").click(function() {
    var rows = $("#sample-table-2").find('input');
    for (var i = 0; i < rows.length; i++) {
        if (rows[i].type == "checkbox") {
            var e = rows[i];
            e.checked = this.checked;
        }
    }
});

