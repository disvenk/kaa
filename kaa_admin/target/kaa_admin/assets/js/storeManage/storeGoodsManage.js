var urlle = sessionStorage.getItem('urllen');
var pageNum=1;
var data = {
    productName: "",
    categoryId:"",
    storeId: "",
    pageNum: pageNum
};

//初始化加载所有列表数据
initData();
//初始化一级分类
initCategory();
//初始化门店
initStore();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../store/findStoreProductList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
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

function queryData() {
    $.ajax({
        type: 'POST',
        url: '../store/findStoreProductList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
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

    //绑定产生表格数据
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += ' <tr class="'+res.data[i].id+'">' +
            // '<td>'+'<input type="checkbox" id="checkid"> '+ '</td>' +
            '<td>'+res.data[i].productCode+'</td>' +
            '<td>'+res.data[i].productName+'</td>' +
            '<td><img style="width: 65px;height:65px;" src="'+res.data[i].productPicture+'" alt=""></td>' +
            '<td>'+res.data[i].pno+'</td>' +
            '<td>'+res.data[i].productCategory+'</td>' +
            '<td>'+res.data[i].salesPrice+'</td>' +
            '<td>'+res.data[i].views+'</td>' +
            '<td>'+res.data[i].updateDate+'</td>' +
            '<td>'+res.data[i].storeName+'</td>' +
            '<td>'+res.data[i].sort+'</td>' +
            '<td>'+
            '<div class="view setting-btn">查看</div>' +
            '</td>' +
            '</tr>';
    }
    $('#tbody').append(tr);

    //查看功能绑定
    //编辑
    $('.view').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('class');
        location.href = '../store/storeGoodsManageDetailHtml?id='+id+''
    });

}

//初始化分类资料
function initCategory() {
    $.ajax({
        type: 'POST',
        url:  '../base/getPlaProductCategoryLevelOneList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $('#category').append('<option id=""></option>')
            for(var i = 0 ;i < res.data.length; i++){
                var options = '';
                options += '<option id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                $('#category').append(options);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//初始化门店资料
function initStore() {
    $.ajax({
        type: 'POST',
        url:  '../store/getStoStoreInfoList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $('#store').append('<option id=""></option>')
            for(var i = 0 ;i < res.data.length; i++){
                var options = '';
                options += '<option id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                $('#store').append(options);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}
//搜索
$('#btnQuery').click(function () {
    $('.spinner').show();
    var productName = $('#productName').val();
    var categoryId = $('#category').find("option:checked").attr("id");
    var storeId = $('#store').find("option:checked").attr("id");
    data = {
        productName: productName,
        categoryId: categoryId,
        storeId: storeId,
        pageNum: 1
    };
    initData();
});

//重置
$('#resert').click(function () {
    $('#productName').val('');
    $('#category').val('');
    $('#store').val('');
    data = {
        productName: '',
        categoryId: '',
        storeId: '',
        pageNum: 1
    };
    window.location.reload();
})

// //全选框的全选功能
// $("#checkAll").click(function() {
//     var rows = $("#sample-table-2").find('input');
//     for (var i = 0; i < rows.length; i++) {
//         if (rows[i].type == "checkbox") {
//             var e = rows[i];
//             e.checked = this.checked;
//         }
//     }
// });

