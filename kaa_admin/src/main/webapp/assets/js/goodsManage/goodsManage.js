var url = window.location.href;
var pageSign = url.split('=')[1];
var page;
var pageNum;
if(pageSign=="" || pageSign==undefined){
    pageNum=1;
    page=1;
}else {
    page=pageSign;
    pageNum=pageSign;
}

$('#adminAdd').click(function(){
    location.href='../product/goodsManageAddHtml';
});

$('#addFromStore').click(function(){
    location.href='DesignerGoodsFromStore.html';
});
var urlle = sessionStorage.getItem('urllen');

//分类
$('#Suplierscope').change(function () {
    change();
});

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
        //分类
        var Suplierscope = '<option value=""></option>';
        for (var i = 0; i < res.data.length; i++) {

            Suplierscope += ' <option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';

        }
        $('#Suplierscope').append(Suplierscope);
        change();
    },
    error: function (err) {
        alert(err.message)
    }
});

function change() {
    $.ajax({
        type: 'POST',
        url: urlle + 'base/getPlaProductCategoryList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parentId: $('#Suplierscope option:selected').attr('id')}),
        success: function (res) {
            $('#secCategory').html('');
            //分类
            var secCategory = '';
            if(res.data.length!=0){
                for (var i = 0; i < res.data.length; i++) {
                    secCategory += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
                }
                $('#secCategory').append(secCategory);
            }else{
                $('#secCategory').append('<option style="width: 50px" value=""></option>');
            }

        },
        error: function (err) {
            alert(err.message)
        }
    });
}

var data = {
    pageNum: pageNum
};
function post() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'product/productList',
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
                    pageNum = $(".current").html();
                    page =$(".current").html();
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
    var goodsName = $('#goodsName').val();
    var goodsPho = $('#goodsPho').val();
    var type = $('#secCategory').find("option:checked").attr("id");
    var isDesign = $('#isDesign').find("option:selected").text();
    var isMoren = $('#isMoren').find("option:selected").text();
    if(isDesign == '是'){
        isDesign = true
    }else if(isDesign == '否'){
        isDesign = false
    }

    if(isMoren == '是'){
        isMoren = true
    }else if(isMoren == '否'){
        isMoren = false
    }
    data = {
        name: goodsName,
        categoryId: type,
        pno: goodsPho,
        designer: isDesign,
        pageNum: 1,
        isMoren:isMoren
    };
   post();
});
//重置
$('#resert').click(function () {
    $('#goodsName').val('');
    $('#goodsPho').val('');
    $('#type').val('');
    $('#isDesign').val();
    $('#Suplierscope').val("");
    $('#secCategory').val("");
    var isMoren = $('#isMoren').val("");
    data = {
        name: '',
        categoryId: '',
        pno: '',
        designer: '',
        pageNum: 1,
       isMoren:''
    };
    post()
})
function ajax() {
    $.ajax({
        type: 'POST',
        url:  urlle + 'product/productList',
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
            '<td>'+res.data[i].productCode+'</td>' +
            '<td>'+res.data[i].name+'</td>' +
            '<td><img style="width: 65px;height: 65px" src="'+res.data[i].mainpicHref+'" alt=""></td>' +
            '<td>'+res.data[i].brand+'</td>' +
            '<td>'+res.data[i].categoryName+'</td>' +
            '<td>'+ (res.data[i].isDesigner == true ? "是" : "否") +'</td>' +
            '<td>'+res.data[i].color+'</td>' +
            '<td>'+res.data[i].size+'</td><td>'+timetrans(res.data[i].updateDate)+'</td>' +
            '<td>'+res.data[i].saleschannelName+'</td><td>'+res.data[i].onlinePrice+'</td><td>'+res.data[i].offlinePrice+'</td><td>'+(res.data[i].isAdd == true ? "是" : "否")+'</td><td>'+res.data[i].remark+'</td>' +
            '<td style="width:150px"><div id="storageEdit" class="setting-btn edit">编辑</div><div class="setting-btn detail">详情</div><div class="setting-btn delete">删除</div>';
        if (res.data[i].isAdd == true) {
            tr += '<div class="setting-btn" onclick="isAdd('+res.data[i].id+',false)">取消门店商品库默认</div>';
        } else {
            tr += '<div class="setting-btn" onclick="isAdd('+res.data[i].id+',true)">门店商品库默认</div>';
        }
        tr += '</td></tr>';
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
                url: urlle + 'product/deleteProduct',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: pid}),
                success: function (res) {
                    $('#myModal').modal('hide');
                   window.location.href="../product/goodsManageHtml?="+page;
                    //window.location.reload();
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
        location.href = '../product/goodsManageEditHtml?id='+id+'='+page;
    });
    //详情
    $('.detail').click(function () {
        $('.spinner').show();
        var _this = $(this);
        id = _this.parent().parent().attr('class')
        location.href = '../product/goodsManageDetailHtml?id='+id+'='+page;
    });
    // //合并列
    // var tr = $('#tbody tr');
    // for(var i = 0; i < tr.length; i++){
    //     if(tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')){
    //         tr.eq(i + 1).find('td').eq(18).remove()
    //         tr.eq(i).find('td').eq(18).attr('rowspan', $('.'+tr.eq(i).attr('class')+'').length);
    //     }
    // }
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


function isAdd(id, isAdd) {
    $.ajax({
        type: 'POST',
        url: urlle + 'product/productIsAdd',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id, isAdd: isAdd}),
        success: function (res) {
            window.location.href="../product/goodsManageHtml?="+page;
            //window.location.reload();
        },
        error: function (err) {
            alert(err.message);
        }
    });
}