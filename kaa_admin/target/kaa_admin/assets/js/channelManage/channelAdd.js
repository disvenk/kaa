var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');

//提交编辑信息
$('#confirm').click(function () {
    var name = $('#name').val();
    var zone = $('#zone').val();
    var address = $('#address').val();
    var contact = $('#contact').val();
    var telephone = $('#telephone').val();
    var type = $('#type').val();
    var data = {
        id:id,
        name: name,
        zone: zone,
        address: address,
        contact: contact,
        telephone: telephone,
        type: type,
    };

        $.ajax({
            type: 'POST',
            url: urlle + 'channel/saveChannel',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode != 100){
                    alert(res.message)
                }else{
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        setTimeout(function () {
                            location.href = '../channel/channelHtml';
                        }, 800)
                    })
                }

            },
            error: function (err) {
                alert(err.message)
            }
        })


});