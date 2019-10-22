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
            productSupplierName += '<option value="'+res.data[i].id+'">'+res.data[i].suplierName+'</option>';
            $('#supplierList').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});


var pageNum = 1;
var data = {
    pageNum: pageNum,
    pno: '',
    name: '',
    suplierId: ''
}
function list() {
    $.ajax({
        type: 'POST',
        url: '../suplierProduct/productList',
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
        url: '../suplierProduct/productList',
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
        tr += '<tr id="'+res.data[i].id+'"><td>'+res.data[i].pno+'</td><td>'+res.data[i].name+'</td>'+
            '<td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td><td>'+res.data[i].categoryName+'</td>'+
            '<td>'+res.data[i].color+'</td>' +
            '<td>'+res.data[i].size+'</td>' +
            '<td>'+res.data[i].updateTime+'</td>' +
            '<td>'+res.data[i].remarks+'</td>' +
            '<td>'+res.data[i].suplierName+'</td>' +
            '</tr>';
        $('#tbody').append(tr);
    }
}

//搜索
$('#btnQuery').click(function () {
    data = {
        pageNum: 1,
        pno: $('#pno').val(),
        name: $('#name').val(),
        suplierId: $('#supplierList').find("option:checked").val(),
    };
    list();
});
//重置
$('#resert').click(function () {
    $('#pno').val('');
    $('#name').val('');
    $('#supplierList').val('');
    data = {
        pageNum: 1,
        pno: $('#pno').val(),
        name: $('#name').val(),
        suplierId: $('#supplierList').find("option:checked").val()
    };
    list();
});
