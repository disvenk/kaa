var urlle = sessionStorage.getItem('urllen');
var pageNum = 1;
var data = {
    pageNum: pageNum,
    productName: '',
    nameMobile: '',
    suplierOrderNo: '',
    orderStatus: '',
    startTime: '',
    endTime: '',
    producedStatus: '',
    channelType: '',
    channelName: ''
};
function post() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'orderManage/findSuplierOrderList',
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
                    data.pageNum = pageNum;
                    ajax();
                }
            });
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    });
};
post();
//搜索
$('#search').click(function () {
    $('.spinner').show();
    var productName = $('#productName').val();
    var nameMobile = $('#nameMobile').val();
    var suplierOrderNo = $('#suplierOrderNo').val();
    var startTime = $('#datepickerStart').val();
    var endTime = $('#datepickerEnd').val();
    var producedStatus = $('#producedStatus').find("option:checked").val();
    var channelType = $('#channelType').find("option:selected").val();
    var channelName = $('#channelName').val();
    var orderStatus = $('#orderStatus').find("option:selected").val();
    data = {
        productName: productName,
        nameMobile: nameMobile,
        suplierOrderNo: suplierOrderNo,
        startTime: startTime,
        endTime: endTime,
        orderStatus: orderStatus,
        producedStatus: producedStatus,
        channelType: channelType,
        channelName: channelName,
        pageNum: 1
    };
    post();
});
//重置
$('#resert').click(function () {
    $('#productName').val('');
    $('#nameMobile').val('');
    $('#suplierOrderNo').val('');
    $('#datepickerStart').val('');
    $('#datepickerEnd').val('');
    $('#producedStatus').find("option:checked").val('');
    $('#channelType').find("option:selected").val('');
    $('#channelName').val('');
    $('#orderStatus').find("option:selected").val('');
    data = {
        productName: '',
        nameMobile: '',
        suplierOrderNo: '',
        startTime: '',
        endTime: '',
        orderStatus: '',
        producedStatus: '',
        channelType: '',
        channelName: '',
        pageNum: 1
    };
    post();
})
//请求数据
function ajax() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'orderManage/findSuplierOrderList',
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
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    });
}
//加载数据
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    var table1 = '';
    for (var i = 0; i < res.data.length; i++) {
        table1 += '<tr style="background-color:rgb(243,242,244);height: 50px">' +
            '<td style="border: 0"><span class="color-blue">订单号:</span>' + res.data[i].suplierOrderNo + '</td>' +
            '<td style="border: 0"><span class="color-blue">下单日期：</span>' + res.data[i].orderDate + '</td>' +
            '<td style="border: 0"><span class="color-blue">交易状态：</span>' + res.data[i].statusName + '</td>' +
            '<td style="border: 0"><span >工单状态：</span><span class="color-blue" onclick="selectAll('+ res.data[i].suplierOrderNo +')">查看所有</span></td>' +
            // '<td><span class="color-blue">生产状态：</span>' + res.data[i].producedStatusName + '</td>' +
            '<td style="border: 0"></td><td style="border: 0"></td><td style="border: 0"></td></tr>';
        for (var j = 0; j < res.data[i].productList.length; j++) {
            var detailListArr = res.data[i].productList[j];
            table1 += '<tr class="'+res.data[i].id+'"><td><div class="order-info-wrap"><img class="order-info-img"  src="' + detailListArr.href + '" alt=""><div class="sales-order-info">' +
                '<div class="order-info-name" style="width: 215px">名称：<span>' + detailListArr.productName + '</div>' +
                '<div class="order-info-code" style="width: 215px;text-align: left">商品ID：<span>' + detailListArr.productCode + '</span></div>' +
                '<div class="order-info-desc" style="width: 215px">颜色：<span>' + detailListArr.color + '</span> &nbsp;&nbsp;尺寸：<span>' + detailListArr.size + '</span></div>' +
                '</div></div></td>' +
                '<td>￥' + detailListArr.price + '</td><td>X' + detailListArr.qty + '</td>' +
                '<td>￥'+detailListArr.subtotal+ '</td>'+
                '<td>' +
                '   <div>'+ res.data[i].channelType +'</div>' +
                '   <div>'+ res.data[i].channelName +'</div>' +
                '   <div>\n</div>';
            if (res.data[i].salesOrderNo != null) {
                table1 += '   <div>订单号</div>' +
                    '   <div>'+ res.data[i].salesOrderNo +'</div>';
            }

            table1 += '</td>' +
                '<td>' ;
            // if (detailListArr.suplierId == null) {
            //     table1 += '<div class="sales-order-operate mt4 supplier">' +
            //         '<span class="color-blue" onclick="suplierList('+res.data[i].id+','+detailListArr.detId+')">指派供应商</span></div>' ;
            // } else {
            //     table1 += '<div class="sales-order-operate">'+detailListArr.suplierName+'</div>' +
            //         '<div class="sales-order-operate mt4">' +
            //         '<span class="color-blue" onclick="suplierList('+res.data[i].id+','+detailListArr.detId+')">更改供应商</span></div>' +
            //         '<div><span class="color-blue">生产状态：</span>' + detailListArr.producedStatusName + '</div>' +
            //         '<div class="sales-order-operate mt4" onclick="inspectionLog('+res.data[i].id+','+detailListArr.pid+',\''+detailListArr.color+'\',\''+detailListArr.size+'\')">质检记录</div>' ;
            // }
            table1 +=  '   <div>'+ res.data[i].name +'</div>' +
            '   <div>'+ res.data[i].mobile +'</div>';

            table1 += '</td>' +
                '<td>' +
                '<div class="sales-order-operate orderEdit">编辑</div>';
            var status = res.data[i].status;
            if (status == 4) {
                table1 +=  '<div class="sales-order-operate mt4" onclick="suplierOrderLogistics('+res.data[i].id+')">物流信息</div>';
            } else if (status == 1) {
                table1 +=  '<div class="sales-order-operate mt4" onclick="suplierOrderShipping('+res.data[i].id+')">发货</div>';
            }
            table1 += '<div class="sales-order-operate mt4 orderDetail">订单详情</div>' +
                '</td></td></tr>';
        }
    }
    $('#tbody').append(table1);

    //详情
    $('.orderDetail').click(function () {
        var id = $(this).parent().parent().attr('class');
        location.href = '../orderManage/orderManageDetailHtml?id='+id+''
    });
    //编辑
    $('.orderEdit').click(function () {
        var id = $(this).parent().parent().attr('class');
        location.href = '../orderManage/orderManageEditHtml?id='+id+''
    });

    //合并列
    var tr = $('#tbody tr');
    for(var i = 0; i < tr.length; i++){
        if(tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')){
            tr.eq(i + 1).find('td').eq(6).remove();
            tr.eq(i).find('td').eq(6).attr('rowspan', $('.'+tr.eq(i).attr('class')+'').length);
        }
    }


}

