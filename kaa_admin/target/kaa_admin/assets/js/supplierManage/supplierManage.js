var urlle = sessionStorage.getItem('urllen');
var pageNum = 1;
var data = {
    name: "",
    approveStatus: "",
    pageNum: pageNum,

};

$('#supplierAdd').click(function () {
    location.href = '../suplier/supplierManageAddHtml';
});


//初始化加载所有列表数据
initData();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../suplier/suplierList',
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
            if (res.data.length < 1) {
                $('.tcdPageCode').html('');
                return;
            }
            load(res);

            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount: res.pageSum,//总共的页码
                current: 1,//当前页
                backFn: function (p) {//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum = pageNum;
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
        url: '../suplier/suplierList',
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
    $('#thead').html('');
    var thead = '';
    thead = ' <tr><th>供应商编号</th>\n' +
        '  <th>供应商名称</th>\n' +
        '  <th>登录账号</th>\n' +
        '  <th>注册手机号</th>\n' +
        '  <th>联系人</th>\n' +
        '  <th>头像</th>\n' +
        '  <th>联系人电话</th>\n' +
        '  <th>工厂地址</th>\n' +
        '  <th>创办年限</th>\n' +
        '  <th>主营业务</th>\n' +
        '  <th>注册时间</th>\n' +
        '  <th>状态</th>\n' +
        '  <th>操作</th>\n' +
        '  </tr>';
    $('#thead').append(thead)
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
        var approveStatusName;
        if (res.data[i].approveStatus == '0') {
            approveStatusName = '待审核';
        } else if (res.data[i].approveStatus == '1') {
            approveStatusName = '审核成功';
        } else if (res.data[i].approveStatus == '2') {
            approveStatusName = '审核失败';
        }

        tr += ' <tr id="' + res.data[i].id + '">' +
            '<td>' + res.data[i].code + '</td>' +
            '<td>' + res.data[i].name + '</td>' +
            '<td>' + res.data[i].userCode + '</td>' +
            '<td>' + res.data[i].registerMobile + '</td>' +
            '<td>' + res.data[i].contact + '</td>' +
            '<td><img style="width: 65px;height: 65px;" src="' + res.data[i].icon + '" alt=""></td>' +
            '<td>' + res.data[i].contactPhone + '</td>' +
            '<td>' + res.data[i].address + '</td>' +
            '<td>' + res.data[i].openYears + '</td>' +
            '<td>' + res.data[i].scope + '</td>' +
            '<td>' + res.data[i].createdDate + '</td>' +
            '<td>' + approveStatusName + '</td>' +
            '<td style="min-width: 80px">' +
            '<div class="approve setting-btn">审核</div>' +
            '<div class="approvelog setting-btn">审核历史</div>' +
            '<div class="edit setting-btn">编辑</div>' +
            '<div class="delete setting-btn">删除</div>' +
            '<div class="reset setting-btn">重置密码</div>' +
            '</td>' +
            '</tr>';
    }
    $('#tbody').append(tr);

    //删除
    $('.delete').click(function () {
        $('#deleteModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        $('#suredelete').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/deleteSuplier',
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

    //审核历史
    $('.approvelog').click(function () {
        $('#approvelogModel').modal('show');
        var _this = $(this);
        //供应商id
        var id = _this.parent().parent().attr('id');
        //供应商名称
        var suplierName = _this.parent().parent().find('td').eq(2).html();
        // console.log(storeName);
        $('#suplierNameS').html(suplierName);
        //审核说明
        var explains = $('#explains').val();
        $.ajax({
            type: 'POST',
            url: urlle + 'suplier/suplierApproveHistory',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(
                {
                    suplierId: id
                }),
            success: function (res) {
                if (res.data.length < 1) {
                    $('#approvelogModel .table').css('display', '');
                    $('#approvelogModel #body').html('');
                    $('#supplierName').html('');
                    $('#supplierName').html(suplierName)
                } else {
                    $('#approvelogModel .table').css('display', '');
                    $('#approvelogModel #body').html('');
                    $('#supplierName').html('');
                    $('#supplierName').html(suplierName)
                    for (var i = 0; i < res.data.length; i++) {
                        var tr = '';
                        tr += ' <tr>' +
                            '<td>' + res.data[i].approveDate + '</td>' +
                            '<td>' + res.data[i].explain + '</td>' +
                            '<td>' + res.data[i].operate + '</td>' +
                            '</tr>';
                        $('#approvelogModel #body').append(tr);
                    }
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
        //关闭窗口
        $('#closed').click(function () {
            $('#approvelogModel').modal('hide');
        })
    })

    //审核
    $('.approve').click(function () {
        $('#approveModal').modal('show');
        var _this = $(this);
        //供应商id
        var id = _this.parent().parent().attr('id');
        //供应商名称
        var suplierName = _this.parent().parent().find('td').eq(2).html();
        //供应商编码
        var suplierCode = _this.parent().parent().find('td').eq(1).html();
        //供应商手机号
        var contactPhone = _this.parent().parent().find('td').eq(7).html();

        $('#suplierName').html(suplierName);
        $('#suplierCode').val(suplierCode);
        //审核通过
        $('#btnApprove').click(function () {
            //审核说明
            var explains = $('#explains').val();
            var code = $('#suplierCode').val();
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/updateApproveStatus',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(
                    {
                        id: id,
                        explains: explains,
                        code: code,
                        approveStatus: 1,
                        operate: '审核通过'
                    }),
                success: function (res) {
                    if(res.stateCode != 100){
                        alert(res.message);
                    }else{
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
                            data: JSON.stringify({status:5, phonenumber: contactPhone})
                        });
                        location.reload();
                    }

                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })

        //审核不通过
        $('#btnReject').click(function () {
            //审核说明
            var explains = $('#explains').val();
            var code = $('#suplierCode').val();
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/updateApproveStatus',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(
                    {
                        id: id,
                        explains: explains,
                        code: code,
                        approveStatus: 2,
                        operate: '审核不通过'
                    }
                ),
                success: function (res) {
                    if(res.stateCode != 100){
                        alert(res.message);
                    }else{
                        $('#approveModal').modal('hide');
                        location.reload();
                    }
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });

    //重置密码
    $('.reset').click(function () {
        $('#resetModal').modal('show');
        var _this = $(this);
        //门店id
        var id = _this.parent().parent().attr('id');
        $('#sureReset').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/updateSuplierPassword',
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
        var id = _this.parent().parent().attr('id');
        var userId = _this.parent().parent().attr('id');
        location.href = '../suplier/supplierManageEditHtml?id=' + id + '';
    });

    //商品列表
    $('.list').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        location.href = '../suplier/supplierGoodsListManageHtml?id=' + id + '';
    });


    //表格格式化
    function initTableCheckbox() {
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
        //批量删除
        $('#supplierRemove').click(function () {
            $('#deleteModal').modal('show');
            var arr = $('#sample-table-2 .xuanzhong');
            var arr1 = {suplierIds: []};
            for (var i = 0; i < arr.length; i++) {
                arr1.suplierIds.push({id: arr[i].id})
            }
            $('#suredelete').click(function () {
                $.ajax({
                    type: 'POST',
                    url: '../suplier/deleteMoreSuplier',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify(arr1),
                    success: function (res) {
                        $('#deleteModal').modal('hide');
                        location.reload();
                    },
                    error: function (err) {
                        alert(err.message);
                    }
                })
            })
        })
    }

    initTableCheckbox();
}

//搜索
$('#btnQuery').click(function () {
    $('.spinner').show();
    var name = $('#txtUserCode').val();
    var approve = $('#select').find("option:selected").val();
    data = {
        name: name,
        approveStatus: approve,
        pageNum: 1
    };
    initData();
});

//重置
$('#resert').click(function () {
    $('#txtUserCode').val('');
    $('#select').find("option:selected").val('');
    data = {
        name: '',
        approveStatus: '',
        pageNum: 1
    };
    window.location.reload();
})

