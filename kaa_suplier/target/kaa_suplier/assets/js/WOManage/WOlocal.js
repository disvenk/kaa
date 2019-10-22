var url = window.location.href;
var type;
var urgent;
if(url.split('=')[1] == 66){
    type = 3;
     urgent = 2;
}else{
    type = url.split('=')[1];
    urgent = '';
}

laydate.render({
    elem: '#placeOrder',range: true
});
laydate.render({
    elem: '#give',range: true
});
if(type == 2){
    $('.page-state').html('新工单');
    $('#btnMark').css('marginLeft','10px');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','inline-block');
    $('#btnDownload').css('display','inline-block');
    $('#btnOutput').css('display','inline-block');
    $('#btnAdd').css('display','inline-block');
    $('#btnSend').css('display','none');
    $('#btnOK').css('display','none');
    $('#btnProduce').css('display','inline-block');
    $('#btnPayStatus').css('display','none');
}else if(type == 3){
    $('.page-state').html('生产中');
    $('#btnMark').css('marginLeft','0');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnSend').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnProduce').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnAdd').css('display','none');
    $('#btnOK').css('display','none');
    $('#btnPayStatus').css('display','none');
}else if(type == 4){
    $('.page-state').html('已发货');
    $('#btnMark').css('marginLeft','0');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnProduce').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnAdd').css('display','none');
    $('#btnSend').css('display','none');
    $('#btnPayStatus').css('display','none');
    $('#btnOK').css('display','none');
}else if(type == 7){
    $('.page-state').html('已完成');
    $('#btnMark').css('marginLeft','0');
    $('#btnMark').css('display','inline-block');
    $('#btnPayStatus').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnProduce').css('display','none');
    $('#btnAdd').css('display','none');
    $('#btnSend').css('display','none');
    $('#btnOK').css('display','none');
}
// if(sessionStorage.getItem('loginType') == 4){
//         $('#btnMark').css('display','none');
//         $('#btnPayStatus').css('display','none');
//         $('#btnCode').css('display','none');
//         $('#btnDownload').css('display','none');
//         $('#btnOutput').css('display','none');
//         $('#btnProduce').css('display','none');
//         $('#btnAdd').css('display','none');
//         $('#btnSend').css('display','none');
//         $('#btnOK').css('display','none');
// }
$('#btnAdd').click(function () {
    location.href = '../WOManage/localAddHtml';
});
//初始化加载所有列表数据
var pageNum = 1;
var data = {
    supOrderNo: '', //工单号
    pno: '',
    producedStatus : type, //生产状态
    startTime: '',
    endTime: '',
    deliveryDateStart: '',
    deliveryDateEnd: '',
    payStatus:'',
    pageNum: pageNum,
    urgent: urgent
};
function initData() {
    window.parent.factory();
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../supOrderOffline/supOrderListOffline',
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
                $('#display').css('display', 'block');
                $('.tcdPageCode').html('');
                $('#sample-table-2').css('display', 'none');
                return;
            }
            $('#sample-table-2').css('display', '');
            $('#display').css('display', 'none');

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
initData();

