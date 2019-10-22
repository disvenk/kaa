var url = window.location.href;
var id = url.split('=')[1];
//视频
function detail(id) {
    $.ajax({
        type: 'POST',
        url: '../video/getSalesVedioInfo',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8'
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                $('.left-topic').html(res.data.title);
                $('#viewsNum').html(res.data.views);
                $('.left-intro').html(res.data.shortDescription);
                $('.left-detail').html(res.data.description);
                $('#my-video').attr('src', res.data.vedioUrl);
            }else{
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon:0});
        }
    });
}
detail(id);
//相关视频
function list(id) {
    $.ajax({
        type: 'POST',
        url: '../video/findRelatedVedioInfoList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8'
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                $.ajax({
                    type: 'POST',
                    url: '../video/updateSalesVedioViews',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8'
                    },
                    data: JSON.stringify({id: id}),
                    success: function (res) {
                        if(res.stateCode == 100){

                        }else{
                            layer.alert(res.message, {icon:0});
                        }
                    },
                    error: function (err) {
                        layer.alert(err.message, {icon:0});
                    }
                });
                $('#relateVideo').html('');
                for(var i = 0; i < res.data.length; i++){
                    var img = '';
                    img += '<div onclick="play('+res.data[i].id+')"><img style="width: 35px;height: 35px;position: absolute;left: 150px;top: 70px;" src="../assets/img/salePlat/play.png" alt="">' +
                        '<img src="'+res.data[i].pictureUrl+'"></div>';
                    $('#relateVideo').append(img);
                }
            }else{
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon:0});
        }
    });
}
list(id);
function play(id) {
    window.open('../video/videoDetailHtml?id='+id+'');
}