//加载视频

$.ajax({
    type: 'POST',
    url: '../video/findHomePageVedioInfoList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:1}),
    success: function (res) {
        if(res.data.length < 4){
            $('#videoMore').css('display', 'none');
        }else{
            $('#videoMore').css('display', '');
        }
        for (var i = 0; i < res.data.length; i++) {
            var index = i+1;
            var Suplierscope = '';
            Suplierscope += '<li onclick="videoDetail('+res.data[i].id+')" class="video-list-item video">'+res.data[i].title+'</li>';
            $('#video1').append(Suplierscope);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

$.ajax({
    type: 'POST',
    url: '../video/findHomePageVedioInfoList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:2}),
    success: function (res) {
        if(res.data.length < 4){
            $('#videoMore1').css('display', 'none');
        }else{
            $('#videoMore1').css('display', '');
        }
        for (var i = 0; i < res.data.length; i++) {
            var Suplierscope = '';
            Suplierscope += '<li onclick="videoDetail('+res.data[i].id+')" class="video-list-item video">'+res.data[i].title+'</li>';
            $('#video2').append(Suplierscope);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

$.ajax({
    type: 'POST',
    url: '../video/findHomePageVedioInfoList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:3}),
    success: function (res) {
        if(res.data.length < 4){
            $('#experienceUser').css('display', 'none');
        }else{
            $('#experienceUser').css('display', '');
        }
        for (var i = 0; i < res.data.length; i++) {
            var index = i+1;
            var Suplierscope = '';
            Suplierscope += '<li onclick="tys('+res.data[i].id+')" class="video-list-item video">'+res.data[i].title+'</li>';
            $('#video3').append(Suplierscope);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

function videoDetail(id) {
    location.href='../video/videoDetailHtml?id='+id;
}
//标签
$.ajax({
    type: 'POST',
    url: '../base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({parameterType: 1}),
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var label = '';
            label += '<span class="labelName" onclick="labelName('+res.data[i].id+')">'+res.data[i].name+'</span>';
            $('#firstCategory').append(label);
        }
    },
    error: function (err) {
        layer.alert(err.message)
    }
});
function labelName(id) {
    location.href = '../salesHome/searchHtml?type='+id+'';
}
//轮播
$.ajax({
    type: 'POST',
    url: '../salesProduct/salesBannerList',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100) {
            for(var i = 0; i < res.data.length; i++){
                var div = '';
                div += '<div style="cursor:pointer;" value="'+res.data[i].id+'" class="item"><img src="'+res.data[i].picaddress+'" alt="..."></div>';
                $('.carousel-inner').append(div);
                var ol = '';
                ol += '<li data-target="#carousel-example-generic" data-slide-to="'+i+'"></li>';
                $('.carousel-indicators').append(ol);
            }
            $('.carousel-inner .item').eq(0).addClass('active');
            $('.carousel-indicators li').eq(0).addClass('active');
            //轮播点击事件
            $('.carousel-inner .item').click(function () {
                location.href = '../salesHome/bannerHtml?id='+$(this).attr('value')+'';
            })

        } else {
            // alert(res.message);
        }
    },
    error: function (err) {
        // alert(err.message)
    }
})
//新品推荐
var type = 0;
function latest() {
    $.ajax({
        type: 'POST',
        url: '../salesProduct/productNearList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type: type}),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#upNew_content').html('');
                for(var i = 0; i < res.data.length; i++){
                    var div = '';
                    div += '     <div style="padding-left: 6px;padding-right: 6px;" value="'+res.data[i].id+'" class="col col-md-3 col-sm-3 col-lg-3 col-xs-3">' +
                        '       <div class="thumbnail">' +
                        '                    <img src="'+res.data[i].href+'" alt="..." onerror="this.src=\'../assets/img/default.jpg\'">' +
                        '                    <div class="caption row">' +
                        '                        <div>¥<span style="margin-left: 4px">'+res.data[i].price+'</span></div>' +
                        '                        <div><span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span> '+res.data[i].views+'</div>' +
                        '                    </div>' +
                        '                    <div class="introduce">'+res.data[i].name+'</div></div></div>';
                    $('#upNew_content').append(div);
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
            } else {
                // alert(res.message);
            }
        },
        error: function (err) {
            // alert(err.message)
        }
    })
}
latest();
$('.upNew .upNew_header span').eq(0).addClass('choose_active');
$('.upNew .upNew_header span').click(function () {
    $(this).siblings().removeClass('choose_active');
    $(this).addClass('choose_active');
    type = $(this).attr('value');
    latest();
});
$.ajax({
    type: 'POST',
    url: '../salesProduct/salesHeadcategoryList',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({type: type}),
    success: function (res) {
        if (res.stateCode == 100) {
            for(var i = 0; i < res.data.length; i++){
                var div = '';
                div += ' <div class="content_1"> <div style="padding: 2px 0"> <div class="upNew"><div class="upNew_header">' +
                    '<span style="color: rgb(66,159,214);font-weight: 700;margin-left: 0;font-size: 14px;position: relative;top: 2px;">'+res.data[i].name+'</span>'+'<img style="width: 10px;height: 10px;vertical-align: middle;margin-left: 4px;" src="../assets/img/salePlat/gang.png" alt="">'+
                    '<span style="float: right;" class="seeMore">更多</span>'+
                    '</div> <div class="upNew_content row proList"></div></div></div></div>';
                $('.content1').append(div);
                for(j = 0; j < res.data[i].productList.length; j++){
                    var con = '';
                    con += '<div style="padding-left: 6px;padding-right: 6px;" class="col col-md-3 col-sm-3 col-lg-3 col-xs-3" value="'+res.data[i].productList[j].id+'"> <div class="thumbnail">' +
                        '<img style="width: 100%" src="'+res.data[i].productList[j].href+'" alt="..." onerror="this.src=\'../assets/img/default.jpg\'"> <div class="caption row"> <div>¥<span style="margin-left: 4px">'+res.data[i].productList[j].price+'</span></div><div>' +
                        '<span class="glyphicon glyphicon-eye-open" style="margin-right: 4px"></span>'+res.data[i].productList[j].views+'</div>' +
                        '</div> <div class="introduce">'+res.data[i].productList[j].name+'</div></div></div>';
                    $('.content_1 .proList').eq(i).append(con);
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
            $('.seeMore').click(function () {
                location.href = '../salesHome/searchHtml?id=';
            })
        } else {
            // alert(res.message);
        }
    },
    error: function (err) {
        // alert(err.message)
    }
});
$('.col').click(function () {
    location.href = '../salesHome/detailHtml?id='+$(this).attr('value')+'';
});
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});
function payBefore() {
    location.href = '../salesHome/indexHtml#menu/second-menu?type=0';
}
function sendBefore() {
    location.href = '../salesHome/indexHtml#menu/second-menu?type=1';
}
function receiveBefore() {
    location.href = '../salesHome/indexHtml#menu/second-menu?type=4';
}

// 轮播通知点击跳转

function cmsContent(id){
    location.href = '../salesHome/noticeInfoHtml?='+id;
}

$('.box-entrance img').click(function () {
    location.href = '../salesHome/boxHtml';
});

// 视频列表更多按钮
// 主播推荐和新娘课堂
$('#videoMore').click(function () {
    location.href='../video/videoListHtml?type=1';
});

$('#videoMore1').click(function () {
    location.href='../video/videoListHtml?type=2';
});
$('.video').click(function () {
    location.href='../video/videoDetailHtml'
});
$('#vd1').click(function () {
    location.href='../video/videoListHtml?type=1';
});
$('#vd2').click(function () {
    location.href='../video/videoListHtml?type=2';
});
$('#vd3').click(function () {
    location.href='../video/experienceUserHtml?type=3'
});
// 体验师
$('#experienceUser').click(function () {
    location.href='../video/experienceUserHtml?type=3'
});
function tys(id) {
    location.href='../video/experienceUserDetailHtml?id='+id;
}
