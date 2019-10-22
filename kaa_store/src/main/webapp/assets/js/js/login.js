jQuery(function($) {
    $(document).on('click', '.toolbar a[data-target]', function(e) {
        e.preventDefault();
        var target = $(this).data('target');
        $('.widget-box.visible').removeClass('visible');//hide others
        $(target).addClass('visible');//show target
    });
});
$('#login').click(function () {
    var data = {
        usercode: $('#userName').val(),
        password: $('#password').val(),
        loginType: 0
    };
    $.ajax({
        type: 'POST',
        url: '/kaa_store/account/login',
        cache: false,
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
           sessionStorage.setItem("token",res.data.authToken);
                sessionStorage.setItem("userName",res.data.userName);
           location.href = '/kaa_store/storeHome/storeHomeHtml';
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    })
})