laydate.render({
    elem: '#placeOrder'
});
var url = window.location.href;
var id;
var pid;
$('#addClient').click(function () {
    $('#myModal').modal('show');
});
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
            $('#selectCate').html('');
            if(res.data.length > 0){
                for(var i = 0; i < res.data.length; i++){
                    var category = '';
                    category += '<option value="'+res.data[i].id+'" class="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                    $('#selectCate').append(category);
                }
            }else{}
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
            $('#seleteSize').html('');
            if(res.data.length > 0){
                for(var i = 0; i < res.data.length; i++){
                    var selectsize = '';
                    selectsize += '<option id="size+'+i+'" class="'+res.data[i].id+'" value="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                    $('#seleteSize').append(selectsize);
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
            $('#seleteColor').html('');
            if(res.data.length > 0){
                for(var i = 0; i < res.data.length; i++){
                    var color = '';
                    color += '<option id="color+'+i+'" class="'+res.data[i].id+'" value="'+res.data[i].name+'">'+res.data[i].name+'</option>';
                    $('#seleteColor').append(color);
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
                    '<span><input class="procedure-input wor" type="number" placeholder="单价"><select><option value="">元</option></select></span><i onclick="$(this).parent().remove()" class="glyphicon glyphicon-remove"></i></span>');
            });
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
                    '<select class="unit"><option value="">米</option><option value="">英寸</option></select></span><br><span style="position: relative;left: 92px;top: -12px;"><input class="procedure-input wor metap" type="number" placeholder="单价">' +
                    '<select><option value="">元</option></select><i style="top: -55px;left: 8px" onclick="$(this).parent().parent().remove()" class="glyphicon glyphicon-remove"></i></span>');
            });
        }else{
            layer.alert(res.message, {icon: 0});
        }
    },error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});
//客户
function customer() {
    $('#select').html('');
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/selectSupplierCustomer',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({}),
        success:function (res) {
            if(res.stateCode == 100){
                $('#select').append('<option value=""></option>');
                for(var i = 0; i < res.data.length; i++){
                    var option = '';
                    option += '<option value="'+res.data[i].id+'">'+res.data[i].customer+'</option>';
                    $('#select').append(option);
                }
            }else{
                layer.alert(res.message, {icon:0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon:0})
        }
    });
}
customer();
$('#select').change(function () {
    id = $(this).children('option:selected').val();
    if($(this).children('option:selected').val() == ''){
        $('#address').html('');
    }else{
        addreeeList($(this).children('option:selected').val(),'')
    }
});
//地址
function addreeeList(id,addressId) {
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
        success:function (res) {
            if(res.stateCode == 100){
                $('#address').html('');
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var address = '';
                        address += '<label class="diy_label" style="color: #999"><input class="'+res.data[i].id+'" style="margin-right: 6px" name="address" type="radio"/><span style="margin-right: 5px">'+res.data[i].provinceName+'</span>'+
                            '<span style="margin-right: 5px">'+ res.data[i].cityName+'</span><span style="margin-right: 5px">'+ res.data[i].zoneName +'</span>'+
                            '<span style="margin-right: 5px">'+res.data[i].address1+'</span>(<span style="margin-right: 5px">'+res.data[i].receiver +'</span>收）<span>'+ res.data[i].mobile +'</span></label></br>';
                        $('#address').append(address);
                        if(addressId == $('#address input[type=radio]').eq(i).attr('class')){
                            $('#address input[type=radio]').eq(i).attr('checked', 'checked');
                        }else{
                            $("#address input[type=radio]").eq(0).attr('checked', true);
                        }
                    }
                    $('#address').append('<div><span id="addAddress" onclick="$(\'#myModal1\').modal(\'show\')">新增收货地址</span></div>');
                }else{
                    $('#address').append('<div><span id="addAddress" onclick="$(\'#myModal1\').modal(\'show\')">新增收货地址</span></div>');
                }
            }else{
                layer.alert(res.message, {icon:0})
                $('#address').html('');
                $('#address').append('<div><span id="addAddress" onclick="$(\'#myModal1\').modal(\'show\')">新增收货地址</span></div>');
            }
        },error: function (err) {
            layer.alert(err.message, {icon:0})
            $('#address').html('');
            $('#address').append('<div><span id="addAddress" onclick="$(\'#myModal1\').modal(\'show\')">新增收货地址</span></div>');
        }
    });
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/getCustomer',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success:function (res) {
            if(res.stateCode == 100){
                $('#conta').val(res.data.contact);
                $('#contaTel').val(res.data.contactTel);
            }else{
                layer.alert(res.message, {icon:0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon:0})
        }
    });
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

