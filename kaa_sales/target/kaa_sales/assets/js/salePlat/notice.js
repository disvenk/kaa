var url = window.location.href;
var id = url.split('=')[1];

//回显详情
if (id) {
    $.ajax({
        type: 'POST',
        url: '../announcementManage/findCmsContent',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            if(res.stateCode == 100){

                $('#nitice1').html(res.data.title);
                $('#nitice2').html("时间："+res.data.updateDate);
                $('#nitice3').html(res.data.content);
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}