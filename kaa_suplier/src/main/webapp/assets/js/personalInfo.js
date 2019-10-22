
$('#changeName').click(function () {
    location.href='../suplierHome/changeNameHtml';
});
$('#changePassword').click(function () {
    location.href='../suplierHome/changePasswordHtml';
});

$.ajax({
    type: 'POST',
    url: '../account/loginInfo',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if (res.stateCode == 100) {
            $("#name").html(res.data.name)
            $("#mobile").html(res.data.mobile)
            $("#icon").attr('src',res.data.icon);
        } else {
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message)
    }
});
