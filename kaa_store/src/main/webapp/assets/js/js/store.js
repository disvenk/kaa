var type = '';
$('.sec').click(function () {
    $(this).siblings().removeClass('sec_active');
    $(this).addClass('sec_active');
    $('.sec img')[0].src = '../assets/img/hunsha_01.png';
    $('.sec img')[1].src = '../assets/img/lifu_01.png';
    $('.sec img')[2].src = '../assets/img/xiuhe_01.png';
    $('.sec img')[3].src = '../assets/img/xizhuang_01.png';
    var img = $(this).children()[0].src;
    var arr = img.split('0');
    $(this).children()[0].src = arr[0] + '02.png';
    // 刷新iframe
    document.all.iframeName.src = '/kaa_store/storeHome/indexHtml?type=2';
    // 向iframe传值
   type = 2
});

