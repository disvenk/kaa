var urlle = sessionStorage.getItem('urllen');
var url = window.location.href;
var id = url.split("=")[1];
var ue = UE.getEditor('ueditor',{
    initialFrameWidth: '100%',
    initialStyle: 'p{line-height:0;margin:0}'
});

//图片转化
function convertImgToBase64(url, callback, outputFormat) {
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}

var imgs = [];
//本地上传图片
document.getElementById("upLoad").addEventListener("change",function(e){
    var log = $('#img .img_container > div').length;
    if(log > 1){
        alert('最多上传1张图片');
    }else{
        var files = document.getElementById("upLoad").files;
        if(log + files.length > 1){
            alert('最多上传1张图片');
        }else{
            for(var i= 0; i < files.length; i++){
                var img = new Image();
                var reader =new FileReader();
                reader.readAsDataURL(files[i]);
                reader.onload =function(e){
                    var dx =(e.total/1024)/1024;
                    if(dx>=2){
                        alert("文件大小大于2M");
                        return;
                    }
                    img.src =this.result;
                    img.style.width = '138px';
                    img.style.height ="93px";
                    var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $.ajax({
                        type: 'POST',
                        url: urlle + 'uploadFile/saveUploadFile',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({base64: this.result}),
                        success: function (res) {
                            if (res.stateCode == 100) {
                                $('#img .img_container').append(image);
                                imgs.push({href: res.data.key});
                                $("input[type='file']").val('');
                            } else {
                                alert(res.message);
                            }
                        },
                        error: function (err) {
                            alert(err.message)
                        }
                    })
                }
            }
        }
    }
});

function deleteImg(i) {
    $.ajax({
        type: 'POST',
        url: urlle + 'uploadFile/removeUploadFile',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: imgs[i.parent().index()].href}),
        success: function (res) {
            if (res.stateCode == 100) {

            } else {
                // alert(res.message);
            }
        },
        error: function (err) {
            // alert(err.message)
        }
    })
    imgs.splice(i.parent().index(), 1);
    i.parent().remove();
}

//视频转化
var $videoUploader = document.getElementById('videoUploader');
var $video = document.getElementById('video');
var reader = new FileReader();
$videoUploader.addEventListener('change', function () {
    loadFile(function (result) {
        $video.src = '';
        $('.progress').css('display', 'block');
        $.ajax({
            type: 'POST',
            url: urlle + 'uploadFile/saveUploadFileVideo',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({base64: result}),
            success: function (res) {
                if (res.stateCode == 100) {
                    $('.progress').css('display', 'none');
                    $video.src = result;
                    $('#video').attr('value' ,res.data.key);

                } else {
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    })
});

function loadFile(callback) {
    if ($videoUploader.files.length === 0) return;
    var oFile = $videoUploader.files[0];
    reader.readAsDataURL(oFile);
    reader.onload = function (e) {
        callback && callback(this.result)
    }
}

    //编辑
    $.ajax({
        type: 'POST',
        url: urlle + 'footerContent/findFooterContent',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                    var img = '';
                    img += '<div class="img_container1"><img src="' + res.data.picture + '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $('#img .img_container').append('' + img + '');
                    imgs.push({href: res.data.key});

                    $("#goodsName").val(res.data.name);
                    $("#remark").val(res.data.shortDesc);
                    $("#watch").val(res.data.watch);
                    $("#Suplierscope").val(res.data.kind);
                $('#video').attr('src', '' + res.data.vedioUrl + '');

                //UE.getEditor('ueditor').setContent('' + res.data.desc + '');
                ue.ready(function() {
                    ue.setContent(res.data.desc);
                });
            }
        }
    })

//提交编辑信息
var sss;
$('#confirm').click(function () {
    if ($('.progress').css('display') == 'block') {
        layer.alert('视频正在上传中！', {icon: 0});
    } else {
        // $('.spinner').show();
        var name = $('#goodsName').val().trim();
        var video = $('#video').attr('value');
        var Suplierscope = $('#Suplierscope option:selected').attr("value");
        var shortDesc = $("#remark").val();
        console.log(Suplierscope);
        var watch = $("#watch").val()
        if (name == '') {
            layer.alert('名称必填！', {icon: 0});
            return;
        } else if (imgs.length == 0) {
            layer.alert('请上传图片！', {icon: 0});
            return;
        } else if (Suplierscope == "") {
            layer.alert('请选择分类！', {icon: 0});
            return;
        } else if (watch == "" || watch == 0 || (watch < -1)) {
            layer.alert('浏览量未输入且不能为负！', {icon: 0});
            return;
        }


                var data = {
                    id: id,
                    name: name,
                    kind: Suplierscope,
                    description: ue.getContent(),
                    vedioUrl: video,
                    picture: imgs,
                    shortDesc: shortDesc,
                    watch: watch
                }


                zouqi(data);





    }
})






    function zouqi(data) {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'footerContent/insertFooterContent',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                // $('.spinner').hide();
                $('.spinner-wrap').hide();
                if (res.stateCode == 100) {
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        setTimeout(function () {
                            location.href = '../salesHeadCategory/bottomVideoHtml';
                        }, 1500)
                    })
                } else {
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
