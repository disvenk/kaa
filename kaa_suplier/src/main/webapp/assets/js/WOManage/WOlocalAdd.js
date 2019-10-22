laydate.render({
    elem: '#placeOrder'
});
var customerId;
var left = $('#_input').offset().left  + 'px';
$('#position').css({'position': 'absolute', 'top': '179px', 'left': left});
$(window).resize(function(){
    left = $('#_input').offset().left  + 'px';
    $('#position').css({'position': 'absolute', 'top': '179px', 'left': left});
});
//    客户输入框模糊查询
$('#_input').bind('input propertychange',function () {
    $('#position').css('display', 'block');
    $('#position').html('');
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/selectSupplierCustomer',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({customer: $("#_input").val()}),
        success: function (res) {
            $('#position').html('');
            for(var i = 0; i < res.data.length; i++){
                var div = '';
                div += '<div style="padding: 8px;cursor: pointer" onclick="sele($(this))" value="'+res.data[i].id+'" class="'+res.data[i].customerPhone+'">'+res.data[i].customer+'</div>';
                $('#position').append(div);
                if($('#_input').val() == res.data[i].customer){
                    addresslist(res.data[i].id)
                }else{
                    addresslist();
                }
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
})
$('#_input').blur(function () {
    setTimeout(function () {
        $('#position').css('display', 'none');
        $('#position').html('');
    },300)
});
function sele(it) {
    $('#_input').focus();
    $('#_input').val(it.html());
    $('#customerTel').val(it.attr('class'));
    customerId = it.attr('value');
    addresslist(it.attr('value'));
}
//地址列表
function addresslist(id) {
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/supplierCustomerAddressList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#address').html('');
            if(res.data.length > 0){
                for(var i = 0; i < res.data.length; i++){
                    var address = '';
                    address += '<label class="diy_label" style="color: #999"><input class="'+res.data[i].id+'" style="margin-right: 6px" name="address" type="radio"/><span style="margin-right: 5px">'+res.data[i].provinceName+'</span>'+
                        '<span style="margin-right: 5px">'+ res.data[i].cityName+'</span><span style="margin-right: 5px">'+ res.data[i].zoneName +'</span>'+
                        '<span style="margin-right: 5px">'+res.data[i].address1+'</span>(<span style="margin-right: 5px">'+res.data[i].receiver +'</span>收）<span>'+ res.data[i].mobile +'</span></label></br>';
                    $('#address').append(address);
                }
            }else{}
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
var imgs = [];
//本地上传图片
document.getElementById("upLoad2").addEventListener("change",function(e){
    var log = $('#img2 .img_container2').length;
    if(log > 4){
        alert('最多上传5张图片');
    }else{
        var files = document.getElementById("upLoad2").files;
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
    imgs.splice(i.parent().index() - 1, 1);
    i.parent().remove();
    if(imgs.length == 0){
        $('#example').css('display', '');
    }
}
//提交保存
$('#confirm').click(function () {
    var data = {
        supOrderId: '',
        insideOrderNo: $('#number').val(),
        customer: $('#_input').val(),
        customerPhone: $('#customerTel').val(),
        remarks: $('#remark').val(),
        deliveryDate: $('#placeOrder').val(),
        pno: $('#pno').val(),
        categoryName: $('#categoryName').val(),
        color: $('#color').val(),
        size: $('#size').val(),
        throatheight: $('#throatheight').val(),
        shoulder: $('#shoulder').val(),
        bust: $('#bust').val(),
        waist: $('#waist').val(),
        hipline: $('#hipline').val(),
        height: $('#height').val(),
        weight: $('#weight').val(),
        qty: $('#qty').val(),
        outputPrice: $('#outputPrice').val(),
        material: $('#material').val(),
        technics: $('#technics').val(),
        description: $('#description').val(),
        imgs: imgs,
        customerId: customerId,
        customerAddressId: $('input[name=address]:checked').attr('class')
    };
    $.ajax({
        type: 'POST',
        url: '../supOrderOffline/supOrderOfflineSave',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
               layer.alert('添加成功',{icon: 1});
                setTimeout(function () {
                  location.href = '../WOManage/WOlocalHtml?type=2';
                },1000)
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
});

