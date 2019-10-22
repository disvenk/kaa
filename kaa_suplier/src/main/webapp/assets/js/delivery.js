function delivery(id) {
    $.ajax({
        type: 'POST',
        url: '../suplierOrder/supOrderDeliveryLogList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#myModalInfo').html('');
                for (var i = 0; i < res.data.length; i++) {
                    var div = ""
                    div += ' <div class="modal-info">\n' +
                        '                    <span class="modal-info-item w40">' + res.data[i].createTime + '</span>\n' +
                        '                    <span class="modal-info-item w30">' + res.data[i].instruction + '</span>\n' +
                        '                    <span class="modal-info-item w30">' + res.data[i].action + '</span>\n' +
                        '                </div>'
                    $('#myModalInfo').append(div);
                }
            } else {
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message)
        }
    })
    $("#myModal").modal('show')
}



