
$.ajax({
    type: 'POST',
    url: '../suplierProduct/productList',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({pageNum:1}),
    success: function (res) {
        if(res.stateCode == 100){
            $('#goodsList').html("")
            for(var i = 0; i < res.data.length; i++){
                var div = ""
                div += '<div class="order-infos clearfix" id="'+res.data[i].id+'">'
                    +'      <img style="width: 82px;height: 82px" class="order-infos-model" src="'+res.data[i].href+'" alt="">'
                    +'      <div class="order-infos-item">'
                    +'          <span>货号：<span>'+res.data[i].pno+'</span></span><br>'
                    +'          <span class="order-infos-span">库存：<span>'+res.data[i].stock+'</span></span><br>'
                    +'          <span class="order-infos-span">价格：<span>￥'+res.data[i].price+'</span></span><br>'
                    +'      </div>'
                    +'      <div class="make-order">'
                    +'          <div class="make-order-btn" onclick="editPro('+res.data[i].id+')">编辑</div>'
                    +'          <div class="make-order-btn" onclick="delPro('+res.data[i].id+')">删除</div>'
                    +'      </div>'
                    +'  </div>'
                $('#goodsList').append(div);
            }
            //查看大图
            $('#goodsList .order-infos-model').click(function () {
                var href = $(this).attr('src');
                $('#myPictureModal').css('display', 'block');
                $('#myPictureModal').append('<img style="width: 60%;position: absolute;left: 0;bottom: 0;right: 0;top: 0;margin: auto" src="'+href+'" alt="">')
            })
        }else{
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message)
    }
})

function editPro(id) {
    location.href= '../suplierProduct/productHtml?id='+id;
}

function delPro(id) {
    $('#myModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../suplierProduct/removeProduct',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if (res.stateCode == 100) {
                    window.location.reload();
                } else {
                    $('#myModal').modal('hide');
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                $('#myModal').modal('hide');
                layer.msg(err.message)
            }
        })
    })
}

