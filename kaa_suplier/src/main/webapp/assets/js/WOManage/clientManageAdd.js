var url = window.location.href;

var id = url.split('=')[1];
var key;

//本地上传图片
document.getElementById("upLoad2").addEventListener("change",function(e){
    var log = $('#img2 .img_container2').length;
    if(log > 1){
        alert('最多上传1张图片');
    }else{
        var files = document.getElementById("upLoad2").files;
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
                    var image = '<div class="img_container2"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $.ajax({
                        type: 'POST',
                        url: '../uploadFile/saveUploadFile',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({base64: this.result}),
                        success: function (res) {
                            if (res.stateCode == 100) {
                                $('#example').css('display', 'none');
                                $('#addPosition2').before(image);
                                key = res.data.key;
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
    key = '';
    i.parent().remove();
    $('#example').css('display', '');
}
//提交保存
$('#confirm').click(function () {
    if($('#customerPhone').val() != '' && !(/^[1][3,4,5,7,8][0-9]{9}$/.test($('#customerPhone').val()))){
        layer.alert('手机号不正确',{icon:0})
    }else {
        var data = {
            id: id,
            customer: $('#customer').val(),
            customerInit: $('#customerInit').val(),
            customerPhone: $('#customerPhone').val(),
            remark: $('#remark').val(),
            icon: key
        };
        $.ajax({
            type: 'POST',
            url: '../supplierCustomer/addSupplierCustomer',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if (res.stateCode == 100) {
                    layer.alert('修改成功', {icon: 1});
                    setTimeout(function () {
                        location.href = '../WOManage/clientManageHtml';
                    }, 1000)
                } else {
                    layer.alert(res.message, {icon: 0})
                }
            }, error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
    }
});

