var url = window.location.href;
var id;
var pid;
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
                    workerSort += '<span class="procedure-wrap" data="'+res.data[i].name+'" value="'+res.data[i].id+'"><input disabled="disabled" type="checkbox" name="worksort">'+res.data[i].name+'<span>' +
                        '<input class="procedure-input wor" type="number" placeholder="工价" readonly value="'+res.data[i].price+'"><select disabled="disabled"><option value="">元</option></select></span></span>';
                    $('#workerSort > div').append(workerSort);
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
                    rawmaterial += '<span data="'+res.data[i].name+'" value="'+res.data[i].id+'" class="procedure-wrap"><input disabled="disabled" type="checkbox">'+res.data[i].name+'<span><input readonly class="procedure-input wor metan" type="number" placeholder="数量">' +
                        '<select disabled="disabled" class="unit"><option value="">米</option><option value="">码</option><option value="">个</option>' +
                        '<option value="">包</option><option value="">条</option><option value="">件</option>' +
                        '<option value="">对</option><option value="">颗</option><option value="">粒</option></select></span><br><span style="position: relative;left: 65px;top: -12px;"><input readonly class="procedure-input wor metap" type="number" placeholder="单价"><select disabled="disabled"><option value="">元</option></select></span></span>';
                    $('#rawmaterial > div').append(rawmaterial);
                }
            }else{}
        }else{
            layer.alert(res.message, {icon: 0});
        }
    },error: function (err) {
        layer.alert(err.message, {icon: 0});
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
                        address += '<label class="diy_label" style="color: #999"><input disabled="disabled" class="'+res.data[i].id+'" style="margin-right: 6px" name="address" type="radio"/><span style="margin-right: 5px">'+res.data[i].provinceName+'</span>'+
                            '<span style="margin-right: 5px">'+ res.data[i].cityName+'</span><span style="margin-right: 5px">'+ res.data[i].zoneName +'</span>'+
                            '<span style="margin-right: 5px">'+res.data[i].address1+'</span>(<span style="margin-right: 5px">'+res.data[i].receiver +'</span>收）<span>'+ res.data[i].mobile +'</span></label></br>';
                        $('#address').append(address);
                        if(addressId == $('#address input[type=radio]').eq(i).attr('class')){
                            $('#address input[type=radio]').eq(i).attr('checked', 'checked');
                        }else{
                            $("#address input[type=radio]").eq(0).attr('checked', true);
                        }
                    }
                }else{
                }
            }else{
                layer.alert(res.message, {icon:0})
                $('#address').html('');
            }
        },error: function (err) {
            layer.alert(err.message, {icon:0})
            $('#address').html('');
        }
    });
}
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
                for (var i = 0; i < res.data.pictures.length; i++) {
                    var img = '';
                    img += '<div class="img_container2"><img src="' +res.data.pictures[i].href+ '" class="goods-img" alt=""></div>';
                    $('#img2').append(img);
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
});