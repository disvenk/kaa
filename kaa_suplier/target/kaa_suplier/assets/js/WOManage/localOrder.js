var url = window.location.href;
var producedStatus = url.split('?')[1];
//初始化加载所有列表数据
var pageNum = 1;
var current1;
var current2;
var data = {
    orderNo: '',
    pno: '',
    createdDateSta:'',
    createdDateEnd: '',
    deliveryDateStr: '',
    deliveryDateEnd: '',
    customerId: '',
    pageNum: pageNum,
    producedStatus: producedStatus
};
if(producedStatus == 2){
    $('#btnAdd').show();
}else{
    $('#btnAdd').hide();
}
function current() {
    var dd = new Date();
    dd.setDate(dd.getDate());//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate();
    current1 = y+"-"+m+"-"+d+' 00:00:00';
    current2 = y+"-"+m+"-"+d+' 23:59:59';
    $('#give').val(y+"-"+m+"-"+d+' - '+y+"-"+m+"-"+d);
    data = {
        orderNo: '',
        pno: '',
        createdDateSta:'',
        createdDateEnd: '',
        deliveryDateStr: current1,
        deliveryDateEnd: current2,
        customerId: '',
        pageNum: pageNum,
        producedStatus: 4
    };
}
function last() {
    var dd = new Date();
    dd.setDate(dd.getDate());//获取AddDayCount天后的日期
    var y = dd.getFullYear();
    var m = dd.getMonth()+1;//获取当前月份的日期
    var d = dd.getDate() - 1;
    current1 = y+"-"+m+"-"+d+' 00:00:00';
    current2 = y+"-"+m+"-"+d+' 23:59:59';
    $('#give').val(y+"-"+m+"-"+d+' - '+y+"-"+m+"-"+d);
    data = {
        orderNo: '',
        pno: '',
        createdDateSta:'',
        createdDateEnd: '',
        deliveryDateStr: current1,
        deliveryDateEnd: current2,
        customerId: '',
        pageNum: pageNum,
        producedStatus: 4
    };
}
if(url.split('?')[1] == 66){
    data.producedStatus = 4;
    last();
    initData();
}else if(url.split('?')[1] == 99){
    data.producedStatus = 4;
    current();
    initData();
}else{
    data = {
        orderNo: '',
        pno: '',
        createdDateSta:'',
        createdDateEnd: '',
        deliveryDateStr: '',
        deliveryDateEnd: '',
        customerId: '',
        pageNum: pageNum,
        producedStatus: url.split('?')[1]
    };
    initData();
}
window.parent.order();
window.parent.factory();

laydate.render({
    elem: '#placeOrder',range: true
});
laydate.render({
    elem: '#give',range: true
});

