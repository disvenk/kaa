var url = window.location.href;
var id = url.split('=')[1];
var imgUrl;
var categoryName;
//搜索
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});
//分类
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
//         // for(var i = 0 ;i < res.data[0].categoryName.length; i++){
//         //     var firstCategory = '';
//         //     firstCategory += '<li id="'+res.data[0].categoryName[i].categoryId+'">'+res.data[0].categoryName[i].categoryName+'</li>';
//         //     $('.left_category').append(firstCategory);
//         // }
//         for(var i = 0 ;i < res.data[0].RelateCategoryName.length; i++){
//             var secCategory = '';
//             secCategory += '<span class="sec" id="'+res.data[0].RelateCategoryName[i].RelateCategoryId+'">'+res.data[0].RelateCategoryName[i].RelateCategoryName+'</span>';
//             $('#secCategory').append(secCategory);
//             var secCatego = '';
//             secCatego += '<a class="'+res.data[0].RelateCategoryName[i].RelateCategoryId+'">'+res.data[0].RelateCategoryName[i].RelateCategoryName+'</a>';
//             $('.content_header img').after(secCatego);
//         }
//         $('.content_header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('class')+'';
//         });
//     },
//     error: function (err) {
//         layer.alert(err.message);
//     }
// });

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
            li += '<li class="goods-item" id="'+res.data[i].id+'"><div class="goods-item-wrap" style="background-color: rgb(243,242,244);border: 2px solid #ffffff;padding: 8px">'+
                '<div class="goods-item-img"><img src="'+res.data[i].href+'" alt=""  onerror="this.src=\'../assets/img/default.jpg\'"></div>'+
                '<div class="goods-item-info clearfix">'+
                '<div class="goods-item-price">价格：<span>¥'+res.data[i].price+'</span><span style="float: right;color: rgb(164,168,173)"><span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span>'+res.data[i].views+'</span></div>'+
                '<div class="goods-item-desc">'+res.data[i].name+'</div></div></div></li>';
            $('.hot_container').append(li);
        }
        $('.hot_container .goods-item').click(function () {
            var goodsId = $(this).attr('id');
            location.href = '../salesHome/detailHtml?id='+goodsId+''
        })
    },
    error: function (err) {
        layer.alert(err.message);
    }
});
//详情
    $.ajax({
        type: 'POST',
        url: '../salesProduct/productDetail',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                imgUrl = res.data.href;
                categoryName = res.data.categoryName.name;
                if(res.data.vedioUrl == ''){
                    $('#play').css('display', 'none');
                }
                $('#video').css('display', 'none');
                $('#video').attr('src',res.data.vedioUrl);
                $('#imageMenu ul').html('');
                var imgSrc = '';
                $('#midimg').attr('src',res.data.imgList[0].href);
                for(var i = 0; i < res.data.imgList.length; i++){
                    var li = '';
                    li += '<li><img src="'+res.data.imgList[i].href+'" alt="合一智造"/></li>';
                    $('#imageMenu ul').append(li);
                }
                $('#imageMenu ul li').click(function () {
                    var src = $(this).children().attr('src');
                    $('#midimg').attr('src',src);
                    $('#video').css('display', 'none');
                })
                var price = 0.0; //商品单价
                $('#play').click(function () {
                    $('#video').css('display', 'block');
                    $('#video').css('position', 'absolute');
                    $('#video').css('z-index', '900');
                });
                $('#goodsName').html(res.data.name);
                $('#modalType').html(res.data.productCode);
                $('#downPrice').html(res.data.price);
                //颜色 尺寸
                $('#seletor').html('');
                $('#size').html('');
                for(var i = 0; i < res.data.colors.length; i++){
                    var colorContent = res.data.colors[i];
                        var colorA = '';
                        colorA += '<span class="spColor">'+colorContent+'</span>';
                        $('#seletor').append(colorA);
                }
                for(var i = 0; i < res.data.sizes.length; i++){
                    var sizeContent = res.data.sizes[i];
                        var sizeA = '';
                        sizeA += '<span class="spSize">'+sizeContent+'</span>';
                        $('#size').append(sizeA);

                };
                $('#beSales').html(res.data.sales);
                $('#beViews').html(res.data.views);

                //2017.12.11 单独取得图文详情
                // $('#description').html(res.data.description);

                //摸态窗表格
                for(var i = 0; i < res.data.productPrice.length; i++){
                    var tr = '';
                    tr += '<tr class="'+res.data.id+'"><td>'+res.data.productPrice[i].color+'</td><td>'+res.data.productPrice[i].size+'</td><td>'+res.data.productPrice[i].price+'</td>' +
                        '<td><span class="glyphicon glyphicon-plus"></span><input style="width: 66px" type="number" value="0" onkeyup="this.value=(this.value.match(/^[1-9]\\d*$/)||[\'\'])[0]"><span class="glyphicon glyphicon-minus"></span></td></tr>';
                    $('#myModal #tbody').append(tr);
                }
                $('#myModal #tbody .glyphicon-plus').click(function () {
                       var num = $(this).parent().find('input').val() - 0 + 1;
                       $(this).parent().find('input').val(num);
                    var totalNums = 0;
                    var totalPrice = 0;
                    for(var i=0; i < $('#myModal #tbody tr').length; i++){
                        totalPrice += ($('#myModal #tbody tr').eq(i).children('td').eq(2)[0].innerHTML - 0) * ($('#myModal #tbody tr').eq(i).children('td').eq(3).find('input').val() - 0);
                        totalNums += $('#myModal #tbody tr').eq(i).children('td').eq(3).find('input').val() - 0;
                    };
                    $('#nums').html(totalNums);
                    $('#totalPrice').html(totalPrice.toFixed(2) + '元');
                })
                $('#myModal #tbody .glyphicon-minus').click(function () {
                    var totalNums = 0;
                    var totalPrice = 0;
                    var num = $(this).parent().find('input').val() - 0;
                    if (num == 0) {
                        $(this).parent().find('input').val('0');
                    } else {
                        num = $(this).parent().find('input').val() - 0 - 1;
                        $(this).parent().find('input').val(num);
                    }
                    for(var i=0; i < $('#myModal #tbody tr').length; i++){
                        totalPrice += ($('#myModal #tbody tr').eq(i).children('td').eq(2)[0].innerHTML - 0) * ($('#myModal #tbody tr').eq(i).children('td').eq(3).find('input').val() - 0);
                        totalNums += $('#myModal #tbody tr').eq(i).children('td').eq(3).find('input').val() - 0;
                    };
                    $('#nums').html(totalNums);
                    $('#totalPrice').html(totalPrice.toFixed(2) + '元');
                })
            }else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });

    //2017.12.11 图文详情，单独显示，以减少页面的加载

