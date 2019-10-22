//加载颜色尺寸  ../base/getBaseDataList   parameterType-2/3
function getBaseDataList() {
    $.ajax({
        type: 'POST',
        url: '../base/getBaseDataList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parameterType: 2}),
        success: function (res) {
            if(res.stateCode == 100){
                var color = '';
                for (var i=0; i<res.data.length; i++) {
                    color += '<li class="goods-color-item" value="'+res.data[i].id+'">'+res.data[i].name+'</li>'
                }
                $(".color").append(color);
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
    $.ajax({
        type: 'POST',
        url: '../base/getBaseDataList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parameterType: 3}),
        success: function (res) {
            if(res.stateCode == 100){
                var size = '';
                for (var i=0; i<res.data.length; i++) {
                    size += '<li class="goods-size-item" value="'+res.data[i].id+'">'+res.data[i].name+'</li>'
                }
                $(".size").append(size);
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });

}
getBaseDataList();

//批量设定
$("#batchSave").click(function () {
    var price = $("#price").val();
    var stock = $("#stock").val();
    if (price) $(".price").val(price);
    if (stock) $(".stock").val(stock);
    $('#myModal').modal('hide');
})


