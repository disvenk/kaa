var data = {
    pageNum: 1,
    name: '',
    telephone: ''
  };
function post() {
    $.ajax({
        type: 'POST',
        url: '../feedback/findFeedbackList',
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
    data = {
        name: $('#contact').val(),
        telephone: $('#tel').val(),
        pageNum: 1
    };
    post();
});
//重置
$('#resert').click(function () {
    $('#contact').val('');
    $('#tel').val('');
    data = {
        name: '',
        telephone: '',
        pageNum: 1
    };
    post()
})
function ajax() {
    $.ajax({
        type: 'POST',
        url:  '../feedback/findFeedbackList',
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
            '<td>'+res.data[i].telephone+'</td>' +
            '<td>'+res.data[i].description+'</td>' +
            '<td>'+res.data[i].createdDate+'</td>' +
            '<td style="width:90px"><div class="setting-btn delete">删除</div></td></tr>';
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
                url: '../feedback/deleteFeedback',
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
}