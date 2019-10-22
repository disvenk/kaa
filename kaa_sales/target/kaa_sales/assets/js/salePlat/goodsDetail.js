var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
//详情接口
$.ajax({
    type: 'POST',
    url: urlle + 'storeProductManage/storeProducManagetDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0; i < res.data[0].picture.length; i++){
                var img = '';
                img += '<img style="width: 120px;margin-right: 15px;height: 100px" src="'+res.data[0].picture[i].href+'" alt="">';
                $('#img_container').append(img);
            }
            if(res.data[0].platProductIdStatus == 0){
                $('#add').css('display', 'inline-block');
            }else{
                $('#beenAdd').css('display', 'inline-block');
            }
            $('#goodsName').html(res.data[0].name);
            $('#video').attr('src', ''+res.data[0].vedioUrl+'');
            $('#brand').html(res.data[0].brand);
            $('#category').html(res.data[0].categoryName);
            $('#pno').html(res.data[0].productCode);
            $('#price').html(res.data[0].price);
            $('#supplierDay').html(res.data[0].suplierDay);
            $('#updateDate').html(res.data[0].updateDate);
            $('#remark').html(res.data[0].remarks);
            $('#description').html(res.data[0].description);
            //表格
            for(var i = 0; i < res.data[0].jsonSupplier.length; i++){
                var tr = '';
                tr += ' <tr><td>'+res.data[0].jsonSupplier[i].color+'</td><td>'+res.data[0].jsonSupplier[i].size+'</td>'+
                    '<td>'+res.data[0].jsonSupplier[i].offlinePrice+'</td><td>'+res.data[0].jsonSupplier[i].stock+'</td></tr>';
                $('#tbody').append(tr);
            }
        }else{
            alert(res.message);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
$('#back').click(function () {
    location.href = '../storeProductManage/goodsManageFromStoreHtml';
});
$('#add').click(function () {
    $.ajax({
        type: 'POST',
        url: urlle + 'storeProductManage/insertStoreProductManage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
           if(res.stateCode == 100){
               $('#myModal').modal('show')
           }else{
               alert(res.message);
           }
        },
        error: function (err) {
            alert(err.message);
        }
    });
});
$('#sure').click(function () {
    $('#myModal').modal('hide');
    location.href = '../storeProductManage/goodsManageFromStoreHtml';
})