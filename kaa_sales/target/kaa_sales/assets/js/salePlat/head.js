var head =
    '<div class="content_header" id="header"><div>' +
    '    <img class="back-home" src="../assets/img/salePlat/logo1.jpg" alt="">\n' +
    '    <span class="search"><input class="head-search" type="text" placeholder="搜索商品名称/编号" id="search"><span class="glyphicon glyphicon-search"></span></span>\n' +
    '</div></div>'+
    '<div class="header">\n' +
    '    <div class="header_content">\n' +
    '        <div class="dl-title">\n' +
    '            <span>HI, 欢迎来到合一智造！ </span><a href="../salesHome/indexHtml" id="userName"> </a><a class="login" href="../salesHome/loginHtml">退出</a>\n' +
    '        </div>\n' +
    '        <div class="dl-log">\n' +
    '            <a href="../salesHome/homePageHtml">合一智造首页</a><a class="join" href="">门店入驻</a><a href="../salesHome/serviceHtml">联系客服</a><a id="showCode" href="#">微信平台</a><a href="../guideHome/listHtml">帮助中心</a>\n' +
    '        </div>\n' +
    '    </div>\n' +
    '</div>';

var head2 =
    '<div class="content_header" id="header"><div>' +
    '    <img class="back-home" src="../assets/img/salePlat/logo1.jpg" alt="">\n' +
    '    <span class="search"><input class="head-search" type="text" placeholder="搜索商品名称/编号" id="search"><span class="glyphicon glyphicon-search"></span></span>\n' +
    '</div></div>'+
    '<div class="header">\n' +
    '    <div class="header_content">\n' +
    '        <div class="dl-title">\n' +
    '            <span>HI, 欢迎来到合一智造！</span><a href="../salesHome/loginHtml" class="login">请登录</a><a href="../salesHome/registerHtml">注册</a>\n' +
    '        </div>\n' +
    '        <div class="dl-log">\n' +
    '            <a href="../salesHome/homePageHtml">合一智造首页</a><a href="../salesHome/loginHtml">门店入驻</a><a href="../salesHome/serviceHtml">联系客服</a><a id="showCode" href="#">微信平台</a><a href="../guideHome/listHtml">帮助中心</a>\n' +
    '        </div>\n' +
    '    </div>\n' +
    '</div>';
$('body').append('<div id="showCod" style="width: 220px;height: 140px;position: fixed;top: 35px;display: none;background-color: #fff;padding: 15px 10px"><img style="width: 90px;height: 90px;margin-right: 10px" src="../assets/img/salePlat/351513330186_.pic_hd.jpg">' +
    '<img src="../assets/img/salePlat/261513315098_.pic_hd.png" alt="" style="width: 94px;height: 85px"><div style="width: 100%;margin-top: 3px;color: #999"><span style="display: inline-block;width: 50%;padding-left: 11px">微信公众号</span>' +
    '<span style="display: inline-block;width: 50%;text-align: center">小程序</span></div></div>');
