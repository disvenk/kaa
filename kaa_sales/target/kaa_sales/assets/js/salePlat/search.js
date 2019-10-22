var url = decodeURI(window.location.href);
var params = url.split('?')[1];
var id;
var type;
var nameOrCode;
var categoryId;
if(params.split('=')[0] == 'id'){
    id = params.split('=')[1];
    nameOrCode = '';
}else if(params.split('=')[0] == 'nameOrcode'){
    nameOrCode = params.split('=')[1];
    id = '';
    $('#search').val(nameOrCode)
}else{
    type = params.split('=')[1];
    nameOrCode = '';
    id = '';
}
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});
var data = {
    stratPrice: '',
    endPrice: '',
    sortType: 1,
    pageNum: 1,
    categoryIdList: id == '' ? [] : [{categoryId: id}],
    labelIdList: [],
    nameOrCode: nameOrCode
};
//标签
function findProductLabel(categoryId) {
    $.ajax({
        type: 'POST',
        url: '../salesProduct/findProductLabel',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: categoryId}),
        success: function (res) {
            $('.spinner').hide();
            $('#labelName').html("");
            for(var i = 0; i < res.data.length; i++){
                var label = '';
                label += '<span class="labelName" id="'+res.data[i].id+'">'+res.data[i].name+'</span>';
                $('#labelName').append(label);
            }
            for(var i = 0; i < $('#labelName span').length; i++){
                if(type == $('#labelName span').eq(i).attr('id')){
                    $('#labelName span').eq(i).addClass('selectActive');
                    $('#yourChoose').append('<div class="'+$('#labelName span').eq(i).attr('id')+'">'+$('#labelName span').eq(i).html()+'<i class="glyphicon glyphicon-remove"></i></div>');
                    choose();
                }
            }
            $('.labelName').click(function () {
                if($(this).hasClass('selectActive')){

                }else{
                    $(this).addClass('selectActive');
                    $('#yourChoose').append('<div class="'+$(this).attr('id')+'">'+$(this).html()+'<i class="glyphicon glyphicon-remove"></i></div>');
                    choose();
                }
                //删除
                $('.glyphicon-remove').click(function () {
                    var labelName = $('.labelName');
                    $(this).parent().remove();
                    choose();
                    for(var i = 0; i < labelName.length; i++){
                        if($(this).parent().attr('class') == labelName.eq(i).attr('id')){
                            labelName.eq(i).removeClass('selectActive');
                        }
                    }
                })
            });
            //删除
            $('.glyphicon-remove').click(function () {
                var labelName = $('.labelName');
                $(this).parent().remove();
                choose();
                for(var i = 0; i < labelName.length; i++){
                    if($(this).parent().attr('class') == labelName.eq(i).attr('id')){
                        labelName.eq(i).removeClass('selectActive');
                    }
                }
            })
        },
        error: function (err) {
            layer.alert(err.message)
        }
    });
}
findProductLabel(id);

