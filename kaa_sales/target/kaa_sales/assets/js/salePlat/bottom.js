var bottom =
    // '\n' +
    // '    <ul id="J_NavContent" class="dl-tab-conten">\n' +
    // '\n' +
    // '    </ul>\n' +
    '    <div class="footer_">\n' +
    '        <div class="footer-content">\n' +
    '            <div>\n' +
    '                <h4>新手入门</h4>\n' +
    '                <ul>\n' +
    '                    <li onclick="refresh(1)">登录注册</li>\n' +
    '                    <li onclick="refresh(2)">选货采购</li>\n' +
    '                    <li onclick="refresh(3)">下单流程</li>\n' +
    '                    <li onclick="refresh(4)">线下入口</li>\n' +
    '                </ul>\n' +
    '            </div>\n' +
    '            <div>\n' +
    '                <h4>会员服务</h4>\n' +
    '                <ul>\n' +
    '                    <li onclick="toBox()">申请寄样</li>\n' +
                 ' <li onclick="refresh(5)">合一盒子</li>'+
    '                </ul>\n' +
    '            </div>\n' +
    '            <div>\n' +
    '                <h4>合一保障</h4>\n' +
    '                <ul>\n' +
    '                    <li onclick="refresh(6)">交易售后</li>\n' +
    '                </ul>\n' +
    '            </div>\n' +
    '            <div>\n' +
    '                <h4>门店入驻</h4>\n' +
    '                <ul>\n' +
    '                    <li onclick="refresh(7)">入驻说明</li>\n' +
    '                </ul>\n' +
    '            </div>\n' +
    '            <div style="margin-right: 0">\n' +
    '                <h4 style="text-align: right">官方微信公众号</h4>\n' +
    '                <ul>\n' +
    '                    <li style="text-align: right;padding-right: 12px">\n' +
    '                        <img id="code" src="../assets/img/salePlat/code.png" alt="">\n' +
    '                    </li>\n' +
    '                </ul>\n' +
    '            </div>\n' +
    '        </div>\n' +
    '        <div class="clear"></div>\n' +
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