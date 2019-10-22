//上传图片
var url = window.location.href.split('#')[0];
var headImgUrl = '';
$.ajax({
    type: 'GET',
    url: '../suplierHome/getshareinfo?url='+url,
    dataType: 'json',
    async: false,
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        'Authorization': 'Basic '
    },
    success: function(res){
        wx.config({
            debug: false, // 开启调试模式,调用的所有api的返回值会在客户端layer.msg出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: res.data.appid, // 必填，公众号的唯一标识
            timestamp: res.data.timestamp, // 必填，生成签名的时间戳
            nonceStr: res.data.noncestr, // 必填，生成签名的随机串
            signature: res.data.signature,// 必填，签名
            jsApiList: ['chooseImage','uploadImage','downloadImage', 'getLocalImgData'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
        });
        wx.ready(function(){
            $('#camera').click(function(){
                var img = '';
                if(/android/i.test(navigator.userAgent)) {
                    wx.chooseImage({
                        count: 1, // 默认9
                        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                        sourceType: ['camera','album'], // 可以指定来源是相册还是相机，默认二者都有
                        success: function (res) {
                            img = res.localIds[0]; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                            $('#photo').attr('src', img);
                            wx.uploadImage({
                                localId: img, // 需要上传的图片的本地ID，由chooseImage接口获得
                                isShowProgressTips: 0, // 默认为1，显示进度提示
                                success: function (res) {
                                    headImgUrl = res.serverId; // 返回图片的服务器端ID
                                }
                            });
                        }
                    });
                }
                if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
                    wx.chooseImage({
                        count: 1, // 默认9
                        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                        sourceType: ['camera','album'], // 可以指定来源是相册还是相机，默认二者都有
                        success: function (res) {
                            img = res.localIds[0]; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                            wx.getLocalImgData({
                                localId: img, // 图片的localID
                                success: function (res) {
                                    localData = res.localData; // localData是图片的base64数据，可以用img标签显示
                                    $('#photo').attr('src', localData);
                                }
                            });
                            wx.uploadImage({
                                localId: img, // 需要上传的图片的本地ID，由chooseImage接口获得
                                isShowProgressTips: 0, // 默认为1，显示进度提示
                                success: function (res) {
                                    headImgUrl = res.serverId; // 返回图片的服务器端ID
                                }
                            });
                        }
                    });
                }
            })
        });
        wx.error(function(res){
            layer.msg('网络错误');
        });
    },
    error:function (err) {
        layer.msg('配置错误');
    }
});
//删除
$('#delete').click(function () {
    $('#photo').attr('src', '');
    headImgUrl = '';
});
//提交
$("#btnSave").click(function () {
    var sex = $('input:radio[name="sex"]:checked').val();
    var modelSet = $('input:radio[name="modelSet"]:checked').val();
    var scope = '';
    $.each($('input:checkbox:checked'),function(){
        scope += $(this).val() + ',';
    });
    scope += $("#rests").val();
   var data = {
        name: $("#name").val(),
        phone: $("#phone").val(),
        sex: sex,
        personID: $("#personID").val(),
        companyName: $("#companyName").val(),
        address: $("#address").val(),
        openYears: $("#openYears").val(),
        scope: scope,
        smith: $("#smith").val(),
        sewer: $("#sewer").val(),
        editer: $("#editer").val(),
        modelSet: modelSet,
        description: $("#description").val(),
        qualifications: headImgUrl
    };
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(data.companyName == ''){
        layer.msg('公司名称不能为空');
    }else if(data.address == ''){
        layer.msg('工厂地址不能为空');
    }else if(data.scope == ''){
        layer.msg('请选择主营业务');
    }else if(data.smith == ''){
        layer.msg('车工人数不能为空');
    }else if(data.sewer == ''){
        layer.msg('裁剪人数不能为空');
    }else if(data.modelSet == '' || data.modelSet == undefined){
        layer.msg('请选择模架');
    }else if(data.sex == '' ||data.sex == undefined){
        layer.msg('请选择性别');
    }else if(data.personID == '' || !reg.test(data.personID)){
        layer.msg('身份证号错误');
    }else if(data.phone == '' || !/^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/.test(data.phone)){
        layer.msg('手机号不能为空');
    }else {
        $.ajax({
            type: 'POST',
            url: '../suplierHome/joinSuplier',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if (res.stateCode == 100) {
                    location.href='../suplierHome/auditHtml';
                } else {
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.msg(err.message)
            }
        })
    }
})
