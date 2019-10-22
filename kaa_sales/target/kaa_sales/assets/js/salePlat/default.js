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
//         layer.alert(err.message);
//     }
// });
// $('.glyphicon-search').click(function () {
//     var nameOrcode = $('#search').val();
//     location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
// });