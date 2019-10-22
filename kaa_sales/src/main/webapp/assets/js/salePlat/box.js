var nums = 0;
var pageNum = 1;
//商品列表
function list(pageNum) {
    $.ajax({
        type: 'POST',
        url:  '../boxHome/boxProductList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8'
        },
        data: JSON.stringify({pageNum: pageNum}),
        success: function (res) {
            if(res.stateCode == 100){
                if(res.pageNum * res.pageSize > res.total || res.pageNum * res.pageSize == res.total){
                    $('.box-more').css('display', 'none');
                }else{
                    $('.box-more').css('display', 'block');
                }
                if(res.data.length == 0){
                }else{
                    for (var i = 0; i < res.data.length; i++) {
                        var list = '';
                        list += '   <li class="box-product-item"><div class="product-item-modal" onclick="detail('+res.data[i].id+')"></div>' +
                            '<div  value="'+res.data[i].id+'" onclick="select($(this))" class="add_on" style="display: none;position: absolute;width: 260px;height: 40px;line-height: 40px;text-align: center;color: #fff;background-color: rgb(66,159,214);left: 10px;top: 232px;z-index: 1000">放入盒子</div>' +
                            '<img class="box-model" onclick="detail('+res.data[i].id+')" src="'+res.data[i].href+'" alt="">\n' +
                            '<div class="box-product-intro">'+res.data[i].name+'' +
                            '</div></li>';
                        $('.box-product-items').append(list);
                    }
                    $('.box-product-items li').mouseover(function () {
                        $(this).find('.add_on').css('display', 'block');
                    });
                    $('.box-product-items li').mouseleave(function () {
                        $(this).find('.add_on').css('display', 'none');
                    });
                }
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    });
}
list(pageNum);
$('.box-more').click(function () {
    pageNum++;
    list(pageNum);
});
function select(it) {
        if(it.html() == '移出盒子'){
                it.parent().find('.product-item-modal').hide();
                it.css('background-color', 'rgb(66,159,214)');
                it.removeClass('addBox');
                it.html('放入盒子');
                nums--;
        }else{
            if(nums > 2){
                layer.alert('最多选择3个商品', {icon:0});
            }else{
                $.ajax({
                    type: 'POST',
                    url: '../boxHome/boxOrderCount',
                    contentType: 'application/json; charset=utf-8',
                    dataType: 'json',
                    headers: {
                        'Accept': 'application/json; charset=utf-8'
                    },
                    data: JSON.stringify({id: it.attr('value')}),
                    success: function (res) {
                        if(res.stateCode == 100){
                            it.parent().find('.product-item-modal').show();
                            it.css('background-color', '#999');
                            it.addClass('addBox');
                            it.html('移出盒子');
                            nums++;
                        }else {
                            layer.alert(res.message, {icon:0});
                        }
                    },
                    error: function (err) {
                        layer.alert(err.message, {icon:0});
                    }
                });
            }
        }
}
// 商品详情模态窗
function detail(id) {
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
                 layer.open({
                     title: false,
                     btn:false,
                     area: ['978px', '800px'],
                     offset: '120px',
                     scrollbar: false,
                     content: '<div class="info-wrap">\n' +
                     '    <div class="info-head clearfix">\n' +
                     '        <img class="info-head-img" src="'+res.data.href+'" alt="">\n' +
                     '        <div class="info-head-right">\n' +
                     '            <div class="info-right-topic">\n' +
                     '                <span class="product-name">'+res.data.name+'</span>\n' +
                     '                <span class="product-num">'+res.data.productCode+'</span>\n' +
                     '            </div>\n' +
                     '            <div class="product-color"> </div>\n' +
                     '            <div class="product-size">\n' +
                     '            </div>\n' +
                     '        </div>\n' +
                     '    </div>\n' +
                     '    <div class="product-detail">\n' +
                     '        <span class="product-detail-topic">商品详情</span>\n' +
                     '        <div class="product-detail-area">\n' +
                     '        </div>\n' +
                     '    </div>\n' +
                     '</div>'
                 });
                 $('.product-color').html('');
                 $('.product-color').append('颜色');
                 for(var i = 0; i < res.data.colors.length; i++){
                     var color = '';
                     color += '<span class="product-color-option">'+res.data.colors[i]+'</span>';
                     $('.product-color').append(color);
                 }
                 $('.product-size').html('');
                 $('.product-size').append('尺寸');
                 for(var i = 0; i < res.data.sizes.length; i++){
                     var size = '';
                     size += '<span class="product-size-option">'+res.data.sizes[i]+'</span>';
                     $('.product-size').append(size);
                 }
                 $('.product-detail-area').html('');
                 $('.product-detail-area').html(res.data.description);
             }else{
                 layer.alert(res.message, {icon: 0})
             }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    });
}
function catchVip() {
    if(!token){
        layer.alert('请先登录',{icon:0});
        setTimeout(function () {
            location.href = '../salesHome/loginHtml';
        },2000)
    }else{
        $.ajax({
            type: 'POST',
            url:  '../boxHome/boxInfo',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            success: function (res) {
                if(res.stateCode == 100){
                    if(nums == 0){
                        layer.alert('请选择商品',{icon:0})
                    }else {
                        var ids = [];
                        for (var i = 0; i < $('.addBox').length; i++) {
                                ids.push($('.addBox').eq(i).attr('value'));
                        }
                        var price = res.data.needDeposit;
                        if(res.data.boxCheck){
                            location.href = '../salesHome/orderConfirmHtml?boxIds='+ids+'';
                        }else{
                            location.href='../salesHome/boxJoinHtml?boxIds='+ids+'';
                        }
                    }
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        });
    }
}
