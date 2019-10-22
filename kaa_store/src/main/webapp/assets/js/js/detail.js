var t = n = 0, count;
$(document).ready(function(){
    count=$("#banner_list a").length;
    $("#banner_list a:not(:first-child)").hide();
    $("#banner li").click(function() {
        var i = $(this).text() - 1;//获取Li元素内的值，即1，2，3，4
        n = i;
        if (i >= count) return;
        $("#banner_info").html($("#banner_list a").eq(i).find("img").attr('alt'));
        $("#banner_info").unbind().click(function(){window.open($("#banner_list a").eq(i).attr('href'), "_blank")});
        $("#banner_list a").filter(":visible").fadeOut(500).parent().children().eq(i).fadeIn(1000);
        document.getElementById("banner").style.background="";
        $(this).toggleClass("on");
        $(this).siblings().removeAttr("class");
    });

    // 解决 ie6 select框 问题
    $.fn.decorateIframe = function(options) {
        if ($.browser.msie && $.browser.version < 7) {
            var opts = $.extend({}, $.fn.decorateIframe.defaults, options);
            $(this).each(function() {
                var $myThis = $(this);
                //创建一个IFRAME
                var divIframe = $("<iframe />");
                divIframe.attr("id", opts.iframeId);
                divIframe.css("position", "absolute");
                divIframe.css("display", "none");
                divIframe.css("display", "block");
                divIframe.css("z-index", opts.iframeZIndex);
                divIframe.css("border");
                divIframe.css("top", "0");
                divIframe.css("left", "0");
                if (opts.width == 0) {
                    divIframe.css("width", $myThis.width() + parseInt($myThis.css("padding")) * 2 + "px");
                }
                if (opts.height == 0) {
                    divIframe.css("height", $myThis.height() + parseInt($myThis.css("padding")) * 2 + "px");
                }
                divIframe.css("filter", "mask(color=#fff)");
                $myThis.append(divIframe);
            });
        }
    }
    $.fn.decorateIframe.defaults = {
        iframeId: "decorateIframe1",
        iframeZIndex: -1,
        width: 0,
        height: 0
    }
    //放大镜视窗
    $("#bigView").decorateIframe();

    //点击到中图
    var midChangeHandler = null;

    $("#imageMenu li img").bind("click", function(){
        if ($(this).attr("id") != "onlickImg") {
            midChange($(this).attr("src").replace("small", "mid"));
            $("#imageMenu li").removeAttr("id");
            $(this).parent().attr("id", "onlickImg");
        }
        $('#video').css('display', 'none');
    }).bind("mouseover", function(){
        if ($(this).attr("id") != "onlickImg") {
            window.clearTimeout(midChangeHandler);
            midChange($(this).attr("src").replace("small", "mid"));
            $(this).css({ "border": "1px solid #990000" });
        }
    }).bind("mouseout", function(){
        if($(this).attr("id") != "onlickImg"){
            $(this).removeAttr("style");
            midChangeHandler = window.setTimeout(function(){
                midChange($("#onlickImg img").attr("src").replace("small", "mid"));
            }, 1000);
        }
    });

    function midChange(src) {
        $("#midimg").attr("src", src).load(function() {
            changeViewImg();
        });
    }

    //大视窗看图
    function mouseover(e) {
        if ($("#winSelector").css("display") == "none") {
            $("#winSelector,#bigView").show();
        }

        $("#winSelector").css(fixedPosition(e));
        e.stopPropagation();
    }


    function mouseOut(e) {
        if ($("#winSelector").css("display") != "none") {
            $("#winSelector,#bigView").hide();
        }
        e.stopPropagation();
    }


    $("#midimg").mouseover(mouseover); //中图事件
    $("#midimg,#winSelector").mousemove(mouseover).mouseout(mouseOut); //选择器事件

    var $divWidth = $("#winSelector").width(); //选择器宽度
    var $divHeight = $("#winSelector").height(); //选择器高度
    var $imgWidth = $("#midimg").width(); //中图宽度
    var $imgHeight = $("#midimg").height(); //中图高度
    var $viewImgWidth = $viewImgHeight = $height = null; //IE加载后才能得到 大图宽度 大图高度 大图视窗高度

    function changeViewImg() {
        $("#bigView img").attr("src", $("#midimg").attr("src").replace("mid", "big"));
    }

    changeViewImg();

    $("#bigView").scrollLeft(0).scrollTop(0);
    function fixedPosition(e) {
        if (e == null) {
            return;
        }
        var $imgLeft = $("#midimg").offset().left; //中图左边距
        var $imgTop = $("#midimg").offset().top; //中图上边距
        X = e.pageX - $imgLeft - $divWidth / 2; //selector顶点坐标 X
        Y = e.pageY - $imgTop - $divHeight / 2; //selector顶点坐标 Y
        X = X < 0 ? 0 : X;
        Y = Y < 0 ? 0 : Y;
        X = X + $divWidth > $imgWidth ? $imgWidth - $divWidth : X;
        Y = Y + $divHeight > $imgHeight ? $imgHeight - $divHeight : Y;

        if ($viewImgWidth == null) {
            $viewImgWidth = $("#bigView img").outerWidth();
            $viewImgHeight = $("#bigView img").height();
            if ($viewImgWidth < 200 || $viewImgHeight < 200) {
                $viewImgWidth = $viewImgHeight = 800;
            }
            $height = $divHeight * $viewImgHeight / $imgHeight;
            $("#bigView").width($divWidth * $viewImgWidth / $imgWidth);
            $("#bigView").height($height);
        }

        var scrollX = X * $viewImgWidth / $imgWidth;
        var scrollY = Y * $viewImgHeight / $imgHeight;
        $("#bigView img").css({ "left": scrollX * -1, "top": scrollY * -1 });
        $("#bigView").css({ "top": 0, "left": $(".preview").offset().left + $(".preview").width() + 15 });

        return { left: X, top: Y };
    }


})

function showAuto()

{

    n = n >=(count - 1) ? 0 : ++n;

    $("#banner li").eq(n).trigger('click');

}
$('#buyCar').click(function () {
    location.href = 'shoppingTrolley.html';
});
//商品数量加减
$('#price .glyphicon-plus-sign').click(function () {
    var num = $(this).siblings('input').val() - 0 + 1;
    $(this).siblings('input').val(num);
});
$('#price .glyphicon-minus-sign').click(function () {
    var num = $(this).siblings('input').val() - 0;
    if(num == 1){
        $(this).siblings('input').val('1');
    }else {
        num = $(this).siblings('input').val() - 0 - 1;
        $(this).siblings('input').val(num);
    }
});
function number() {
    if($('#price input').val()- 0 == ''){
        $('#price input').val('1')
    }
}
$('#play').click(function () {
    $('#video').css('display', 'block');
    $('#video').css('position', 'absolute');
    $('#video').css('z-index', '900');
});
//下单
$('#xiadan').click(function () {
   location.href = 'xiadan.html' ;
});
// 选择颜色
$('#seletor span').click(function () {
    $(this).addClass('selectedColor');
    $(this).siblings().removeClass('selectedColor');
});
// 选择尺码
$('#size span').click(function () {
    $(this).addClass('selectedSize');
    $(this).siblings().removeClass('selectedSize');
})