function pro1() {
    $.ajax({
        type: 'POST',
        url: '../area/findProvince',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for (var i = 0; i < res.data.length; i++) {
                var province1 = '';
                province1 += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                $('#cmbProvince1').append(province1);
            }
            code1 = $('#cmbProvince1 option').eq(0).val();
            provinceCity();
            $("#cmbProvince1").change(function () {
                $('#cmbCity1').html('');
                $('#cmbArea1').html('');
                code1 = $("#cmbProvince1").val();
                provinceCity()
            });

            function provinceCity() {
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: code1}),
                    success: function (res) {
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity1 = '';
                            cmbCity1 += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity1').append(cmbCity1)
                        }
                        //区
                        code1 = $('#cmbCity1 option').eq(0).val();
                        area();
                        $("#cmbCity1").change(function () {
                            code1 = $("#cmbCity1").val();
                            area();
                        });

                        function area() {
                            $('#cmbArea1').html('');
                            $.ajax({
                                type: 'POST',
                                url: '../area/findCity',
                                contentType: 'application/json; charset=utf-8',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                data: JSON.stringify({code: code1}),
                                success: function (res) {
                                    $('#cmbArea1').html('');
                                    for (var i = 0; i < res.data.length; i++) {
                                        var cmbArea1 = '';
                                        cmbArea1 += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                                        $('#cmbArea1').append(cmbArea1)
                                    }
                                },
                                error: function (err) {
                                    alert(err.message)
                                }
                            })
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
pro1();
function pro() {
    $.ajax({
        type: 'POST',
        url: '../area/findProvince',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for (var i = 0; i < res.data.length; i++) {
                var province = '';
                province += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                $('#cmbProvince').append(province);
            }
            code = $('#cmbProvince option').eq(0).val();
            provinceCity();
            $("#cmbProvince").change(function () {
                $('#cmbCity').html('');
                $('#cmbArea').html('');
                code = $("#cmbProvince").val();
                provinceCity()
            });

            function provinceCity() {
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: code}),
                    success: function (res) {
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity = '';
                            cmbCity += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        //区
                        code = $('#cmbCity option').eq(0).val();
                        area();
                        $("#cmbCity").change(function () {
                            code = $("#cmbCity").val();
                            area();
                        });

                        function area() {
                            $('#cmbArea').html('');
                            $.ajax({
                                type: 'POST',
                                url: '../area/findCity',
                                contentType: 'application/json; charset=utf-8',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                data: JSON.stringify({code: code}),
                                success: function (res) {
                                    $('#cmbArea').html('');
                                    for (var i = 0; i < res.data.length; i++) {
                                        var cmbArea = '';
                                        cmbArea += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                                        $('#cmbArea').append(cmbArea)
                                    }
                                },
                                error: function (err) {
                                    alert(err.message)
                                }
                            })
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
pro();
//详情
$.ajax({
    type: 'POST',
    url: '../suplierSalesOrder/salesOrderDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: url.split('=')[1]}),
    success: function (res) {
        if (res.stateCode == 100) {
            addreeeList(res.data.customerId,res.data.customerAddressId);
            $('#pno').val(res.data.pno);
            $('#description').val(res.data.description);
            $('#weight').val(res.data.weight);
            $('#throatheight').val(res.data.throatheight);
            $('#hipline').val(res.data.hipline);
            $('#material').val(res.data.material);
            $('#price').val(res.data.subtotal);
            $('#name').val(res.data.name);
            $('#bust').val(res.data.bust);
            $('#waist').val(res.data.waist);
            $('#technics').val(res.data.technics);
            $('#shoulder').val(res.data.shoulder);
            $('#remark').val(res.data.remarks);
            $('#height').val(res.data.height);
            $('#qty').val(res.data.qty);
            $('#outputPrice').val(res.data.price);
            pid = res.data.pid;
            if(res.data.pictures.length > 0){
                $('#example').css('display', 'none');
                for (var i = 0; i < res.data.pictures.length; i++) {
                    var img = '';
                    img += '<div class="img_container2"><img src="' +res.data.pictures[i].href+ '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $('#addPosition2').before(img);
                    imgs.push({href: res.data.pictures[i].key});
                }
            }else{}
            //分类
            var categor = res.data.categoryName;
            for(var i = 0; i < $('#selectCate option').length; i++){
                if($('#selectCate option').eq(i).text() == categor){
                    $('#selectCate option').eq(i).attr('selected', 'selected')
                }
            }
            //颜色
                for (var i = 0; i < $('#seleteColor option').length; i++) {
                    if($('#seleteColor option').eq(i).text() == res.data.color){
                        $('#seleteColor option').eq(i).attr('selected', 'selected')
                    }
                }
            //尺寸
                for (var i = 0; i < $('#seleteSize option').length; i++) {
                    if($('#selectCate option').eq(i).text() == res.data.size){
                        $('#selectCate option').eq(i).attr('selected', 'selected')
                    }
                }
            //工序
            for(var i = 0; i < $('#workerSort .procedure-wrap').length; i++){
                for(var j = 0; j < res.data.procedures.length; j++){
                    if($('#workerSort .procedure-wrap').eq(i).attr('value') == res.data.procedures[j].procedureId){
                        $('#workerSort .procedure-wrap').eq(i).find('input[type=checkbox]').attr('checked', 'checked');
                        $('#workerSort .procedure-wrap').eq(i).find('.wor').val(res.data.procedures[j].price);
                    }
                }
            }
            //原材料
            var met;
            for(var i = 0; i < $('#rawmaterial .procedure-wrap').length; i++){
                for(var j = 0; j < res.data.materials.length; j++){
                    if($('#rawmaterial .procedure-wrap').eq(i).attr('value') == res.data.materials[j].materialId){
                        $('#rawmaterial .procedure-wrap').eq(i).find('input[type=checkbox]').attr('checked', 'checked');
                        $('#rawmaterial .procedure-wrap').eq(i).find('.metan').val(res.data.materials[j].count);
                        $('#rawmaterial .procedure-wrap').eq(i).find('.metap').val(res.data.materials[j].price);
                        met = res.data.materials[j].unit;
                        for(k = 0;k < 9; k++){
                            if($('#rawmaterial .procedure-wrap').eq(i).find('option').eq(k).text() == res.data.materials[j].unit){
                                $('#rawmaterial .procedure-wrap').eq(i).find('option').eq(k).attr('selected', 'selected')
                            }
                        }
                    }
                }
            }
            //客户资料
            $('#orderNo').val(res.data.orderNo);
            $('#insideOrderNo').val(res.data.insideOrderNo);
            $('#conta').val(res.data.customer);
            $('#placeOrder').val(res.data.deliveryTime);
            $('#contaTel').val(res.data.customerPhone);
            for (var i = 0; i < $('#select option').length; i++) {
                if($('#select option').eq(i).attr('value') == res.data.customerId){
                    $('#select option').eq(i).attr('selected', 'selected')
                }
            }
        } else {
            layer.alert(res.message, {icon:0});
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon:0})
    }
})



$('#sure4').click(function () {
    var param = {
        id: id,
        receiver: $('#receiver').val(),
        mobile: $('#mobile').val(),
        province: $('#cmbProvince1').find('option:selected').val(),
        provinceName: $('#cmbProvince1').find('option:selected').text(),
        city: $('#cmbCity1').find('option:selected').val(),
        cityName: $('#cmbCity1').find('option:selected').text(),
        zone: $('#cmbArea1').find('option:selected').val(),
        zoneName: $('#cmbArea1').find('option:selected').text(),
        address: $('#address1').val()
    };
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/addSupplierCustomerAddress',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(param),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#myModal1').modal('hide');
                $('#myModal1').modal('hide');
                $('#receiver').val('');
                $('#mobile').val('');
                $('#address1').val('');
                addreeeList(id,'');
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(res.message, {icon: 0});
        }
    });
});
$('#sure3').click(function () {
    var param1 = {
        customerName: $('#customer1').val(),
        contactName: $('#contact1').val(),
        mobile: $('#contel1').val(),
        receiver: $('#receiver1').val(),
        receiverTel: $('#recetel1').val(),
        provinceId: $('#cmbProvince').find('option:selected').val(),
        province: $('#cmbProvince').find('option:selected').text(),
        cityId: $('#cmbCity').find('option:selected').val(),
        city: $('#cmbCity').find('option:selected').text(),
        zoneId: $('#cmbArea').find('option:selected').val(),
        zone: $('#cmbArea').find('option:selected').text(),
        address: $('#address2').val()
    };
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/addCustomerAndAddress',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(param1),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#myModal').modal('hide');
                $('#conta').val($('#contact1').val());
                $('#contaTel').val($('#contel1').val());
                $('#customer1').val('');
                $('#contact1').val('');
                $('#contel1').val('');
                $('#receiver1').val('');
                $('#recetel1').val('');
                $('#address2').val('');
                customer();
                setTimeout(function () {
                    for(var i = 0; i < $('#select option').length; i++){
                        if($('#select option').eq(i).attr('value') == res.data.id){
                            $('#select option').eq(i).attr('selected', 'selected')
                        }
                    }
                },200);
                addreeeList(res.data.id,'');
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(res.message, {icon: 0});
        }
    });
});
//
$('#outputPrice').bind('input propertychange', function() {
    $('#price').val($('#outputPrice').val() * $('#qty').val());
});
$('#qty').bind('input propertychange', function() {
    $('#price').val($('#outputPrice').val() * $('#qty').val());
});
    $('#pno').blur(function() {
        if($('#pno').val() == ''){
            layer.alert('商品库中不存在该编号的商品',{icon:0})
        }else{
            proDetail();
        }
    });
