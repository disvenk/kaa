window.sessionStorage.removeItem('token');
$('.login').click(function () {
    var data = {
        usercode: $('#userName').val(),
        password: $('#password').val(),
        loginType: 1
    };
    $.ajax({
        type: 'POST',
        url: '../account/login',
        cache: false,
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                sessionStorage.setItem("token",res.data.authToken);
                if(document.referrer.split('=')[1] == '' || document.referrer.split('=')[1] == undefined){
                   location.href = '../salesHome/homePageHtml';
                }else{
                   history.go('-1');
                }
                // location.href = '../storeSalesOrder/indexHtml';
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    })
});
function keyLogin(){
    if (event.keyCode==13)  //回车键的键值为13
        $('.login').click(); //调用登录按钮的登录事件
}
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
//         //一级分类跳转
//         $('#header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
//         });
//     },
//     error: function (err) {
//         alert(err.message);
//     }
// });
// $('.glyphicon-search').click(function () {
//     var nameOrcode = $('#search').val();
//     location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
// });