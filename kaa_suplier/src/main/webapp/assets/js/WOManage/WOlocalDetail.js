var url = window.location.href;
var id = url.split('=')[1];
var procedureNum;
if(sessionStorage.getItem('loginType') == 4){
    $('#type1').css('display', 'none')
    $('#type2').css('display', 'none')
}
$.ajax({
    type: 'POST',
    url: '../supOrderOffline/supOrderOffline',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            // console.log(res);
            $('#supOrderNo').html(res.data.supOrderNo);
            $('#insideOrderNo').html(res.data.insideOrderNo);
            $('#customer').html(res.data.customer);
            $('#customerPhone').html(res.data.customerPhone);
            $('#receiver').html(res.data.receiver);
            $('#mobile').html(res.data.mobile);
            $('#createTime').html(res.data.supOrderDate);
            $('#getDate').html(res.data.deliveryDate);
            $('#produceState').html(res.data.producedStatusName);
            $('#address').html(res.data.provinceName + res.data.cityName + res.data.zoneName + res.data.address);
            $('#remarks').html(res.data.remarks);
            $('#material').html(res.data.material);
            $('#technics').html(res.data.technics);
            $('#description').html(res.data.description);

            for(var i = 0; i< res.data.imgList.length; i++){
                var img = '';
                img += '<img src="'+res.data.imgList[i].href+'" style="width: 80px;height: 80px;margin-right: 10px;cursor:pointer;">';
                $('#img2').append(img)
            }
            $('#img2 > img').click(function () {
                $('#getModal').modal('show');
                $('#getModal .modal-body img').attr('src', $(this).attr('src'));
            });


            $('#pno').html(res.data.pno);
            $('#categoryName').html(res.data.categoryName);
            $('#color').html(res.data.color);
            $('#size').html(res.data.size);
            $('#throatheight').html(res.data.throatheight);
            $('#shoulder').html(res.data.shoulder);
            $('#bust').html(res.data.bust);
            $('#waist').html(res.data.waist);
            $('#hipline').html(res.data.hipline);
            $('#height').html(res.data.height);
            $('#weight').html(res.data.weight);
            $('#qty').html(res.data.qty);

            procedureNum = res.data.qty;
            procedureManage(res.data.procedures);
            $('#outputPrice').html(res.data.outputPrice);
            $('#code').JsBarcode(res.data.supOrderNo);

            for(var i = 0; i< res.data.materials.length; i++){
                var materials = '';
                materials += ''+res.data.materials[i].materialName+' | <span>'+res.data.materials[i].count+'</span><span>'+res.data.materials[i].unit+'</span><br>';
                $('#materials').append(materials)
            }

            // 查看二维码
            var qrcode = new QRCode(document.getElementById('qrCode'), {
                width : 150,//设置宽高
                height : 150
            });
            qrcode.makeCode("" +
                "http://f.heyizhizao.com/suplierHome/getOrderHtml?" +
                // "http://localhost:8080/kaa_suplier/suplierHome/getOrderHtml?" +
                "orderId=" + id);
            if (res.data.imgList.length > 0) {
                var img =
                    // '<img style="width: 50px;height: 50px;position: absolute;left: 72px;top:75px;right: 0px;bottom: 0px;border: 5px solid #fff" ' +
                    '<img style="width: 50px;height: 50px;position: absolute;top:-100px;left:50px;border: 2px solid #fff" ' +
                    'src="'+res.data.imgList[0].href+'" alt=""/>';
                $("#qrCode").append(img);
            }

        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
function procedureManage(procedures) {
    // 工序、工价维护
    $.ajax({
        type: 'POST',
        url: '../procedure/findProcedureListCombox',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success:function (res) {
            if(res.stateCode==100){
                var map = {};
                //订单工序
                for (var j=0; j<procedures.length; j++) {
                    var procedureId = procedures[j].procedureId
                    var price = procedures[j].price
                    var procedureName = procedures[j].procedureName
                    map[procedureId] = [price, procedureName];
                }

                for(var i = 0; i< res.data.length; i++){
                    var procedure = '';
                    if (map[res.data[i].id]) {
                        procedure += '<input  disabled="disabled" id="pro'+res.data[i].id+'" type="checkbox" checked="checked">'+map[res.data[i].id][1]+' | <span>'+map[res.data[i].id][0]+'</span>* '+procedureNum+'<br>';
                    } else {
                        procedure += '<input disabled="disabled" id="pro'+res.data[i].id+'" type="checkbox">'+res.data[i].name+' | <span>'+res.data[i].price+'</span>* '+procedureNum+'<br>';
                    }
                    $('#procedureList').append(procedure);
                }



            }else{
                layer.alert(res.message, {icon: 0})
            }
        },
        error: function (err) {
            alert(err.message);
        }
    })
}



//下载word
$("#download").click(function () {
    var url = "../supOrderOffline/exportWord?supOrderId=" + id;
    // window.open(url);
    window.location.href = (url);

})

