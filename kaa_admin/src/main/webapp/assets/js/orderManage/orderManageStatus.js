var url = window.location.href;
var suplierOrderNo = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
var data = {
    pageNum: 1,
    suplierOrderNo : suplierOrderNo
};
$('#supplierOrderNo').val('采购订单号:'+suplierOrderNo);
//列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../orderManage/supOrderStatusList',
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
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../orderManage/supOrderStatusList',
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
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}

//加载数据
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        var tr = '';
        tr += '<tr id="'+res.data[i].orderId+'">\n' +
            '                                <td>'+res.data[i].orderNo+'</td>\n' +
            '                                <td>'+res.data[i].producedStatusName+'</td>\n' +
            '                                <td>' +
            '                                   <span class="storage-operate ml10p" onclick="detail('+res.data[i].orderId+')">查看</span>' +
            '                                </td>\n' +
            '                            </tr>';
        $('#tbody').append(tr);
    }
}
//详情
function detail(id) {
    location.href='../WOManage/kaaDetailHtml?id='+id+'';
}