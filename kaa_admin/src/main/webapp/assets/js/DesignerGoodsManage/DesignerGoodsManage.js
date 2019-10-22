$('#addFromAdmin').click(function () {
    location.href = '../designerProduct/designerManageFromStoreHtml';
});
var urlle = sessionStorage.getItem('urllen');
//时间戳转换
function timetrans(date){
    var date = new Date(date);//如果date为10位不需要乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() <10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() <10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y+M+D+h+m+s;
}
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
var pageNum = 1;
var data = {
    pageNum: pageNum
};
//搜索
$('#orderSearch').click(function () {
    var goodsName = $('#orderBuyer').val();
    var type = $('#type').find("option:checked").attr("id");
    data = {
        name: goodsName,
        categoryId: type,
        pageNum: 1
    };
    list();
});
//重置
$('#orderReset').click(function () {
    $('#orderBuyer').val('');
    $('#type').val('');
    data = {
        name: '',
        categoryId: '',
        pageNum: 1
    };
    list()
})
//列表
function list() {
    $('.spinner').show();
    $.ajax({
        type: 'POST',
        url: urlle + 'designerProduct/designerProductManageList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            load(res);
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
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: urlle + 'designerProduct/designerProductManageList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            load(res)
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}


function load(res) {
    $('.spinner').hide();
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        if(res.data[i].status == '1'){
            res.data[i].status = '已上架';
        }else {
            res.data[i].status = '未上架';
        }
        for(var j = 0; j < res.data[i].supplierList.length; j++){
            tr += ' <tr class="'+res.data[i].id+'">' +
                '<td>'+res.data[i].productCode+'</td>' +
                '<td>'+res.data[i].name+'</td>' +
                '<td><img style="width: 65px;height:65px;" src="'+res.data[i].mainpicHref+'" alt=""></td>' +
                '<td>'+res.data[i].pno+'</td>' +
                '<td>'+res.data[i].brand+'</td>' +
                '<td>'+res.data[i].categoryName+'</td>' +
                '<td>'+res.data[i].supplierList[j].offlinePrice+'</td><td>'+res.data[i].supplierList[j].color+'</td>' +
                '<td>'+res.data[i].supplierList[j].size+'</td><td>'+res.data[i].supplierList[j].stock+'</td><td>'+timetrans(res.data[i].updateDate)+'</td>' +
                '<td class="status">'+res.data[i].status+'</td>' +
                '<td>'+res.data[i].supplierList[j].remark+'</td>' +
                '<td style="width:90px"><div id="storageEdit" class="edit setting-btn">编辑</div><div class="delete setting-btn">删除</div><div class="statusS setting-btn">上下架</div></td></tr>';
        }
    }
    $('#tbody').append(tr);

    //删除
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        $('#sure').click(function () {
            $('.spinner').show();
            $.ajax({
                type: 'POST',
                url: urlle + 'designerProduct/deleteDesignerProduct',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('.spinner').hide();
                    $('#myModal').modal('hide');
                    window.location.reload();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });
    //上下架
    var statu;
    var id;
    var _this;
    $('.statusS').click(function () {
        $('#myModal1').modal('show');
        _this = $(this);
        id = _this.parent().parent().attr('class');
        var status = _this.parent().parent().find('.status').html();
        if(status == '已上架'){
            statu = 1;
        }else{
            statu = 0;
        }
    });
    $('#sure1').click(function () {
        $('#myModal1').modal('hide');
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'designerProduct/deleteDesignerProductStatus',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id,status: statu}),
            success: function (res) {
                $('.spinner-wrap').hide();
                if(statu == 1){
                    _this.parent().parent().find('.status').html('未上架')
                }else{
                    _this.parent().parent().find('.status').html('已上架')
                }
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
        location.href = '../designerProduct/designerGoodsEditHtml?id='+id+''
    });
}