// // 分类
// $.ajax({
//     type: 'POST',
//     url: '../storeProductManage/storeProductCategoryList',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         $('.spinner').hide();
//         // for(var i = 0 ;i < res.data[0].categoryName.length; i++){
//         //     var firstCategory = '';
//         //     firstCategory += '<li id="'+res.data[0].categoryName[i].categoryId+'">'+res.data[0].categoryName[i].categoryName+'</li>';
//         //     $('.left_category').append(firstCategory);
//         // }
//         for(var i = 0 ;i < res.data[0].RelateCategoryName.length; i++){
//             var secCategory = '';
//             secCategory += '<span class="sec" id="'+res.data[0].RelateCategoryName[i].RelateCategoryId+'">'+res.data[0].RelateCategoryName[i].RelateCategoryName+'</span>';
//             $('#secCategory').append(secCategory);
//             // var secCatego = '';
//             // secCatego += '<a class="'+res.data[0].RelateCategoryName[i].RelateCategoryId+'">'+res.data[0].RelateCategoryName[i].RelateCategoryName+'</a>';
//             // $('.content_header img').after(secCatego);
//         }
//         // $('.content_header a').click(function () {
//         //     location.href = '../salesHome/searchHtml?id='+$(this).attr('class')+'';
//         // })
//         //标签选择事件
$.ajax({
    type: 'POST',
    url: '../base/getPlaProductCategoryListAll',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        $('.spinner').hide();
        for(var i = 0; i < res.data.length; i++){
             var firstCategory = '';
             var heading = 'heading'+i;
             var collapse = 'collapse'+i;
             firstCategory += '<div class="panel" style="margin-bottom: 0;color: #999"><div class="panel-heading" style="background-color: rgb(248,248,248)" role="tab" id="'+heading+'">'+
                '<h4 class="panel-title"><a style="color: #999" role="button" data-toggle="collapse" data-parent="#accordion" href="#'+collapse+'" aria-expanded="true" aria-controls="'+collapse+'">'+res.data[i].name+'</a></h4></div>'+
            '<div id="'+collapse+'" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="'+heading+'"><div class="panel-body" style="padding: 0"></div></div></div>';
            $('.left_category').append(firstCategory);
             for(var j = 0; j < res.data[i].categoryList.length; j++){
                 var secCategory = '';
                 secCategory += '<li value="'+res.data[i].categoryList[j].id+'">'+res.data[i].categoryList[j].name+'</li>';
                 $('.left_category .panel-body').eq(i).append(secCategory);
             }
        }
        for(var j = 0; j < $('.left_category .panel-body li').length; j++){
            if($('.left_category .panel-body li').eq(j).attr('value') == id){
                $('.left_category .panel-body li').eq(j).addClass('selectActive');
                $('#yourChoose').append('<span class="'+id+'">'+$('.left_category .panel-body li').eq(j).html()+'<i class="glyphicon glyphicon-remove"></i></span>')
            }
        }
        //二级分类标签
        $('.left_category .panel-body li').click(function (){
            $('#yourChoose').html('');
            $('.left_category .panel-body li').removeClass('selectActive');
            $(this).addClass('selectActive');
            $('#yourChoose').html('');
            $('#yourChoose').append('<span class="'+$(this).attr('value')+'">'+$(this).html()+'<i class="glyphicon glyphicon-remove"></i></span>');
            choose();
            categoryId = $(this).attr('value');
            findProductLabel(categoryId);
            //删除
            $('.glyphicon-remove').click(function () {
               $('.left_category .panel-body .selectActive').removeClass('selectActive');
                $(this).parent().remove();
                $('#yourChoose').html('');
                findProductLabel();
                choose();
            });
        });
        //删除
        $('.glyphicon-remove').click(function () {
            $('.left_category .panel-body .selectActive').removeClass('selectActive');
            $(this).parent().remove();
            $('#yourChoose').html('');
                findProductLabel();
            choose();
        });
        // function click() {
        //     $('.left_category .panel-body li').click(function () {
        //         //删除
        //         $('.glyphicon-remove').click(function () {
        //             var sec = $('.left_category .panel-body li');
        //             $(this).parent().remove();
        //             choose();
        //             for(var i = 0; i < sec.length; i++){
        //                 if($(this).parent().attr('class') == sec.eq(0).attr('value')){
        //                     sec.eq(i).removeClass('selectActive');
        //                 }
        //             }
        //         })
        //     });
        // }
        // click();
    },
    error: function (err) {
        layer.alert(err.message);
    }
});
// 列表
function post() {
    $.ajax({
        type: 'POST',
        url: '../salesProduct/salesProductList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('#list').html('');
            $('.spinner').hide();
            load(res);
            if(res.data.length = 0){
                $('.tcdPageCode').html('暂无结果');
            }else{
                //分页
                $('.tcdPageCode').html('');
                $(".tcdPageCode").createPage({
                    pageCount:res.pageSum,//总共的页码
                    current:1,//当前页
                    backFn:function(p){//p是点击的页码
                        data.pageNum = $(".current").html();
                        list();
                    }
                });
            }
        },
        error: function (err) {
            layer.alert(err.message);
        }
    });
}
function list() {
    $.ajax({
        type: 'POST',
        url: '../salesProduct/salesProductList',
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
        },
        error: function (err) {
            layer.alert(err.message);
        }
    });
};
// post();

