var url = window.location.href;
var id = url.split('=')[1];
showDetail();
//图片转化
function convertImgToBase64(url, callback, outputFormat){
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function(){
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img,0,0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}
//显示资料
function showDetail() {
    $.ajax({
        type: 'POST',
        url: '../banner/getBanner',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                //商品编号
                $('#name').val('' + res.data.name + '');
                //商品名称
                $('#description').val('' + res.data.description + '');
                //排序
                $('#sort').val('' + res.data.sort + '');
                var aaa = res.data.href
                convertImgToBase64(aaa, function (Base64Img) {
                    aaa = Base64Img;
                    var cc = '<div><img src="' + aaa + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
                    $('#img1 .img_container1').append(cc);
                    $('.delete_img').click(function () {
                        $(this).parent().remove();
                    })
                });
            }
        },
        error: function (err) {
            layer.alert(err)
        }
    });
}

$('#back').click(function () {
    location.href = '../banner/bannerManageHtml';
});
//本地上传图片
document.getElementById("upLoad1").addEventListener("change",function(e){
    var files =this.files;
    var img = new Image();
    var reader =new FileReader();
    reader.readAsDataURL(files[0]);
    reader.onload =function(e){
        var dx =(e.total/1024)/1024;
        if(dx>=2){
            layer.alert("文件大小大于2M");
            return;
        }
        img.src =this.result;
        img.style.width = '138px';
        img.style.height ="93px";
        $('#img1 .img_container1').html('');
        var image = '<div><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
        $('#img1 .img_container1').append(image);
        $('.delete_img').click(function () {
            $(this).parent().remove();
        })
    }
});
//提交编辑信息
$('#submit').click(function () {
    var name = $('#name').val();
    var description = $('#description').val();
    var sort = $('#sort').val();

    var data = {
        id:id,
        name: name,
        description: description,
        sort: sort,
        href: $('#img1 .img_container1 img').attr('src')
    };
    if(name == ''){
        layer.alert('banner名称为必填！', {icon: 0});
    } else if ($('#img1 .img_container1 div').length == 0) {
        layer.alert('请选择Banner图片！', {icon: 0});
    }  else if (sort == '') {
        layer.alert('请填入序号！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url:  '../banner/saveEditBanner',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').show();
                if(res.stateCode == 100){
                    layer.alert('保存成功！', {icon: 0});
                    location.href = '../banner/bannerManageHtml';
                }else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }


});