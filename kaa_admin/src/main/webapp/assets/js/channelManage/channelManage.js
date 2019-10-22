var urlle = sessionStorage.getItem('urllen');
var pageNum = 1;
var data = {
    pageNum: pageNum
};
function post() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'channel/list',
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
                    pageNum = $(".current").html();
                    data.pageNum = pageNum;
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
    var contact = $('#contact').val();
    var tel = $('#tel').val();
    data = {
        contact: contact,
        telephone: tel,
        pageNum: 1
    };
    post();
});
//重置
$('#resert').click(function () {
    $('#contact').val('');
    $('#tel').val('');
    data = {
        contact: '',
        telephone: '',
        pageNum: 1
    };
    post()
});
function ajax() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'channel/list',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    });
}


function load(res) {
    $('#tbody').html('');
    $('.spinner').hide();
    var tr = '';
    for(var i = 0; i < res.data.length; i++){
        tr += ' <tr class="'+res.data[i].id+'">' +
            '<td>'+res.data[i].name+'</td>' +
            '<td>'+res.data[i].address+'</td>' +
            '<td>'+res.data[i].contact+'</td>' +
            '<td>'+res.data[i].telephone+'</td>' +
            '<td>'+res.data[i].type+'</td>' +
            '<td>'+res.data[i].createTime+'</td>' +
            '<td style="width:90px"><div id="storageEdit" class="setting-btn edit">编辑</div><div class="setting-btn delete">删除</div></td></tr>';
    }
    $('#tbody').append(tr);
    //删除
    $('#tbody .delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'channel/channelRemove',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    window.location.reload();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })

    });
    //编辑
    $('#tbody .edit').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../channel/channelEditHtml?id='+id+''
    });
}