//客户
$.ajax({
    type: 'POST',
    url: '../supplierCustomer/selectSupplierCustomer',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({}),
    success:function (res) {
       if(res.stateCode == 100){
           $('#select').append('<option value=""></option>');
           for(var i = 0; i < res.data.length; i++){
               var option = '';
               option += '<option value="'+res.data[i].id+'">'+res.data[i].customer+'</option>';
               $('#select').append(option);
           }
       }else{
           layer.alert(res.message, {icon:0})
       }
    },error: function (err) {
        layer.alert(err.message, {icon:0})
    }
})
function initData() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    // 加载列表
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/orderList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success:function (res) {
            layer.close(index);
            $('#tbody').html('');
            if(res.stateCode == 100){
                $('#tbody').html('');
                $('#sample-table-2 thead').html('');
                if(res.data.length > 0){
                    $('.tcdPageCode').show();
                    $('#display').hide();
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
                }else{
                    $('.tcdPageCode').hide();
                    $('#display').show();
                }

            }else{
                layer.alert(res.message, {icon:0});
            }
        }
    })
}
function load(res) {
    $('#tbody').html('');
    $('#sample-table-2 thead').html('');
    $('#sample-table-2 thead').append('<tr>\n' +
        '                                    <td>订单号</td>\n' +
        '                                    <td>内部编号</td>\n' +
        '                                    <td>供应商产品编号</td>\n' +
        '                                    <td>图片</td>\n' +
        '                                    <td>客户</td>\n' +
        '                                    <td>分类</td>\n' +
        '                                    <td>颜色</td>\n' +
        '                                    <td>尺寸</td>\n' +
        '                                    <td>件数</td>\n' +
        '                                    <td>销售单价</td>\n' +
        '                                    <td>下单日期</td>\n' +
        '                                    <td>交货日期</td>\n' +
        '                                    <td>快递公司</td>\n' +
        '                                    <td>快递单号</td>\n' +
        '                                    <td>备注</td>\n' +
        '                                    <td>状态</td>\n' +
        '                                    <td>操作</td>\n' +
        '                                </tr>');
    for(var i=0;i<res.data.length;i++){
        if(res.data[i].subTime == '' || res.data[i].subTime == null){
            res.data[i].subTime == '';
        }
        var tr='';
        tr+='<tr id="'+res.data[i].id+'">' +
            '<td>'+res.data[i].orderNo+'</td>' +
            '<td>'+res.data[i].insideOrderNo+'</td>' +
            '<td>'+res.data[i].pno+'</td>' +
            '<td><img style="width: 60px;height: 60px" src="'+res.data[i].href+'" alt=""></td>' +
            '<td>'+res.data[i].customer+'</td>' +
            '<td>'+res.data[i].categoryName+'</td>' +
            '<td>'+res.data[i].color+'</td>' +
            '<td>'+res.data[i].size+'</td>' +
            '<td>'+res.data[i].qty+'</td>' +
            '<td>'+res.data[i].price+'</td>' +
            '<td>'+res.data[i].createdDate+'</td>' +
            '<td><div style="color: #000">'+res.data[i].deliveryDate+'</div><div style="color: red">'+res.data[i].subTime+'</div></td>' +
            '<td>'+res.data[i].deliveryCompanyName+'</td>' +
            '<td onclick="delivery('+res.data[i].id+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryNo+'</td>' +
            '<td onclick="remarkLog('+res.data[i].id+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].remarks+'</td>' +
            '<td>'+res.data[i].producedStatusName+'</td>';
            if(producedStatus == 2){
               tr += '<td style="line-height: 25px">' +
                   '<div value="'+res.data[i].id+'" class="btnEdit">编辑</div>' +
                   '<div class="btnDel">删除</div>' +
                   '<span style="border: 0;border-radius: 5px;background-color: #3b5999;color: #fff;outline:none;cursor: pointer;' +
                   'padding: 4px 10px" onclick="btnMake('+res.data[i].id+')">生成工单</span></td></tr>';
            }else if(producedStatus == 3){
                tr += '<td style="line-height: 25px">' +
                    '<div value="'+res.data[i].id+'" onclick="detail('+res.data[i].id+')">详情</div>' +
                    '<span style="border: 0;border-radius: 5px;background-color: #3b5999;color: #fff;outline:none;cursor: pointer;' +
                    'padding: 4px 10px" onclick="sendCargo('+res.data[i].id+')">发货</span></td></tr>';
            }else if(producedStatus == 4 || url.split('?')[1] == 66 || url.split('?')[1] == 66){
            tr += '<td style="line-height: 25px">' +
                '<div value="'+res.data[i].id+'" onclick="detail('+res.data[i].id+')">详情</div>' +
                '<span style="border: 0;border-radius: 5px;background-color: #3b5999;color: #fff;outline:none;cursor: pointer;' +
                'padding: 4px 10px" onclick="finish('+res.data[i].id+')">结束订单</span></td></tr>';
        }else if(producedStatus == 7){
                tr += '<td style="line-height: 25px">' +
                    '<div value="'+res.data[i].id+'" onclick="detail('+res.data[i].id+')">详情</div></td></tr>';
            }
        $('#tbody').append(tr);
    }
    //表格格式化
    function initTableCheckbox() {
        var $thr = '';
        var $thr = $('#sample-table-2 thead tr');
        var $checkAllTh = '';
        var $checkAllTh = $('<th class="center"><input type="checkbox" id="checkAll" name="checkAll" /></th>');
        /*将全选/反选复选框添加到表头最前，即增加一列*/
        $thr.prepend($checkAllTh);
        /*“全选/反选”复选框*/
        var $checkAll = $thr.find('input');
        $checkAll.click(function (event) {
            /*将所有行的选中状态设成全选框的选中状态*/
            $tbr.find('input').prop('checked', $(this).prop('checked'));
            /*并调整所有选中行的CSS样式*/
            if ($(this).prop('checked')) {
                $tbr.find('input').parent().parent().addClass('xuanzhong');
            } else {
                $tbr.find('input').parent().parent().removeClass('xuanzhong');
            }
            /*阻止向上冒泡，以防再次触发点击操作*/
            event.stopPropagation();
        });
        /*点击全选框所在单元格时也触发全选框的点击操作*/
        $checkAllTh.click(function () {
            $(this).find('input').click();

        });
        var $tbr = $('#sample-table-2 tbody tr');
        var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
        /*每一行都在最前面插入一个选中复选框的单元格*/
        $tbr.prepend($checkItemTd);
        /*点击每一行的选中复选框时*/
        $tbr.find('input').click(function (event) {
            /*调整选中行的CSS样式*/
            $(this).parent().parent().toggleClass('xuanzhong');
            /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
            $checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
            /*阻止向上冒泡，以防再次触发点击操作*/
            event.stopPropagation();
        });
    }

    initTableCheckbox();
    // 删除
    var id;
    $('.btnDel').click(function () {
        $('#deleteModal').modal('show');
        var _this = $(this);
        id = _this.parent().parent().attr('id');
        $('#sureDel').unbind('click');
        $('#sureDel').one('click',function () {
            $.ajax({
                type: 'POST',
                url: '../suplierSalesOrder/removeOrder',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#deleteModal').modal('hide');
                    if(res.stateCode == 100){
                        window.parent.order();
                        window.parent.factory();
                        initData();
                    }else{
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.alert(res.message, {icon: 0});
                }
            })
        })
    })
    $('.btnEdit').click(function () {
        location.href='../WOManage/localOrderEditHtml?id='+$(this).attr('value');
    })
}

