$.ajax({
    type: 'POST',
    url: '../video/findVedioInfoList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id:3}),
    success: function (res) {
        if(res.stateCode == 100){
            for (var i = 0; i < res.data.length; i++) {
                var Suplierscope = '';
                Suplierscope += '  <div class="video-list clearfix"><div class="video-list-left">'+
                    '<img class="video-list-img" src="'+res.data[i].pictureUrl+'" alt="" onclick="detail('+res.data[i].id+')">'+
                    '</div><div class="video-list-right"><div class="list-right-topic">'+res.data[i].title+'</div>'+
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
function detail(id) {
    location.href='../video/experienceUserDetailHtml?id='+id;
}
//表单提交
$('.tys-submit').click(function () {
    if($('#name').val() == ''){
        layer.alert('请输入姓名', {icon:0});
    }else if(!(/^[1][3,4,5,7,8][0-9]{9}$/.test($('#mobile').val()))){
        layer.alert('电话不正确', {icon:0});
    }else{
        var index = layer.load(1, {shade: [0.6,'#000']});
        var data = {
            name: $('#name').val(),
            age: $('#age').val(),
            mobile: $('#mobile').val()
        };
        $.ajax({
            type: 'POST',
            url: '../video/AddSalesTeacher',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                  layer.close(index);
                  layer.alert('感谢您的参与！', {icon:1});
                    $('#name').val('');
                    $('#age').val('');
                    $('#mobile').val('');
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        });
    }
});