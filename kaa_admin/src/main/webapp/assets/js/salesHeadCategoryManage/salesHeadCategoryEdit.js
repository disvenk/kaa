var url = window.location.href;
var id = url.split('=')[1];
showDetail();

//显示资料
function showDetail() {
    $.ajax({
        type: 'POST',
        url: '../salesHeadCategory/getSalesHeadCategory',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                //商品编号
                $('#name').val('' + res.data.name + '');
                //商品名称
                $('#description').val('' + res.data.description + '');
                //排序
                $('#sort').val('' + res.data.sort + '');
            }
        },
        error: function (err) {
            layer.alert(err)
        }
    });
}

$('#back').click(function () {
    location.href = '../salesHeadCategory/salesHeadCategoryManageHtml';
});

//提交编辑信息
$('#submit').click(function () {
    var name = $('#name').val();
    var description = $('#description').val();
    var sort = $('#sort').val();

    var data = {
        id:id,
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
            url:  '../salesHeadCategory/saveEditSalesHeadCategory',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                $('.spinner-wrap').show();
                if(res.stateCode == 100){
                    layer.alert('保存成功！', {icon: 0});
                    location.href = '../salesHeadCategory/salesHeadCategoryManageHtml';
                }else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }

});