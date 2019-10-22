/**
 * Created by 12452 on 2017/10/24.
 */

//分类
$.ajax({
    type: 'POST',
    url: '../storeProductManage/storeProductCategoryList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0 ;i < res.data.length; i++){
            var options = '';
            options += '<option value="'+res.data[i].categoryId+'">'+res.data[i].categoryName+'</option>';
            $('#type').append(options);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});

var data = {
    pageNum: 1
};
//搜索
$('#orderSearch').click(function () {
    var pno = $('#pno').val();
    var name = $('#name').val();
    var type = $('#type').find("option:checked").attr("value");
    data = {
        pno: pno,
        name: name,
        categoryId: type,
        pageNum: 1
    };
    list()
});
//重置
$('#resert').click(function () {
    $('#pno').val("");
    $('#name').val("");
    $('#type').val("");
    data = {
        pno: '',
        name:  '',
        categoryId:  '',
        pageNum: 1
    };
    list()
})
//列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/productList',
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
            load(res)
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
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
        url: '../storeSuplierOrder/productList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res)
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}

function load(res) {
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += '<tr id="'+res.data[i].id+'">\n' +
            // '    <td><input type="checkbox"></td>\n' +
            '    <td>'+res.data[i].pno+'</td>\n' +
            '    <td><img class="order-info-img" src="'+res.data[i].href+'" alt=""></td>\n' +
            '    <td>'+res.data[i].name+'</td>\n' +
            '    <td>'+res.data[i].brand+'</td>\n' +
            '    <td>'+res.data[i].categoryName+'</td>\n' +
            '    <td>'+res.data[i].price+'</td>\n' +
            '    <td>'+res.data[i].suplierDay+'</td>\n' +
            '    <td>'+res.data[i].updateDate+'</td>\n' +
            '    <td><span class="to-goods-detail color-blue" onclick="goodsDetail('+res.data[i].id+')">详情</span></td>\n' +
            '  </tr>';
    }
    $('#tbody').append(tr);
    initTableCheckbox();
}


function goodsDetail(id) {
    location.href = '../storeSuplierOrder/buyGoodsDetailHtml?id=' + id;
}




function initTableCheckbox() {
    var $thr = '';
    var $thr = $('#sample-table-1 thead tr');
    var $checkAllTh = '';
    var $checkAllTh = $('<th class="center"><input type="checkbox" id="checkAll" name="checkAll" />全选</th>');
    /*将全选/反选复选框添加到表头最前，即增加一列*/
    $thr.prepend($checkAllTh);
    /*“全选/反选”复选框*/
    var $checkAll = $thr.find('input');
    $checkAll.click(function (event) {
        /*将所有行的选中状态设成全选框的选中状态*/
        $tbr.find('input').prop('checked', $(this).prop('checked'));
        /*并调整所有选中行的CSS样式*/
        if ($(this).prop('checked')) {
            $tbr.find('input').parent().parent().addClass('xuanzhong');
        } else {
            $tbr.find('input').parent().parent().removeClass('xuanzhong');
        }
        /*阻止向上冒泡，以防再次触发点击操作*/
        event.stopPropagation();
    });
    /*点击全选框所在单元格时也触发全选框的点击操作*/
    $checkAllTh.click(function () {
        $(this).find('input').click();

    });
    var $tbr = $('#sample-table-1 tbody tr');
    var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
    /*每一行都在最前面插入一个选中复选框的单元格*/
    $tbr.prepend($checkItemTd);
    /*点击每一行的选中复选框时*/
    $tbr.find('input').click(function (event) {
        /*调整选中行的CSS样式*/
        $(this).parent().parent().toggleClass('xuanzhong');
        /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
        $checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
        /*阻止向上冒泡，以防再次触发点击操作*/
        event.stopPropagation();
    });

    $("#add").click(function () {
        var ids = '';
        var checked = $tbr.find('input:checked').parent().parent().attr('id');
        for (var i=0; i<$tbr.find('input:checked').parent().parent().length; i++) {
            var id = $tbr.find('input:checked').parent().parent().eq(i).attr('id');
            ids += id+",";
        }
        location.href='../storeSuplierOrder/buyOrderBuyHtml?ids='+ids.substr(0,ids.length-1);
    })
}
