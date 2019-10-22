
$('#addFromAdmin').click(function () {
    location.href = '../boxManage/boxGoodsFromStoreHtml';
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

var pageNum = 1;
var data = {
    pageNum: pageNum,
    name: '',
    productCode: ''
};
//搜索
$('#orderSearch').click(function () {
    var name = $('#name').val();
    var productCode = $('#productCode').val();
    data = {
        name: name,
        productCode: productCode,
        pageNum: 1
    };
    list();
});
//重置
$('#orderReset').click(function () {
    $('#name').val('');
    $('#productCode').val('');
    data = {
        name: '',
        productCode: '',
        pageNum: 1
    };
    list()
})
//列表
function list() {
    $('.spinner').show();
    $.ajax({
        type: 'POST',
        url: urlle + 'boxManage/boxManageList',
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
        url: urlle + 'boxManage/boxManageList',
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
    $('#sample-table-2 thead').append(' <tr> <th>商品ID</th><th>名称</th> <th>图片</th> <th>分类</th>'+
        '<th>价格</th> <th>更新日期</th><th>状态</th> <th>库存</th><th>已订阅数量</th><th>二维码</th><th>操作</th></tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr+='<tr id="'+res.data[i].id+'">' +
            '<td>'+res.data[i].productCode+'</td>' +
            '<td>'+res.data[i].name+'</td>' +
            '<td><img style="width: 65px;height: 65px;" src="' + res.data[i].href + '" alt=""></td>' +
            '<td>'+res.data[i].categoryName+'</td>' +
            '<td>'+res.data[i].maxPrice+'</td>' +
            '<td>'+res.data[i].updateDate+'</td>' +
            '<td class="status">'+res.data[i].statusName+'</td>' +
            '<td>'+res.data[i].stock+'</td>' +
            '<td>'+res.data[i].orderCount+'</td>' +
            '<td>' +
            // '   <img style="width: 65px;height: 65px;" src="" alt="">' +
            // '   <div id="qr'+res.data[i].id+'"></div>' +
            // '   <div class="qrcode setting-btn" onclick="findQrcode('+res.data[i].id+',\''+res.data[i].href+'\')">查看二维码</div>' +
            '</td>' +
            '<td style="min-width: 80px">' +
            '<div class="edit setting-btn">编辑</div>' +
            '<div class="delete setting-btn">删除</div>' +
            '<div class="statusS setting-btn">上下架</div>' +
            '</tr>'
        $('#tbody').append(tr);

    }

    //删除
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        $('#sure').click(function () {
            $('.spinner').show();
            $.ajax({
                type: 'POST',
                url: urlle + 'boxManage/deleteProduct',
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
        id = _this.parent().parent().attr('id');
        var status = _this.parent().parent().find('.status').html();
        if(status == '已上架'){
            statu = 0;
        }else{
            statu = 1;
        }
    });
    $('#sure1').click(function () {
        $('#myModal1').modal('hide');
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'boxManage/updateProductStatus',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id,status: statu}),
            success: function (res) {
                $('.spinner-wrap').hide();
                // window.location.reload();
                if(statu == 1){
                    _this.parent().parent().find('.status').html('已上架')
                }else{
                    _this.parent().parent().find('.status').html('已下架')
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
        id = _this.parent().parent().attr('id');
        location.href='../boxManage/boxManageEditHtml?id='+id+'';
    });
}


//查看二维码
function findQrcode(id, src) {
    console.log(src);
    $('#myModal3').modal('show');
    $('#qrCode').html('');
    var qrcode = new QRCode(document.getElementById('qrCode'), {
        width : 300,//设置宽高
        height : 300
    });
    qrcode.makeCode("http://s.heyizhizao.com/boxMobile/weixinAuthorizationHtml?id="+id+"&type=2");
    var img = '<img style="width: 65px;height: 65px;position: absolute;left: 133px;top: 140px;right: 0px;bottom: 0px;border: 5px solid #fff" ' +
        'src="'+src+'" alt="">';
    $("#qrCode").append(img);
}