function proDetail() {
    $('.img_container2').remove();
    //详情
    $.ajax({
        type: 'POST',
        url: '../suplierProduct/productDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({pno: $('#pno').val()}),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#pno').val(res.data.pno);
                $('#description').val(res.data.description);
                $('#weight').val(res.data.weight);
                $('#throatheight').val(res.data.throatheight);
                $('#hipline').val(res.data.hipline);
                $('#material').val(res.data.material);
                $('#outputPrice').val(res.data.price);
                $('#name').val(res.data.name);
                $('#bust').val(res.data.bust);
                $('#waist').val(res.data.waist);
                $('#technics').val(res.data.technics);
                $('#shoulder').val(res.data.shoulder);
                $('#remark').val(res.data.remarks);
                $('#height').val(res.data.height);
                pid = res.data.id;
                if(res.data.pictures.length > 0){
                    $('#example').css('display', 'none');
                    for (var i = 0; i < res.data.pictures.length; i++) {
                        var img = '';
                        img += '<div class="img_container2"><img src="' +res.data.pictures[i].href+ '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                        $('#addPosition2').before(img);
                        imgs.push({href: res.data.pictures[i].key});
                    }
                }else{}
                //分类
                var categor = res.data.categoryId;
                for(var i = 0; i < $('#seleteCate option').length; i++){
                    if($('#seleteCate option').eq(i).attr('value') == categor){
                        $('#seleteCate option').eq(i).attr('selected', 'selected')
                    }
                }
                //颜色
                if (res.data.colors.length == 0) {
                } else {
                    for (var i = 0; i < res.data.colors.length; i++) {
                        var seletCol = '';
                        seletCol += '<option>'+res.data.colors[i]+'</option>';
                        $('#seleteColor').append(seletCol);
                    }
                }
                //尺寸
                if (res.data.sizes.length == 0) {
                } else {
                    for (var i = 0; i < res.data.sizes.length; i++) {
                        $('#size input[value='+res.data.sizes[i]+']').attr('checked', true);
                    }
                }
                //工序
                for(var i = 0; i < $('#workerSort .procedure-wrap').length; i++){
                    for(var j = 0; j < res.data.procedures.length; j++){
                        if($('#workerSort .procedure-wrap').eq(i).attr('value') == res.data.procedures[j].procedureId){
                            $('#workerSort .procedure-wrap').eq(i).find('input[type=checkbox]').attr('checked', 'checked');
                            $('#workerSort .procedure-wrap').eq(i).find('.wor').val(res.data.procedures[j].price);
                        }
                    }
                }
                //原材料
                var met;
                for(var i = 0; i < $('#rawmaterial .procedure-wrap').length; i++){
                    for(var j = 0; j < res.data.materials.length; j++){
                        if($('#rawmaterial .procedure-wrap').eq(i).attr('value') == res.data.materials[j].materialId){
                            $('#rawmaterial .procedure-wrap').eq(i).find('input[type=checkbox]').attr('checked', 'checked');
                            $('#rawmaterial .procedure-wrap').eq(i).find('.metan').val(res.data.materials[j].count);
                            $('#rawmaterial .procedure-wrap').eq(i).find('.metap').val(res.data.materials[j].price);
                            met = res.data.materials[j].unit;
                            for(k = 0;k < 9; k++){
                                if($('#rawmaterial .procedure-wrap').eq(i).find('option').eq(k).text() == res.data.materials[j].unit){
                                    $('#rawmaterial .procedure-wrap').eq(i).find('option').eq(k).attr('selected', 'selected')
                                }
                            }
                        }
                    }
                }
            } else {
                $('#example').css('display', '');
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            $('#example').css('display', '');
            layer.alert(err.message, {icon:0})
        }
    })
}
//提交信息
var procedureList;
var materialList;
$('#confirm').click(function () {
    if($('#outputPrice').val() == ''){
        layer.alert('请输入单价',{icon:0});
        return;
    }
    if($('#qty').val() == ''){
        layer.alert('请输入件数',{icon:0});
        return;
    }
    if($('#select').find("option:checked").attr("value") == ''){
        layer.alert('请选择客户',{icon:0});
        return;
    }
    if($('input[name=address]:checked').attr('class') == ''){
        layer.alert('请选择地址',{icon:0});
        return;
    }
    var index = layer.load(2, {shade: [0.6,'#000']});
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
        id: url.split('=')[1],
        orderNo: $('#orderNo').val(),
        insideOrderNo: $('#insideOrderNo').val(),
        customerId: $('#select').find("option:checked").attr("value"),
        customer: $('#conta').val(),
        customerPhone: $('#contaTel').val(),
        customerAddressId: $('input[name=address]:checked').attr('class'),
        deliveryTime: $('#placeOrder').val(),
        pid: pid,
        categoryName: $('#selectCate').find("option:checked").attr("class"),
        material: $('#material').val(),
        technics: $('#technics').val(),
        description: $('#description').val(),
        shoulder: $('#shoulder').val(),
        color: $('#seleteColor').find("option:checked").attr("value"),
        size: $('#seleteSize').find("option:checked").attr("value"),
        bust: $('#bust').val(),
        waist: $('#waist').val(),
        hipline: $('#hipline').val(),
        height: $('#height').val(),
        weight: $('#weight').val(),
        throatheight: $('#throatheight').val(),
        price: $('#outputPrice').val(),
        qty: $('#qty').val(),
        remarks: $('#remark').val(),
        productPictureList: imgs,
        procedureList: procedureList,
        materialList: materialList
    };
    $.ajax({
        type: 'POST',
        url: '../suplierSalesOrder/saveOrder',
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
                    history.go('-1');
                },600)
            } else {
                layer.close(index);
                layer.alert(res.message, {icon:0});
            }
        },
        error: function (err) {
            layer.close(index);
            layer.alert(err.message, {icon:0})
        }
    })
});