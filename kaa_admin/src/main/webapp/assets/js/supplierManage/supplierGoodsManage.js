$('#supplierGoodsAdd').click(function(){
    location.href='../suplier/supplierGoodsManageAddHtml';
});

$('#addFromStore').click(function(){
    location.href='DesignerGoodsFromStore.html';
});
var urlle = sessionStorage.getItem('urllen');
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
        $('.spinner').hide();
        $('#type').append('<option id="" value=""></option>')
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
function post() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'suplier/goodsManageList',
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
            load(res)
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
//搜索
$('#search').click(function () {
    $('.spinner').show();
    var name = $('#name').val();
    var suplierName = $('#suplierName').val();
    var type = $('#type').find("option:checked").attr("id");
    data = {
        name: name,
        categoryId: type,
        suplierName: suplierName,
        pageNum: 1
    };
    post();
});
//重置
$('#resert').click(function () {
    $('#name').val('');
    $('#suplierName').val('');
    data = {
        name: '',
        suplierName: '',
        categoryId:'',
        pageNum: 1
    };
    window.location.reload();
})
function ajax() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'suplier/goodsManageList',
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
    for(var i = 0; i < res.data.length; i++){
        var tr = '';
        for(var j = 0; j < res.data[i].supplierList.length; j++){
            tr += ' <tr class="'+res.data[i].id+'">' +
                '<td>'+res.data[i].name+'</td>' +
                '<td><img style="width: 65px;height: 65px;" src="'+res.data[i].mainpicHref+'" alt=""></td>' +
                '<td>'+res.data[i].pno+'</td>' +
                '<td>'+res.data[i].brand+'</td>' +
                '<td>'+res.data[i].categoryName+'</td>' +
                '<td>'+res.data[i].supplierList[j].offlinePrice+'</td>' +
                '<td>'+res.data[i].supplierList[j].color+'</td>' +
                '<td>'+res.data[i].supplierList[j].size+'</td>' +
                '<td>'+res.data[i].supplierList[j].stock+'</td>' +
                '<td>'+res.data[i].material+'</td>' +
                '<td>'+timetrans(res.data[i].updateDate)+'</td>' +
                '<td>'+res.data[i].suplierName+'</td>' +
                '<td>'+res.data[i].supplierList[j].categoryRemark+'</td>'+ '<td>' +
                '<div class="supplierGoodsEdit setting-btn">编辑</div>' +
                '<div class="supplierGoodsDelete setting-btn">删除</div>' +
                '</td>' +
                '</tr>';
        }
        $('#tbody').append(tr);
    }

    //删除
    $('.supplierGoodsDelete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var pid = _this.parent().parent().attr('class');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/deleteSuplierGoods',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: pid}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    if(res.stateCode == 100) {
                        location.reload();
                    }
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })

    });
    //编辑
    $('.supplierGoodsEdit').click(function () {
        var _this = $(this);
        id = _this.parent().parent().attr('class');
        location.href = '../suplier/supplierGoodsManageEditHtml?id='+id+''
    });
    //合并列
    var tr = $('#tbody tr');
    for(var i = 0; i < tr.length; i++){
        if(tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')){
            tr.eq(i + 1).find('td').eq(17).remove()
            tr.eq(i).find('td').eq(17).attr('rowspan', $('.'+tr.eq(i).attr('class')+'').length);
        }
    }
    // function mc(tableId, startRow, endRow, col) {
    //     var tb = document.getElementById(tableId);
    //     if (col >= tb.rows[0].cells.length) {
    //         return;
    //     }
    //     if (col == 0) { endRow = tb.rows.length-1; }
    //     for (var i = startRow; i < endRow; i++) {
    //         if (tb.rows[startRow].cells[col].innerHTML == tb.rows[i + 1].cells[0].innerHTML) {
    //             tb.rows[i + 1].removeChild(tb.rows[i + 1].cells[0]);
    //             tb.rows[startRow].cells[col].rowSpan = (tb.rows[startRow].cells[col].rowSpan | 0) + 1;
    //             if (i == endRow - 1 && startRow != endRow) {
    //                 mc(tableId, startRow, endRow, col + 1);
    //             }
    //         } else {
    //             mc(tableId, startRow, i + 0, col + 1);
    //             startRow = i + 1;
    //         }
    //     }
    // }
    // mc('tbody',0,10,0);
}