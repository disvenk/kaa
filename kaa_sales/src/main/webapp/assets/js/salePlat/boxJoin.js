var url = window.location.href;
var array = url.split('=');
var ids = array[1];
function type() {
    $.ajax({
        type: 'POST',
        url: '../boxHome/boxTypeList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $('.join-detail-date').html('');
            $('.join-detail-date').append('会员类型');
            if (res.stateCode == 100) {
                $('.vip-deposit').html(res.data.needDeposit);
                for(var i = 0; i < res.data.typeList.length; i++){
                    var typeList = '';
                    typeList += '<span class="join-date-item" id="'+res.data.typeList[i].price+'" value="'+res.data.typeList[i].id+'">'+res.data.typeList[i].name+'</span>';
                    $('.join-detail-date').append(typeList);
                }
                // 选择套餐
                if(!res.data.isOnePay){
                    $('.join-date-item').eq(0).attr('data-toggle', 'tooltip');
                    $('.join-date-item').eq(0).attr('data-placement', 'top');
                    $('.join-date-item').eq(0).attr('title', res.data.message);
                    $('.join-date-item').eq(0).css('color', '#999');
                    $('.join-date-item').eq(1).addClass('border-pick');
                    $('.vip-cost').html($('.join-date-item').eq(1).attr('id'));
                    $('.red-price').html($('.join-date-item').eq(1).attr('id') - 0 + res.data.needDeposit);
                    $('.join-date-item').eq(0).hover(function () {
                        $('.join-date-item').eq(0).css('cursor', 'not-allowed');
                    })
                }else{
                    $('.join-date-item').eq(0).addClass('border-pick');
                    $('.vip-cost').html($('.join-date-item').eq(0).attr('id'));
                    $('.red-price').html($('.join-date-item').eq(0).attr('id') - 0 + res.data.needDeposit);
                }
                $('.join-date-item').click(function () {
                    if(!res.data.isOnePay){
                        if($('.join-date-item').eq(0).html() == $(this).html()){
                           $(this).attr('data-toggle', 'tooltip');
                            $(this).attr('data-placement', 'top');
                            $(this).attr('title', res.data.message);
                            $(this).hover(function () {
                                $(this).css('cursor', 'not-allowed');
                            })
                        }else{
                            $('.join-date-item').removeClass('border-pick');
                            $(this).addClass('border-pick');
                            $('.vip-cost').html($(this).attr('id'));
                            $('.red-price').html($(this).attr('id') - 0 + res.data.needDeposit);
                        }
                    }else{
                        $('.join-date-item').removeClass('border-pick');
                        $(this).addClass('border-pick');
                        $('.vip-cost').html($(this).attr('id'));
                        $('.red-price').html($(this).attr('id') - 0 + res.data.needDeposit);
                    }
                })

            } else {
                layer.alert(res.message,{icon:0});
            }
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    });
}
type();
//立即购买
$('.buy-btn').click(function () {
    //判断登录
    var token = sessionStorage.getItem('token');
    if (!token) {
        // alert("请先登录")
        // location.href = "../salesHome/loginHtml";

        layer.alert("请先登录", {icon:0})
        IntervalName = setInterval(function () {
            location.href = "../salesHome/loginHtml";
        },2000)
        return;
    }

    $.ajax({
        type: 'POST',
        url: '../boxHome/saveBoxPayOrder',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: $('.border-pick').attr('value')}),
        success: function (res) {
            if (res.stateCode == 100) {
                location.href='../payOrder/buyOrderPayHtml?id='+res.data.orderId+'&type=2'+'&boxIds='+ids+'';
            }else{
                layer.alert(res.message, {icon:0})
            }
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    })
});

$('.join-item').click(function () {
    $('.join-item').removeClass('active');
    $(this).addClass('active');
})

$('.join1').click(function () {
    $('.join-img1').css('display','block');
    $('.join-img2').css('display','none');
    $('.join-img3').css('display','none');
})

$('.join2').click(function () {
    $('.join-img1').css('display','none');
    $('.join-img2').css('display','block');
    $('.join-img3').css('display','none');
})

$('.join3').click(function () {
    $('.join-img1').css('display','none');
    $('.join-img2').css('display','none');
    $('.join-img3').css('display','block');
})