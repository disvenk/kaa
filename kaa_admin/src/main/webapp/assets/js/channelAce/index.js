//分页
// $(".tcdPageCode").createPage({
//     pageCount: 10,//总共的页码
//     current: 3,//当前页
//     backFn: function (p) {//p是点击的页码
//         $("#page").val(p);
//     }
// });
//删
$('.delete').click(function(){
    var _this = $(this);
    _this.parent().parent().remove();
});
//编辑
$('.edit').click(function(){
    location.href = '../../../jsp/channelEdit.html';
});
var username= '';
var tel= '';
function get() {
    $('#tbody').html('');
    $.ajax({
        type : "GET",  //提交方式
        url : 'http://192.168.0.183:8080/applicant/lists?contact='+username+'&telephone='+tel+'',//路径
        success : function(res) {//返回数据根据结果进行相应的处理
            if ( res.status == 200 ) {
                if(res.data == null){
                    $('.table').css('display', 'none');
                    $('#null').css('display', 'block');
                }else {
                    $('.table').css('display', '');
                    $('#null').css('display', 'none');
                    var tr = '';
                    for(var i = 0; i < res.data.length; i++){
                        tr += '<tr><td>'+res.data[i].name+'</td><td>'+res.data[i].address+'</td>' +
                            '<td>'+res.data[i].contact+'</td><td>'+res.data[i].telephone+'</td>' +
                            '<td>'+res.data[i].type+'</td><td><button class="edit">编辑</button><button class="delete">删除</button></td></tr>'
                    }
                    $('#tbody').append(tr);
                }
            } else {
                alert('网络错误，请重试...');
            }
        }
    });
}
// get();
//搜索
$('#search').click(function () {
    username = $('#male').val();
    tel = $('#tel').val();
    get();
});
//重置
$('#refresh').click(function () {
    $('#male').val('');
    $('#tel').val('');
    username= '';
    tel= '';
    get();
})