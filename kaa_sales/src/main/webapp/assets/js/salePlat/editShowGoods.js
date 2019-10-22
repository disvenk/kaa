var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
var categoryId;
var pno ;
var brand;
var platProductId;
var vedio;
var imgs = [];
jQuery(function($){
    function showErrorAlert (reason, detail) {
        var msg='';
        if (reason==='unsupported-file-type') { msg = "Unsupported format " +detail; }
        else {
            //console.log("error uploading file", reason, detail);
        }
        $('<div class="alert"> <button type="button" class="close" data-dismiss="alert">&times;</button>'+
            '<strong>File upload error</strong> '+msg+' </div>').prependTo('#alerts');
    }

    //$('#editor1').ace_wysiwyg();//this will create the default editor will all buttons

    //but we want to change a few buttons colors for the third style
    $('#editor1').ace_wysiwyg({
        toolbar:
            [
                'font',
                null,
                'fontSize',
                null,
                {name:'bold', className:'btn-info'},
                {name:'italic', className:'btn-info'},
                {name:'strikethrough', className:'btn-info'},
                {name:'underline', className:'btn-info'},
                null,
                {name:'insertunorderedlist', className:'btn-success'},
                {name:'insertorderedlist', className:'btn-success'},
                {name:'outdent', className:'btn-purple'},
                {name:'indent', className:'btn-purple'},
                null,
                {name:'justifyleft', className:'btn-primary'},
                {name:'justifycenter', className:'btn-primary'},
                {name:'justifyright', className:'btn-primary'},
                {name:'justifyfull', className:'btn-inverse'},
                null,
                {name:'createLink', className:'btn-pink'},
                {name:'unlink', className:'btn-pink'},
                null,
                {name:'insertImage', className:'btn-success'},
                null,
                'foreColor',
                null,
                {name:'undo', className:'btn-grey'},
                {name:'redo', className:'btn-grey'}
            ],
        'wysiwyg': {
            fileUploadError: showErrorAlert
        }
    }).prev().addClass('wysiwyg-style2');

    if ( typeof jQuery.ui !== 'undefined' && ace.vars['webkit'] ) {

        var lastResizableImg = null;
        function destroyResizable() {
            if(lastResizableImg == null) return;
            lastResizableImg.resizable( "destroy" );
            lastResizableImg.removeData('resizable');
            lastResizableImg = null;
        }
    }
    // 转码照片为base64的字符串
    var readFileIntoDataUrl = function (fileInfo) {
        var loader = $.Deferred(),
            fReader = new FileReader();
        fReader.onload = function (e) {
            loader.resolve(e.target.result);
        };
        fReader.onerror = loader.reject;
        fReader.onprogress = loader.notify;
        fReader.readAsDataURL(fileInfo);
        return loader.promise();
    };
    var uploadFiles = [];
    // 监听选取照片的按钮<input type="file" data-role="magic-overlay" data-target="#pictureBtn" data-edit="insertImage" />，
    // 把每次选取的照片文件放入数组中
    $("#descripitionImg").change(function(){
        $.each(this.files,function(index,fileObj){
            uploadFiles.push(fileObj);
        });
    });
    function uploadImgs(index){
        // 使用FormData对象上传文件
        // 这里上传使用递归上传，上传一个再次调用函数本身，再次上传，知道全部上传完成
        var formData = new FormData();
        if(0 >= index){
            // 再次上传文本编辑器里的内容
        }else{
            //console.log('执行照片提交。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。');
            index -=1;
            formData.append(uploadFiles[index].name,uploadFiles[index]);
            var xhr = new XMLHttpRequest();
            xhr.open("POST",FILEUPLOAD_URL2,true);
            xhr.send(formData);
            xhr.onload = function(data){
                // 判断图片是否存在
                var imgTag = [];
                readFileIntoDataUrl(uploadFiles[index]).done(function (dataUrl) {
                    imgTag = $("#editor").find("img[src='" + dataUrl + "']");
                    if (imgTag.length > 0) {
                        // 图片存在, 上传当前文件.
                        // uploadImage方法为你的上传图片方法.
                        var url = JSON.parse(data.currentTarget.response).resMsg;
                        imgTag.attr("src", url);
                        // 重新调用函数
                        uploadImgs(index);

                    }

                });
            }
        }
    }
    //获取内容
    $('#confirm').click(function () {
        // console.log($('#editor1').html())
    });
});
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
//颜色
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({parameterType: 2}),
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var color = '';
            color += '<label style="margin-right: 16px"><input id="'+res.data[i].id+'" style="margin-right: 5px" name="color" type="radio" value="'+res.data[i].name+'" />'+res.data[i].name+' </label>';
            $('#color').append(color);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});
//尺寸
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({parameterType: 3}),
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var size = '';
            size += '<label style="margin-right: 16px"><input id="'+res.data[i].id+'" style="margin-right: 5px;position: relative;top: -4px;" name="size" type="radio" value="'+res.data[i].name+'" />'+res.data[i].name+' </label>';
            $('#size').append(size);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});
