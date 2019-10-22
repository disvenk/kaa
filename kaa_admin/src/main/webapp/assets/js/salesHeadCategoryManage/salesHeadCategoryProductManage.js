var urlle = sessionStorage.getItem('urllen');
var url=window.location.href;
var id = url.split('=')[1];
var pageNum=1;
var data = {
    categoryId:id,
    pageNum: pageNum
};

//初始化加载所有列表数据
initData();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../salesHeadCategory/findSalesHeadCategoryProductList',
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
        url:  '../salesHeadCategory/findSalesHeadCategoryProductList',
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

//加载数据
function load(res) {
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('.tcdPageCode').html('');
        return;
    }

    //绑定产生表格数据

    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr += ' <tr class="' + res.data[i].id + '">' +
            '<td>' + res.data[i].productCode + '</td>' +
            '<td>' + res.data[i].name + '</td>' +
            '<td><img style="width: 65px" src="' + res.data[i].href + '" alt=""></td>' +
            '<td>' + res.data[i].description + '</td>' +
            '<td>' + res.data[i].categoryName + '</td>' +
            '<td>' + res.data[i].updateDate + '</td>' +
            '<td>' + res.data[i].sort + '</td>' +
            '<td>' +
            '<div class="delete">删除</div>'+
            '</td>' +
            '</tr>';

        $('#tbody').append(tr);
    }
    //删除
    $('.delete').click(function () {
        $('#deleteModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        $('#suredelete').click(function () {
            $.ajax({
                type: 'POST',
                url: '../salesHeadCategory/deleteSalesHeadCategoryProduct',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#deleteModal').modal('hide');
                    location.reload();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });
}


//新增
$('#btnAdd').click(function () {
    location.href = '../salesHeadCategory/salesGoodsManageHeadCategoryHtml?id='+id+'';
});

//返回
$('#back').click(function () {
    location.href = '../salesHeadCategory/salesHeadCategoryManageHtml';
});