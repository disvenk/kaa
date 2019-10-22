var url = window.location.href;
var id = url.split('=')[1];
//详情
$.ajax({
    type: 'POST',
    url: '../supOrder/supOrderOnline',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            $('#supOrderNo').html(res.data.supOrderNo);
            $('#supplierName').html(res.data.supplierName);
            $('#supplierOrderNo').html(res.data.supplierOrderNo);
            $('#supplierPhone').html(res.data.supplierPhone);
            $('#deliveryDate').html(res.data.deliveryDate);
            $('#supOrderDate').html(res.data.supOrderDate);
            $('#expectsendDate').html(res.data.expectsendDate);
            $('#deliveryCompanyName').html(res.data.deliveryCompanyName);
            $('#deliveryNo').html(res.data.deliveryNo);
            var tr = '<tr><td>'+res.data.pno+'</td><td><img style="width: 60px;height: 60px" src="'+res.data.href+'" alt=""></td><td>'+res.data.categoryName+'</td><td>'+res.data.color+'</td><td>'+res.data.size+'</td>' +
                '<td>'+res.data.throatheight+'</td><td>'+res.data.shoulder+'</td><td>'+res.data.bust+'</td><td>'+res.data.waist+'</td><td>'+res.data.hipline+'</td>' +
                '<td>'+res.data.height+'</td><td>'+res.data.weight+'</td><td>'+res.data.other+'</td><td>'+res.data.qty+'</td>' +
                '<td>'+res.data.outputPrice+'</td></tr>';
            $('#tbody').append(tr);
            $('#material').html(res.data.material);
            $('#technics').html(res.data.technics);
            $('#description').html(res.data.description);
            for(var i = 0; i< res.data.imgList.length; i++){
                var img = '';
                img += '<img src="'+res.data.imgList[i].href+'" style="width: 80px;height: 80px;margin-right: 10px">';
                $('#img').append(img)
            }
            $('#imgcode').JsBarcode(res.data.supOrderNo);
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});


//下载word
$("#download").click(function () {
    var url = "../supOrder/exportWordOnline?supOrderId=" + id;
    // window.open(url);
    window.location.href = (url);

})