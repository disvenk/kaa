var type = 1;
//初始化加载所有列表数据
var pageNum = 1;
var data = {
    supplierOrderNo: '',
    supOrderNo: '', //工单号
    productCode: '',
    producedStatus : type, //生产状态
    startTime: '',
    endTime: '',
    deliveryDateStart: '',
    deliveryDateEnd: '',
    pageNum: pageNum
};
//状态及数量
function platform() {
    $.ajax({
        type: 'POST',
        url: '../supOrder/producedStatusCountOnline',
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
    if(type == 1 || type == 2){
        $('#btnDownload').css('display','none');
        $('#btnOutput').css('display','none');
    }else if(type == 3 || type == 4 || type == 5 || type == 6 || type == 7 || type == 8 || type == 9 || type == 10){
        $('#btnDownload').css('display','inline-block');
        $('#btnOutput').css('display','inline-block');
    }
    //列表
    data.producedStatus = type;
    initData(type);
}
function initData(type) {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../supOrder/supOrderListOnline',
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
        url: '../supOrder/supOrderListOnline',
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
    if(type == 1){
        $('#sample-table-2 thead').append(' <tr><th>采购订单号</th><th>商品ID</th> <th>供应商产品编号</th><th>图片</th>'+
            '<th>分类</th> <th>颜色</th> <th>尺寸</th> <th>客户备注</th><th>其它</th><th>件数</th><th>下单日期</th>'+
            '<th>期望发货日期</th><th>备注</th><th>状态</th> <th>操作</th></tr>');
    }else{
        $('#sample-table-2 thead').append(' <tr><th>采购订单号</th>  <th>工单号</th> <th>商品ID</th> <th>供应商产品编号</th><th>图片</th><th>供应商</th>'+
            '<th>分类</th> <th>颜色</th> <th>尺寸</th> <th>客户备注</th><th>其它</th><th>件数</th> <th>价格</th><th>下单日期</th>'+
            '<th>交货日期</th> <th>期望发货日期</th><th>快递信息</th> <th>备注</th><th>状态</th> <th>操作</th></tr>');
    }
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        if(type == 1){
            tr += '<tr id="'+res.data[i].supOrderId+'"> <td>'+res.data[i].supplierOrderNo+'</td><td>'+res.data[i].productCode+'</td> <td>'+res.data[i].pno+'</td><td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
                '<td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].supplierOrderRemarks+'</td> <td>'+res.data[i].other+'</td>'+
                '<td>'+res.data[i].qty+'</td><td>'+res.data[i].supOrderDate+'</td><td>'+res.data[i].expectsendDate+'</td>'+
                '<td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td>' +
                '<td>'+res.data[i].producedStatusName+'</td>';
        }else{
            tr += '<tr id="'+res.data[i].supOrderId+'"> <td>'+res.data[i].supplierOrderNo+'</td> <td>'+res.data[i].supOrderNo+'</td> <td>'+res.data[i].productCode+'</td> <td>'+res.data[i].pno+'</td><td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
                ' <td>'+res.data[i].supplierName+'</td><td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].supplierOrderRemarks+'</td> <td>'+res.data[i].other+'</td>'+
                '<td>'+res.data[i].qty+'</td> <td>'+res.data[i].outputPrice+'</td> <td>'+res.data[i].supOrderDate+'</td> <td>'+res.data[i].deliveryDate+'</td> <td>'+res.data[i].expectsendDate+'</td>'+
                '<td><div onclick="delivery('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'<br>'+res.data[i].deliveryNo+'</div></td>' +
                '<td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td>' +
                '<td>'+res.data[i].producedStatusName+'</td>';
        }
        if(type == 1){
            tr += '<td><div onclick="getModal('+res.data[i].supOrderId+','+res.data[i].outputPrice+')">分配供应商</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div></td></tr>';
        }else if(type == 4 || type == 5 || type == 6 || type == 7 || type == 8 || type == 9 || type == 10){
            tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
        } else if(type == 2){
            tr += '<td><div onclick="deleteModal('+res.data[i].supOrderId+')">取消工单</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div></td></tr>';
        }else if(type == 3){
            tr += '<td><div onclick="deleteModal('+res.data[i].supOrderId+')">取消工单</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
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
        supplierOrderNo: $('#woInput').val(),
        supOrderNo: $('#goodsNoInput').val(),
        productCode: $('#cargoInput').val(),
        producedStatus : type, //生产状态
        startTime: $('#datepickerStart').val(),
        endTime: $('#datepickerEnd').val(),
        deliveryDateStart: $('#datepickerStart1').val(),
        deliveryDateEnd: $('#datepickerEnd1').val(),
        pageNum: 1
    };
    initData(type);
});
//重置
$('#resert').click(function () {
    $('#woInput').val('');
    $('#goodsNoInput').val('');
    $('#cargoInput').val('');
    $('#datepickerStart').val('');
    $('#datepickerEnd').val('');
    $('#datepickerStart1').val('');
    $('#datepickerEnd1').val('');
    type = data.producedStatus;
    data = {
        supplierOrderNo: '',
        supOrderNo: '', //工单号
        productCode: '',
        producedStatus : type, //生产状态
        startTime: '',
        endTime: '',
        deliveryDateStart: '',
        deliveryDateEnd: '',
        pageNum: 1
    };
    initData(type);
});
//详情
function detail(id) {
    location.href='../WOManage/kaaDetailHtml?id='+id+'';
}
//工单记录
function log(id) {
    $('#recordModal').modal('show');
    $('#recordModal .modal-body tbody').html('');
    $.ajax({
        type: 'POST',
        url: '../supOrder/supOrderDeliveryLogList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var tb = '';
                        tb += '<tr><td>'+res.data[i].createTime+'</td><td>'+res.data[i].instruction+'</td><td>'+res.data[i].action+'</td></tr>';
                        $('#recordModal .modal-body tbody').append(tb);
                    }
                }else {

                }
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
}
//取消
function deleteModal(id) {
    $('#deleteModal').modal('show');
    $('#suredelete').unbind('click');
    $('#suredelete').one('click', function () {
        $.ajax({
            type: 'POST',
            url: '../supOrder/supOrderCancel',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({instruction: $('#deleteModal .modal-body .update-text').val(), ids:[{id: id}]}),
            success: function (res) {
                $('#deleteModal').modal('hide');
                if(res.stateCode == 100){
                    type = data.producedStatus;
                    platform();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    })
}
//分配供应商
function getModal(id, price) {
    $('#getModal').modal('show');
    $.ajax({
        type: 'POST',
        url: '../supOrder/produceSupplier',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            $('#supplier').html(res.data.supplierName);
            $('#price').html(res.data.suplierPrice);
            $('#date').val(res.data.deliveryTime);
            $('#sure1').unbind('click');
            $('#sure1').one('click',function () {
                $.ajax({
                    type: 'POST',
                    url: '../supOrder/saveSupOrderSupplier',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({supOrderId: id,deliveryTime: $('#date').val(),description: $('#introduce').val()}),
                    success: function (res) {
                        $('#getModal').modal('hide');
                        if(res.stateCode == 100){
                            type = data.producedStatus;
                            platform();
                        }else{
                            layer.alert(res.message, {icon: 0});
                        }
                    },error: function (err) {
                        layer.alert(err.message, {icon: 0});
                    }
                })
            })
        },
        error: function (err) {

        }
    });
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
                url: '../supOrder/supOrderRemarksUpdate',
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
                        type = data.producedStatus;
                        initData(type);
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
//导出Excel
$("#btnOutput").click(function () {
    var url = "../supOrder/exportExcelOnline?supOrderNo=" + data.supOrderNo +
        "&pno="+data.pno +
        "&supplierOrderNo="+data.supplierOrderNo +
        "&productCode="+data.productCode +
        "&producedStatus="+data.producedStatus +
        "&startTime="+data.startTime +
        "&endTime="+data.endTime +
        "&deliveryDateStart="+data.deliveryDateStart +
        "&deliveryDateEnd="+data.deliveryDateEnd
    ;
    // window.open(url);
    window.location.href = (url);

})
//备注记录
function remarkLog(id) {
    $.ajax({
        type: 'POST',
        url: '../supOrder/supOrderRemarkLogList',
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
//快递信息
function delivery(id) {
    $.ajax({
        type: 'POST',
        url: '../supOrder/supOrderDeliveryRecordList',
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



//批量下载word
$("#btnDownload").click(function () {
    var ids = '';
    var length = $('#tbody > .xuanzhong');
    for(var i = 0; i < length.length; i++){
        ids += length.eq(i).attr('id') + ",";
    }
    if (ids.length == 0) {
        layer.alert("请选择需要下载的工单", {icon: 0});
        return;
    }
    var url = "../supOrder/exportWordListOnline?ids=" + ids.substring(0, ids.length - 1);
    window.open(url);
    // window.location.href = (url);

});