var url = window.location.href;
var id = url.split('=')[1];
var storeId = url.split('=')[2];
console.log(storeId);
var pageNum = 1;

var data = {
    id:storeId,
  pageNum:pageNum
};

//加载数据
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){

        var tr = '';
        tr += '<tr>\n' +
            '                                <td>'+res.data[i].createdDate+'</td>\n' +
            '                                <td>'+res.data[i].name+'</td>\n' +
            '                                <td>'+res.data[i].price+'</td>\n' +
            '                               <td>'+res.data[i].deposit+'</td>\n' +
            '                               <td>'+res.data[i].count+'</td>\n' +
            '                               <td>'+res.data[i].termTime+'</td>\n' +
            '                            </tr>';
        $('#tbody').append(tr);
    }
}

//展示分页条
function page() {
    $.ajax({
        type: 'POST',
        url: '../heYiBox/findBoxOperateLog',
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
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum = pageNum;
                    checkBox();
                }
            });
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}

page();

//展示列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../heYiBox/findBoxOperateLog',
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
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            load(res);
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}

function checkBox(){
$.ajax({
    type: 'POST',
    url: '../heYiBox/findUserStoreBox',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id:id}),
    success: function (res) {
        if (res.data.isBox == true) {
            $('#username').html(res.data.userName);
            $('#mobile').html(res.data.mobile);
            $('#isBox').html("已开通");
            $('#deposit').html(res.data.yaJin+'<span class="back-money" onclick="returnYaJin('+res.data.boxId+')">退押金</span>');
            $('#count').html(res.data.count);
            $('#termTime').html(res.data.termTime);
            list();
        } else {
            $('#username').html("");
            $('#mobile').html("");
            $('#isBox').html("未开通");
            $('#deposit').html("");
            $('#count').html("");
            $('#termTime').html("");
            $('.tcdPageCode').html('');
            $('#tbody').html('<tr><td colspan="6">暂无更多信息</td></tr>');
        }
    },
    error: function (err) {
        alert(err.message);
        $('.tcdPageCode').html('');
    }
});
}

checkBox();

function returnYaJin(storeId){
    var yaJin = $("#deposit").html();
    var arr = yaJin.split("");
    if(arr[0]=="0"){
        alert("押金已退还")
        return
    }
    $('#myModal').modal('show');

        $.ajax({
            type: 'POST',
            url: '../heYiBox/returnYaJin',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: storeId}),
            success: function (res) {
                $('#myModal').modal('show');
                $('#sure').click(function () {
                    $('#myModal').modal('hide');
                })
                checkBox();
            },
            error: function (err) {
                alert(err.message);
            }
        });
}