function queryData() {
    $.ajax({
        type: 'POST',
        url: '../supOrderOffline/supOrderListOffline',
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
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('#display').css('display', 'block');
        $('.tcdPageCode').html('');
        $('#sample-table-2').css('display', 'none');
        return;
    }
    $('#sample-table-2').css('display', '');
    $('#display').css('display', 'none');
    $('#sample-table-2 thead').html('');
    if(sessionStorage.getItem('loginType') == 4){
        $('#sample-table-2 thead').append(' <tr><th>紧急程度</th><th>订单号</th><th>工单号</th> <th>内部编号</th>'+
            '<th>供应商产品编号</th> <th>图片</th> <th>客户</th> <th>分类</th> <th>颜色</th> <th>尺寸</th> <th>件数</th>'+
            '<th>下单日期</th> <th>交货日期</th><th>快递信息</th> <th>备注</th> <th>状态</th><th>操作</th></tr>');
    }else{
        $('#sample-table-2 thead').append(' <tr><th>紧急程度</th><th>订单号</th><th>工单号</th> <th>内部编号</th>'+
            '<th>供应商产品编号</th> <th>图片</th> <th>客户</th> <th>分类</th> <th>颜色</th> <th>尺寸</th> <th>件数</th><th>销售单价</th>'+
            '<th>下单日期</th> <th>交货日期</th><th>快递信息</th> <th>备注</th> <th>状态</th><th>操作</th></tr>');
    }
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var payStatus;
        if (res.data[i].payStatus == '1') {
            payStatus = '已结款';
        } else if (res.data[i].payStatus == '2') {
            payStatus = '未结款';
        }else{
            payStatus = '';
        }
        var tr = '';
     if(sessionStorage.getItem('loginType') == 4){
       tr += '<tr id="'+res.data[i].supOrderId+'"> <td><select value="'+res.data[i].supOrderId+'" class="state-select"><option value="1" style="color: green;">正常</option><option value="2" style="color: red;">紧急</option></select></td><td>'+res.data[i].salesOrderNo+'</td><td>'+res.data[i].supOrderNo+'</td> <td>'+res.data[i].insideOrderNo+'</td> <td>'+res.data[i].pno+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
        '<td>'+res.data[i].customer+'</td> <td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].qty+'</td>'+
        '<td>'+res.data[i].supOrderDate+'</td> <td>'+res.data[i].deliveryDate+'</td> <td><div onclick="delivery('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'<br>'+res.data[i].deliveryNo+'</div></td>' +
        '<td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td> <td>'+res.data[i].producedStatusName+'</td>'
     }else{
         tr += '<tr id="'+res.data[i].supOrderId+'"> <td><select value="'+res.data[i].supOrderId+'" class="state-select"><option value="1" style="color: green;">正常</option><option value="2" style="color: red;">紧急</option></select></td><td>'+res.data[i].salesOrderNo+'</td><td>'+res.data[i].supOrderNo+'</td> <td>'+res.data[i].insideOrderNo+'</td> <td>'+res.data[i].pno+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
             '<td>'+res.data[i].customer+'</td> <td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].qty+'</td> <td>'+res.data[i].outputPrice+'</td>'+
             '<td>'+res.data[i].supOrderDate+'</td> <td>'+res.data[i].deliveryDate+'</td> <td><div onclick="delivery('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'<br>'+res.data[i].deliveryNo+'</div></td>' +
             '<td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td> <td>'+res.data[i].producedStatusName+'</td>'
     }



        tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></tr>';
        // if(type == 2){
        //     if(sessionStorage.getItem('loginType') == 4){
        //         tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div></tr>';
        //     }else{
        //         tr += '<td><div onclick="edit('+res.data[i].supOrderId+')">编辑</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div>' +
        //             '<div onclick="delete_this('+res.data[i].supOrderId+')">删除</div><div onclick="produce('+res.data[i].supOrderId+')">安排生产</div></td></tr>';
        //     }
        // } else if(type == 3){
        //     if(sessionStorage.getItem('loginType') == 4){
        //         tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></tr>';
        //     }else{
        //         tr += '<td><div onclick="edit('+res.data[i].supOrderId+')">编辑</div><div onclick="ship('+res.data[i].supOrderId+')">发货</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div>' +
        //             '<div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
        //     }
        // }else if(type == 4){
        //     if(sessionStorage.getItem('loginType') == 4){
        //         tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></tr>';
        //     }else{
        //         tr += '<td><div onclick="edit('+res.data[i].supOrderId+')">编辑</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div>' +
        //             '<div onclick="modify('+res.data[i].supOrderId+')">重新修改</div><div onclick="complete('+res.data[i].supOrderId+')">完成工单</div>'+
        //             '<div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
        //     }
        // }else if(type == 7){
        //     if(sessionStorage.getItem('loginType') == 4){
        //         tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></tr>';
        //     }else{
        //         tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="payStatus('+res.data[i].supOrderId+')">修改结款状态</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div>' +
        //             '</td></tr>';
        //     }
        //
        // }
        $('#tbody').append(tr);

        if(res.data[i].urgent==1){
            $(".state-select").eq(i).find('option[value=1]').attr('selected','selected');
            $(".state-select").eq(i).css('color','green');
        } else {
            $(".state-select").eq(i).find('option[value=2]').attr('selected','selected');
            $(".state-select").eq(i).css('color','red');
        }
    }
    $('.state-select').change(function(){
        var data = {
            id:$(this).attr('value'),
            urgent:$(this).find('option:selected').val()
        };
        if($(this).find('option:selected').val() == 1){
            $(this).css('color', 'green');
        }else{
            $(this).css('color', 'red');
        }
        $.ajax({
            type: 'POST',
            url: '../supOrderOffline/updateUrgent',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.alert('修改成功',{icon: 1});
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
    });
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
    // $('#sample-table-2 tbody tr').click(function () {
    //     $(this).find('td').eq(0).find('input').click();
    //
    // });
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
    data = {
        salesOrderNo: $('#salesOrderNo').val(),
        supOrderNo: $('#woInput').val(), //工单号
        pno: $('#goodsNoInput').val(),
        producedStatus : type, //生产状态
        startTime: $('#placeOrder').val().split(' ')[0]?$('#placeOrder').val().split(' ')[0] + ' 00:00:00': '',
        endTime: $('#placeOrder').val().split(' ')[2]?$('#placeOrder').val().split(' ')[2] + ' 23:59:59': '',
        deliveryDateStart: $('#give').val().split(' ')[0]?$('#give').val().split(' ')[0] + ' 00:00:00': '',
        deliveryDateEnd: $('#give').val().split(' ')[2]?$('#give').val().split(' ')[2] + ' 23:59:59': '',
        payStatus: $('#payStatus').find('option:selected').val(),
        pageNum: pageNum
    };
    initData();
});
//重置
$('#resert').click(function () {
    $('#goodsNoInput').val('');
    $('#woInput').val('');
    $('#placeOrder').val('');
    $('#give').val('');
    data = {
        salesOrderNo: '',
        supOrderNo: '', //工单号
        pno: '',
        producedStatus : type, //生产状态
        startTime: '',
        endTime: '',
        deliveryDateStart: '',
        deliveryDateEnd: '',
        payStatus:'',
        pageNum: pageNum
    };
    initData();
});
//详情
function detail(id) {
    location.href = '../WOManage/localDetailHtml?id='+id+'';
}
//编辑
function edit(id) {
    location.href = '../WOManage/localEditHtml?id='+id+'&type='+type+'';
}

//修改结款状态
function payStatus(id) {

    $('#payStatusModal').modal('show');
    //已结款
    $('#btnApprove').click(function () {
        pay({'payStatus':'1', 'ids':[{'id':id}]});
    })
    //未结款
    $('#btnReject').click(function () {
        pay({'payStatus':'2', 'ids':[{'id':id}]});
    })
}

//批量修改结款状态
$('#btnPayStatus').click(function () {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要备注的项', {icon: 0})
    } else{
        $('#payStatusModal').modal('show');
        var ids = [];
        var length = $('#tbody > .xuanzhong');
        for(var i = 0; i < length.length; i++){
            var ob = {
                id: length.eq(i).attr('id')
            }
            ids[i] = ob;
        }
        //已结款
        $('#btnApprove').click(function () {
            pay({'payStatus':'1', 'ids':ids});
        })
        //未结款
        $('#btnReject').click(function () {
            pay({'payStatus':'2', 'ids':ids});
        })
    }

})

