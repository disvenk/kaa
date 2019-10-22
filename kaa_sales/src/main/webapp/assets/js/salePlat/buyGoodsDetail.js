//快递信息
function delivery(id) {
    $.ajax({
        type: 'POST',
        url: '../storeSuplierOrder/buyOrderDeliveryMessage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#deliveryModal').modal('show');
            $('#deliveryModal iframe').attr('src', '');
            $('#deliveryModal tbody').html('');
            if(res.stateCode == 100){
                    var ts = '';
                    ts = '<tr><td>'+res.data.createTime+'</td><td>'+res.data.deliveryCompanyName+'</td>' +
                        '<td>'+res.data.deliveryNo+'</td><td class="'+res.data.deliveryCom+'"  style="cursor: pointer" onclick="check($(this),'+res.data.deliveryNo+')">查看物流</td></tr>';
                    $('#deliveryModal tbody').append(ts);
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
//获取快递信息
function check(_this, no) {
    $.ajax({
        type: 'POST',
        url: '../express/searchExpress',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({com: _this.attr('class'), nu: no}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#deliveryModal iframe').attr('src', res.data.result);
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
