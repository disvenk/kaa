var page;
var data = {
    pageNum: 1,
    name:'',
    telephone: ''
};
function post() {
    $.ajax({
        type: 'POST',
        url:  '../channel/desChannellist',
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
            var pageSum =res.pageSum;
            var total=res.total;
            var pageSize=(res.total != 0? res.pageSize : 0);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageSum:pageSum,
                total:total,
                pageSize:(res.total != 0? res.pageSize : 0),
                pageCount:res.pageSum,//总共的页码
                current:page,//当前页
                backFn:function(p){//p是点击的页码
                    data.pageNum = $(".current").html();
                    page =$(".current").html();
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
$('#search').click(function () {
    $('.spinner').show();
    var name = $('#name').val();
    var telephone = $('#telephone').val();
    data = {
        name: name,
        telephone: telephone,
        pageNum: 1
    };
    post();
});
//重置
$('#resert').click(function () {
    $('#telephone').val('');
    $('#name').val('');
    data = {
        name: '',
        telephone: '',
        pageNum: 1
    };
    post()
});
function ajax() {
    $.ajax({
        type: 'POST',
        url:  '../channel/desChannellist',
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
    $('.spinner').hide();
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += ' <tr class="'+res.data[i].id+'">' +
            '<td>'+res.data[i].name+'</td>' +
            '<td>'+res.data[i].telephone+'</td>' +
            '<td>'+res.data[i].address+'</td>' +
            '<td>'+res.data[i].type+'</td>' +
            '<td>'+res.data[i].createdDate+'</td>' +
            '<td style="width:150px"><div id="storageEdit" style="cursor: pointer" class="setting-btn edit">编辑</div><div style="cursor: pointer" class="setting-btn delete">删除</div></td></tr>';
    }
    $('#tbody').append(tr);
    //删除
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var pid = _this.parent().parent().attr('class');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: '../channel/channelDesRemove',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: pid}),
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
    $('.edit').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../channel/designerJoinEditHtml?id='+id;
    });
}