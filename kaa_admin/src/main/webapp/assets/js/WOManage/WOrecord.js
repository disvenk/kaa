var suplierId;
var urlle = sessionStorage.getItem('urllen');
//供应商
$.ajax({
    type: 'POST',
    url: urlle + 'product/suplierNameList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var productSupplierName = '';
            productSupplierName += '<option id="'+res.data[i].id+'" value="'+res.data[i].suplierName+'">'+res.data[i].suplierName+'</option>';
            $('#supplierList').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

//所属工位
$.ajax({
    type: 'POST',
    url: '../base/getAllSupWorkerStationType',
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
        url: '../produceRecordManage/produceRecordList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#tbody').html('');
                if (res.data.length < 1) {
                    $('.tcdPageCode').html('');
                }else {
                    load(res);

                    //分页
                    $('.tcdPageCode').html('');
                    $(".tcdPageCode").createPage({
                        pageCount: res.pageSum,//总共的页码
                        current: 1,//当前页
                        backFn: function (p) {//p是点击的页码
                            data.pageNum = $(".current").html();
                            ajax();
                        }
                    });

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
        url: '../produceRecordManage/produceRecordList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.close(index);
                load(res);
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
};

function load(res) {
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'"><td>'+res.data[i].orderNo+'</td><td>'+res.data[i].pno+'</td>'+
            '<td>'+res.data[i].productCode+'</td> <td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td><td>'+res.data[i].categoryName+'</td>'+
            '<td>'+res.data[i].color+'</td>' +
            '<td>'+res.data[i].size+'</td>' +
            '<td>'+res.data[i].qty+'</td>';
        for(var j = 0; j < res.data[i].procedureTypeList.length; j++){
            tr += '<td>'+res.data[i].procedureTypeList[j].workerName+'<br>'+res.data[i].procedureTypeList[j].productionDate+'</td>';
        }
        tr += '<td>'+res.data[i].producedStatusName+'</td>' +
            '<td>'+res.data[i].suplierName+'</td>' +
            '</tr>';
        $('#tbody').append(tr);
    }
}

//搜索
$('#btnQuery').click(function () {
    data = {
        pageNum: 1,
        orderNo: $('#woInput').val(),
        productCode: $('#goodsIdInput').val(),
        supplierProductCode: $('#goodsNoInput').val(),
        supplierName: $('#supplierList').find("option:checked").val(),
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
    $('#supplierName').val('');
    $('#orderState').val('');
    $("#time").removeAttr("checked");
    data = {
        pageNum: 1,
        orderNo: $('#woInput').val(),
        productCode: $('#goodsIdInput').val(),
        supplierName: $('#supplierList').find("option:checked").val(),
        supplierProductCode: $('#goodsNoInput').val(),
        procedureType: $('#type').find("option:checked").attr("id") ? undefined : '',
        producedStatus: $('#orderState').find("option:checked").val(),
        timeStatus: $("#time").is(':checked') ? 1 : ''
    };
    list();
});