function queryData(data) {
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/findOrderCount',
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
function btnMake(id) {
    $('#finishModal').modal('show');
    //生成工单
    $('#sure3').unbind('click');
    $('#sure3').one('click' ,function () {
        index = layer.load(2, {shade: [0.6,'#000']});
        var sorder = $('#modal_radio input[name=order]:checked').attr('value');
        $('#finishModal').modal('hide');
        if(sorder == 1){
            $.ajax({
                type: 'POST',
                url: '../suplierSalesOrder/saveSupOrderOne',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    if(res.stateCode == 100){
                        layer.close(index);
                        initData();
                        window.parent.order();
                        window.parent.factory();
                    }else{
                        layer.close(index);
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.close(index);
                    layer.alert(res.message, {icon: 0});
                }
            })
        }else{
            $.ajax({
                type: 'POST',
                url: '../suplierSalesOrder/saveSupOrderMany',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#finishModal').modal('hide');
                    layer.close(index);
                    if(res.stateCode == 100){
                        initData();
                        window.parent.order();
                        window.parent.factory();
                    }else{
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.close(index);
                    layer.alert(res.message, {icon: 0});
                }
            })
        }
    });

}
//搜索

$('#btnQuery').click(function () {
    data = {
        orderNo: $('#woInput').val(),
        pno: $('#goodsNoInput').val(),
        createdDateSta:$('#placeOrder').val().split(' ')[0]?$('#placeOrder').val().split(' ')[0] + ' 00:00:00': '',
        createdDateEnd: $('#placeOrder').val().split(' ')[2]?$('#placeOrder').val().split(' ')[2] + ' 23:59:59': '',
        deliveryDateStr: $('#give').val().split(' ')[0]?$('#give').val().split(' ')[0] + ' 00:00:00': '',
        deliveryDateEnd: $('#give').val().split(' ')[2]?$('#give').val().split(' ')[2] + ' 23:59:59': '',
        customerId: $('#select').find('option:selected').attr('value'),
        pageNum: 1,
        producedStatus: producedStatus

    };
    initData()
});

// 重置

$('#resert').click(function () {
    $('#woInput').val('');
    $('#goodsNoInput').val('');
    $('#placeOrder').val('');
    $('#give').val('');
    data = {
        orderNo: '',
        pno: '',
        createdDateSta:'',
        createdDateEnd: '',
        deliveryDateStr: '',
        deliveryDateEnd: '',
        customerId: '',
        pageNum: 1,
        producedStatus: producedStatus
    };
    initData()
});
$('#btnAdd').click(function () {
    location.href='../WOManage/localOrderAddHtml'
});
function sendCargo(id) {
    $('#sendModal').modal('show');
    //快递公司
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
                    option += '<option id="' + res.data[i].id + '" value=' + res.data[i].name + '>' + res.data[i].name + '</option>';
                    $('#company').append(option);
                }
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    });
    $('#sure1').unbind('click');
    $('#sure1').one('click',function () {
        var ship = {
            deliveryCompany: $('#company').find('option:checked').attr('id'),
            deliveryCompanyName: $('#company').val(),
            deliveryNo: $('#orderNo').val(),
            id: id
        };
        $('#sendModal').modal('hide');
        $.ajax({
            type: 'POST',
            url: '../suplierSalesOrder/orderDeliver',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ship),
            success: function (res) {
                if(res.stateCode == 100){
                    initData();
                    window.parent.order();
                    window.parent.factory();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    })
}
//结束工单
function finish(id) {
    $('#overModal').modal('show');
    $('#sure4').unbind('click');
    $('#sure4').one('click' ,function () {
        $('#overModal').modal('hide');
        $.ajax({
            type: 'POST',
            url: '../suplierSalesOrder/orderFinish',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    initData();
                    window.parent.order();
                    window.parent.factory();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(res.message, {icon: 0});
            }
        })
    })
}
//详情
function detail(id) {
    location.href = '../WOManage/localOrderDetailHtml?id='+id;
}
//批量备注
function remarkAll() {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要备注的项', {icon: 0})
    } else{
        $('#remarkModal').modal('show');
        $('#sure7').unbind('click');
        $('#sure7').one('click',function (){
            var remarkIds = [];
            var length = $('#tbody > .xuanzhong');
            for(var i = 0; i < length.length; i++){
                var ob = {
                    id: length.eq(i).attr('id')
                }
                remarkIds[i] = ob;
            }
            var remarkParams = {
                remarks: $('#remarkModal .update-text').val(),
                supOrderIds: remarkIds
            };
            $.ajax({
                type: 'POST',
                url: '../suplierSalesOrder/orderRemarksUpdate',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(remarkParams),
                success: function (res) {
                    $('#remarkModal').modal('hide');
                    if(res.stateCode == 100){
                        initData();
                    }else{
                        layer.alert(res.message, {icon: 0});
                    }
                },error: function (err) {
                    layer.alert(err.message, {icon: 0});
                }
            })
        })
    }
}

