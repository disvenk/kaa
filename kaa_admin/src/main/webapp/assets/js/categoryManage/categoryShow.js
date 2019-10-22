/**
 * Created by 12452 on 2017/10/20.
 */
var categoryid = '';
var pageNum = 1;
var data = {
    pageNum: pageNum
};
var ss;
var urlle = sessionStorage.getItem('urllen');
var productIds = '';
//分类
$.ajax({
    type: 'POST',
    url: urlle + 'base/getPlaProductCategoryLevelOneList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        $('#type').append('<option id=""></option>')
        for(var i = 0 ;i < res.data.length; i++){
            var options = '';
            options += '<option id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
            $('#type').append(options);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
function post() {
    $.ajax({
        type: 'POST',
        url:  '../category/getPlaProductCategoryListAll',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('#tbody').html('');
            if(res.data.length<1){
                $('.tcdPageCode').html('');
            }
            load(res)
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    data.pageNum = $(".current").html();
                    ajax();
                }
            });
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }

    });
}

post();

//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    /*categoryName = $('#categoryName').val();*/
    var type = $('#type').find("option:checked").attr("id");
    data = {
       /* categoryName:categoryName,*/
        categoryId: type,
        pageNum:1
    };
    // console.log(data);
    post();
});
//重置
$('#orderReset').click(function () {
    $('#categoryName').val('');
    categoryName = $('#categoryName').val('');
    data = {
        categoryId: '',
        pageNum: 1
    };
    ajax();
});
//新增分类
$('#addCategory').click(function () {
    $('#approveModal').modal('show');
    $('#sure2').click(function (){
        var name = $('#name').val();
        var suplierDay = $('#suplierDay').val();
        var remark = $('#remark').val();
        ss = {};
        ss = {
            suplierDay: suplierDay,
            name: name,
            remark: remark,
            /*parentId: parentId*/
        }
        if (name == '') {
            layer.alert('分类名称不能为空！', {icon: 0});
        }else if(suplierDay == ''){
            layer.alert('供应周期不能为空！', {icon: 0});
        }else {
            $.ajax({
                type: 'POST',
                url: urlle + 'category/newCategoryAddManage',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(ss),
                success: function (res) {
                    if (res.stateCode == 100) {
                        window.location.reload();
                    } else {
                        alert(res.message)
                    }
                },
                error: function (err) {
                    alert(err.message)
                }
            })
        }
    })

})

// ajax加载列表
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../category/getPlaProductCategoryListAll',
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
};

function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        productIds='';
        var table1 = '';
        table1 += '<tr class="'+res.data[i].id+'" style="background-color:rgb(243,242,244);height: 50px;">' +
            '<td style="border: none"><span class="color-blue"></span>' + res.data[i].name + '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + res.data[i].suplierDay +'(天)'+ '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + res.data[i].updateDate + '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + res.data[i].remark + '</td>' +
            '<td style="border: none"><span id="storageEdit" class="edit1 editModal setting-btn">编辑</span><span class="delete1 setting-btn">删除</span><span class="addCategory1 setting-btn"><br>添加下级分类</span></td></tr>';
        '<td style="border: none" class="null"></td></tr>';
        for (var j = 0; j < res.data[i].categoryList.length; j++) {
            var detailListArr = res.data[i].categoryList[j];
            table1 += '<tr id="' + detailListArr.id + '">' +
                '<td>' + detailListArr.name + '</td>' +
                '<td>' + detailListArr.suplierDay +'(天)'+ '</td>' +
                '<td>' + detailListArr.updateDate + '</td>' +
                '<td>' + detailListArr.remark + '</td>'+
                '<td style="width:100px"><span id="storageEdit" class="edit2 editModal setting-btn">编辑</span><span class="delete2 setting-btn">删除</span><span class="addCategory1 setting-btn"><br>添加下级分类</span></td></tr>';
        }
        $('#tbody').append(table1);
    }
    // 一级分类删除
    $('.delete1').click(function(){
        $('#myModal').modal('show');
        var categoryId = $(this).parent().parent().attr('class');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: '../category/deleteCategoryParentId',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: categoryId}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    if(res.stateCode != 100){
                        alert(res.message);
                    }
                    window.location.reload();
                },
                error: function (err) {
                    $('#myModal').modal('hide');
                    alert(err.message);
                }
            });
        })

    })
    // 二级分类删除
    $('#tbody .delete2').click(function(){
        $('#myModal').modal('show');
        var categoryId = $(this).parent().parent().attr('id');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: '../category/deleteCategoryId',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: categoryId}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    window.location.reload()
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    })
    // 编辑
    $('#tbody .editModal').click(function(){
        $('#approveModal').modal('show');
        var id = $(this).parent().parent().attr('id');
        var parentId = $(this).parent().parent().attr('class');
        $.ajax({
            type: 'POST',
            url: '../category/categoryManageEditDetail',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id,parentId: parentId}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#name').val(res.data[0].name);
                    $('#remark').val(res.data[0].remark);
                    $('#suplierDay').val(res.data[0].suplierDay);
                    $('#sure2').click(function () {
                        ss = {};
                        var name = $('#name').val();
                        var remark = $('#remark').val();
                        var suplierDay = $('#suplierDay').val();
                        ss = {
                            suplierDay: suplierDay,
                            name: name,
                            remark: remark,
                            id:id,
                            parentId: parentId
                        }
                        if (name == '') {
                            layer.alert('分类名称不能为空！', {icon: 0});
                        }else if(suplierDay == ''){
                            layer.alert('供应周期不能为空！', {icon: 0});
                        }else{
                            $.ajax({
                                type: 'POST',
                                url: urlle + 'category/updateCategoryManageParentId',
                                dataType: 'json',
                                contentType: 'application/json; charset=utf-8',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                data: JSON.stringify(ss),
                                success: function (res) {
                                    if(res.stateCode == 100){
                                        window.location.reload();
                                    }else {
                                        alert(res.message)
                                    }
                                },
                                error: function (err) {
                                    alert(err.message)
                                }
                            })
                        }
                    })
                }else{
                    alert(res.message);
                }

            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
    //添加二级分类
    $('#tbody .addCategory1').click(function () {
        $('#approveModal').modal('show');
        var id = $(this).parent().parent().attr('id');
        var id = $(this).parent().parent().attr('class');
        $('#sure2').click(function (){
            var name = $('#name').val();
            var remark = $('#remark').val();
            var suplierDay = $('#suplierDay').val();
            ss = {};
            ss = {
                suplierDay: suplierDay,
                name: name,
                remark: remark,
                id:id,
                /*parentId: parentId*/
            }
            if (name == '') {
                layer.alert('分类名称不能为空！', {icon: 0});
            }else if(suplierDay == ''){
                layer.alert('供应周期不能为空！', {icon: 0});
            }else{
                $.ajax({
                    type: 'POST',
                    url: urlle + 'category/categoryAddManage',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify(ss),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            window.location.reload();
                        } else {
                            alert(res.message)
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        })


    })
}