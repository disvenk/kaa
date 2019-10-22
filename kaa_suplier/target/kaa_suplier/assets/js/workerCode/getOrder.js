
var url = window.location.href;
var orderId = url.split('=')[1];
var orderNo;

//获取工单详情
$.ajax({
    type: 'POST',
    url: '../workerHome/supOrderDetail',
    contentType: 'application/json; charset=utf-8',
    data: JSON.stringify({id: orderId}),
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100){

            $("#userName").html(res.data.userName);
            if (res.data.procedures.length != 0) {
                $("#procedures").html('');
                $("#procedures").append('<option value="">请选择</option>');
            }
            for (var i=0; i<res.data.procedures.length; i++) {
                var opt = '<option value="'+res.data.procedures[i].procedureId+'">'+res.data.procedures[i].procedureName+'</option>';
                $("#procedures").append(opt);
            }
            orderNo = res.data.supOrderNo;
            $("#workerOrderNo").html(res.data.supOrderNo);
            $("#orderNo").html(res.data.supOrderNo);
            $("#insideOrderNo").html(res.data.insideOrderNo);
            $("#supOrderDate").html(res.data.supOrderDate);
            $("#deliveryDate").html(res.data.deliveryDate);
            $("#producedStatusName").html(res.data.producedStatusName);
            var array = [];
            for (var i=0; i<res.data.imgList.length; i++) {
                var img = '<img class="goods-img" src="'+res.data.imgList[i].href+'" alt="">'
                $("#img_box").append(img);
                array.push(res.data.imgList[i].href);
            }
            $("#img_box img").click(function () {
                var obj = {
                    urls : array,
                    current : $(this).attr('src')
                };
                previewImage.start(obj);
            });
        } else {
            layer.msg(res.message);
            window.setTimeout(function () {
                location.href = "../suplierHome/workerHomeHtml";
            },2000);
        }
    },
    error: function (err) {
        layer.msg(err)
    }
});



//确认领取
$('.sure-get').click(function () {
    if ($("#procedures").val() == '') {
        layer.msg("请选择工序");
        return;
    }

    //调领取接口
    $.ajax({
        type: 'POST',
        url: '../workerHome/saveSupOrderProductionLog',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({orderId: orderId, procedureId: $("#procedures").val()}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100){
                location.href='../suplierHome/getSuccessHtml?orderNo=' + orderNo;
            } else {
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err)
        }
    });


});

$('div.pinch-zoom').each(function () {
    new RTP.PinchZoom($(this), {});
});