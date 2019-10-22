var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');

//时间戳转换
function timetrans(date){
    var date = new Date(date);//如果date为10位不需要乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() <10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() <10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y+M+D+h+m+s;
}
//供应商
$.ajax({
    type: 'POST',
    url: urlle + 'product/suplierNameList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var productSupplierName = '';
            productSupplierName += '<option id="'+res.data[i].id+'" value="'+res.data[i].suplierName+'">'+res.data[i].suplierName+'</option>';
            $('#productSupplierName').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
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

//分类
$.ajax({
    type: 'POST',
    url: urlle + 'base/getPlaProductCategoryLevelOneList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        //分类
        for(var i = 0; i < res.data.length; i++){
            var Suplierscope = '';
            Suplierscope += '<option id="'+res.data[i].id+'" value="'+res.data[i].name+'">'+res.data[i].name+'</option>';
            $('#Suplierscope').append(Suplierscope);
        }
        change();
    },
    error: function (err) {
        alert(err.message)
    }
});
function change() {
    $.ajax({
        type: 'POST',
        url: urlle + 'base/getPlaProductCategoryList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parentId: $('#Suplierscope option:selected').attr('id')}),
        success: function (res) {
            $('#secCategory').html('');
            //分类
            for(var i = 0; i < res.data.length; i++){
                var secCategory = '';
                secCategory += '<option id="'+res.data[i].id+'" value="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                $('#secCategory').append(secCategory);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
$('#Suplierscope').change(function () {
    change();
});
//商品标签
$.ajax({
    type: 'POST',
    url: urlle + 'base/plaProductBaseList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        //颜色
        for(var i = 0; i < res.data[0].productColor.length; i++){
            var productColor = '';
            productColor += '<label style="margin-right: 16px"><input id="'+res.data[0].productColor[i].colorId+'" style="margin-right: 5px" name="productColor" type="checkbox" value="'+res.data[0].productColor[i].colorName+'" />'+res.data[0].productColor[i].colorName+' </label>';
            $('#color').append(productColor);
        }
        //尺寸
        for(var i = 0; i < res.data[0].productSize.length; i++){
            var productSize = '';
            productSize += '<label style="margin-right: 16px"><input id="'+res.data[0].productSize[i].sizeId+'" style="margin-right: 5px" name="productSize" type="checkbox" value="'+res.data[0].productSize[i].sizeName+'" />'+res.data[0].productSize[i].sizeName+' </label>';
            $('#size').append(productSize);
        }
        //动态绑定颜色尺寸关系
        $('#color label').click(function () {
            each();
        });
        $('#size label').click(function () {
            each();
        });

        //生产颜色尺寸关系
        function each() {
            $("#assemblage").html("");
            var aa = $("#color input[type='checkbox']:checked");
            var bb = $("#size input[type='checkbox']:checked");
            for(var i = 0; i < aa.length; i++){
                for(var j = 0; j < bb.length; j++){
                    var span = '';
                    span += '<span class="col col-md-2 col-lg-2 col-sm-2 col-xs-2" style="text-align: center;margin-top: 5px;margin-right: 26px;padding: 5px 15px;border: 1px dotted #fff">'+aa.eq(i).attr('value')+'+'+bb.eq(j).attr('value')+'</span>';
                    $('#assemblage').append(span);
                }
            }
        }
    },
    error: function (err) {
        alert(err.message)
    }
})
//添加
function add() {
    if($("#assemblage").html() == ''){
        layer.alert('请选择规格！', {icon: 0});
    }else{
        $('#tbody').html('');
        for(var i = 0; i < $("#assemblage span").length; i++){
            var tr2 = '';
            tr2 += ' <tr>' +
                '<td class="table_color">' +$("#assemblage").children("span").eq(i).html().split('+')[0]+ '</td>' +
                '<td class="table_size">' +$("#assemblage").children("span").eq(i).html().split('+')[1]+ '</td>' +
                '<td class="table_price"><input type="number" value="'+$('#unification .price').val()+'"></td>' +
                '<td class="table_stock"><input type="number" value="'+$('#unification .stock').val()+'"></td>' +
                '<td class="table_remark"><input type="text" style="height: 26px;width: 90px;border-radius: 6px !important;"></td>' +
                '<td><span class="storage-operate deleteTr" onclick="$(this).parent().parent().remove()">删除</span></td></tr>';
            $('#tbody').append(tr2);
        }
    }
}
//本地上传图片
var imgs = []; //图片数组
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
//提交编辑信息
$('#confirm').click(function () {
    var name = $('#goodsName').val();
    var img_content = $('.img_container > div img').attr('src');
    var brand = $('#brand').val();
    var Suplierscope = $('#Suplierscope').val();
    var pno = $('#pno').val();
    var productSupplierId = $('#productSupplierName option:selected').attr('id');
    var suplierDay = $('#suplierDay').val();
    var material = $('#material').val();
    var sort = $('#sort').val();
    var suplierRemark = $('#remark').val();
    var productSupplierList = [];
    var table = $('#tbody tr');
    for(var i = 0; i < table.length; i++){
        var obj = {};
        obj = {
            color: table.eq(i).find('td').eq(0).html(),
            size: table.eq(i).find('td').eq(1).html(),
            offlinePrice: table.eq(i).find('td').eq(2).find('input').eq(0).val(),
            stock: table.eq(i).find('td').eq(3).find('input').eq(0).val(),
            categoryRemark: table.eq(i).find('td').eq(4).find('input').eq(0).val()
        };
        productSupplierList[i] = obj;
    }
    var remark = $('#remark').val();
    var tr = $('#tbody tr');
    var time = timetrans(new Date());
    var img_con = $('.img_container .img_container1');
    // var src = [];
    // for(var i = 0; i < img_con.length; i++){
    //     var imageSrc = {
    //
    //     };
    //     imageSrc.href = img_con.eq(i).find('img').eq(0).attr('src');
    //     src[i] = imageSrc;
    // }
    var data = {
        categoryId: $('#secCategory option:selected').attr('id'),
        label_id: $('#labelNmae input:checked').attr('id'),
        name: name,
        pno: pno,
        productSupplierId:productSupplierId,
        id: id,
        brand: brand,
        suplierDay: suplierDay,
        material: material,
        suplierRemark: remark,
        sort: sort,
        updateDate: time,
        productSupplierList: productSupplierList,
        productPictureList: imgs
    };
    if(name==''){
        layer.alert('货品名称必填！', {icon:0});
    } else if (pno==''){
        layer.alert('货号为必填！', {icon:0});
    } else if (suplierDay==''){
        layer.alert('供货周期为必填！', {icon:0});
    } else if (!$("#color input[name=productColor]").is(":checked")){
        layer.alert('请选择颜色！', {icon:0});
    } else if (!$("#size input[name=productSize]").is(":checked")){
        layer.alert('请选择尺寸！', {icon:0});
    } else if ($('#tbody tr').length==0){
        layer.alert('请添加商品！', {icon:0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'suplier/insertSuplierGoods',
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
                    $('#myModal #sure').click(function () {
                        location.href = '../suplier/supplierGoodsManageHtml';
                    })
                }else {
                    // alert(res.message)
                    layer.alert(res.message, {icon:0});
                }
            },
            error: function (err) {
                // alert(err.message)
                layer.alert(err.message, {icon:0});
            }
        })
    }

});