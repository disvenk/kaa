var urlle = sessionStorage.getItem('urllen');
var pageNum=1;
var data = {
    userCode: "",
    mobile: "",
    pageNum: pageNum
};

//初始化加载所有列表数据
initData();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../store/findStoreList',
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
        url: '../store/findStoreList',
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
    var tr = '';
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        tr += ' <tr class="' + res.data[i].id + '" name="'+res.data[i].userId+'">' +
            // '<td>' + '<input type="checkbox" id="checkid"> ' + '</td>' +
            '<td><img style="width: 65px;height:65px;" src="' + res.data[i].icon + '" alt=""></td>' +
            '<td>' + res.data[i].userCode + '</td>' +
            '<td>' + res.data[i].userName + '</td>' +
            '<td>' + res.data[i].sex + '</td>' +
            '<td>' + res.data[i].mobile + '</td>' +
            '<td>' + res.data[i].storeName + '</td>' +
            // '<td>' + res.data[i].storeAddress + '</td>' +
            // '<td><img style="width: 65px;height:65px;" src="' + res.data[i].storePicture + '" alt=""></td>' +
            // '<td><img style="width: 65px;height:65px;" src="' + res.data[i].credentials + '" alt=""></td>' +
            '<td>' + res.data[i].storeSalesCategory + '</td>' +
            '<td>' + res.data[i].storeContact + '</td>' +
            '<td>' + res.data[i].storeContactPhone + '</td>' +
            '<td>' + res.data[i].userType + '</td>' +
            '<td>' + res.data[i].createdDate + '</td>' +
            '<td>' + res.data[i].haveBuy + '</td>' +
            '<td>' + res.data[i].finalLogindate + '</td>' +
            '<td>' + res.data[i].storeStatusName + '</td>' +
            '<td>';
        if (res.data[i].storeStatus == '0' || res.data[i].storeStatus != '') {
            tr += '<div class="approve setting-btn">审核</div>' +
                '<div class="approvelog setting-btn">审核历史</div>';
        }
        tr += '<div class="reset setting-btn">重置密码</div>' +
            '<div class="edit setting-btn">编辑</div>' +
            // '<div class="delete setting-btn">删除</div>' +
            '<div class="box setting-btn">合一盒子</div>'+
            '</td>' +
            '</tr>';
    }
    $('#tbody').append(tr);

    //删除
    $('.delete').click(function () {
        $('#deleteModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        $('#suredelete').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'store/removeStore',
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

    //审核
    $('.approve').click(function () {
        $('#approveModal').modal('show');
        var _this = $(this).parent().prev();
        //门店id
        var id = $(this).parent().parent().attr('class');
        //门店名称
        var storeName = $(this).parent().parent().find('td').eq(5).html();
        //门店手机号
        var mobile = $(this).parent().parent().find('td').eq(4).html();

        $('#storeName').html(storeName);
        //审核通过
        $('#btnApprove').click(function () {
            //审核说明
            var description = $('#description').val();
            $.ajax({
                type: 'POST',
                url: urlle + 'store/upateStoreApproveLog',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(
                    {
                        storeId: id,
                        description: description,
                        approveStatus: 1,
                        operate: '审核通过'
                    }),
                success: function (res) {
                    $('#approveModal').modal('hide');
                    $.ajax({
                        type: 'POST',
                        url: '../verify/sendMessage',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({status:4, phonenumber: mobile})
                    });
                    window.location.reload();
                    insertStoreProduct(id);
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
        //审核不通过
        $('#btnReject').click(function () {
            //审核说明
            var description = $('#description').val();
            $.ajax({
                type: 'POST',
                url: urlle + 'store/upateStoreApproveLog',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(
                    {
                        storeId: id,
                        description: description,
                        approveStatus: 2,
                        operate: '审核不通过'
                    }
                ),
                success: function (res) {
                    $('#approveModal').modal('hide');
                    if(res.stateCode==100){
                        window.location.reload()
                    }
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });

    //审核历史
    $('.approvelog').click(function () {
        $('#approvelogModal').modal('show');
        var _this = $(this);
        //门店id
        var id = _this.parent().parent().attr('class');
        //门店名称
        var storeName = _this.parent().parent().find('td').eq(6).html();
        $('#storeNameLog').html(storeName);

        //审核历史记录
        $.ajax({
            type: 'POST',
            url: urlle + 'store/findStoreApproveLog',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                //绑定产生表格数据
                var tr = '';
                $('#tblog').html('');
                for (var i = 0; i < res.data.length; i++) {
                    tr += ' <tr>' +
                        '<td>' + res.data[i].approveDate + '</td>' +
                        '<td>' + res.data[i].description + '</td>' +
                        '<td>' + res.data[i].operate + '</td>' +
                        '</tr>';
                }
                $('#tblog').append(tr);
            },
            error: function (err) {
                alert(err.message);
            }
        });
    });

    //重置密码
    $('.reset').click(function () {
        $('#resetModal').modal('show');
        var _this = $(this);
        //门店id
        var id = _this.parent().parent().attr('class');
        $('#sureReset').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'store/updateStorePassword',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(
                    {
                        id: id
                    }),
                success: function (res) {
                    $('#resetModal').modal('hide');
                    $('#myModal1').modal('show');
                    $('#sure').click(function () {
                        $('#myModal1').modal('hide');
                        window.location.reload();
                    })
                    // initData();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });

    //编辑
    $('.edit').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../store/storeManageEditHtml?id=' + id + ''
    });

    //进入合一盒子
    $('.box').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('name');
        storeId = _this.parent().parent().attr('class');
        location.href = '../store/storeToBoxHtml?id=' + id + '='+storeId;
    });

}

//搜索
$('#btnQuery').click(function () {
    $('.spinner').show();
    var userCode = $('#txtUserCode').val();
    var mobile = $('#txtMobile').val();
    data = {
        userCode: userCode,
        mobile: mobile,
        pageNum: 1
    };
    initData();
});
//重置
$('#resert').click(function () {
    $('#txtUserCode').val('');
    $('#txtMobile').val('');
    data = {
        userCode: '',
        mobile: '',
        pageNum: 1
    };
    window.location.reload();
})

//全选框的全选功能
$("#checkAll").click(function() {
    var rows = $("#sample-table-2").find('input');
    for (var i = 0; i < rows.length; i++) {
        if (rows[i].type == "checkbox") {
            var e = rows[i];
            e.checked = this.checked;
        }
    }
});

//添加默认商品至门店
function insertStoreProduct(id) {
    $.ajax({
        type: 'POST',
        url: '../product/insertStoreProduct',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(
            {
                id: id
            }),
        success: function (res) {

        }
    });
}