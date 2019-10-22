var url = window.location.href;
var id = url.split('=')[1];
$.ajax({
    type: 'POST',
    url: '../supOrder/supOrderOffline',
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
            $('#insideOrderNo').html(res.data.insideOrderNo);
            $('#customer').html(res.data.customer);
            $('#customerPhone').html(res.data.customerPhone);
            $('#receiver').html(res.data.receiver);
            $('#mobile').html(res.data.mobile);
            $('#address').html(res.data.provinceName + res.data.cityName + res.data.zoneName + res.data.address);
            $('#remarks').html(res.data.remarks);
            $('#material').html(res.data.material);
            $('#technics').html(res.data.technics);
            $('#description').html(res.data.description);
            if (res.data.imgList.length == 0) {
            } else {
                for (var i = 0; i < res.data.imgList.length; i++) {
                    var img = '';
                    img += '<img src="'+res.data.imgList[i].href+'" style="width: 120px;height: 120px;margin-right: 20px">';
                    $('#img2').append(img);
                }
            }
            var tr = '';
            tr = '<tr><td>'+res.data.pno+'</td><td>'+res.data.categoryName+'</td><td>'+res.data.color+'</td><td>'+res.data.size+'</td><td>'+res.data.throatheight+'</td>' +
                '<td>'+res.data.shoulder+'</td><td>'+res.data.bust+'</td><td>'+res.data.waist+'</td><td>'+res.data.hipline+'</td><td>'+res.data.height+'</td>' +
                '<td>'+res.data.weight+'</td><td>'+res.data.qty+'</td><td>'+res.data.outputPrice+'</td></tr>';
            $('#tbody').append(tr);
            $('#code').JsBarcode(res.data.supOrderNo);
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        alert(err.message);
    }
});

//下载word
$("#download").click(function () {
    var url = "../supOrder/exportWordOffline?supOrderId=" + id;
    // window.open(url);
    window.location.href = (url);

})