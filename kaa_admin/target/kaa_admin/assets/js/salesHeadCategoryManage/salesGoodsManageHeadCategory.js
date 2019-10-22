var url = window.location.href;
var id = url.split('=')[1];
var pageNum=1;
var data = {
    name: "",
    categoryId:"",
    headCategoryId:id,
    pageNum:1
};
//初始化分类
initCategory();

//初始化加载所有列表数据
initData();


function initData() {
    $.ajax({
        type: 'POST',
        url: '../salesHeadCategory/findSalesProductList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            //清空原表格内容
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
                return;
            }

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

function queryData() {
    $.ajax({
        type: 'POST',
        url:   '../salesHeadCategory/findSalesProductList',
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
        $('.tcdPageCode').html('');
        return;
    }
    $('#sample-table-2 thead').html('');
    $('#sample-table-2 thead').append(' <tr>\n' +
        '                                <th>商品编号</th>\n' +
        '                                <th>商品名称</th>\n' +
        '                                <th>图片</th>\n' +
        '                                <th>分类</th>\n' +
        '                                <th>更新日期</th>\n' +
        '                                <th>排序</th>\n' +
        '                            </tr>')
    //绑定产生表格数据

    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr += ' <tr id="' + res.data[i].id + '">' +
            '<td>' + res.data[i].productCode + '</td>' +
            '<td>' + res.data[i].name + '</td>' +
            '<td><img style="width: 65px" src="' + res.data[i].href + '" alt=""></td>' +
            '<td>' + res.data[i].categoryName + '</td>' +
            '<td>' + res.data[i].updateDate + '</td>' +
            '<td>' + res.data[i].sort + '</td>' +
            '</tr>'

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

//初始化分类资料
function initCategory() {
    $.ajax({
        type: 'POST',
        url:  '../base/getPlaProductCategoryLevelOneList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $('#category').append('<option id=""></option>')
            for(var i = 0 ;i < res.data.length; i++){
                var options = '';
                options += '<option id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                $('#category').append(options);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//搜索
$('#btnQuery').click(function () {
    var productCode = $('#txtProductCode').val();
    var name = $('#txtName').val();
    var categoryId = $('#category').find("option:checked").attr("id");

    data = {
        productCode:productCode,
        name: name,
        categoryId:categoryId,
        headCategoryId:id,
        pageNum:1
    };
    initData();
});

//返回
$('#back').click(function () {
    location.href = '../salesHeadCategory/salesHeadCategoryProductManageHtml?id='+id+'';
});

//新增
$('#btnAdd').click(function () {
    $('#addModal').modal('show');
    var arr1 = [];
    var arr = $('#sample-table-2 .xuanzhong');
    for(var i = 0; i < arr.length; i++){
        var obj = {};
        obj.id = $('#sample-table-2 .xuanzhong').eq(i).attr('id');
        arr1.push(obj)
    }

    if(arr1.length==0)
    {
        alert('请至少要选择一件商品');
        return;
    }

    $('#sureadd').click(function () {
        var params = {
            headCategoryId: id,
            productIds: arr1
        };
        $.ajax({
            type: 'POST',
            url: '../salesHeadCategory/addSalesHeadCategoryProduct',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(params),
            success: function (res) {
                location.href = '../salesHeadCategory/salesHeadCategoryProductManageHtml?id='+id+'';
            },
            error: function (err) {
                alert(err.message);
            }
        })
    })
});