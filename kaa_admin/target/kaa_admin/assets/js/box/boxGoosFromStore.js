var urlle = sessionStorage.getItem('urllen');
var data = {
    labelId: '',
    stratPrice: '',
    endPrice: '',
    productCode:'',
    pageNum: 1,
    plaProductCategory: [],
    plaProductLabelId: []
};
//标签
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({parameterType: 1}),
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var label = '';
            label += '<span class="labelName" id="'+res.data[i].id+'">'+res.data[i].name+'</span>';
            $('#labelName').append(label);
        }

        $('.labelName').click(function () {
            $('.spinner-wrap').show();
            if($(this).hasClass('selectActive')){

            }else{
                $(this).addClass('selectActive');
                $('#yourChoose').append('<div class="'+$(this).attr('id')+'">'+$(this).html()+'<i class="glyphicon glyphicon-remove"></i></div>')
                choose();
            }
            //删除
            $('.glyphicon-remove').click(function () {
                var labelName = $('.labelName');
                $(this).parent().remove();
                choose();
                for(var i = 0; i < labelName.length; i++){
                    if($(this).parent().attr('class') == labelName.eq(i).attr('id')){
                        labelName.eq(i).removeClass('selectActive');
                    }
                }
            })
        });
    },
    error: function (err) {
        alert(err.message)
    }
});
//分类
$.ajax({
    type: 'POST',
    url: urlle + 'base/getPlaProductCategoryListAll',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0 ;i < res.data.length; i++){
            var firstCategory = '';
            firstCategory += '<span id="'+res.data[i].id+'">'+res.data[i].name+'</span>';
            $('#firstCategory').append(firstCategory);
        }

        for(var i = 0 ;i < res.data[0].categoryList.length; i++){
            var secCategory = '';
            secCategory += '<span class="sec" id="'+res.data[0].categoryList[i].id+'">'+res.data[0].categoryList[i].name+'</span>';
            $('#secCategory').append(secCategory);
        }
        //标签选择事件
        $('#firstCategory span').click(function () {
            $(this).siblings().removeClass('selectActive');
            $(this).addClass('selectActive');
            $.ajax({
                type: 'POST',
                url: urlle + 'base/getPlaProductCategoryList',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({parentId: $(this).attr('id')}),
                success: function (res) {
                    $('#secCategory').html('');
                    for(var i = 0 ;i < res.data.length; i++){
                        var secCategory = '';
                        secCategory += '<span id="'+res.data[i].id+'">'+res.data[i].name+'</span>';
                        $('#secCategory').append(secCategory);
                    }
                    for(var i = 0; i < $('#secCategory span').length; i++){
                        for(var j = 0; j < $('#yourChoose span').length; j++){
                            if($('#secCategory span').eq(i).attr('id') == $('#yourChoose span').eq(j).attr('class')){
                                $('#secCategory span').eq(i).addClass('selectActive');
                            }
                        }
                    }
                    click();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        });
        //相关分类标签
        function click() {
            $('#secCategory span').click(function () {
                $('.spinner-wrap').show();
                if($(this).hasClass('selectActive')){

                }else{
                    $(this).addClass('selectActive');
                    $('#yourChoose').append('<span class="'+$(this).attr('id')+'">'+$(this).html()+'<i class="glyphicon glyphicon-remove"></i></span>')
                    choose();
                }
                //删除
                $('.glyphicon-remove').click(function () {
                    $('.spinner-wrap').show();
                    var sec = $('#secCategory span');
                    $(this).parent().remove();
                    choose();
                    for(var i = 0; i < sec.length; i++){
                        if($(this).parent().attr('class') == sec.eq(i).attr('id')){
                            sec.eq(i).removeClass('selectActive');
                        }
                    }
                })
            });
        }
        click();
    },
    error: function (err) {
        alert(err.message);
    }
});
//列表
function post() {
    $.ajax({
        type: 'POST',
        url: urlle + 'boxManage/BoxAddBoxManageList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('.spinner-wrap').hide();
            $('#list').html('');
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    data.pageNum = $(".current").html();
                    list();
                }
            });
        },
        error: function (err) {
            alert(err.message);
        }
    });
}
function list() {
    $('.spinner-wrap').show();
    $.ajax({
        type: 'POST',
        url: urlle + 'boxManage/BoxAddBoxManageList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('.spinner-wrap').hide();
           load(res);
        },
        error: function (err) {
            alert(err.message);
        }
    });
};
post();

function load(res) {
    $('.spinner').hide();
    $('#list').html('');
    for(var i = 0 ;i < res.data.length; i++){
        var li = '';
        li += '<li class="goods-item col col-md-2" id="'+res.data[i].id+'"><div class="goods-item-wrap" style="background-color: #ffffff;padding: 8px">'+
            '<div class="goods-item-img"><img style="height: 180px" src="'+res.data[i].mainpicHref+'" alt=""></div>'+
            '<div class="goods-item-add addShop" style="display: none"><span style="margin-right: 7px" class="glyphicon glyphicon-plus-sign"></span>添加至展示商品</div>'+
            '<div class="goods-item-add benAdd" style="display: none;background-color: #888"><span style="margin-right: 7px" class="glyphicon glyphicon-plus-sign"></span>已添加</div>'+
            '<div class="goods-item-info clearfix">'+
            '<div class="goods-item-price">批发价：<span>¥'+res.data[i].price+'</span></div>'+
            '<div class="goods-item-desc">'+res.data[i].name+'</div>' +
            '<div class="goods-item-days">单件供货周期:<span style="margin-left: 6px">'+res.data[i].suplierDay+'</span>天</div></div></div></li>';
        $('#list').append(li);
        if(res.data[i].platProductIdStatus == 0){
            $('.addShop').eq(i).css('display', 'block');
            $('.benAdd').eq(i).css('display', 'none');
        }else {
            $('.benAdd').eq(i).css('display', 'block');
            $('.addShop').eq(i).css('display', 'none');
        }
    }
    $('.addShop').click(function () {
        $('.spinner-wrap').show();
        var _ths = $(this);
        var id = $(this).parent().parent().attr('id');
        $.ajax({
            type: 'POST',
            url: urlle + 'boxManage/insertBoxProduct',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                $('.spinner-wrap').hide();
                if(res.stateCode == 100){
                    $('#myModal').modal('show')
                    $('#sure').click(function () {
                        _ths.css('display', 'none');
                        _ths.next().css('display', 'block');
                        $('#myModal').modal('hide');
                    })
                }else{
                    $('#myModal').modal('hide');
                    alert(res.message);
                }
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })
    $('#list li .goods-item-img').click(function () {
        var goodsId = $(this).parent().parent().attr('id');
        location.href = '../product/goodsManageDetailHtml?id='+goodsId+''
    });
}

$('#button').click(function () {
    var startPrice = $('#startPrice').val();
    var endPrice = $('#endPrice').val();
    var productCode = $('#productCode').val();
    data.startPrice = startPrice;
    data.endPrice = endPrice;
    data.productCode = productCode;
    post();
});

function choose() {
    var cate = $('#yourChoose span');
    data.plaProductCategory = [];
    data.plaProductLabelId = [];
    for(var i = 0; i < cate.length; i++){
        data.plaProductCategory[i] = {
            categoryId: ''
        };
        data.plaProductCategory[i].categoryId = cate.eq(i).attr('class');
    }
    var label = $('#yourChoose div');
    for(var i = 0; i < label.length; i++){
        data.plaProductLabelId[i] = {
            labelId: ''
        };
        data.plaProductLabelId[i].labelId = label.eq(i).attr('class');
    }
    post();
}
