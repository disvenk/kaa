$.ajax({
    type: 'POST',
    url:  '../announcementManage/findCmsList',
    dataType: 'json',
    success: function (res) {
        //分类
        for (var i = 0; i < res.data.length; i++) {
            var index = i+1;
            var Suplierscope = '';
            // Suplierscope += ' <div class="scroll'+index+'">' +
            // '                    <span>['+res.data[i].style+']</span><span class="marquee">'+res.data[i].title+'</span>' +
            // '                </div>' +
            // '                <div class="scrol'+index+'" style="display: none">' +
            // '                    <span onclick="cmsContent('+res.data[i].id+')">['+res.data[i].style+']</span><marquee class="marque" scrollamount="3" onmouseover=this.start() onmouseout=this.stop()>'+res.data[i].title+'</marquee>' +
            // '                </div>';
            Suplierscope += ' <div class="scroll">\n' +
                '                    <span>['+res.data[i].style+']</span><span data-toggle="tooltip" data-placement="bottom" title="'+res.data[i].title+'" onclick="cmsContent('+res.data[i].id+')">'+res.data[i].title+'</span>\n' +
                '                </div>';
            $('#cmsContent').append(Suplierscope);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});
// //公告跑马灯
// $("#cmsContent .marquee").mouseover(function () {
//     $(this).parent().css('display', 'none');
//     $(this).parent().next('div').css('display', 'block');
// });

