var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
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
        for(var i = 0; i < res.data[0].productLabel.length; i++){
            var input = '';
            input += '<label style="margin-right: 16px"><input style="margin-right: 5px" name="Fruit" type="checkbox" value="'+res.data[0].productLabel[i].labelName+'" />'+res.data[0].productLabel[i].labelName+' </label>';
            $('#labelNmae').append(input);
            $('#labelNmae input').attr('disabled','disabled');
        };
        //销售渠道
        for(var i = 0; i < res.data[0].salesChannel.length; i++){
            var salesChannel = '';
            salesChannel += '<label style="margin-right: 16px"><input id="'+res.data[0].salesChannel[i].channelId+'" style="margin-right: 5px" name="saleschannelName" type="checkbox" value="'+res.data[0].salesChannel[i].channelName+'" />'+res.data[0].salesChannel[i].channelName+' </label>';
            $('#saleschannelName').append(salesChannel);
            $('#saleschannelName input').attr('disabled', 'disabled');
        }
    },
    error: function (err) {
        alert(err.message)
    }
})
//详情
$.ajax({
    type: 'POST',
    url: urlle + 'designerProduct/productDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if (res.stateCode == 100) {
            $('#productCode').html(''+res.data[0].productCode+'');
            $('#goodsName').html('' + res.data[0].name + '');
            for (var i = 0; i < res.data[0].picture.length; i++) {
                var img = '';
                img += '<img style="height: 100px;width: 100px;margin-right: 15px" src="' + res.data[0].picture[i].href + '" class="goods-img" alt="">';
                $('#img').append('' + img + '');
            }
            if(res.data[0].platProductIdStatus == 0){
                $('#add').css('display', 'inline-block');
            }else{
                $('#beenAdd').css('display', 'inline-block');
            }
            if (res.data[0].isDesigner == true) {
                res.data[0].isDesigner = '是'
            } else {
                res.data[0].isDesigner = '否'
            }
            ;
            //标签选中
            //商品标签
            if (res.data[0].labelName.length == 0) {
            } else {
                for (var i = 0; i < res.data[0].labelName.length; i++) {
                    $('#labelNmae input[value=' + res.data[0].labelName[i].labelName + ']').attr('checked', true);
                }
            }
            //销售渠道
            if (res.data[0].schannelName.length == 0) {
            } else {
                for (var i = 0; i < res.data[0].schannelName.length; i++) {
                    $('#saleschannelName input[value=' + res.data[0].schannelName[i].saleschannelName + ']').attr('checked', true);
                }
            }
            //颜色
            for (var i = 0; i < res.data[0].jsonSupplier.length; i++) {
                var color = '';
                color += '<span style="margin-right: 10px">' + res.data[0].jsonSupplier[i].color + '</span>';
                $('#color').append(color);
            }
            //尺寸
            for (var i = 0; i < res.data[0].jsonSupplier.length; i++) {
                var size = '';
                size += '<span style="margin-right: 10px">' + res.data[0].jsonSupplier[i].size + '</span>';
                $('#size').append(size);
            }
            $('#video').attr('src', '' + res.data[0].vedioUrl + '');
            $('#brand').html('' + res.data[0].brand + '');
            $('#categoryName').html('' + res.data[0].jsonSupplier[0].categoryName + '');
            $('#pno').html('' + res.data[0].pno + '');
            $('#productSupplierName').html('' + res.data[0].productSupplierName + '');
            $('#suplierDay').html('' + res.data[0].suplierDay + '');
            $('#price').html('' + res.data[0].price + '');
            $('#material').html('' + res.data[0].material + '');
            $('#isDesigner').html('' + res.data[0].isDesigner + '');
            $('#remark').html('' + res.data[0].remark + '');
            $('#description').html('' + res.data[0].description + '');
            //表格
            for (var i = 0; i < res.data[0].jsonSupplier.length; i++) {
                var tr = '';
                tr += '<tr><td>' + res.data[0].jsonSupplier[i].color + '</td>' +
                    '<td>' + res.data[0].jsonSupplier[i].size + '</td><td>' + res.data[0].jsonSupplier[i].stock + '</td>' +
                    '<td>' + res.data[0].jsonSupplier[i].priceNormal + '</td><td>' + res.data[0].jsonSupplier[i].onlinePrice + '</td>' +
                    '<td>' + res.data[0].jsonSupplier[i].offlinePrice + '</td><td>' + res.data[0].jsonSupplier[i].categoryRemark + '</td></tr>';
                $('#tbody').append(tr);
            }
        } else {
            alert(res.message);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
$('#add').click(function () {
    $.ajax({
        type: 'POST',
        url: urlle + 'designerProduct/insertDesignerProducManage',
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
    location.href = '../designerProduct/designerGoodsHtml';
})
$('#goBack').click(function () {
    location.href = '../designerProduct/designerManageFromStoreHtml';
});