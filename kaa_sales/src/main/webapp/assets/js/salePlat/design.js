
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
//设计师专区信息
$.ajax({
    type: 'POST',
    url: '../designer/designerList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({pageNum: 1}),
    success: function (res) {
        if(res.data.length == 0 || res.data.length == null || res.data.length == undefined){
           $('#display').css('display', 'block');
        }else{
            $('#display').css('display', 'none');
            for(var i = 0; i < res.data.length; i++){
                var div = '';
                div += res.data[i].description;
                $('.main-content').append(div)
            }
        }
    },
    error: function (err) {
        layer.alert(err.message);
    }
});