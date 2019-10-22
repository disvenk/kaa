var url = window.location.href;
var id = url.split('=')[1];
var page = url.split('=')[2];
var urlle = sessionStorage.getItem('urllen');
var vedio;
var secondCategoryName;
var imgs = [];
var ue = UE.getEditor('ueditor',{
    initialFrameWidth: '100%',
    initialStyle: 'p{line-height:0;margin:0}'
});
//时间戳转换
function timetrans(date) {
    var date = new Date(date);//如果date为10位不需要乘1000
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
    var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
    return Y + M + D + h + m + s;
}

//图片转化
function convertImgToBase64(url, callback, outputFormat) {
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var img = new Image;
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
}

//供应商
$.ajax({
    type: 'POST',
    url: urlle + 'product/suplierNameList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for (var i = 0; i < res.data.length; i++) {
            var productSupplierName = '';
            productSupplierName += '<option id="' + res.data[i].id + '" value="' + res.data[i].suplierName + '">' + res.data[i].suplierName + '</option>';
            $('#productSupplierName').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});
//分类
$.ajax({
    type: 'POST',
    url: urlle + 'base/getPlaProductCategoryLevelOneList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        //分类
        for (var i = 0; i < res.data.length; i++) {
            var Suplierscope = '';
            Suplierscope += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            $('#Suplierscope').append(Suplierscope);
        }
        change();
    },
    error: function (err) {
        alert(err.message)
    }
});
function sele() {
    $.ajax({
        type: 'POST',
        url: urlle + 'product/getCategorySupplierDay',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: $('#secCategory option:selected').attr('id')}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#suplierDay').val(res.data.categorySupplierDay);
            }
        },error: function (err) {

        }
    })
}
function change() {
    $.ajax({
        type: 'POST',
        url: urlle + 'base/getPlaProductCategoryList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parentId: $('#Suplierscope option:selected').attr('id')}),
        success: function (res) {
            $('#secCategory').html('');
            //分类
            for (var i = 0; i < res.data.length; i++) {
                var secCategory = '';
                secCategory += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
                $('#secCategory').append(secCategory);
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}
$('#secCategory').change(function () {
    sele();
})
$('#Suplierscope').change(function () {
    change();
});
//商品标签
$.ajax({
    type: 'POST',
    url: urlle + 'base/plaProductBaseList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        //商品标签
        for (var i = 0; i < res.data[0].productLabel.length; i++) {
            var input = '';
            input += '<label style="margin-right: 16px"><input id="' + res.data[0].productLabel[i].labelId + '" style="margin-right: 5px" name="labelNmae" type="checkbox" value="' + res.data[0].productLabel[i].labelName + '" />' + res.data[0].productLabel[i].labelName + ' </label>';
            $('#labelNmae').append(input);
        }
        //销售渠道
        for (var i = 0; i < res.data[0].salesChannel.length; i++) {
            var salesChannel = '';
            salesChannel += '<label style="margin-right: 16px"><input id="' + res.data[0].salesChannel[i].channelId + '" style="margin-right: 5px" name="saleschannelName" type="checkbox" value="' + res.data[0].salesChannel[i].channelName + '" />' + res.data[0].salesChannel[i].channelName + ' </label>';
            $('#saleschannelName').append(salesChannel);
        }
        //颜色
        for (var i = 0; i < res.data[0].productColor.length; i++) {
            var productColor = '';
            productColor += '<label style="margin-right: 16px"><input id="' + res.data[0].productColor[i].colorId + '" style="margin-right: 5px" name="productColor" type="checkbox" value="' + res.data[0].productColor[i].colorName + '" />' + res.data[0].productColor[i].colorName + ' </label>';
            $('#color').append(productColor);
        }
        //尺寸
        for (var i = 0; i < res.data[0].productSize.length; i++) {
            var productSize = '';
            productSize += '<label style="margin-right: 16px"><input id="' + res.data[0].productSize[i].sizeId + '" style="margin-right: 5px" name="productSize" type="checkbox" value="' + res.data[0].productSize[i].sizeName + '" />' + res.data[0].productSize[i].sizeName + ' </label>';
            $('#size').append(productSize);
        }
        //动态绑定颜色尺寸关系
        $('#color label').click(function () {
            each();
        });
        $('#size label').click(function () {
            each();
        });

        //生产颜色尺寸关系
        function each() {
            $("#assemblage").html("");
            var aa = $("#color input[type='checkbox']:checked");
            var bb = $("#size input[type='checkbox']:checked");
            for(var i = 0; i < aa.length; i++){
                for(var j = 0; j < bb.length; j++){
                    var span = '';
                    span += '<span class="col col-md-2 col-lg-2 col-sm-2 col-xs-2" style="text-align: center;margin-top: 5px;margin-right: 26px;padding: 5px 15px;border: 1px dotted #fff">'+aa.eq(i).attr('value')+'+'+bb.eq(j).attr('value')+'</span>';
                    $('#assemblage').append(span);
                }
            }
        }
        detail();
        productDescription();
    },
    error: function (err) {
        alert(err.message)
    }
})

function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: urlle + 'product/productEditDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if (res.stateCode == 100) {
                $('#productCode').html('' + res.data[0].productCode + '');
                $('#goodsName').val('' + res.data[0].name + '');

                for (var i = 0; i < res.data[0].picture.length; i++) {
                    var img = '';
                    img += '<div class="img_container1"><img src="' +res.data[0].picture[i].href+ '" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $('#img .img_container').append('' + img + '');
                    imgs.push({href: res.data[0].picture[i].key});
                }
                if (res.data[0].isDesigner == true) {
                    res.data[0].isDesigner = '是'
                } else {
                    res.data[0].isDesigner = '否'
                };
                //商品标签
                if (res.data[0].labelName.length == 0) {
                } else {
                    for (var i = 0; i < res.data[0].labelName.length; i++) {
                        $('#labelNmae input[value=' + res.data[0].labelName[i].labelName + ']').attr('checked', true);
                    }
                }
                //销售渠道
                if (res.data[0].schannelName.length == 0) {
                } else {
                    for (var i = 0; i < res.data[0].schannelName.length; i++) {
                        $('#saleschannelName input[value=' + res.data[0].schannelName[i].saleschannelName + ']').attr('checked', true);
                    }
                }
                //标签选中
                secondCategoryName = res.data[0].secondCategoryName
                $('#isDesign input[value=' + res.data[0].isDesigner + ']').attr('checked', true);
                $("#Suplierscope").val('' + res.data[0].firstCategoryName + '');
                $.ajax({
                    type: 'POST',
                    url: urlle + 'base/getPlaProductCategoryList',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({parentId: res.data[0].firstCategoryId}),
                    success: function (res) {
                        $('#secCategory').html('');
                        //分类
                        for (var i = 0; i < res.data.length; i++) {
                            var secCategory = '';
                            secCategory += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
                            $('#secCategory').append(secCategory);
                        }
                        for (var i = 0; i < $('#secCategory option').length; i++) {
                            if ($('#secCategory option').eq(i).text() == secondCategoryName) {
                                $('#secCategory option').eq(i).attr('selected', 'selected')
                            }
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                });
                //2017.12.11 图文详情单独
                // $('#editor1').html('' + res.data[0].description + '');

                $('#video').attr('src', '' + res.data[0].vedioUrl + '');
                $('#brand').val('' + res.data[0].brand + '');
                // $('#pno').val('' + res.data[0].pno + '');
                // $('#productSupplierName').val('' + res.data[0].productSupplierName + '');
                $('#suplierDay').val('' + res.data[0].suplierDay + '');
                $('#price').val('' + res.data[0].price + '');
                $('#material').val('' + res.data[0].material + '');
                $('#technics').val('' + res.data[0].technics + '');

                $('#colligate').val('' + res.data[0].colligate + '');
                $('#sales').val('' + res.data[0].sales + '');
                $('#views').val('' + res.data[0].views + '');
                $('#createTime').html('' + res.data[0].updateDate + '');
                $('#remark').val('' + res.data[0].remark + '');
                vedio = res.data[0].vedioUrl;

                //表格
                for (var i = 0; i < res.data[0].jsonSupplier.length; i++) {
                    var tr1 = '';
                    tr1 += ' <tr id="' + res.data[0].jsonSupplier[i].gids + '"><td class="table_color">' + res.data[0].jsonSupplier[i].color + '</td>' +
                        '<td class="table_size">' + res.data[0].jsonSupplier[i].size + '</td>' +
                        '<td class="table_stock"><input type="number" value="' + res.data[0].jsonSupplier[i].stock + '"></td>' +
                        // '<td class="table_price"><input type="number" value="' + res.data[0].jsonSupplier[i].priceNormal + '"></td>' +
                        '<td class="table_online"><input type="number" value="' + res.data[0].jsonSupplier[i].onlinePrice + '"></td>' +
                        '<td class="table_offline"><input type="number" value="' + res.data[0].jsonSupplier[i].offlinePrice + '"></td><td class="table_remark"><input style="width: 90px" type="text" value="' + res.data[0].jsonSupplier[i].categoryRemark + '"></td>' +
                        '<td><span class="storage-operate deleteTr" onclick="$(this).parent().parent().remove()">删除</span></td></tr>';
                    $('#tbody').append(tr1);
                }
                //视频转化
                var $videoUploader = document.getElementById('videoUploader');
                var $video = document.getElementById('video');
                var reader = new FileReader();
                $videoUploader.addEventListener('change', function () {
                    loadFile(function (result) {
                        $video.src = '';
                        $('.progress').css('display', 'block');
                        $.ajax({
                            type: 'POST',
                            url: urlle + 'uploadFile/saveUploadFileVideo',
                            dataType: 'json',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({base64: result}),
                            success: function (res) {
                                if (res.stateCode == 100) {
                                    $('.progress').css('display', 'none');
                                    $video.src = result;
                                    $('#video').attr('value' ,res.data.key);
                                } else {
                                    alert(res.message);
                                }
                            },
                            error: function (err) {
                                alert(err.message)
                            }
                        })
                    })
                });

                function loadFile(callback) {
                    if ($videoUploader.files.length === 0) return;
                    var oFile = $videoUploader.files[0];
                    reader.readAsDataURL(oFile);
                    reader.onload = function (e) {
                        callback && callback(this.result)
                    }
                }
            } else {
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//2017.12.11 图文详情单独加载
function productDescription() {
    $.ajax({
        type: 'POST',
        url: urlle + 'product/getProductDescription',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            ue.setContent('' + res.data.description + '');
        },
        error: function (err) {
            alert(err.message)
        }
    })
}

//本地上传图片
document.getElementById("upLoad").addEventListener("change",function(e){
    var log = $('#img .img_container > div').length;
    if(log > 4){
        alert('最多上传5张图片');
    }else{
        var files = document.getElementById("upLoad").files;
        if(log + files.length > 5){
            alert('最多上传5张图片');
        }else{
            for(var i= 0; i < files.length; i++){
                var img = new Image();
                var reader =new FileReader();
                reader.readAsDataURL(files[i]);
                reader.onload =function(e){
                    var dx =(e.total/1024)/1024;
                    if(dx>=2){
                        alert("文件大小大于2M");
                        return;
                    }
                    img.src =this.result;
                    img.style.width = '138px';
                    img.style.height ="93px";
                    var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $.ajax({
                        type: 'POST',
                        url: urlle + 'uploadFile/saveUploadFile',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({base64: this.result}),
                        success: function (res) {
                            if (res.stateCode == 100) {
                                $('#img .img_container').append(image);
                                imgs.push({href: res.data.key});
                                $("input[type='file']").val('');
                            } else {
                                alert(res.message);
                            }
                        },
                        error: function (err) {
                            alert(err.message)
                        }
                    })
                }
            }
        }
    }
});
function deleteImg(i) {
    $.ajax({
        type: 'POST',
        url: urlle + 'uploadFile/removeUploadFile',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: imgs[i.parent().index()].href}),
        success: function (res) {
            if (res.stateCode == 100) {

            } else {
                // alert(res.message);
            }
        },
        error: function (err) {
            // alert(err.message)
        }
    })
    imgs.splice(i.parent().index(), 1);
    i.parent().remove();
}
//添加
function add() {
    if($("#assemblage").html() == ''){
        layer.alert('请选择规格！', {icon: 0});
    }else{
        $('#unification').css('display', 'block');
        $('#tbody').html('');
        for(var i = 0; i < $("#assemblage span").length; i++){
            var tr2 = '';
            tr2 += ' <tr>' +
                '<td class="table_color">' +$("#assemblage").children("span").eq(i).html().split('+')[0]+ '</td>' +
                '<td class="table_size">' +$("#assemblage").children("span").eq(i).html().split('+')[1]+ '</td>' +
                '<td class="table_stock"><input type="number" value="'+$('#unification .stock').val()+'"></td>' +
                // '<td class="table_price"><input type="number" value="'+$('#unification .price').val()+'"></td>' +
                '<td class="table_online"><input type="number" value="'+$('#unification .onlinePrice').val()+'"></td>' +
                '<td class="table_offline"><input type="number" value="'+$('#unification .inlinePrice').val()+'"></td>' +
                '<td class="table_remark"><input type="text" style="height: 26px;width: 90px;border-radius: 6px !important;"></td>' +
                '<td><span class="storage-operate deleteTr" onclick="$(this).parent().parent().remove()">删除</span></td></tr>';
            $('#tbody').append(tr2);
        }
    }
}

//提交编辑信息
var sss;
$('#confirm').click(function () {
    if($('.progress').css('display') == 'block'){
        layer.alert('视频正在上传中！', {icon: 0});
    }else{
        var name = $('#goodsName').val();
        var img_content = $('.img_container > div img').attr('src');
        var video = $('#video').attr('src');
        var labelName = $('#labelNmae input:radio:checked').val();
        var brand = $('#brand').val();
        var Suplierscope = $('#Suplierscope').val();
        // var pno = $('#pno').val();
        // var productSupplierName = $('#productSupplierName option:selected').attr('id');
        var suplierDay = $('#suplierDay').val();
        var price = $('#price').val();
        var material = $('#material').val();
        var technics = $('#technics').val();

        var isDesign = $('#isDesign input:radio:checked').val();
        if (isDesign == '是') {
            isDesign = true;
        } else {
            isDesign = false;
        }
        var productSupplierList = [];
        var table = $('#tbody tr');
        for (var i = 0; i < table.length; i++) {
            var obj = {};
            obj = {
                color: table.eq(i).find('td').eq(0).html(),
                size: table.eq(i).find('td').eq(1).html(),
                stock: table.eq(i).find('td').eq(2).find('input').eq(0).val(),
                onlinePrice: table.eq(i).find('td').eq(3).find('input').eq(0).val(),
                offlinePrice: table.eq(i).find('td').eq(4).find('input').eq(0).val(),
                categoryRemark: table.eq(i).find('td').eq(5).find('input').eq(0).val()
            };
            productSupplierList[i] = obj;
        }
        var object = document.getElementsByName("labelNmae");
        var check_val = [];
        for (var i = 0; i < object.length; i++) {
            if (object[i].checked) {
                check_val.push({labelId: object[i].id, labelName: object[i].value})
            }
        }
        var object1 = document.getElementsByName("saleschannelName");
        var check_val1 = [];
        for (k in object1) {
            if (object1[k].checked)
                check_val1.push({saleschannelId: object1[k].id, saleschannelName: object1[k].value});
        }
        var saleschannelName = $('#saleschannelName input:radio:checked').val();
        var colligate = $('#colligate').val();
        var sales = $('#sales').val();
        var views = $('#views').val();
        var createTime = $('#createTime').html();
        var remark = $('#remark').val();
        var tr = $('#tbody tr');
        var time = timetrans(new Date());
        var img_con = $('.img_container .img_container1');
        // var src = [];
        // for (var i = 0; i < img_con.length; i++) {
        //     var imageSrc = {};
        //     imageSrc.href = img_con.eq(i).find('img').eq(0).attr('src');
        //     src[i] = imageSrc;
        // }
        if (video == vedio) {
            video = ''
        } else {
            video = $('#video').attr('src');
        }
        var data = {
            categoryId: $('#secCategory option:selected').attr('id'),
            label_id: $('#labelNmae input:checked').attr('id'),
            name: name,
            // pno: pno,
            id: id,
            brand: brand,
            isDesigner: isDesign,
            description: ue.getContent(),
            vedioUrl: $('#video').attr('value'),
            // productSupplierId: $('#productSupplierName option:selected').attr('id'),
            suplierDay: suplierDay,
            material: material,
            technics: technics,
            remarks: remark,
            views: views,
            sales: sales,
            colligate: colligate,
            prices: price,
            updateDate: time,
            productSupplierList: productSupplierList,
            productPictureList: imgs,
            productLabelList: check_val,
            productSchannelList: check_val1
        };
        if (name == '') {
            layer.alert('商品名称必填！', {icon: 0});
        }else if (!$("#isDesign input[name=sex]").is(":checked")) {
            layer.alert('请选择是否是设计师款！', {icon: 0});
        } else if ($('#tbody tr').length > 0) {
            sss = true;
            for(var i = 0; i < $('#tbody tr').length; i++){
                if($('#tbody tr').eq(i).find('td').eq(3).find('input').eq(0).val() == ''){
                    sss = false;
                    layer.alert('线上价格不能为空！', {icon: 0});
                }else if($('#tbody tr').eq(i).find('td').eq(4).find('input').eq(0).val() == ''){
                    layer.alert('线下价格不能为空！', {icon: 0});
                    sss = false;
                }
            }
            if(sss){
                $('.spinner-wrap').show();
                $.ajax({
                    type: 'POST',
                    url: urlle + 'product/updateProduct',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify(data),
                    success: function (res) {
                        $('.spinner-wrap').hide();
                        if (res.stateCode == 100) {
                            $('#myModal').modal('show');
                            $('#sure').click(function () {
                                $('#myModal').modal('hide');
                                setTimeout(function () {
                                    location.href = '../product/goodsManageHtml?='+page;
                                }, 1500)
                            })
                        } else {
                            alert(res.message)
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        }
    }
});

$('#goBack').click(function () {
    location.href = '../product/goodsManageHtml?='+page;;
});