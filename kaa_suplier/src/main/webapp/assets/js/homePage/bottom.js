var bottom =
    // '\n' +
    // '    <ul id="J_NavContent" class="dl-tab-conten">\n' +
    // '\n' +
    // '    </ul>\n' +
    '    <div class="footer_">\n' +

    '        <div class="footer_footer">\n' +
    '            <div>\n' +
    '                <a onclick="location.href=\'../salesHome/aboutHtml\'">关于我们</a>\n' +
    '                <a onclick="location.href=\'../salesHome/privacyHtml\'">隐私保护</a>\n' +
    '                <a onclick="location.href=\'../salesHome/treatmentHtml\'"">用户协议</a>\n' +
    '            </div>\n' +
    '            <div style="margin-top: 25px">\n' +
    '                2017 苏州知行合一网络科技有限公司 All Rights Reserved.  版权所有 隐私申明   <a id="target" style="width: 220px" target="view_window" href="http://www.miitbeian.gov.cn">苏ICP备17002309号-2</a>' +
    '            </div>\n' +
    '        </div>\n' +
    '    </div>\n';
$("#bottom").html(bottom);
function toBox() {
    window.open('../salesHome/boxHtml');
}
// var offsetLeft6 = $('#target').offset().left + 190 + 'px';
// $('#cnzz_stat_icon_1271449140').css({'position': 'absolute', 'bottom': '35px', 'left': offsetLeft6});
// $(window).resize(function(){
//         offsetLeft6 = $('#target').offset().left + 190 + 'px';
//         $('#cnzz_stat_icon_1271449140').css({'position': 'absolute', 'bottom': '35px', 'left': offsetLeft6});
// });
function refresh(type) {
    switch (type == null ? 0 : type) {
        case 1: window.open("../guideHome/listHtml?#menu/login");break;
        case 2: window.open("../guideHome/listHtml?#menu/buy");break;
        case 3: window.open("../guideHome/listHtml?#menu/code");break;
        case 4: window.open("../guideHome/listHtml?#menu/offline");break;
        case 5: window.open("../guideHome/listHtml?#menu/contact");break;
        case 6: window.open("../guideHome/listHtml?#menu/insurance");break;
        case 7: window.open("../guideHome/listHtml?#menu/guiding");break;
        case 8: window.open("../guideHome/listHtml?#menu/servant");break;
        case 9: window.open("../guideHome/listHtml?#menu/about");break;
        case 10: window.open("../guideHome/listHtml?#menu/privacy");break;
        case 11: window.open("../guideHome/listHtml?#menu/treatment");break;
    }
}