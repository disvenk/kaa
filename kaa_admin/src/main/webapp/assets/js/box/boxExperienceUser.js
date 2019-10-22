var urlle = sessionStorage.getItem('urllen');
var pageNum = 1;
var data = {
    pageNum: pageNum
};
function post() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'boxManage/findSalesTeacherList',
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

function ajax() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'boxManage/findSalesTeacherList',
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
        tr += ' <tr>' +
            '<td>'+res.data[i].name+'</td>' +
            '<td>'+res.data[i].age+'</td>' +
            '<td>'+res.data[i].mobile+'</td>' +
            '<td>'+res.data[i].createdDate+'</td>'
    }
    $('#tbody').append(tr);
}