//快递信息
function delivery(id) {
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/orderDeliveryRecordList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#deliveryModal').modal('show');
            $('#deliveryModal iframe').attr('src', '');
            $('#deliveryModal tbody').html('');
            if(res.stateCode == 100){
                for(var i = 0; i < res.data.length; i++){
                    var ts = '';
                    ts += '<tr><td>'+res.data[i].createTime+'</td><td>'+res.data[i].deliveryCompanyName+'</td>' +
                        '<td>'+res.data[i].deliveryNo+'</td><td class="'+res.data[i].deliveryCom+'" style="cursor: pointer" onclick="check($(this),'+res.data[i].deliveryNo+')">查看物流</td></tr>';
                    $('#deliveryModal tbody').append(ts);
                }
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
//获取快递信息
function check(_this, no) {
    $.ajax({
        type: 'POST',
        url: '../express/searchExpress',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({com: _this.attr('class'), nu: no}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#deliveryModal iframe').attr('src', res.data.result);
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
//备注记录
function remarkLog(id) {
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/orderRemarkLogList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#remarkLogModal').modal('show');
            $('#remarkLogModal tbody').html('');
            if(res.stateCode == 100){
                for(var i = 0; i < res.data.length; i++){
                    var ts = '';
                    ts += '<tr><td>'+res.data[i].createTime+'</td><td style="max-width: 300px">'+res.data[i].remarks+'</td>' +
                        '<td>'+res.data[i].name+'</td></tr>';
                    $('#remarkLogModal tbody').append(ts);
                }
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}

//导出Excel
$("#btnOutput").click(function () {
    var url = "../suplierSalesOrder/exportExcel?orderNo=" + data.orderNo +
        "&pno="+data.pno +
        "&customerId="+data.customerId +
        "&producedStatus="+data.producedStatus +
        "&createdDateSta="+data.createdDateSta +
        "&createdDateEnd="+data.createdDateEnd +
        "&deliveryDateStr="+data.deliveryDateStr +
        "&deliveryDateEnd="+data.deliveryDateEnd
    ;
    // window.open(url);
    window.location.href = (url);

})