$.ajax({
    type: 'POST',
    url: urlle + 'storeProductManage/storeProductManageEditDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        categoryId = res.data[0].categoryId;
        brand = res.data[0].brand;
        pno = res.data[0].pno;
        vedio = res.data[0].vedioUrl;
        platProductId = res.data[0].platProductId;
        $('#video').attr('src', res.data[0].vedioUrl);
        $('#goodsName').val(''+res.data[0].name+'');
        $('#brand').html(''+res.data[0].brand+'');
        $('#categoryName').html(''+res.data[0].categoryName+'');
        $('#pno').html(''+res.data[0].productCode+'');
        $('#man').val(''+res.data[0].colligate+'');
        $('#views').html(''+res.data[0].views+'');
        $('#goodsName').val(''+res.data[0].name+'');
        $('#beizhu').val(''+res.data[0].remarks+'');
        $('#editor1').html(''+res.data[0].description+'');
        if(res.data[0].status == '已上架'){
            res.data[0].status = '上架';
        }else{
            res.data[0].status = '下架';
        }
        $('#upDown').val(res.data[0].status);
        for (var i = 0; i < res.data[0].picture.length; i++) {
            var img = '';
            img += '<div class="img_container1"><img src="' +res.data[0].picture[i].href+ '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
            $('#img .img_container').append('' + img + '');
            imgs.push({href: res.data[0].picture[i].key});
        }
        //表格
        for(var i = 0; i < res.data[0].jsonSupplier.length; i++) {
            var tr1 = '';
            tr1 += ' <tr><td>' + res.data[0].jsonSupplier[i].color + '</td>' +
                '<td>' + res.data[0].jsonSupplier[i].size + '</td>' +
                '<td><input class="tbody_price" type="number" value="' + res.data[0].jsonSupplier[i].offlinePrice + '"></td>' +
                '<td class="table_stock"><input type="number" value="' + res.data[0].jsonSupplier[i].stock + '"></td>' +
                '<td><span class="storage-operate deleteTr" onclick="$(this).parent().parent().remove()">删除</span></td></tr>';
            $('#tbody').append(tr1);
        }
        //视频转化
        var $videoUploader = document.getElementById('videoUploader');
        var $video = document.getElementById('video');
        var reader = new FileReader();
        $videoUploader.addEventListener( 'change',function () {
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
            reader.onload = function(e){
                callback && callback(this.result)
            }
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
//本地上传图片
document.getElementById("upLoad").addEventListener("change",function(e){
    var log = $('#img .img_container > div').length;
    if(log > 4){
        alert('最多上传5张图片');
    }else{
        var files = document.getElementById("upLoad").files;
        if(log + files.length > 5){
            alert('最多上传5张图片');
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
//添加
function add() {
    var radioNu;
    var radioSizeNu;
    var radio = $('#color').find('input:checked').length;
    var radioSize = $('#size').find('input:checked').length;
    if(radio == 0 || radioSize == 0){
        layer.alert('请选择颜色和尺寸', {icon: 0})
    }else{
        radioNu = $('#color').find('input:checked').val();
        radioSizeNu = $('#size').find('input:checked').val();
        var tr2 = '';
        tr2 = ' <tr><td>' + radioNu + '</td>' +
            '<td>' + radioSizeNu + '</td>' +
            '<td><input class="table_price" type="number" value=""></td>' +
            '<td class="table_stock"><input type="number" value=""></td>' +
            '<td><span class="storage-operate deleteTr" onclick="$(this).parent().parent().remove()">删除</span></td></tr>';
        $('#tbody').append(tr2);
    }
}

//提交
function submit() {
    if($("#assemblage").html() == ''){
        layer.alert('请选择规格！', {icon: 0});
    }else{
        $('.spinner-wrap').show();
        var name = $('#goodsName').val();
        var img_content = $('.img_container > div img').attr('src');
        var video = $('#video').attr('src');
        var sort = $('#man').val();
        var brand = $('#brand').html();
        var categoryName = $('#categoryName').html();
        var pno = $('#pno').html();
        var beizhu = $('#beizhu').val();
        var upDown = $('#upDown').find('option:selected').val();
        if(video == vedio){
            video = ''
        }else{
            video = $('#video').attr('src');
        }
        if(upDown == '上架'){
            upDown = 1;
        }else {
            upDown = 0;
        }
        var productSupplierList = [];
        var table = $('#tbody tr');
        for(var i = 0; i < table.length; i++){
            var obj = {};
            obj = {
                brand: brand,
                color: table.eq(i).find('td').eq(0).html(),
                size: table.eq(i).find('td').eq(1).html(),
                stock: table.eq(i).find('td').eq(3).find('input').eq(0).val(),
                offlinePrice: table.eq(i).find('td').eq(2).find('input').eq(0).val()
            };
            productSupplierList[i] = obj;
        };
        var data = {
            brand: brand,
            pno: pno,
            name: name,
            id: id,
            description: $('#editor1').html(),
            vedioUrl: $('#video').attr('value'),
            remarks: beizhu,
            storeProductSupplierList: productSupplierList,
            storeProductPictureList: imgs,
            colligate: sort,
            status: upDown,
            categoryId: categoryId,
            platProductId: platProductId
        };
        $.ajax({
            type: 'POST',
            url: urlle + 'storeProductManage/updateStoreProductManage',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').hide();
                if(res.stateCode == 100){
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        setTimeout(function () {
                            location.href = '../storeProductManage/showGoodsHtml';
                        },1500)
                    })
                }else{
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
}