$.ajax({
    type: 'POST',
    url: '../salesProduct/getProductDescription',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            $('#description').html(res.data.description);
        }else {
            alert(res.message);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

    $('#placeOrder').click(function () {
        if(sessionStorage.getItem('token') == '' || sessionStorage.getItem('token') == null || sessionStorage.getItem('token') == undefined){
            layer.alert('请先登录', {icon: 0});
            setTimeout(function () {
                location.href = '../salesHome/loginHtml'
            }, 1000)
        }else{
            $('#myModal').modal('show');
        }
    });
    $('#myModal #sure').click(function () {
        // location.href = '../salesHome/placeOrderHtml';
        if($('#nums').html() == 0){
            layer.alert('请选择商品', {icon: 0});
        }else{
            var data = [];
            for(var i = 0; i < $('#tbody tr').length; i++){
                var obj = {};
                if($('#tbody tr').eq(i).children('td').eq(3).find('input').val() - 0 > 0){
                    obj = {
                        href: imgUrl,
                        name: $('#goodsName').html(),
                        productCode: $('#modalType').html(),
                        nums: $('#tbody tr').eq(i).children('td').eq(3).find('input').val() - 0,
                        price: $('#tbody tr').eq(i).children('td').eq(2)[0].innerHTML - 0,
                        color: $('#tbody tr').eq(i).children('td').eq(0)[0].innerHTML,
                        size: $('#tbody tr').eq(i).children('td').eq(1)[0].innerHTML,
                        totalPrice: $('#totalPrice').html(),
                        totalNums: $('#nums').html(),
                        categoryName: categoryName,
                        pid: $('#tbody tr').eq(i).attr('class')
                    };
                    data.push(obj)
                }
            }
            $.ajax({
                type: 'POST',
                url: '../session/setData',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(data),
                success: function (res) {
                    location.href = '../salesHome/placeOrderHtml';
                },
                error: function (err) {
                    layer.alert(err.message);
                }
            });
        }
    })