//结款接口
function pay(data) {
    $.ajax({
        type: 'POST',
        url: '../supOrderOffline/supOrderPayStatusSave',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode != 100){
                alert(res.message);
            }else{
                $('#payStatusModal').modal('hide');
                location.reload();
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}


//生产
function produce(id) {
    $('#produceModal').modal('show');
    $('#sure4').unbind('click');
    $('#sure4').one('click',function () {
     $.ajax({
         type: 'POST',
         url: '../suplierOrder/supOrderTaking',
         dataType: 'json',
         contentType: 'application/json; charset=utf-8',
         headers: {
             'Accept': 'application/json; charset=utf-8',
             'Authorization': 'Basic ' + sessionStorage.getItem('token')
         },
         data: JSON.stringify({ids: [{id: id}]}),
         success: function (res) {
             $('#produceModal').modal('hide');
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
//删除
function delete_this(id) {
    $('#deleteModal').modal('show');
    $('#sure5').unbind('click');
    $('#sure5').one('click',function () {
        $.ajax({
            type: 'POST',
            url: '../supOrderOffline/supOrderOfflineRemove',
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
//工单记录
function log(id) {
    $('#recordModal').modal('show');
    $('#recordModal .modal-body tbody').html('');
    $.ajax({
        type: 'POST',
        url: '../suplierOrder/supOrderDeliveryLogList',
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
//发货
function ship(id) {
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
    var supOrderIds = [
        {id: id}
    ];
    $('#sure1').unbind('click');
    $('#sure1').one('click',function () {
        var ship = {
            deliveryCompany: $('#company').find('option:checked').attr('id'),
            deliveryCompanyName: $('#company').val(),
            deliveryNo: $('#orderNo').val(),
            supOrderIds: supOrderIds
        };
        $.ajax({
            type: 'POST',
            url: '../suplierOrder/supOrderDeliver',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ship),
            success: function (res) {
                $('#sendModal').modal('hide');
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
//重新修改
function modify(id) {
    $('#updateModal').modal('show');
    $('#sure6').unbind('click');
    $('#sure6').one('click',function () {
        var ddd = {
            instruction: $('#updateModal .update-text').val(),
            supOrderIds: [{id: id}]
        };
        $.ajax({
            type: 'POST',
            url: '../suplierOrder/supOrderModifyOffline',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ddd),
            success: function (res) {
                $('#updateModal').modal('hide');
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
//完成工单
function complete(id) {
    $('#finishModal').modal('show');
    $('#sure3').unbind('click');
    $('#sure3').one('click',function () {
        $.ajax({
            type: 'POST',
            url: '../suplierOrder/supOrderFinishOffline',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({ids: [{id: id}]}),
            success: function (res) {
                $('#finishModal').modal('hide');
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
            url: '../suplierOrder/supOrderRemarksUpdate',
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
//批量安排生产
$('#btnProduce').click(function () {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要生产的项', {icon: 0})
    } else{
        $('#produceModal').modal('show');
        $('#sure4').unbind('click');
        $('#sure4').one('click',function () {
            var remarkIds = [];
            var length = $('#tbody > .xuanzhong');
            for(var i = 0; i < length.length; i++){
                var ob = {
                    id: length.eq(i).attr('id')
                }
                remarkIds[i] = ob;
            }
            var ids = {
                ids: remarkIds
            };
            $.ajax({
                type: 'POST',
                url: '../suplierOrder/supOrderTaking',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(ids),
                success: function (res) {
                    $('#produceModal').modal('hide');
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
})

//批量发货
$('#btnSend').click(function () {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要发货的项', {icon: 0})
    } else{
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
            var remarkIds = [];
            var length = $('#tbody > .xuanzhong');
            for(var i = 0; i < length.length; i++){
                var ob = {
                    id: length.eq(i).attr('id')
                }
                remarkIds[i] = ob;
            }

            var ship = {
                deliveryCompany: $('#company').find('option:checked').attr('id'),
                deliveryCompanyName: $('#company').val(),
                deliveryNo: $('#orderNo').val(),
                supOrderIds: remarkIds
            };
            $.ajax({
                type: 'POST',
                url: '../suplierOrder/supOrderDeliver',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(ship),
                success: function (res) {
                    $('#sendModal').modal('hide');
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
})

//批量完成
$('#btnOK').click(function () {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要完成的项', {icon: 0})
    } else{
        $('#finishModal').modal('show');
        $('#sure3').unbind('click');
        $('#sure3').one('click',function () {
            var remarkIds = [];
            var length = $('#tbody > .xuanzhong');
            for(var i = 0; i < length.length; i++){
                var ob = {
                    id: length.eq(i).attr('id')
                }
                remarkIds[i] = ob;
            }
            var ids = {
                ids: remarkIds
            };
            $.ajax({
                type: 'POST',
                url: '../suplierOrder/supOrderFinishOffline',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(ids),
                success: function (res) {
                    $('#finishModal').modal('hide');
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
})



//导出Excel
$("#btnOutput").click(function () {
    var url = "../supOrderOffline/exportExcel?supOrderNo=" + data.supOrderNo +
    "&pno="+data.pno +
    "&producedStatus="+data.producedStatus +
    "&startTime="+data.startTime +
    "&endTime="+data.endTime +
    "&deliveryDateStart="+data.deliveryDateStart +
    "&deliveryDateEnd="+data.deliveryDateEnd +
    "&payStatus="+data.payStatus
    ;
    // window.open(url);
    window.location.href = (url);

})


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
    var url = "../supOrderOffline/exportWordList?ids=" + ids.substring(0, ids.length - 1);
    // window.open(url);
    window.location.href = (url);

});


//快递信息
function delivery(id) {
    $.ajax({
        type: 'POST',
        url: '../suplierOrder/supOrderDeliveryRecordList',
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
        url: '../suplierOrder/supOrderRemarkLogList',
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
//生成条形码
function btnCode() {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择选项', {icon: 0})
    } else{
        for(var i = 0; i < $('#tbody > .xuanzhong').length; i++){
            var LODOP=getLodop();
            LODOP.PRINT_INIT("打印条形码");
            LODOP.ADD_PRINT_BARCODE(30,41,200,90,"Code39",$('#tbody > .xuanzhong').eq(i).find('td').eq(1).html());
            LODOP.SET_PRINT_PAGESIZE(1,"600","400","");
            // LODOP.PRINT();
            LODOP.PREVIEW()
        }
    }
}