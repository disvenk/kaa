var url = window.location.href;
var id = url.split('=')[1];
$.ajax({
    type: 'POST',
    url:  '../boxHome/boxProductDetail',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    headers: {
        'Accept': 'application/json; charset=utf-8'
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            console.log(res)
            $('.right-name').html(res.data.name);
            $('.right-number').html(res.data.productCode);
            $('.product-info-img').attr('src', res.data.href);
            $('.product-color').html('');
            $('.product-color').append('颜色');
                var color = '';
                color = '<span class="product-color-option">'+res.data.colors[0]+'</span>';
                $('.product-color').append(color);
            $('.product-size').html('');
            $('.product-size').append('尺寸');
            for(var i = 0; i < res.data.sizes.length; i++){
                var size = '';
                size += '<span class="product-size-option">'+res.data.sizes[i]+'</span>';
                $('.product-size').append(size);
            }
            $('.product-detail-show').html('');
            $('.product-detail-show').html(res.data.description);
        }else{
            layer.msg(res.message)
        }
    },
    error: function (err) {
        layer.msg(err.message)
    }
});