var urlle = sessionStorage.getItem('urllen');
var pageNum=1;
var data = {
    name: "",
    pageNum: pageNum
};

//初始化加载所有列表数据
initData();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../salesHeadCategory/findSalesHeadCategoryList',
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
        url:  '../salesHeadCategory/findSalesHeadCategoryList',
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

    //绑定产生表格数据

    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr += ' <tr class="' + res.data[i].id + '">' +
            '<td>' + res.data[i].name + '</td>' +
            '<td>' + res.data[i].description + '</td>' +
            '<td>' + res.data[i].updateDate + '</td>' +
            '<td>' + res.data[i].sort + '</td>' +
            '<td><div class="add setting-btn">商品维护</div>' +
            '<div class="edit setting-btn">编辑</div>' +
            '<div class="delete setting-btn">删除</div>'+
            '<div class="div_hide setting-btn">隐藏</div><div class="div_show setting-btn">显示</div></td>' +
            '</tr>';
        $('#tbody').append(tr);
        if (res.data[i].status == 1) {
            $('#tbody tr .div_show').eq(i).css('display', 'none');
            $('#tbody tr .div_hide').eq(i).css('display', '');
        }
        else  {
            $('#tbody tr .div_hide').eq(i).css('display', 'none');
            $('#tbody tr .div_show').eq(i).css('display', '');
        }
    }
    //删除
    $('.delete').click(function () {
        $('#deleteModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        $('#suredelete').click(function () {
            $.ajax({
                type: 'POST',
                url:  '../salesHeadCategory/deleteSalesHeadCategory',
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

    //隐藏
    var _this;
    var id;
    $('.div_hide').click(function () {
        $('#hideModal').modal('show');
        _this = $(this);
        id = _this.parent().parent().attr('class');
    });
    $('#surehide').click(function () {
        $.ajax({
            type: 'POST',
            url:  '../salesHeadCategory/setDisabledSalesHeadCategory',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id,status:0}),
            success: function (res) {
                $('#hideModal').modal('hide');
                _this.css('display', 'none');
                _this.next().css('display', '');
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
    //显示
    var _this1;
    var id1;
    $('.div_show').click(function () {
        $('#showModal').modal('show');
        _this1 = $(this);
        id1 = _this1.parent().parent().attr('class');
    });
    $('#showModal').click(function () {
        $.ajax({
            type: 'POST',
            url:  '../salesHeadCategory/setDisabledSalesHeadCategory',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id1,status:1}),
            success: function (res) {
                $('#showModal').modal('hide');
                _this1.css('display', 'none');
                _this1.prev().css('display', '');
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
    //编辑
    $('.edit').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../salesHeadCategory/salesHeadCategoryEditHtml?id=' + id + ''
    });

    //商品维护
    $('.add').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../salesHeadCategory/salesHeadCategoryProductManageHtml?id='+ id + ''
    });
}

//搜索
$('#btnQuery').click(function () {
    var name = $('#txtName').val();
    data = {
        name: name,
        pageNum: 1
    };
    initData();
});

//新增
$('#btnAdd').click(function () {
    location.href = '../salesHeadCategory/salesHeadCategoryAddHtml';
});