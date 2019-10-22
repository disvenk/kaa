
var s;
var procedu;
var mater;
$(function () {
    //分类
    $.ajax({
        type: 'POST',
        url: '../productBase/findCategoryListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type:1}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#category').html('');
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var category = '';
                        category += '<option value="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                        $('#category').append(category);
                    }
                }else{}
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
    //颜色
    $.ajax({
        type: 'POST',
        url: '../productBase/findCategoryListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type:2}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#color').html('');
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var color = '';
                        color += '<input id="color+'+i+'" type="checkbox" class="'+res.data[i].id+'"/><label for="color+'+i+'">'+res.data[i].name+'</label>';
                        $('#color').append(color);
                    }
                }else{}
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
    //原材料
    $.ajax({
        type: 'POST',
        url: '../productBase/findCategoryListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type:4}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#rawmaterial').html('');
                mater = res.data.length;
                if(res.data.length > 0){
                    $('#rawmaterial').append('<div style="width: 800px;display: inline-block"></div>');
                    for(var i = 0; i < res.data.length; i++){
                        var rawmaterial = '';
                        rawmaterial += '<span data="'+res.data[i].name+'" value="'+res.data[i].id+'" class="procedure-wrap"><input type="checkbox">'+res.data[i].name+'<span><input class="procedure-input wor metan" type="number" placeholder="数量">' +
                            '<select class="unit"><option value="">米</option><option value="">码</option><option value="">个</option>' +
                            '<option value="">包</option><option value="">条</option><option value="">件</option>' +
                            '<option value="">对</option><option value="">颗</option><option value="">粒</option></select></span><br><span style="position: relative;left: 65px;top: -12px;"><input class="procedure-input wor metap" type="number" placeholder="单价"><select><option value="">元</option></select></span></span>';
                        $('#rawmaterial > div').append(rawmaterial);
                    }
                }else{}
                $('#rawmaterial').append('<span id="addca"><i style="color: #d6d6d6" class="glyphicon glyphicon-plus"></i>添加面辅料</span>');
                $('#addca').click(function () {
                    $('#rawmaterial > div').append('<span class="procedure-wrap addmete" data="" value=""><input type="checkbox"><input class="procedure-input metam" type="text" placeholder="材料">' +
                        '<span><input class="procedure-input wor metan" type="number" placeholder="数量">' +
                        '<select class="unit"><option value="">米</option><option value="">码</option><option value="">个</option>' +
                        '<option value="">包</option><option value="">条</option><option value="">件</option>' +
                        '<option value="">对</option><option value="">颗</option><option value="">粒</option></select></span><br><span style="position: relative;left: 92px;top: -12px;"><input class="procedure-input wor metap" type="number" placeholder="单价">' +
                        '<select><option value="">元</option></select><i style="top: -55px;left: 8px" onclick="$(this).parent().parent().remove()" class="glyphicon glyphicon-remove"></i></span>');
                });
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
    //尺寸
    $.ajax({
        type: 'POST',
        url: '../productBase/findCategoryListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type:3}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#size').html('');
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var size = '';
                        size += '<input id="size+'+i+'" type="checkbox" class="'+res.data[i].id+'"/><label for="size+'+i+'">'+res.data[i].name+'</label>';
                        $('#size').append(size);
                    }
                }else{}
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
    //工序
    $.ajax({
        type: 'POST',
        url: '../procedure/findProcedureListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if(res.stateCode == 100){
                $('#workerSort').html('');
                procedu = res.data.length;
                if(res.data.length > 0){
                    $('#workerSort').append('<div style="width: 800px;display: inline-block"></div>');
                    for(var i = 0; i < res.data.length; i++){
                        var workerSort = '';
                        workerSort += '<span class="procedure-wrap" data="'+res.data[i].name+'" value="'+res.data[i].id+'"><input type="checkbox" name="worksort">'+res.data[i].name+'<span>' +
                            '<input class="procedure-input wor" type="number" placeholder="工价"  value="'+res.data[i].price+'"><select><option value="">元</option></select></span></span>';
                        $('#workerSort > div').append(workerSort);
                    }
                }else{}
                $('#workerSort').append('<span id="addgx"><i style="color: #d6d6d6" class="glyphicon glyphicon-plus"></i>添加工序</span>');
                //新增工序
                $('#addgx').click(function () {
                    $('#workerSort > div').append('<span class="procedure-wrap addpro" data="" value=""><input type="checkbox" name="worksort"><input class="procedure-input gongxu" type="text" placeholder="工序">' +
                        '<span><input class="procedure-input wor" type="number" placeholder="工价"><select><option value="">元</option></select></span><i onclick="$(this).parent().remove()" class="glyphicon glyphicon-remove"></i></span>');
                });
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
});
var imgs = [];
//本地上传图片
document.getElementById("upLoad2").addEventListener("change",function(e){
    var log = $('#img2 .img_container2').length;
    if(log > 4){
        layer.alert('最多上传5张图片',{icon:0});
    }else{
        var files = document.getElementById("upLoad2").files;
        if(log + files.length > 5){
            layer.alert('最多上传5张图片',{icon:0});
        }else{
            for(var i= 0; i < files.length; i++){
                var img = new Image();
                var reader =new FileReader();
                reader.readAsDataURL(files[i]);
                reader.onload =function(e){
                    var dx =(e.total/1024)/1024;
                    if(dx>=10){
                        layer.alert("文件大小大于10M",{icon:0});
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
                                layer.alert(res.message, {icon:0});
                            }
                        },
                        error: function (err) {
                            layer.alert(err.message, {icon:0})
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
var procedureList;
var materialList;
$('#confirm').click(function () {
    var index = layer.load(2, {shade: [0.6,'#000']});
    if($('#color input:checkbox:checked').length == 0){
        layer.close(index);
        layer.alert('请选择颜色',{icon:0});
        return;
    }
    if($('#size input:checkbox:checked').length == 0){
        layer.close(index);
        layer.alert('请选择尺寸',{icon:0});
        return;
    }
    procedureList = [];
        $('#workerSort .procedure-wrap input:checkbox:checked').each(function () {
            var object = {
                procedureName: '',
                price: '',
                procedureId: ''
            };
            object.procedureName = $(this).parent().attr('data') ? $(this).parent().attr('data'): $(this).parent().find('.gongxu').val();
            object.price = $(this).parent().find('.wor').val();
            object.procedureId = $(this).parent().attr('value');
            procedureList.push(object);
        });
    materialList = [];
        $('#rawmaterial .procedure-wrap input:checkbox:checked').each(function () {
            var object1 = {
                materialId: '',
                materialName: '',
                price: '',
                count: '',
                unit: ''
            };
            object1.materialId = $(this).parent().attr('value');
            object1.materialName = $(this).parent().attr('data') ? $(this).parent().attr('data'): $(this).parent().find('.metam').val();
            object1.price = $(this).parent().find('.metap').val();
            object1.count = $(this).parent().find('.metan').val();
            object1.unit = $(this).parent().find('.unit option:selected').text();
            materialList.push(object1);
        });
    var colors = [];
    var sizes = [];
    $('#color input:checkbox:checked').each(function () {
        colors.push($(this).attr('class'));
            });
    $('#size input:checkbox:checked').each(function () {
        sizes.push($(this).attr('class'));
    });
    //前提控制颜色和尺寸不能为空
    var productPriceList = [];
    if (colors.length > sizes.length) {
        for (var i=0; i <= colors.length - sizes.length; i++) {
            sizes.push(sizes[0]);
        }
    } else if (colors.length <= sizes.length) {
        for (var i=0; i <= sizes.length - colors.length; i++) {
            colors.push(colors[0]);
        }
    }
    for (var i=0; i<colors.length; i++) {
        productPriceList[i] = {
            colorId: colors[i],
            sizeId: sizes[i]
        }
    }
    var data = {
        pno: $('#pno').val(),
        name: $('#name').val(),
        categoryId: $('#category').find("option:checked").attr("value"),
        material: $('#material').val(),
        technics: $('#technics').val(),
        description: $('#description').val(),
        shoulder: $('#shoulder').val(),
        bust: $('#bust').val(),
        waist: $('#waist').val(),
        hipline: $('#hipline').val(),
        height: $('#height').val(),
        weight: $('#weight').val(),
        throatheight: $('#throatheight').val(),
        price: $('#price').val(),
        remarks: $('#remark').val(),
        productPictureList: imgs,
        productPriceList: productPriceList,
        procedureList: procedureList,
        materialList: materialList
    };
    $.ajax({
        type: 'POST',
        url: '../suplierProduct/saveSuplierProduct',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                setTimeout(function () {
                    location.href = '../WOManage/basicStorageHtml';
                },600)
            } else {
                layer.close(index);
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon:0})
        }
    })
});