//加载数据
function load(res) {
    $('#list').html('');
    for(var i = 0 ;i < res.data.length; i++){
        var li = '';
        li += '<li class="goods-item col col-md-3" id="'+res.data[i].id+'" style="margin-top: 5px"><div class="goods-item-wrap" style="background-color: rgb(243,242,244);border: 2px solid #ffffff;padding: 8px">'+
            '<div class="goods-item-img"><img style="width: 100%;height: 219px;" src="'+res.data[i].href+'" alt=""  onerror="this.src=\'../assets/img/default.jpg\'"></div>'+
            '<div class="goods-item-info clearfix">'+
            '<div class="goods-item-price">价格：<span>¥'+res.data[i].price+'</span><span style="float: right;color: rgb(164,168,173)"><span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span>'+res.data[i].views+'</span></div>'+
            '<div class="goods-item-desc">'+res.data[i].name+'</div></div></div></li>';
        $('#list').append(li);
        if(res.data[i].platProductIdStatus == 0){
            $('.addShop').eq(i).css('display', 'block');
            $('.benAdd').eq(i).css('display', 'none');
        }else {
            $('.benAdd').eq(i).css('display', 'block');
            $('.addShop').eq(i).css('display', 'none');
        }
    }
    $('#list li').click(function () {
        if(sessionStorage.getItem('token') == '' || sessionStorage.getItem('token') == undefined || sessionStorage.getItem('token') == null){
            layer.alert('请先登录', {icon: 0});
            setTimeout(function () {
                location.href = '../salesHome/loginHtml'
            }, 1000)
        }else{
            var goodsId = $(this).attr('id');
            location.href = '../salesHome/detailHtml?id='+goodsId+''
        }
    });
    $('#sure').click(function () {
        $('#myModal').modal('hide');
        window.location.reload();
    })
}

//搜索
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    data.nameOrcode = nameOrcode;
    post();
});
$('#synthesise').click(function () {
    $(this).parent().children().removeClass('selectSort');
    $(this).addClass('selectSort');
    data.sortType = 1;
    post();
});
$('#new').click(function () {
    $(this).parent().children().removeClass('selectSort');
    $(this).addClass('selectSort');
    data.sortType = 3;
    post();
});
$('#salesNum').click(function () {
    $(this).parent().children().removeClass('selectSort');
    $(this).addClass('selectSort');
    data.sortType = 2;
    post();
});
$('#priceSort').click(function () {
    $(this).parent().children().removeClass('selectSort');
    $(this).addClass('selectSort');
    if($('#priceSort .glyphicon-arrow-down').css('display') == 'none'){
        $('#priceSort .glyphicon-arrow-down').css('display', 'inline-block');
        $('#priceSort .glyphicon-arrow-up').css('display', 'none')
        data.sortType = 4;
    }else if($('#priceSort .glyphicon-arrow-up').css('display') == 'none'){
        $('#priceSort .glyphicon-arrow-down').css('display', 'none');
        $('#priceSort .glyphicon-arrow-up').css('display', 'inline-block')
        data.sortType = 5;
    }else{
        $('#priceSort .glyphicon-arrow-down').css('display', 'none')
        data.sortType = 5;
    }
    post();
});
$('#button').click(function () {
    var startPrice = $('#startPrice').val();
    var endPrice = $('#endPrice').val();
    data.startPrice = startPrice;
    data.endPrice = endPrice;
    post();
});
function choose() {
    var cate = $('#yourChoose span');
    data.categoryIdList = [];
    data.labelIdList = [];
    for(var i = 0; i < cate.length; i++){
        data.categoryIdList[i] = {
            categoryId: ''
        };
        data.categoryIdList[i].categoryId = cate.eq(i).attr('class');
    }
    var label = $('#yourChoose div');
    for(var i = 0; i < label.length; i++){
        data.labelIdList[i] = {
            labelId: ''
        };
        data.labelIdList[i].labelId = label.eq(i).attr('class');
    }
    post();
}
//最热款式
$.ajax({
    type: 'POST',
    url: '../salesProduct/productTideList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0 ;i < res.data.length; i++){
            var li = '';
            li += '<li class="goods-item" id="'+res.data[i].id+'" style="margin-top: 5px"><div class="goods-item-wrap" style="background-color: rgb(243,242,244);border: 2px solid #ffffff;padding: 8px">'+
                '<div class="goods-item-img"><img src="'+res.data[i].href+'" alt=""  onerror="this.src=\'../assets/img/default.jpg\'"></div>'+
                '<div class="goods-item-info clearfix">'+
                '<div class="goods-item-price"><span>¥'+res.data[i].price+'</span><span style="float: right;color: rgb(164,168,173)"><span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span>'+res.data[i].views+'</span></div>'+
                '<div class="goods-item-desc">'+res.data[i].name+'</div></div></div></li>';
            $('.hot_container').append(li);
        }
        $('.hot_container .goods-item').click(function () {
            if(sessionStorage.getItem('token') == '' || sessionStorage.getItem('token') == undefined || sessionStorage.getItem('token') == null){
                layer.alert('请先登录', {icon: 0});
                setTimeout(function () {
                    location.href = '../salesHome/loginHtml'
                }, 1000)
            }else{
                var goodsId = $(this).attr('id');
                location.href = '../salesHome/detailHtml?id='+goodsId+''
            }
        })
    },
    error: function (err) {
        layer.alert(err.message);
    }
});
