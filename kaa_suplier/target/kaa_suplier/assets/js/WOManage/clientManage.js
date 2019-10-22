//新增
$('#btnAdd').click(function () {
    location.href='../WOManage/clientManageAddHtml';
});

//编辑
$('.btnEdit').click(function () {
    location.href='../WOManage/clientManageEditHtml';
});

$('.btnAddress').click(function () {
    location.href='../WOManage/clientAddressHtml';
})
var urlle = sessionStorage.getItem('urllen');
var url = window.location.href;
var type = url.split('=')[1];
var ids = [];


//初始化加载所有列表数据
var pageNum = 1;
var data = {
    customer: '',
    customerInit: '',
    customerPhone : '',
    pageNum: pageNum
};
function initData() {
    window.parent.platform();
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/supplierCustomerList',
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
        url: '../supplierCustomer/supplierCustomerList',
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
        $('#sample-table-2 thead').append(' <tr> <th>客户名称</th> <th>联系人</th> <th>联系方式</th><th>头像</th>'+
            '<th>更新时间</th> <th>备注</th><th>操作</th> </tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr+='<tr id="'+res.data[i].id+'">' +
            '<td>'+res.data[i].customer+'</td>' +
            '<td>'+res.data[i].customerInit+'</td>' +
            '<td>'+res.data[i].customerPhone+'</td>' +
            '<td><img style="width: 65px;height: 65px;" src="' + res.data[i].icon + '" alt=""></td>' +
            '<td>'+res.data[i].updateDate+'</td>' +
            '<td>'+res.data[i].remark+'</td>' +
            '<td style="min-width: 80px">' +
            '<div class="edit setting-btn">编辑</div>' +
            '<div class="delete setting-btn">删除</div>' +
            '<div class="addressList setting-btn" onclick="addresslist('+res.data[i].id+')">地址列表</div>' +
            '</tr>'
        $('#tbody').append(tr);
    }
    //编辑
    $('.edit').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        location.href = '../WOManage/clientManageEditHtml?id=' + id + '';
    });

//删除
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        $('#suredelete').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'supplierCustomer/removeSupplierCustomer',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    if(res.stateCode == 100){
                        location.reload();
                    }else{
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.alert(res.message, {icon: 0});
                }
            });
        })
    });

}



//搜索
$('#btnQuery').click(function () {
    data = {
        customer: $('#customer').val(),
        customerInit: $('#customerInit').val(),
        customerPhone: $('#customerPhone').val(),
        pageNum: pageNum
    }
    initData();
});
//重置
$('#resert').click(function () {
    $('#customer').val('');
    $('#customerInit').val('');
    $('#customerPhone').val('');
    data = {
        customer: '',
        customerInit: '',
        customerPhone: '',
        pageNum: pageNum
    };
    initData();
});
//地址列表
function addresslist(id) {
    location.href = '../WOManage/clientAddressHtml?id=' + id + '';
}