//分类
var token = sessionStorage.getItem('token');
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
        for(var i = 0; i < res.data.length; i++){
            // var li = '';
            // li += '<div class="maintip"><li value="'+res.data[i].id+'">'+res.data[i].name+'</li></div>'+
            //     '<div class="tips"></div>';
            // $('#firstCategory').append(li);
            for(var j = 0; j < res.data[i].categoryList.length; j++){
                // var p = '';
                // p += '<div value="'+res.data[i].categoryList[j].id+'">'+res.data[i].categoryList[j].name+'</div>'
                // $('.tips').eq(i).append(p);
                var a = '';
                a += '<a style="text-align: center" value="'+res.data[i].categoryList[j].id+'" href="#">'+res.data[i].categoryList[j].name+'</a>';
                $('#header .search').before(a);
            }
        }
        // if(!token){
        //     $('#header .search').after('<a id="box12" href="../salesHome/loginHtml">合一盒子</a>');
        // }else{
        //     $('#header .search').after('<a id="box12" href="../salesHome/boxJoinHtml?boxIds=">合一盒子</a>');
        // }
        $('#header .search').after('<a href="../salesHome/boxJoinHtml?boxIds=">会员服务</a>');
        $('#header .search').after('<a href="../salesHome/designHtml">设计师区</a>');
        //一级分类跳转
        $('#header a').click(function () {
            location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
        });
        //二级分类跳转
        // $('.tips div').click(function () {
        //     location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
        // });
        //标签选择事件
        // $(".maintip").each(function(index){   //遍历A部分，注意这里绑定事件用了index参数
        //     $(this).mouseover(function(){   //鼠标经过A时触发事件
        //         var obj=$(this).offset();   //获取被鼠标经过的A的偏移位置，offset()是个好东西，不懂的朋友得去了解下
        //         var yobj=obj.top-170+"px";     //后面要让B垂直偏移的距离
        //         $(this).css({"width":"50%","z-index":"9999","border-right":"none"});  //A改变样式，变为选中状态的效果
        //         $(".tips:eq("+index+")").css({"left":'50%',"top":yobj}).show();   //对应的（这里利用了索引）B改变样式并显示出来
        //     })
        //         .mouseout(function(){     //鼠标离开A时触发的事件
        //             $(".tips").hide();     //B隐藏
        //             $(this).css({"width":"50%","z-index":"1"})   //A变回原始样式
        //         })
        // });
        //
        // $(".tips").each(function(){  //遍历B
        //     $(this).mouseover(function(){  //鼠标经过B时触发事件
        //         $(this).prev(".maintip").css({"background-color":"rgb(69,69,69)","z-index":"9999","color": "#fff","border-left":"4px solid rgb(66,159,214)"})  //对应的A变为选中状态效果
        //         $(this).show();  //A不要隐藏了，解决因为上面写的鼠标离开A导致A隐藏
        //     })
        //         .mouseout(function(){  //鼠标离开B触发事件，其实就是让B隐藏，同时A变为原始状态
        //             $(this).hide();
        //             $(this).prev(".maintip").css({"border-left":"none","z-index":"1","background-color":"rgb(51,51,51)"});
        //         })
        // });

    },
    error: function (err) {
        // alert(err.message);
    }
});
if (!token) {
    $("#head").html(head2); //未登录
    $('.glyphicon-search').click(function () {
        var nameOrcode = $('#search').val();
        location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
    });
} else {
    // $('#header .search').after('<a href="../salesHome/boxJoinHtml?boxIds=">会员服务</a>');
    $("#head").html(head);
    $('.glyphicon-search').click(function () {
        var nameOrcode = $('#search').val();
        location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
    });
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
            if(res.stateCode == 100){
                $("#userName").html(res.data.name)
                if(res.data.userStatus == 0){
                    $('#head .dl-log .join').html('我的云店');
                    //获取前端展示地址
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
                            $("#head .dl-log .join").click(function () {
                                window.open(res.data.url);
                            });
                        },
                        error: function (err) {
                            alert(err.message);
                        }
                    });
                }else  if(res.data.userStatus == 1){
                    $('#head .dl-log .join').attr('href','../salesHome/joinHtml?type=1');
                }else  if(res.data.userStatus == 2){
                    $('#head .dl-log .join').attr('href','../salesHome/joinHtml?type=2');
                }else  if(res.data.userStatus == 3){
                    $('#head .dl-log .join').attr('href','../salesHome/defaultHtml');
                }
            }else{
                // alert(res.message);
            }
        },
        error: function (err) {
        }
    });
}

$('.back-home').click(function () {
    location.href='../salesHome/homePageHtml';
})

$('.back-home').hover(
    $('.back-home').css('cursor','pointer')
);
$('#showCode').mouseover(function () {
    var left = $('#showCode').offset().left - 60 + 'px';
    $('#showCod').css('left', left);
    $('#showCod').css('display', '');
    $('#showCod').css('z-index', '1000000');
});
$('#showCode').mouseout(function () {
    $('#showCod').css('display', 'none');
});
$('#head .dl-title > img').click(function () {
    location.href = '../salesHome/homePageHtml';
})


