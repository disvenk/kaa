var url = window.location.href;
var id = url.split('=')[1];
showDetail();

//显示门店商品明细
function showDetail() {
    $.ajax({
        type: 'POST',
        url: '../store/storeProductDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            //console.log(res)
            if (res.stateCode == 100) {

                //商品编号
                $('#productCode').html('' + res.data.productCode + '');
                //商品名称
                $('#productName').html('' + res.data.productName + '');
                //商品图片
                for (var i = 0; i < res.data.productPictureList.length; i++) {
                    var imgUrl = '';
                    imgUrl += '<img style="margin-right: 10px;width:120px;" src="' + res.data.productPictureList[i].pictureUrl + '" class="goods-img" alt="">'
                    $('#picturediv').append(imgUrl);
                }
                //品牌
                $('#brand').html('' + res.data.brand + '');
                //分类
                $('#productCategory').html('' + res.data.productCategory + '');
                //最低零售价
                $('#salesPrice').html('' + res.data.salesPrice + '');
                //货号
                $('#pno').html('' + res.data.pno + '');
                //单价供货周期
                $('#suplierDay').html('100以内 ' + '' + res.data.suplierDay + '' + ' 天');
                //更新日期
                $('#updateDate').html('' + res.data.updateDate + '');
                //所属门店
                $('#storeName').html('' + res.data.storeName + '');
                //商品规格表
                for (var i = 0; i < res.data.productSpecList.length; i++) {
                    var tr = '';
                    $('#tbspec').html('');
                    for (var i = 0; i < res.data.productSpecList.length; i++) {
                        tr += ' <tr class="' + res.data.productSpecList[i].gid + '">' +
                            '<td>' + res.data.productSpecList[i].color + '</td>' +
                            '<td>' + res.data.productSpecList[i].size + '</td>' +
                            '<td>' + res.data.productSpecList[i].price + '</td>' +
                            '<td>' + res.data.productSpecList[i].stock + '</td>' +
                            '</tr>';
                    }
                    $('#tbspec').append(tr);
                }
                //商品详情
                $('#productdetail').html(res.data.description);
            }
        },
        error: function (err) {
            alert(err)
        }
    });
}
$('#back').click(function () {
    location.href = '../store/storeGoodsManageHtml';
});