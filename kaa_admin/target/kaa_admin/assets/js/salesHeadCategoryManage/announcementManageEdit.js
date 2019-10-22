var url = window.location.href;
var id = url.split('=')[1];
var ue = UE.getEditor('ueditor',{
    initialFrameWidth: '100%',
    initialStyle: 'p{line-height:0;margin:0}'
});
var urlle = sessionStorage.getItem('urllen');
//分类
$.ajax({
    type: 'POST',
    url: urlle + 'announcementManage/findCmsMenuList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        //分类
        for (var i = 0; i < res.data.length; i++) {
            var Suplierscope = '';
            Suplierscope += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            $('#cmsMenu').append(Suplierscope);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

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

                $('#name').val(res.data.title);
                // setTimeout(function () {
                //     UE.getEditor('ueditor').setContent(res.data.content);
                // },200)
                ue.ready(function() {
                    ue.setContent(res.data.content);
                });
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//提交编辑信息
var sss = true;
$('#submitEditor').click(function () {
    // $('.spinner').show();
    var title = $('#name').val();
    var menuId = $('#cmsMenu option:selected').attr('id');
    var content = ue.getContent();

    var data = {
        id:id,
        title: title,
        menuId: menuId,
        content: content,
    };
    if (title == '') {
        layer.alert('标题必填！', {icon: 0});
        return;
    }
    if(sss){
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'announcementManage/cmsContentUpdate',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                // $('.spinner').hide();
                $('.spinner-wrap').hide();
                if (res.stateCode == 100) {
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        setTimeout(function () {
                            location.href = '../announcementManage/announcementManageHtml';
                        }, 1500)
                    })
                } else {
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
});