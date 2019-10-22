$('#back').click(function () {
    location.href = '../banner/bannerManageHtml';
});
//本地上传图片
document.getElementById("upLoad1").addEventListener("change", function (e) {
    var files = this.files;
    var img = new Image();
    var reader = new FileReader();
    reader.readAsDataURL(files[0]);
    reader.onload = function (e) {
        var dx = (e.total / 1024) / 1024;
        if (dx >= 2) {
            layer.alert("文件大小大于2M");
            return;
        }
        img.src = this.result;
        img.style.width = '138px';
        img.style.height = "93px";
        $('#img1 .img_container1').html('');
        var image = '<div><img src="' + this.result + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
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
        name: name,
        description: description,
        sort: sort,
        href: $('#img1 .img_container1 img').attr('src')
    };

    if(name == ''){
        layer.alert('banner名称为必填！', {icon: 0});
    } else if ($('#img1 .img_container1 div').length == 0) {
        layer.alert('请选择Banner图片！', {icon: 0});
    } else if (sort == '') {
        layer.alert('请填入序号！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../banner/addBanner',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').show();
                if (res.stateCode == 100) {
                    layer.alert('新增成功！', {icon: 0});
                    location.href = '../banner/bannerManageHtml';
                } else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }


});