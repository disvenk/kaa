//分页
$(".tcdPageCode").createPage({
    pageCount:10,//总共的页码
    current:3,//当前页
    backFn:function(p){//p是点击的页码
        $("#page").val(p);
    }
});
$('.col').click(function () {
    location.href = '../../../WEB-INF/html/showGoods/goodsDetail.html'
})