var suplierId;
if(sessionStorage.getItem('loginType') == 4){
    $('#sample-table-2 thead tr th').eq(14).remove();
}
var procedureName;
//工序
$.ajax({
    type: 'POST',
    // url: '../base/getAllSupWorkerStationType',
    url: '../procedure/findProcedureListCombox',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        $('#type').append('<option value="">全部</option>');
        for (var i = 0; i < res.data.length; i++) {
            var options = '';
            options += '<option value="' + res.data[i].id + '">' + res.data[i].name + '</option>';
            $('#type').append(options);
            // 此变量为列表表头动态加载
            procedureName += '<th>'+res.data[i].name+'</th>';
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
var pageNum = 1;
var data = {
    pageNum: pageNum,
    orderNo: '',
    productCode: '',
    supplierProductCode: '',
    procedureType: '',
    producedStatus: '',
    timeStatus: ''
}
function list() {
    $.ajax({
        type: 'POST',
        url: '../produceRecord/produceRecordList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#tbody').html('');
                $('#thead').html('');
                if (res.data.length < 1) {
                    $('#display').css('display', 'block');
                    $('.tcdPageCode').html('');
                }else {
                    //分页
                    $('#display').css('display', 'none');
                    $('.tcdPageCode').html('');
                    $(".tcdPageCode").createPage({
                        pageCount: res.pageSum,//总共的页码
                        current: 1,//当前页
                        backFn: function (p) {//p是点击的页码
                            data.pageNum = $(".current").html();
                            ajax();
                        }
                    });
                    ajax();
                }
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
}
list();
function ajax() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../produceRecord/produceRecordList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.close(index);
                $('#thead').html("");
                var th='';
                th+='<tr>' +
                    '<th>工单号</th><th>供应商产品编号</th><th>商品ID</th><th>图片</th><th>分类</th><th>颜色</th>' +
                    '<th>尺码</th><th>件数</th>'+procedureName+'<th>状态</th><th>编辑</th>' +
                    '</tr>';
                $('#thead').append(th);
                $('#tbody').html("");
                for(var i = 0; i < res.data.length; i++){
                    var tr = '';
                    tr += '<tr><td>'+res.data[i].orderNo+'</td><td>'+res.data[i].supplierProductCode+'</td>'+
                        '<td>'+res.data[i].productCode+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td><td>'+res.data[i].categoryName+'</td>'+
                        '<td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td><td>'+res.data[i].qty+'</td>';
                    for(var j = 0; j < res.data[i].procedureTypeList.length; j++){
                        tr += '<td>'+res.data[i].procedureTypeList[j].workerName+'<br>'+res.data[i].procedureTypeList[j].productionDate+'</td>';
                    }
                    if(sessionStorage.getItem('loginType') == 4){
                        tr += '<td>'+res.data[i].producedStatusName+'</td></tr>';
                    }else{
                        tr += '<td>'+res.data[i].producedStatusName+'</td><td style="cursor: pointer;color: rgb(113,161,197)" onclick="edit('+res.data[i].id+')">编辑</td></tr>';
                    }
                    $('#tbody').append(tr);
                }
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
};
//搜索
$('#btnQuery').click(function () {
    data = {
        pageNum: 1,
        orderNo: $('#woInput').val(),
        productCode: $('#goodsIdInput').val(),
        supplierProductCode: $('#goodsNoInput').val(),
        procedureType: $('#type').find("option:checked").val(),
        producedStatus: $('#orderState').find("option:checked").val(),
        timeStatus: $("#time").is(':checked') ? 1 : ''
    };
    list();
});
//重置
$('#resert').click(function () {
    $('#woInput').val('');
    $('#goodsIdInput').val('');
    $('#goodsNoInput').val('');
    $('#type').val('');
    $('#orderState').val('');
    $("#time").removeAttr("checked");
    data = {
        pageNum: 1,
        orderNo: $('#woInput').val(),
        productCode: $('#goodsIdInput').val(),
        supplierProductCode: $('#goodsNoInput').val(),
        procedureType: $('#type').find("option:checked").attr("id") ? undefined : '',
        producedStatus: $('#orderState').find("option:checked").val(),
        timeStatus: $("#time").is(':checked') ? 1 : ''
    };
    list();
});
//新增
$('#btnAdd').click(function () {
    location.href='../WOManage/WOoperateAddHtml?id='+suplierId+'';
});
//编辑
function edit(id) {
    location.href='../WOManage/WOrecordEditHtml?id='+id+'';
}