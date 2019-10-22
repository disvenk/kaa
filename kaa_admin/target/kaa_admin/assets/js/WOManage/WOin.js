var url = window.location.href;
var type = url.split('=')[1];
$('#wo1').focus();
function keyLogin(){
    //回车键的键值为13
    var wo1 = $('#wo1').val();
    var wo2 = $('#wo2').val();
    if (event.keyCode==13) {
        if(wo1 == ''){
            $('#wo1').focus();
        }else{
            $('#wo1').blur();
            $('#wo2').focus();
            if(wo2 == ''){
                $('#wo2').focus();
            }else if(wo2 != wo1){
                $('#fail').html('工单号不一致，请重新输入');
                $('#fail').css('color', 'red');
                $('#wo2').focus();
            }else{
                $('#fail').html('');
                $.ajax({
                    type: 'POST',
                    url: '../supOrderOperation/selectOrderTestDetail',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({orderNo: wo2,productStatus:7}),
                    success: function (res) {
                        if(res.stateCode == 100){
                            $('#fail').html('');
                            $('#orderNo').html(res.data[0].orderNo);
                            $('#color').html(res.data[0].color);
                            $('#supOrderDate').html(res.data[0].supOrderDate);
                            $('#weight').html(res.data[0].weight);
                            $('#description').html(res.data[0].description);
                            $('#suplierName').html(res.data[0].suplierName);
                            $('#throatheight').html(res.data[0].throatheight);
                            $('#hipline').html(res.data[0].hipline);
                            $('#productCode').html(res.data[0].productCode);
                            $('#size').html(res.data[0].size);
                            $('#material').html(res.data[0].material);
                            $('#suplierOrderNo').html(res.data[0].suplierOrderNo);
                            $('#supplierProductCode').html(res.data[0].supplierProductCode);
                            $('#bust').html(res.data[0].bust);
                            $('#waist').html(res.data[0].waist);
                            $('#technics').html(res.data[0].technics);
                            $('#deliveryDate').html(res.data[0].deliveryDate);
                            $('#shoulder').html(res.data[0].shoulder);
                            $('#code').JsBarcode(res.data[0].orderNo);
                            $('#imgContainer').html('');
                            for(var i = 0; i < res.data[0].pictureList.length; i++){
                                var img = '';
                                img += '<img class="img-detail" src="'+res.data[0].pictureList[i].href+'" alt="">';
                                $('#imgContainer').append(img);
                            }
                        }else{
                            $('#fail').html(res.message);
                            $('#fail').css('color', 'red');
                        }
                    },error: function (err) {
                        $('#fail').html(err.message);
                        $('#fail').css('color', 'red');
                    }
                })
            }
        }
    }else{
        return;
    }
}
//入库通过
function success() {
    $.ajax({
        type: 'POST',
        url: '../supOrderOperation/saveSupOrderStorage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({orderNoList: [{orderNo: $('#orderNo').html()}]}),
        success: function (res) {
            $('#wo1').val('');
            $('#wo2').val('');
            $('#wo1').focus();
            $('#wo2').blur();
            if(res.stateCode ==100){
                $('#fail').html('入库通过');
                $('#fail').css('color', 'rgb(23,157,147)');
            }else{
                $('#fail').html(res.message);
                $('#fail').css('color', 'red');
            }
        },error: function (err) {
            $('#fail').html(err.message);
            $('#fail').css('color', 'red');
        }
    })
}
$('#download').click(function () {
    if($('#fail').html() == ''){
        success();
    }else {
        $('#fail').html('请添加工单号');
        $('#fail').css('color', 'red');
    }
});
//入库不通过
function fail() {
    $.ajax({
        type: 'POST',
        url: '../supOrderOperation/saveSupOrderStorageFail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({orderNoList: [{orderNo: $('#orderNo').html()}]}),
        success: function (res) {
            $('#wo1').val('');
            $('#wo2').val('');
            $('#wo1').focus();
            $('#wo2').blur();
            if(res.stateCode ==100){
                $('#fail').html('入库不通过');
                $('#fail').css('color', 'rgb(23,157,147)');
            }else{
                $('#fail').html(res.message);
                $('#fail').css('color', 'red');
            }
        },error: function (err) {
            $('#fail').html(err.message);
            $('#fail').css('color', 'red');
        }
    })
}
$('#back').click(function () {
    if($('#fail').html() == ''){
        fail();
    }else {
        $('#fail').html('请添加工单号');
        $('#fail').css('color', 'red');
    }
});