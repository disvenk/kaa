var url = window.location.href;
var type = url.split('=')[1];
if(type == 1){
    $('.step1').css('display','block');
    $('.step2').css('display','none');
    $('.join-part1').css('display','block');
    $('.join-part2').css('display','none');
}else if(type == 2){
    $('.step1').css('display','none');
    $('.step2').css('display','block');
    $('.join-part1').css('display','none');
    $('.join-part2').css('display','block');
}//分类
// $.ajax({
//     type: 'POST',
//     url: '../base/getPlaProductCategoryListAll',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         for(var i = 0; i < res.data.length; i++){
//             for(var j = 0; j < res.data[i].categoryList.length; j++){
//                 var a = '';
//                 a += '<a value="'+res.data[i].categoryList[j].id+'" href="#">'+res.data[i].categoryList[j].name+'</a>';
//                 $('#header img').after(a);
//             }
//         }
//         //一级分类跳转
//         $('#header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
//         });
//     },
//     error: function (err) {
//         layer.alert(err.message);
//     }
// });
//分类
// $.ajax({
//     type: 'POST',
//     url: '../base/getPlaProductCategoryListAll',
//     dataType: 'json',
//     contentType: 'application/json; charset=utf-8',
//     headers: {
//         'Accept': 'application/json; charset=utf-8',
//         'Authorization': 'Basic ' + sessionStorage.getItem('token')
//     },
//     success: function (res) {
//         for(var i = 0; i < res.data.length; i++){
//             for(var j = 0; j < res.data[i].categoryList.length; j++){
//                 var a = '';
//                 a += '<a value="'+res.data[i].categoryList[j].id+'" href="#">'+res.data[i].categoryList[j].name+'</a>';
//                 $('#header img').after(a);
//             }
//         }
//         //一级分类跳转
//         $('#header a').click(function () {
//             location.href = '../salesHome/searchHtml?id='+$(this).attr('value')+'';
//         });
//     },
//     error: function (err) {
//         layer.alert(err.message);
//     }
// });

// 省市区
var code = 110000;
$.ajax({
    type: 'POST',
    url: '../area/findProvince',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0;i < res.data.length; i++){
            var province = '';
            province += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
            $('#cmbProvince').append(province);
        }
        code = $('#cmbProvince option').eq(0).val();
        provinceCity();
        $("#cmbProvince").change(function(){
            $('#cmbCity').html('');
            $('#cmbArea').html('');
            code = $("#cmbProvince").val();
            provinceCity()
        });
        function provinceCity() {
            $.ajax({
                type: 'POST',
                url: '../area/findCity',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({code: code}),
                success: function (res) {
                    for(var i = 0;i < res.data.length; i++){
                        var cmbCity = '';
                        cmbCity += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                        $('#cmbCity').append(cmbCity)
                    }
                    //区
                    code = $('#cmbCity option').eq(0).val();
                    area();
                    $("#cmbCity").change(function(){
                        code = $("#cmbCity").val();
                        area();
                    });
                    function area() {
                        $('#cmbArea').html('');
                        $.ajax({
                            type: 'POST',
                            url: '../area/findCity',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({code: code}),
                            success: function (res) {
                                $('#cmbArea').html('');
                                for(var i = 0;i < res.data.length; i++){
                                    var cmbArea = '';
                                    cmbArea += '<option value="'+res.data[i].code+'">'+res.data[i].name+'</option>';
                                    $('#cmbArea').append(cmbArea)
                                }
                            },
                            error: function (err) {
                                layer.alert(err.message)
                            }
                        })
                    }
                },
                error: function (err) {
                    layer.alert(err.message)
                }
            })
        }
    },
    error: function (err) {
        layer.alert(err.message)
    }
});

