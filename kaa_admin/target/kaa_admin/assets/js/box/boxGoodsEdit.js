var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
var imgs = [];
var ue = UE.getEditor('ueditor',{
    initialFrameWidth: '100%',
    initialStyle: 'p{line-height:0;margin:0}'
});
//时间戳转换
function timetrans(date) {
    var date = new Date(date);//如果date为10位不需要乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y + M + D + h + m + s;
}

//图片转化
function convertImgToBase64(url, callback, outputFormat) {
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}
function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: urlle + 'boxManage/boxManageDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#productCode').html('' + res.data[0].productCode + '');
                $('#goodsName').html('' + res.data[0].name + '');
                $('#updateDate').html('' + res.data[0].updateDate + '');
                $('#categoryName').html('' + res.data[0].categoryName + '');
                $('#status').html('' + res.data[0].statusName + '');
                if($('#stock').val == null || $('#stock').val == ''){
                    $('#stock').val(''+0+'');
                }else{
                    $('#stock').val('' + res.data[0].stock + '');
                }

                $('#orderCount').html('' + res.data[0].orderCount + ''+'件');
                for (var i = 0; i < res.data[0].picture.length; i++) {
                    var img = '';
                    img += '<div class="img_container1"><img src="' +res.data[0].picture[i].href+ '" class="goods-img" alt=""></div>';
                    $('#img .img_container').append('' + img + '');
                    imgs.push({href: res.data[0].picture[i].key});
                }

                //表格
                for (var i = 0; i < res.data[0].jsonSupplier.length; i++) {
                    var tr1 = '';
                    tr1 += ' <tr>' +
                        '<td class="table_color">' + res.data[0].jsonSupplier[i].color + '</td>' +
                        '<td class="table_size">' + res.data[0].jsonSupplier[i].size + '</td>' +
                        '<td class="table_offline">' + res.data[0].jsonSupplier[i].offlinePrice + '</td>' +
                        '</tr>';
                    $('#tbody').append(tr1);
                }

                // UE.getEditor('ueditor').setContent('' + res.data[0].description + '', false);
                // UE.getEditor('ueditor').addListener("ready", function () {
                //     UE.getEditor('ueditor').setContent(res.data[0].description);
                // });
                ue.ready(function() {
                    ue.setContent(res.data[0].description);
                    // ue.addListener("contentchange", function () {
                    //     $("#introdution").html(ue.getContent());
                    // })
                });
            } else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}
detail();

//提交编辑信息
$('#confirm').click(function () {
        var data = {
            id: id,
            stock: $('#stock').val(),
            description: ue.getContent()
        };
    $('.spinner-wrap').show();
    $.ajax({
        type: 'POST',
        url: urlle + 'boxManage/boxManageDetailEdit',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner-wrap').hide();
            if (res.stateCode == 100) {
                $('#myModal').modal('show');
                $('#sure').click(function () {
                    $('#myModal').modal('hide');
                    setTimeout(function () {
                        location.href = '../boxManage/boxManageHtml';
                    }, 1500)
                })
            } else {
                alert(res.message)
            }
        },
        error: function (err) {
            alert(err.message)
        }
    })
});