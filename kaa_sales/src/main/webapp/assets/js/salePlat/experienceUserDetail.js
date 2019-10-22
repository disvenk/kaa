var url = window.location.href;
var id = url.split('=')[1];
$.ajax({
    type: 'POST',
    url: '../video/getSalesVedioInfo',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:id}),
    success: function (res) {
        if(res.stateCode == 100){
           $('.tys-info-head').attr('src', res.data.pictureUrl);
            $('.list-right-topic').html(res.data.title);
            $('.list-right-num').html(res.data.views);
            $('.list-right-text').html(res.data.shortDescription);
            $('.tys-detail-intro').html(res.data.description);
        }else{
            layer.alert(res.message, {icon: 0});
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});