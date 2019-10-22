//本地上传图片
document.getElementById("addPicture").addEventListener("change",function(e){
        var files =this.files;
        var img = new Image();
        var reader =new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload =function(e){
            var dx =(e.total/1024)/1024;
            if(dx>=2){
                alert("文件大小大于2M");
                return;
            }
            img.src =this.result;
            img.style.width = '280px';
            img.style.height ="130px";
            $('#photo1').css('display', 'none');
            $('#photo2').css('display', 'block');
            $('#photo2 span').css('display', 'block');
            $('#photo2 > img').attr('src', this.result);
            $('.delete_img').click(function () {
                $('#photo1').css('display', 'block');
                $('#photo2').css('display', 'none');
                $('#photo2 span').css('display', 'none');
                $('#photo2 > img').attr('src', '');
            })
        }
});
$('#other').bind('input propertychange', function(){
    var value = $('#other').val();
    $('#others').val(value)
});
//提交
$("#commit").click(function () {
    var sex = $('input:radio[name="sex"]:checked').val();
    var modal = '';
    $.each($('input:checkbox[name="modal"]:checked'), function () {
        modal += $(this).val() + ',';
    });
    var scope = '';
    $.each($('input:checkbox[name="cateGory"]:checked'),function(){
        scope += $(this).val() + ',';
    });
    if($('#others').attr('checked')){
        scope += $("#others").val();
    }
    var data = {
        name: $("#name").val(),
        phone: $("#phone").val(),
        sex: sex,
        personID: $("#idCard").val(),
        companyName: $("#companyName").val(),
        address: $("#companyPosition").val(),
        openYears: $("#companyOld").val(),
        scope: scope,
        scopeType: false,
        smith: $("#turner").val(),
        sewer: $("#croper").val(),
        editer: $("#version").val(),
        modelSet: modal,
        description: $("#textarea").val(),
        qualifications: $('#photo2 img').attr('src')
    };
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(data.companyName == ''){
        layer.alert('公司名称不能为空', {icon: 0});
    }else if(data.address == ''){
        layer.alert('工厂地址不能为空', {icon: 0});
    }else if(data.scope == ''){
        layer.alert('请选择主营业务', {icon: 0});
    }else if(data.smith == ''){
        layer.alert('车工人数不能为空', {icon: 0});
    }else if(data.sewer == ''){
        layer.alert('裁剪人数不能为空', {icon: 0});
    }else if(data.sex == '' ||data.sex == undefined){
        layer.alert('请选择性别', {icon: 0});
    }else if(data.personID == '' || !reg.test(data.personID)){
        layer.alert('身份证号错误', {icon: 0});
    }else if(data.phone == '' || !/^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/.test(data.phone)){
        layer.alert('手机号不能为空', {icon: 0});
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
                    $.ajax({
                        type: 'POST',
                        url: '../verify/sendMessage',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({status:3,name:data.name})
                    });
                    location.href = '../homePage/reviewHtml';
                } else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon:0})
            }
        })
    }
});
function keyLogin(){
    if (event.keyCode==13)  //回车键的键值为13
        $('#commit').click(); //调用登录按钮的登录事件
}
$('#container > .logo img').click(function () {
    location.href = '../homePage/loginHtml';
});