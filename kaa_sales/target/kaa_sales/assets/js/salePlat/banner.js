var url = window.location.href;
var id = url.split('=')[1];
var pageNum = 1;
//分类
// $.ajax({
//     type: 'POST',
//     url: '../base/getPlaProductCategoryListAll',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         for(var i = 0; i < res.data.length; i++){
//             for(var j = 0; j < res.data[i].categoryList.length; j++){
//                 var a = '';
//                 a += '<a value="'+res.data[i].categoryList[j].id+'" href="#">'+res.data[i].categoryList[j].name+'</a>';
//                 $('#header img').after(a);
//             }
//         }
//         //二级分类跳转
//         $('#header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
//         });
//     },
//     error: function (err) {
//         // alert(err.message);
//     }
// });
function latest() {
    $.ajax({
        type: 'POST',
        url: '../salesProduct/salesBannerProductList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({bannerId: id, pageNum: pageNum}),
        success: function (res) {
            if (res.stateCode == 100) {
                //分页
                if(res.data.productList.length < 1){
                    $('.tcdPageCode').css('display', 'none');
                    $('#bannerImg').attr('src', res.data.picaddress);
                    $('.spinner').hide();
                    $('#upNew_content').append('<div style="margin: 100px auto;font-size: 18px;color: #999">暂无商品</div>')
                }else {
                    load(res);
                    $('.tcdPageCode').html('');
                    $('.tcdPageCode').css('display', 'block');
                    $(".tcdPageCode").createPage({
                        pageCount:res.pageSum,//总共的页码
                        current:1,//当前页
                        backFn:function(p){//p是点击的页码
                            pageNum = $(".current").html();
                            ajax();
                        }
                    });
                }
            } else {
                // alert(res.message);
            }
        },
        error: function (err) {
            // alert(err.message)
        }
    })
}
function ajax() {
    $('#upNew_content').html('');
    $('.spinner').show();
    $.ajax({
        type: 'POST',
        url: '../salesProduct/salesBannerProductList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({bannerId: id, pageNum: pageNum}),
        success: function (res) {
            if (res.stateCode == 100) {
                load(res);
            } else {
                $('.spinner').hide();
                alert(res.message);
            }
        },
        error: function (err) {
            $('.spinner').hide();
            alert(err.message)
        }
    })
}
latest();

//加载数据
function load(res) {
    $('.spinner').hide();
    $('#bannerImg').attr('src', res.data.picaddress);
    if(res.data.productList.length == 0){

    }else{
        for(var i = 0; i < res.data.productList.length; i++){
            var div = '';
            div += '     <div value="'+res.data.productList[i].id+'" class="col col-md-3 col-sm-3 col-lg-3 col-xs-3">' +
                '       <div class="thumbnail">' +
                '                    <img onerror="this.src=\'../assets/img/default.jpg\'" src="'+res.data.productList[i].href+'" alt="...">' +
                '                    <div class="caption row">' +
                '                        <div>¥<span style="margin-left: 4px">'+res.data.productList[i].price+'</span></div>' +
                '                        <div><span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span> '+res.data.productList[i].views+'</div>' +
                '                    </div>' +
                '                    <div class="introduce">'+res.data.productList[i].name+'</div></div></div>';
            $('#upNew_content').append(div);
        }
    }
    $('.col').click(function () {
        if(sessionStorage.getItem('token') == '' || sessionStorage.getItem('token') == undefined || sessionStorage.getItem('token') == null){
            layer.alert('请先登录', {icon: 0});
            setTimeout(function () {
                location.href = '../salesHome/loginHtml'
            }, 1000)
        }else{
            location.href = '../salesHome/detailHtml?id='+$(this).attr('value')+'';
        }
    });
}

$('.upNew .upNew_header span').eq(0).addClass('choose_active');
$('.upNew .upNew_header span').click(function () {
    $(this).siblings().removeClass('choose_active');
    $(this).addClass('choose_active');
    type = $(this).attr('value');
    latest();
});
$('.col').click(function () {
    if(sessionStorage.getItem('token') == '' || sessionStorage.getItem('token') == undefined || sessionStorage.getItem('token') == null){
        layer.alert('请先登录', {icon: 0});
        setTimeout(function () {
            location.href = '../salesHome/loginHtml'
        }, 1000)
    }else{
        location.href = '../salesHome/detailHtml?id='+$(this).attr('value')+'';
    }
});
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});