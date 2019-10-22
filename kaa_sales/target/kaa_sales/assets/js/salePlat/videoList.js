var url = window.location.href;
var id = url.split('=')[1];
$.ajax({
    type: 'POST',
    url: '../video/findVedioInfoList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:id}),
    success: function (res) {
        if(res.stateCode == 100){
            for (var i = 0; i < res.data.length; i++) {
                var Suplierscope = '';
                Suplierscope += '  <div class="video-list clearfix"><div class="video-list-left" onclick="detail('+res.data[i].id+')">'+
                    '<img class="video-list-img" src="'+res.data[i].pictureUrl+'" alt="">'+
                    '<img class="play" src="../assets/img/salePlat/play.png"></div><div class="video-list-right"><div class="list-right-topic">'+res.data[i].title+'</div>'+
                    '<div class="list-right-num">'+res.data[i].views+'</div><div class="list-right-text">'+res.data[i].shortDescription+'</div> </div></div>';
                $('.video-container').append(Suplierscope);
            }
        }else{
            layer.alert(res.message, {icon: 0});
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});
//详情
function detail(id) {
    location.href = '../video/videoDetailHtml?id='+id;
}

