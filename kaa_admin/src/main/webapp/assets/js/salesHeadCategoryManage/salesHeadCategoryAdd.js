$('#back').click(function () {
    location.href = '../salesHeadCategory/salesHeadCategoryManageHtml';
});

//提交编辑信息
$('#submit').click(function () {
    var name = $('#name').val();
    var description = $('#description').val();
    var sort = $('#sort').val();

    var data = {
        name: name,
        description: description,
        sort: sort
    };

    if (name == '') {
        layer.alert('请输入名称！', {icon: 0});
    } else if (sort == '') {
        layer.alert('请输入序号！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: '../salesHeadCategory/addSalesHeadCategory',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').show();
                if (res.stateCode == 100) {
                    layer.alert('新增成功！', {icon: 0});
                    location.href = '../salesHeadCategory/salesHeadCategoryManageHtml';
                } else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }


});