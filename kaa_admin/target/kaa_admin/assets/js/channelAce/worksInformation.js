//删除
$('.delete').click(function(){
    var _this = $(this);
    _this.parent().parent().remove();
});
//编辑
$('.edit').click(function(){
    location.href = './worksInformationEdit.html';
});
  //分页
  $(".tcdPageCode").createPage({
    pageCount: 10,//总共的页码
    current: 3,//当前页
    backFn: function (p) {//p是点击的页码
        $("#page").val(p);
        search();
    }
});