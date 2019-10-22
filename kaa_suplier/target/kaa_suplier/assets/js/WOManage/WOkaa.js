var url = window.location.href;
var type = url.split('=')[1];
var ids = [];
laydate.render({
    elem: '#placeOrder',range: true
});
laydate.render({
    elem: '#give',range: true
});
// 2待接单工单
 if(type == 2){
    $('.page-state').html('待接单');
    $('#btnAccept').css('display','inline-block');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','inline-block');
    $('#btnOutput').css('display','none');
    $('#btnSend').css('display','none');
}
// 3生产中工单
if(type == 3){
    $('.page-state').html('生产中');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnSend').css('display','inline-block');
    $('#btnCode').css('display','inline-block');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','inline-block');
    $('#btnOutput').css('display','inline-block');
}
// 4已发货工单
if(type == 4){
    $('.page-state').html('已发货');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 5待质检工单
if(type == 5){
    $('.page-state').html('待质检');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 6质检不合格工单
if(type == 6){
    $('.page-state').html('质检不合格');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 7质检合格未入库工单
if(type == 7){
    $('.page-state').html('质检合格未入库');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 8已入库工单
if(type == 8) {
    $('.page-state').html('已入库');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 9已取消工单
if(type == 9){
    $('.page-state').html('已取消');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}

// 10确认取消工单
if(type == 10){
    $('.page-state').html('确认取消');
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','inline-block');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','inline-block');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','inline-block');
    $('#btnSend').css('display','none');
}
if(sessionStorage.getItem('loginType') == 4){
    $('#btnAccept').css('display','none');
    $('#btnMark').css('display','none');
    $('#btnCode').css('display','none');
    $('#btnDownload').css('display','none');
    $('#btnPno').css('display','none');
    $('#btnOutput').css('display','none');
    $('#btnSend').css('display','none');
}
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
   pageNum: pageNum
};
function initData() {
    window.parent.platform();
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../supOrderOnline/supOrderListOnline',
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
                $('#sample-table-2').css('display', 'none');
                $('#display').css('display', 'block');
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
        url: '../supOrderOnline/supOrderListOnline',
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
        $('#sample-table-2 thead').append(' <tr> <th>工单号</th> <th>商品ID</th> <th>供应商产品编号</th><th>图片</th>'+
            '<th>分类</th> <th>颜色</th> <th>尺寸</th> <th>件数</th><th>下单日期</th>'+
            '<th>交货日期</th> <th>快递信息</th> <th>备注</th> <th>状态</th> <th>操作</th></tr>');
    }else{
        $('#sample-table-2 thead').append(' <tr> <th>工单号</th> <th>商品ID</th> <th>供应商产品编号</th><th>图片</th>'+
            '<th>分类</th> <th>颜色</th> <th>尺寸</th> <th>件数</th> <th>价格</th><th>下单日期</th>'+
            '<th>交货日期</th> <th>快递信息</th> <th>备注</th> <th>状态</th> <th>操作</th></tr>');
    }
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        if(sessionStorage.getItem('loginType') == 4){
            tr += '<tr id="'+res.data[i].supOrderId+'"> <td>'+res.data[i].supOrderNo+'</td> <td>'+res.data[i].productCode+'</td> <td>'+res.data[i].pno+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
                ' <td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].qty+'</td>'+
                '<td>'+res.data[i].supOrderDate+'</td> <td>'+res.data[i].deliveryDate+'</td> <td><div onclick="delivery('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'<br>'+res.data[i].deliveryNo+'</div></td>' +
                ' <td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td> <td>'+res.data[i].producedStatusName+'</td>'
        }else{
            tr += '<tr id="'+res.data[i].supOrderId+'"> <td>'+res.data[i].supOrderNo+'</td> <td>'+res.data[i].productCode+'</td> <td>'+res.data[i].pno+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
                ' <td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].qty+'</td> <td>'+res.data[i].outputPrice+'</td>'+
                '<td>'+res.data[i].supOrderDate+'</td> <td>'+res.data[i].deliveryDate+'</td> <td><div onclick="delivery('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].deliveryCompanyName+'<br>'+res.data[i].deliveryNo+'</div></td>' +
                ' <td><div onclick="remarkLog('+res.data[i].supOrderId+')" style="color: rgb(101,161,199);cursor: pointer;max-width: 140px;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">'+res.data[i].remarks+'</div></td> <td>'+res.data[i].producedStatusName+'</td>'
        }
          if(type == 2){
            if(sessionStorage.getItem('loginType') == 4){
                tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div></td></tr>';
            }else{
                tr += '<td><div onclick="getModal('+res.data[i].supOrderId+')">接单</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div></td></tr>';
            }
          }else if(type == 4 || type == 5 || type == 7 || type == 8 || type == 10){
              tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
          } else if(type == 3){
              if(sessionStorage.getItem('loginType') == 4){
                  tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
              }else{
                  tr += '<td><div onclick="ship('+res.data[i].supOrderId+')">发货</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
              }
          }else if(type == 6){
              if(sessionStorage.getItem('loginType') == 4){
                  tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
              }else{
                  tr += '<td><div onclick="modify('+res.data[i].supOrderId+')">修改完成</div><div onclick="detail('+res.data[i].supOrderId+')">详情</div>'+
                      '<div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';              }
          }else if(type == 9){
              if(sessionStorage.getItem('loginType') == 4){
                  tr += '<td><div onclick="detail('+res.data[i].supOrderId+')">详情</div><div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
              }else{
                  tr += '<td><div onclick="sure('+res.data[i].supOrderId+')">确认取消</div> <div onclick="detail('+res.data[i].supOrderId+')">详情</div>'+
                      '<div onclick="log('+res.data[i].supOrderId+')">工单记录</div></td></tr>';
              }

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
    $('#sample-table-2 tbody tr').click(function () {
        $(this).find('td').eq(0).find('input').click();

    });
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
      supOrderNo: $('#goodsNoInput').val(), //工单号
      pno: $('#woInput').val(),
      producedStatus : type, //生产状态
      startTime: $('#placeOrder').val().split(' ')[0]?$('#placeOrder').val().split(' ')[0]: '',
      endTime: $('#placeOrder').val().split(' ')[2]?$('#placeOrder').val().split(' ')[2]: '',
      deliveryDateStart: $('#give').val().split(' ')[0]?$('#give').val().split(' ')[0]: '',
      deliveryDateEnd: $('#give').val().split(' ')[2]?$('#give').val().split(' ')[2]: '',
      pageNum: pageNum
  }
    initData();
});
//重置
$('#resert').click(function () {
    $('#goodsNoInput').val('');
    $('#woInput').val('');
    $('#placeOrder').val('');
    $('#give').val('');
    data = {
        supOrderNo: '', //工单号
        pno: '',
        producedStatus : type, //生产状态
        startTime: '',
        endTime: '',
        deliveryDateStart: '',
        deliveryDateEnd: '',
        pageNum: pageNum
    };
    initData();
});
//接单
function get(ids) {
    $.ajax({
        type: 'POST',
        url: '../suplierOrder/supOrderTaking',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({ids:ids}),
        success: function (res) {
            $('#getModal').modal('hide');
            if(res.stateCode == 100){
                initData();
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            $('#getModal').modal('hide');
            layer.alert(err.message, {icon: 0});
        }
    })
}
function getModal(id) {
    var array = [{id: ''}];
    $('#getModal .modal-body').html('确定接此单吗？');
    $('#getModal').modal('show');
      array = [{
        id: id
    }];
    $('#sure5').unbind('click');
    $('#sure5').one('click',function () {
        get(array)
    });
}
//详情
function detail(id) {
    location.href = '../WOManage/kaaDetailHtml?id='+id+'';
};
//批量接单
$('#btnAccept').click(function () {
if($('#tbody .xuanzhong').length == 0){
    layer.alert('请选择批量接单的项', {icon: 0})
}else{
    $('#getModal .modal-body').html('确定批量接单吗？');
    $('#getModal').modal('show');
    $('#sure5').unbind('click');
    $('#sure5').one('click',function () {
        var choose = '';
        choose = $('#tbody .xuanzhong');
        for(var i = 0; i < choose.length; i++){
            ids[i] = {
                id: choose.eq(i).attr('id')
            }
        }
        get(ids)
    });
}
});
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
//发货
function ship(id) {
    $('#sendModal').modal('show');
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
//批量发货
$('#btnSend').click(function () {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要备注的项', {icon: 0})
    }else{
        $('#sendModal').modal('show');
        var supOrderIds = [];
        var length = $('#tbody > .xuanzhong');
        for(var i = 0; i < length.length; i++){
            var ob = {
                id: length.eq(i).attr('id')
            }
            supOrderIds[i] = ob;
        }
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
})
//重新修改
function modify(id) {
    $('#updateModal').modal('show');
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
                    $('#companys').append(option);
                }
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    });
    $('#sure6').unbind('click');
    $('#sure6').one('click',function () {
        var ddd = {
            instruction: $('#updateModal .update-text').val(),
            supOrderIds: [{id: id}],
            deliveryCompany: $('#companys').find('option:checked').attr('id'),
            deliveryCompanyName: $('#companys').val(),
            deliveryNo: $('#orderNum').val(),
        };
        $.ajax({
            type: 'POST',
            url: '../suplierOrder/supOrderModify',
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
//确认取消
function sure(id) {
    $('#cancelModal').modal('show');
    $('#sure4').unbind('click');
    $('#sure4').one('click',function () {
        $.ajax({
            type: 'POST',
            url: '../suplierOrder/supOrderCancel',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({ids: [{id: id}]}),
            success: function (res) {
                $('#cancelModal').modal('hide');
                if(res.stateCode == 100){
                    initData();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },error: function (err) {
                $('#cancelModal').modal('hide');
                layer.alert(err.message, {icon: 0});
            }
        })
    });
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
    var url = "../supOrderOnline/exportWordList?ids=" + ids.substring(0, ids.length - 1);
    window.open(url);
    // window.location.href = (url);

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
//导出Excel
$("#btnOutput").click(function () {
    var url = "../supOrderOnline/exportExcel?supOrderNo=" + data.supOrderNo +
        "&pno="+data.pno +
        "&producedStatus="+data.producedStatus +
        "&startTime="+data.startTime +
        "&endTime="+data.endTime +
        "&deliveryDateStart="+data.deliveryDateStart +
        "&deliveryDateEnd="+data.deliveryDateEnd
    ;
    // window.open(url);
    window.location.href = (url);

})
//批量修改供应商产品编号
function pnoAll() {
    if($('#tbody > .xuanzhong').length == 0){
        layer.alert('请选择要修改的项', {icon: 0})
    } else{
        $('#pnoModal').modal('show');
        $('#sure8').unbind('click');
        $('#sure8').one('click',function (){
            var ids = [];
            var length = $('#tbody > .xuanzhong');
            for(var i = 0; i < length.length; i++){
                var obj3 = {
                    id: length.eq(i).attr('id')
                }
                ids[i] = obj3;
            }
            var remarkParams = {
                pno: $('#pnoModal .update-text').val(),
                ids: ids
            };
            $.ajax({
                type: 'POST',
                url: '../produceRecord/updateSupOrderPno',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(remarkParams),
                success: function (res) {
                    $('#pnoModal').modal('hide');
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