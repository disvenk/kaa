$('#btnAdd').click(function () {
    location.href = '../WOManage/basicStorageAddHtml';
});
//初始化加载所有列表数据
var pageNum = 1;
var data = {
    pno: '',
    name: '',
    pageNum: pageNum
};
function initData() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../suplierProduct/productList',
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
        url: '../suplierProduct/productList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                load(res);
            }else{
                layer.alert(res.message, {icon:0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon:0})
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
        $('#sample-table-2 thead').append('<tr><td>供应商产品编号</td><td>商品名称</td>'+
            '<td>图片</td> <td>分类</td> <td>颜色</td> <td>尺寸</td> <td>更新时间</td> <td>备注</td> <td>操作</td> </tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
            tr += '<tr id="'+res.data[i].id+'"> <td>'+res.data[i].pno+'</td> <td>'+res.data[i].name+'</td><td><img style="width: 70px;height: 70px" src="'+res.data[i].href+'" alt=""></td>' +
                ' <td>'+res.data[i].categoryName+'</td> <td>'+res.data[i].color+'</td> <td>'+res.data[i].size+'</td> <td>'+res.data[i].updateTime+'</td>'+
                '<td>'+res.data[i].remarks+'</td> <td style="line-height: 25px"> <div onclick="btnEdit('+res.data[i].id+')">编辑</div><div id="btnDel" onclick="btnDel('+res.data[i].id+')">删除</div><span style="border: 0;' +
                'border-radius: 5px;background-color: #3b5999;color: #fff;outline:none;cursor: pointer;' +
                'padding: 4px 10px" onclick="location.href =\'../WOManage/localOrderAddHtml?id='+res.data[i].pno+'\'">生成订单</span></td>';
        $('#tbody').append(tr);
    }
}
function btnAdd() {
    location.href='../WOManage/basicStorageAddHtml';
}
function btnEdit(id) {
    location.href='../WOManage/basicStorageEditHtml?id='+id;
}
function btnDel(id) {
    $('#deleteModal').modal('show');
    $('#sure5').click(function () {
        $.ajax({
            type: 'POST',
            url: '../suplierProduct/removeProduct',
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
                    layer.alert(res.message, {icon:0})
                }
            },error: function (err) {
                $('#deleteModal').modal('hide');
                layer.alert(err.message, {icon:0})
            }
        })
    });
}
//搜索
$('#btnQuery').click(function () {
    data = {
        pno: $('#pno').val(),
        name: $('#name').val(),
        pageNum: 1
    };
    initData();
});
//重置
$('#resert').click(function () {
    $('#pno').val('');
    $('#name').val('');
    data = {
        pno: '',
        name: '',
        pageNum: 1
    };
    initData();
});