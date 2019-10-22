var type = 2;
//初始化加载所有列表数据
var pageNum = 1;
var data = {
    pno:'',
    orderNo: '',
    producedStatus : type, //生产状态
    createdDateSta: '',
    createdDateEnd: '',
    deliveryDateStr: '',
    deliveryDateEnd: '',
    pageNum: pageNum,
    customerId: '',
    suplierId: ''
};
// //客户
// $.ajax({
//     type: 'POST',
//     url: '../supOrder/producedCustomerOffline',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         if(res.stateCode == 100){
//             $('#customer').append('<option value="">全部</option>');
//             for(var i = 0; i < res.data.length; i++){
//                 var option = '';
//                 option += '<option value="'+res.data[i].customerId+'">'+res.data[i].customerName+'</option>';
//                 $('#customer').append(option);
//             }
//         }else{
//             // alert(res.message);
//         }
//     },
//     error: function (err) {
//     }
// });


//供应商
$.ajax({
    type: 'POST',
    url: '../product/suplierNameList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var productSupplierName = '';
            productSupplierName += '<option value="'+res.data[i].id+'">'+res.data[i].suplierName+'</option>';
            $('#supplierList').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});


//状态及数量
function platform() {
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/findOrderCount',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if(res.stateCode == 100){
                $('.wo-items').html('');
                for(var i = 0; i < res.data.length; i++){
                    var li = '';
                    li += '  <li class="wo-item" onclick="active('+res.data[i].type+', $(this))">'+res.data[i].name +'（'+res.data[i].count+'）'+'</li>';
                    $('.wo-items').append(li);
                }
                active(type,$('.wo-items li').eq(0));
            }else{
                // alert(res.message);
            }
        },
        error: function (err) {
        }
    });
}
platform();
function active(type,_this) {
    type = type;
    _this.siblings().removeClass('active');
    _this.addClass('active');
    $('.admin-detail').html(_this.html());
    //列表
    data.producedStatus = type;
    initData(type);
}
function initData(type) {
    var index = layer.load(2, {shade: [0.6,'#000']});
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
        success: function (res) {
            layer.close(index);
            //清空原表格内容
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
                return;
            }
            load(res, type);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum=pageNum;
                    queryData(type);
                }
            });
        },

        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}
function queryData(type) {
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
        success: function (res) {
            load(res, type);

        }
    })
}
function load(res,type) {
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('.tcdPageCode').html('');
        return;
    }
    $('#sample-table-2 thead').html('');
    $('#sample-table-2 thead').append(' <tr> <th>订单号</th> <th>内部编号</th> <th>供应商产品编号</th><th>图片</th><th>客户</th>'+
        '<th>分类</th> <th>颜色</th> <th>尺寸</th> <th>件数</th> <th>销售价格</th><th>下单日期</th>'+
        '<th>交货日期</th><th>快递公司</th><th>快递单号</th> <th>备注</th><th>供应商</th><th>状态</th> <th>操作</th></tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'">  <td>'+res.data[i].orderNo+'</td> <td>'+res.data[i].insideOrderNo+'</td> <td>'+res.data[i].pno+'</td><td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
            ' <td>'+res.data[i].customer+'</td><td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> '+
            '<td>'+res.data[i].qty+'</td> <td>'+res.data[i].price+'</td> <td>'+res.data[i].createdDate+'</td> <td>'+res.data[i].deliveryDate+'</td> '+
            '<td><div style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'</div></td><td><div>'+res.data[i].deliveryNo+'</div></td>' +
            '<td><div style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td><td>'+res.data[i].suplierName+'</td>' +
            '<td>'+res.data[i].producedStatusName+'</td>';
        if(type == 2 || type == 3 || type == 4 || type == 7 ){
            // tr += '<td><div onclick="detail('+res.data[i].id+')">详情</div></td></tr>';
            tr += '<td><div>暂无</div></td></tr>';
        }
        $('#tbody').append(tr);
    }

    // 增加全选框
    initTableCheckbox();
}
//表格格式化
function initTableCheckbox(){
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
//搜索
$('#btnQuery').click(function () {
    type = data.producedStatus;
    data = {
        orderNo: $('#goodsNoInput').val(),
        pno: $('#pno').val(),
        producedStatus : type, //生产状态
        createdDateSta: $('#datepickerStart').val(),
        createdDateEnd: $('#datepickerEnd').val(),
        deliveryDateStr: $('#datepickerStart1').val(),
        deliveryDateEnd: $('#datepickerEnd1').val(),
        pageNum: 1,
        customerId: $('#customer option:selected').attr('value'),
        suplierId: $('#supplierList option:selected').attr('value')
    };
    initData(type);
});
//重置
$('#resert').click(function () {
    $('#pno').val('');
    $('#goodsNoInput').val('');
    $('#cargoInput').val('');
    $('#datepickerStart').val('');
    $('#datepickerEnd').val('');
    $('#datepickerStart1').val('');
    $('#datepickerEnd1').val('');
    type = data.producedStatus;
    $('#customer').find('option').eq(0).attr('selected', 'selected')
    $('#supplierList').find('option').eq(0).attr('selected', 'selected')
    data = {
        orderNo: '', //工单号
        pno: '',
        producedStatus : type, //生产状态
        createdDateSta: '',
        createdDateEnd: '',
        deliveryDateStr: '',
        deliveryDateEnd: '',
        pageNum: 1,
        customerId: '',
        suplierId: ''
    };
    initData(type);
});
//详情
function detail(id) {
    location.href='../WOManage/orderlocalDetailHtml?id='+id+'';
}