//供应商列表
var orderId = '';
var detailId = '';
var submit = false;
function suplierList(id, detId) {
    $('#mymodal').modal('show');
    if (id != null) {orderId = id;}
    if (detId != null) {detailId = detId;}
    submit = true;
    var data = {
        name: $("#supplierName").val(),
        size: $("#supplierSize").val(),
        scope: $("#supplierScope").val(),
    }
    $.ajax({
        type: 'POST',
        url: '../orderManage/suplierList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                $('#supplierList').html('');
                var supplierList = '';
                for (var i=0; i<res.data.length; i++) {
                    supplierList += '<tr>\n' +
                        '                       <td>'+res.data[i].name+'</td>\n' +
                        '                       <td>'+res.data[i].contact+'</td>\n' +
                        '                       <td>'+res.data[i].contactPhone+'</td>\n' +
                        '                       <td>'+res.data[i].address+'</td>\n' +
                        '                       <td>'+res.data[i].openYears+'</td>\n' +
                        '                       <td>'+res.data[i].scope+'</td>\n' +
                        '                       <td><input type="number" style="width: 70px;height: 26px;border-radius: 6px !important;" class="outputPrice'+res.data[i].id+'"></td>\n' +
                        '                       <td><span onclick="suplierAllotment('+res.data[i].id+')">选择</span></td>\n' +
                        '                   </tr>';
                }
                $("#supplierList").append(supplierList);
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}


//分配供应商
function suplierAllotment(id) {
    if (!submit) {
        alert("不能重复提交")
        return ;
    }
    submit = false;
    var data = {
        orderId: orderId,
        detailId: detailId,
        suplierId: id,
        outputPrice: $('.outputPrice'+id).val()
    }
    $.ajax({
        type: 'POST',
        url: '../orderManage/suplierAllotment',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                alert("成功");
                ajax()
                $('#mymodal').modal('hide');
            }else{
                alert(res.message);
                submit = true;
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

var orderId_log = '';
var pid_log = '';
var color_log = '';
var size_log = '';
//质检记录
function inspectionLog(id, pid, color, size) {
    $('#mymodalSelf').modal('show');
    $.ajax({
        type: 'POST',
        url: '../orderManage/supOrderDeliveryLogList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({orderId:id, pid:pid, color:color, size:size}),
        success: function (res) {
            if(res.stateCode == 100){
                orderId_log = id;
                pid_log = pid;
                color_log = color;
                size_log = size;
                $("#logTbody").html("");
                for (var i=0; i<res.data.length; i ++) {
                    var tr = '<tr>\n' +
                        '                        <td>'+res.data[i].createTime+'</td>\n' +
                        '                        <td>'+res.data[i].personnel+'</td>\n' +
                        '                        <td>'+res.data[i].instruction+'</td>\n' +
                        '                        <td>'+res.data[i].action+'</td>\n' +
                        '                    </tr>';
                    $("#logTbody").append(tr);
                }
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
};

//质检
function updateProducedStatus(status) {
    var data = {
        orderId: orderId_log,
        pid: pid_log,
        color: color_log,
        size: size_log,
        producedStatus: status,
        instruction: $("#instruction").val(),
        personnel: $("#personnel").val()
    };
    $.ajax({
        type: 'POST',
        url: '../orderManage/updateProducedStatus',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                alert("成功");
                location.reload();
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//加载快递公司
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
        } else {
            alert('网络异常，请稍后再试...')
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

//发货
function suplierOrderShipping(id) {
    $('#shipping').modal('show');
    $("#orderShipping").click(function () {
        if (!$('#deliveryNo').val()) {
            alert("请输入快递单号");
            return;
        }
        var param = {
            id: id,
            deliveryCompanyName: $('#deliveryCompany').val(),
            deliveryNo: $('#deliveryNo').val(),
            deliveryCompany: $('#deliveryCompany').find('option:checked').attr('id')
        };
        $.ajax({
            type: 'POST',
            url: '../orderManage/suplierOrderShipping',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(param),
            success: function (res) {
                if (res.stateCode == 100) {
                    alert("成功");
                    location.reload();
                } else {
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message)
            }
        });
    })
}

//物流信息
function suplierOrderLogistics(id) {
    $('#sendInformation').modal('show');
    $.ajax({
        type: 'POST',
        url: '../orderManage/suplierOrderLogistics',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            if (res.stateCode == 100) {
                var com = res.data.deliveryCode;
                var nu = res.data.deliveryNo;
                $("#deliveryCompanyLogName").html(res.data.deliveryCompanyName)
                $("#deliveryLogNo").html(nu)
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
            } else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
function selectAll(id) {
    $('#remarkLogModal').modal('show');
    $.ajax({
        type: 'POST',
        url: '../orderManage/supOrderStatusList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({suplierOrderNo: id}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#remarkLogModal tbody').html('');
                for(var i = 0; i < res.data.length; i++){
                    var tr = '';
                    tr += '<tr id="'+res.data[i].orderId+'">\n' +
                        '                                <td>'+res.data[i].orderNo+'</td>\n' +
                        '                                <td>'+res.data[i].producedStatusName+'</td>\n' +
                        '                                <td>' +
                        '                                   <span class="storage-operate ml10p" onclick="detail('+res.data[i].orderId+')">查看</span>' +
                        '                                </td>\n' +
                        '                            </tr>';
                    $('#remarkLogModal tbody').append(tr);
                }
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}
//详情
function detail(id) {
    location.href='../WOManage/kaaDetailHtml?id='+id+'';
}