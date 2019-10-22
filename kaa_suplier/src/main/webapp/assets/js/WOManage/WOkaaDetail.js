var url = window.location.href;
var id = url.split('=')[1];
if(sessionStorage.getItem('loginType') == 4){
    $('#type1').css('display', 'none')
    $('#type2').css('display', 'none')
}
//详情
$.ajax({
    type: 'POST',
    url: '../supOrderOnline/supOrderOnline',
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
           $('#supOrderDate').html(res.data.supOrderDate);
            $('#deliveryDate').html(res.data.deliveryDate);
            $('#pno').html(res.data.pno);
            $('#code').html(res.data.productCode);
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
            $('#outputPrice').html(res.data.outputPrice);
            $('#material').html(res.data.material);
            $('#technics').html(res.data.technics);
            $('#description').html(res.data.description);
            for(var i = 0; i< res.data.imgList.length; i++){
                var img = '';
                img += '<img src="'+res.data.imgList[i].href+'" style="width: 80px;height: 80px;margin-right: 10px;cursor:pointer;">';
                $('#img').append(img)
            }
            $('#img > img').click(function () {
                $('#getModal').modal('show');
                $('#getModal .modal-body img').attr('src', $(this).attr('src'));
            });
            $('#imgcode').JsBarcode(res.data.supOrderNo);
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});

//图片放大
function imgDetail(src) {
    console.log(src)
}
//下载word
$("#download").click(function () {
    var url = "../supOrderOnline/exportWord?supOrderId=" + id;
    // window.open(url);
    window.location.href = (url);

});