//本地上传图片
document.getElementById("upLoad").addEventListener("change",function(e){
    var log = $('#img .img_container > div').length;
    if(log > 4){
        layer.alert('最多上传5张图片')
    }else{
        var files =this.files;
        var img = new Image();
        var reader =new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload =function(e){
            var dx =(e.total/1024)/1024;
            if(dx>=2){
                layer.alert("文件大小大于2M");
                return;
            }
            img.src =this.result;
            img.style.width = '138px';
            img.style.height ="93px";
            var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('#img .img_container').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});

//本地上传图片
document.getElementById("upLoad1").addEventListener("change",function(e){
    var log = $('#img1 .img_container1 > div').length;
    if(log > 0){
        layer.alert('最多只能上传1张图片')
    }else{
        var files =this.files;
        var img = new Image();
        var reader =new FileReader();
        reader.readAsDataURL(files[0]);
        reader.onload =function(e){
            var dx =(e.total/1024)/1024;
            if(dx>=2){
                layer.alert("文件大小大于2M");
                return;
            }
            img.src =this.result;
            img.style.width = '138px';
            img.style.height ="93px";
            var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle"></span></div>';
            $('#img1 .img_container1').append(image);
            $('.delete_img').click(function () {
                $(this).parent().remove();
            })
        }
    }
});
//搜索
$('.glyphicon-search').click(function () {
    var nameOrcode = $('#search').val();
    location.href = '../salesHome/searchHtml?nameOrcode='+nameOrcode+'';
});
//提交审核信息
$('#submit').click(function(){
    var hrefList = [];
    var img_con = $('#img .img_container div');
    for(var i = 0; i < img_con.length; i++){
        var imageSrc = {

        };
        imageSrc.href = img_con.eq(i).find('img').eq(0).attr('src');
        hrefList[i] = imageSrc;
    }
    var object = document.getElementsByName("saleschannelName");
    var check_val = '';
    for(k in object){
        if(object[k].checked)
            check_val += object[k].value + ',';
    }
    var data = {
        province :$('#cmbProvince').find('option:selected').val(),
        provinceName : $('#cmbProvince').find('option:selected').text(),
        city : $('#cmbCity').find('option:selected').val(),
        cityName : $('#cmbCity').find('option:selected').text(),
        zone : $('#cmbArea').find('option:selected').val(),
        zoneName : $('#cmbArea').find('option:selected').text(),
        name: $('#storeName').val(),
        contact: $('#name').val(),
        address: $('#address').val(),
        contactPhone: $('#tel').val(),
        credentials: $('#img1 .img_container1 img').attr('src'),
        scope: check_val.substring(0,check_val.lastIndexOf(",")),
        hrefList: hrefList
    };
    if(data.name == ''){
        layer.alert('门店名称不能为空',{icon:0})
    }else if(data.provinceName == ''){
        layer.alert('地址不能为空',{icon:0})
    }else if(data.cityName == ''){
        layer.alert('地址不能为空',{icon:0})
    }else if(data.zoneName == ''){
        layer.alert('地址不能为空',{icon:0})
    }else if(data.credentials == '' || data.credentials == undefined){
        layer.alert('资质照片不能为空',{icon:0})
    }else if(data.scope == ''){
        layer.alert('请选择销售商品',{icon:0})
    } else if(data.hrefList == ''){
        layer.alert('门店图片不能为空',{icon:0})
    }else if(data.contact == ''){
        layer.alert('联系人不能为空',{icon:0})
    }else if(!(/^1[34578]\d{9}$/.test(data.contactPhone))){
        layer.alert("手机号码有误，请重填",{icon:0});
        return false;
    }else{
        $.ajax({
            type: 'POST',
            url: '../userCenter/storeJoin',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode==100){
                    $.ajax({
                        type: 'POST',
                        url: '../verify/sendMessage',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({status:2,name:data.contact})
                    })
                    $('.step1').css('display','none');
                    $('.step2').css('display','block');
                    $('.join-part1').css('display','none');
                    $('.join-part2').css('display','block');
                    $('#head .dl-log .join').attr('href','../salesHome/joinHtml?type=2');
                } else {
                    layer.alert(res.message, {icon:0});
                }
            },
            error: function (err) {
                layer.alert(err.message);
            }
        });
    }
})