
var urlle = sessionStorage.getItem('urllen');
$.ajax({
    type: 'POST',
    url: '../account/loginInfo',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100) {
            if (res.data.userStatus != '0') {
              layer.alert('请先完成入驻', {icon:0}, function () {
                  window.open('../salesHome/joinHtml?type='+res.data.userStatus);
              });
            }
        } else {
//                    alert(res.message);
        }
    },
    error: function (err) {
//                alert(err.message);
    }
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
        $('#type').append('<option id=""></option>')
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
//搜索
$('#orderSearch').click(function () {
    var goodsName = $('#orderBuyer').val();
    var type = $('#type').find("option:checked").attr("id");
    data = {
        name: goodsName,
        categoryId: type,
        pageNum: 1
    };
    list();
});
//重置
$('#orderReset').click(function () {
    $('#orderBuyer').val('');
    $('#type').val('');
    data = {
        name: '',
        categoryId: '',
        pageNum: 1
    };
    list()
})
//列表
function list() {
    $.ajax({
        type: 'POST',
        url: urlle + 'storeProductManage/storeProducManagetList',
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
                    data.pageNum = $(".current").html();
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
        url: urlle + 'storeProductManage/storeProducManagetList',
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
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += '<tr class="'+res.data[i].id+'" style="text-align: center">' +
            '<td>'+res.data[i].name+'</td><td>' +
            '<img class="order-info-img" src="'+res.data[i].mainpicHref+'" alt=""  onerror="this.src=\'../assets/img/default.jpg\'"></td>' +
            '<td>'+res.data[i].productCode+'</td><td>'+res.data[i].categoryName+'</td>' +
            '<td>'+res.data[i].price+'</td>' +
            '<td>'+res.data[i].buyprice+'</td>' +
            '<td>'+res.data[i].updateDate+'</td><td>'+res.data[i].colligate+'</td><td>' +
            '<button style="margin-right: 10px" onclick="edit('+res.data[i].id+')">编辑</button>' +
            '<button style="margin-right: 10px" onclick="delet('+res.data[i].id+')">删除</button>' +
            '<button onclick="location.href=\'../storeSuplierOrder/buyOrderBuyHtml?storePid='+res.data[i].id+'\'">采购</button>' +
            '</td></tr>';
    }
    $('.spinner').hide();
    $('#tbody').append(tr);
}

//删除
function delet(id){
    $('#myModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: urlle + 'storeProductManage/deleteProductManage',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                $('#myModal').modal('hide');
                list();
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
};
//编辑
function edit(id) {
    location.href = '../storeProductManage/editShowGoodsHtml?id='+id+'';
}
//我的云店
$('#myStore').click(function () {
    $.ajax({
        type: 'POST',
        url: '../storeProductManage/storeLocation',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
                window.top.open(res.data.url);
        },
        error: function (err) {
            alert(err.message);
        }
    });
});
