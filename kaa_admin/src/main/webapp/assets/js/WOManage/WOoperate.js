
var urll = '../supOrderOperation/workOrderList';
$('.wo-item').click(function () {
    $('.wo-item').removeClass('active');
    $(this).addClass('active');

});
$('#btn1').click(function () {
    location.href='../WOManage/WOsendHtml';
});

$('#btn2').click(function () {
    location.href='../WOManage/WOcheckHtml';
});

$('#btn3').click(function () {
    location.href='../WOManage/WOinHtml';
});

// 收货
$('#state1').click(function () {
    $('.admin-detail').html('收货');
    $('#btn1').css('display','inline-block');
    $('#btn2').css('display','none');
    $('#btn3').css('display','none');
    urll = '../supOrderOperation/workOrderList';
    resert();
});
// 质检
$('#state2').click(function () {
    $('.admin-detail').html('质检');
    $('#btn1').css('display','none');
    $('#btn2').css('display','inline-block');
    $('#btn3').css('display','none');
    urll = '../supOrderOperation/supOrderTestList';
    resert();
});
// 入库
$('#state3').click(function () {
    $('.admin-detail').html('入库');
    $('#btn1').css('display','none');
    $('#btn2').css('display','none');
    $('#btn3').css('display','inline-block');
    urll = '../supOrderOperation/supOrderStorageList';
    resert();
});

//初始化加载所有列表数据
var pageNum = 1;
var data = {
    orderNo: '', //工单号
    name: '',//操作人
    startTime: '',
    endTime: '',
    pageNum: pageNum
};
function initData() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: urll,
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            layer.close(index);
            //清空原表格内容
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
                return;
            }
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum=pageNum;
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
initData();
function queryData() {
    $.ajax({
        type: 'POST',
        url: urll,
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
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('.tcdPageCode').html('');
        return;
    }
    $('#sample-table-2 thead').html('');
    $('#sample-table-2 thead').append(' <tr> <th>工单号</th> <th>操作人</th> <th>时间</th></tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'"> <td>'+res.data[i].orderNo+'</td> <td>'+res.data[i].name+'</td> <td>'+res.data[i].updateDate+'</td> ' ;
        $('#tbody').append(tr);
    }
}
//搜索
$('#btnQuery').click(function () {
    data = {
        orderNo: $('#goodsNoInput').val(), //工单号
        name: $('#name').val(),
        startTime: $('#datepickerStart').val(),
        endTime: $('#datepickerEnd').val(),
        pageNum: pageNum
    };
    initData();
});
//重置
function resert() {
    $('#orderNo').val('');
    $('#name').val('');
    $('#startTime').val('');
    $('#endTime').val('');
    data = {
        orderNo: '', //工单号
        name: '',
        startTime : '',
        endTime: '',
        pageNum: 1
    };
    initData();
}