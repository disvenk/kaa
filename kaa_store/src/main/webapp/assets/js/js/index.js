var id = '';
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
                                layer.alert(err.message,{icon:0})
                            }
                        })
                    }
                },
                error: function (err) {
                    layer.alert(err.message,{icon:0})
                }
            })
        }
    },
    error: function (err) {
        layer.alert(err.message,{icon:0})
    }
});
$.ajax({
    type: 'POST',
    url: '../storeHome/productCategoryList',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for (var i = 0; i < res.data.length; i++) {
            var side = '';
            side += '<div class="sec" id="'+res.data[i].id+'">' + res.data[i].name + '</div>';
            $('#sidebar ul').append(side);
        }
        // $('#sidebar .sec').eq(0).addClass('sec_active');
        var pageNum = 1;
        var data = {
            categoryId: '',
            pageNum: pageNum
        };
        //点击处理导航事件
        $.ajax({
            type: 'POST',
            url: '../storeHome/storeProductList',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data),
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            success: function (res) {
                $(".tcdPageCode").createPage({
                    pageCount:res.pageSum,//总共的页码
                    current:1,//当前页
                    backFn:function(p){//p是点击的页码
                        data.pageNum = $('.current').html();
                        ajax();
                    }
                });
            },
            error: function (err) {
                layer.alert(err,{icon:0})
            }
        });
     function ajax() {
         $.ajax({
             type: 'POST',
             url: '../storeHome/storeProductList',
             contentType: 'application/json; charset=utf-8',
             data: JSON.stringify(data),
             headers: {
                 'Accept': 'application/json; charset=utf-8',
                 'Authorization': 'Basic ' + sessionStorage.getItem('token')
             },
             success: function (res) {
             if(res.stateCode == 100){
                if(res.data.length == 0){
                    $('#container').html('');
                    $(".tcdPageCode").css('display', 'none');
                    layer.alert('暂无对应商品',{icon: 0});
                }else {
                    $('#container').html('');
                    $(".tcdPageCode").css('display', 'block');
                    for(var i = 0; i < res.data.length; i++){
                        var div = '';
                        div += '<div class="col col-md-2 col-sm-4 col-lg-2 col-xs-6 col-ass-6" id="'+res.data[i].id+'"><div class="thumbnail">' +
                            '<img src="'+res.data[i].mainpicHref+'" alt="..."  onerror="this.src=\'../assets/img/default.jpg\'"><div class="caption row"><div>¥'+res.data[i].price+'</div>' +
                            '<div><span class="glyphicon glyphicon-eye-open"></span> '+res.data[i].views+'</div></div>' +
                            '<div class="introduce">'+res.data[i].name+'</div></div></div>';
                        $('#container').append(div);
                    }
                    //商品详情
                    function detail(id) {
                        $('#num').val('1');
                        $('#nowBuy').html('');
                        var buy = '';
                        buy = ' <span id="xiadan"><span class="glyphicon glyphicon-heart-empty"></span><span>立即下单</span></span>' +
                            '<span id="addBuyCar"><span class="glyphicon glyphicon-shopping-cart"></span><span>加入购物车</span></span>';
                        $('#nowBuy').append(buy);
                        $.ajax({
                            type: 'POST',
                            url: '../storeHome/storeProductDetail',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({id: id}),
                            success: function (res) {
                                if(res.stateCode == 100){
                                    $('#price .glyphicon-plus-sign').unbind();
                                    $('#price .glyphicon-minus-sign').unbind();
                                    if(res.data.vedioUrl == ''){
                                        $('#play').css('display', 'none');
                                    }
                                    $('#video').css('display', 'none');
                                    $('#video').attr('src',res.data.vedioUrl);
                                    $('#imageMenu ul').html('');
                                    var imgSrc = '';
                                    for(var i = 0; i < res.data.imgList.length; i++){
                                        var li = '';
                                        li += '<li><img src="'+res.data.imgList[i].href+'" alt="合一智造"/></li>';
                                        $('#imageMenu ul').append(li);
                                        if(res.data.imgList[i].mainpic == true){
                                            imgSrc = res.data.imgList[i].href;
                                            $('#midimg').attr('src',res.data.imgList[i].href);
                                        }
                                    }
                                    $('#imageMenu ul li').click(function () {
                                        var src = $(this).children().attr('src');
                                        $('#midimg').attr('src',src);
                                        $('#video').css('display', 'none');
                                    })
                                    $('#goodsName').html(res.data.name);
                                    $('#modalType').html(res.data.code);
                                    //放大镜
                                    // // //放大镜
                                    // var t = n = 0, count;
                                    // count=$("#banner_list a").length;
                                    // $("#banner_list a:not(:first-child)").hide();
                                    // $("#banner li").click(function() {
                                    //     $(this).css('z-index','1000');
                                    //     var i = $(this).text() - 1;//获取Li元素内的值，即1，2，3，4
                                    //     n = i;
                                    //     if (i >= count) return;
                                    //     $("#banner_info").html($("#banner_list a").eq(i).find("img").attr('alt'));
                                    //     $("#banner_info").unbind().click(function(){window.open($("#banner_list a").eq(i).attr('href'), "_blank")});
                                    //     $("#banner_list a").filter(":visible").fadeOut(500).parent().children().eq(i).fadeIn(1000);
                                    //     document.getElementById("banner").style.background="";
                                    //     $(this).toggleClass("on");
                                    //     $(this).siblings().removeAttr("class");
                                    // });
                                    // //放大镜视窗
                                    // $("#bigView").decorateIframe();
                                    //
                                    // //点击到中图
                                    // var midChangeHandler = null;
                                    //
                                    // $("#imageMenu li img").bind("click", function(){
                                    //     if ($(this).attr("id") != "onlickImg") {
                                    //         midChange($(this).attr("src").replace("small", "mid"));
                                    //         $("#imageMenu li").removeAttr("id");
                                    //         $(this).parent().attr("id", "onlickImg");
                                    //     }
                                    //     $('#video').css('display', 'none');
                                    // }).bind("mouseover", function(){
                                    //     if ($(this).attr("id") != "onlickImg") {
                                    //         window.clearTimeout(midChangeHandler);
                                    //         midChange($(this).attr("src").replace("small", "mid"));
                                    //         $(this).css({ "border": "1px solid #990000" });
                                    //     }
                                    // }).bind("mouseout", function(){
                                    //     if($(this).attr("id") != "onlickImg"){
                                    //         $(this).removeAttr("style");
                                    //         midChangeHandler = window.setTimeout(function(){
                                    //             midChange($("#onlickImg img").attr("src").replace("small", "mid"));
                                    //         }, 1000);
                                    //     }
                                    // });
                                    //
                                    // function midChange(src) {
                                    //     $("#midimg").attr("src", src).load(function() {
                                    //         changeViewImg();
                                    //     });
                                    // }
                                    //
                                    // //大视窗看图
                                    // function mouseover(e) {
                                    //     if ($("#winSelector").css("display") == "none") {
                                    //         $("#winSelector,#bigView").show();
                                    //     }
                                    //
                                    //     $("#winSelector").css(fixedPosition(e));
                                    //     e.stopPropagation();
                                    // }
                                    //
                                    //
                                    // function mouseOut(e) {
                                    //     if ($("#winSelector").css("display") != "none") {
                                    //         $("#winSelector,#bigView").hide();
                                    //     }
                                    //     e.stopPropagation();
                                    // }
                                    //
                                    //
                                    // $("#midimg").mouseover(mouseover); //中图事件
                                    // $("#midimg,#winSelector").mousemove(mouseover).mouseout(mouseOut); //选择器事件
                                    //
                                    // var $divWidth = $("#winSelector").width(); //选择器宽度
                                    // var $divHeight = $("#winSelector").height(); //选择器高度
                                    // var $imgWidth = $("#midimg").width(); //中图宽度
                                    // var $imgHeight = $("#midimg").height(); //中图高度
                                    // var $viewImgWidth = $viewImgHeight = $height = null; //IE加载后才能得到 大图宽度 大图高度 大图视窗高度
                                    //
                                    // function changeViewImg() {
                                    //     $("#bigView img").attr("src", $("#midimg").attr("src").replace("mid", "big"));
                                    // }
                                    //
                                    // changeViewImg();
                                    //
                                    // $("#bigView").scrollLeft(0).scrollTop(0);
                                    // function fixedPosition(e) {
                                    //     if (e == null) {
                                    //         return;
                                    //     }
                                    //     var $imgLeft = $("#midimg").offset().left; //中图左边距
                                    //     var $imgTop = $("#midimg").offset().top; //中图上边距
                                    //     X = e.pageX - $imgLeft - $divWidth / 2; //selector顶点坐标 X
                                    //     Y = e.pageY - $imgTop - $divHeight / 2; //selector顶点坐标 Y
                                    //     X = X < 0 ? 0 : X;
                                    //     Y = Y < 0 ? 0 : Y;
                                    //     X = X + $divWidth > $imgWidth ? $imgWidth - $divWidth : X;
                                    //     Y = Y + $divHeight > $imgHeight ? $imgHeight - $divHeight : Y;
                                    //
                                    //     if ($viewImgWidth == null) {
                                    //         $viewImgWidth = $("#bigView img").outerWidth();
                                    //         $viewImgHeight = $("#bigView img").height();
                                    //         if ($viewImgWidth < 200 || $viewImgHeight < 200) {
                                    //             $viewImgWidth = $viewImgHeight = 800;
                                    //         }
                                    //         $height = $divHeight * $viewImgHeight / $imgHeight;
                                    //         $("#bigView").width($divWidth * $viewImgWidth / $imgWidth);
                                    //         $("#bigView").height($height);
                                    //     }
                                    //
                                    //     var scrollX = X * $viewImgWidth / $imgWidth;
                                    //     var scrollY = Y * $viewImgHeight / $imgHeight;
                                    //     $("#bigView img").css({ "left": scrollX * -1, "top": scrollY * -1 });
                                    //     $("#bigView").css({ "top": 0, "left": $(".preview").offset().left + $(".preview").width() + 15 });
                                    //
                                    //     return { left: X, top: Y };
                                    // }
                                    //
                                    //
                                    // function showAuto()
                                    //
                                    // {
                                    //
                                    //     n = n >=(count - 1) ? 0 : ++n;
                                    //
                                    //     $("#banner li").eq(n).trigger('click');
                                    //
                                    // }

                                    $('#goodsPrice').html(res.data.price);
                                    var num = 1;//商品数量
                                    var color = ''; //用于存储当前选中的颜色
                                    var size = ''; //用于存储当前选中的尺寸
                                    var price = res.data.price; //商品单价
                                    $('#play').click(function () {
                                        $('#video').css('display', 'block');
                                        $('#video').css('position', 'absolute');
                                        $('#video').css('z-index', '900');
                                    });
                                    //颜色 尺寸
                                    $('#seletor').html('');
                                    $('#size').html('');
                                    mp = new Map();//用于记录颜色包含的尺寸
                                    sizeMap = new Map();//仅用于尺寸去重
                                    for(var i = 0; i < res.data.specList.length; i++){
                                        var colorContent = res.data.specList[i].color;
                                        var sizeContent = res.data.specList[i].size;
                                        if (sizeMap.get(sizeContent) == null) {
                                            var sizeA = '';
                                            sizeA += '<span class="spSize">'+sizeContent+'</span>';
                                            $('#size').append(sizeA);
                                            sizeMap.set(sizeContent, 1);
                                        }
                                        if(mp.get(colorContent) == null) {
                                            mp.set(colorContent, new Map())
                                            var colorA = '';
                                            colorA += '<span class="spColor">'+colorContent+'</span>';
                                            $('#seletor').append(colorA);
                                        }
                                        var si = mp.get(colorContent);
                                        si.set(sizeContent, res.data.specList[i].offlinePrice);
                                    }
                                    //绑定颜色点击事件
                                    $("#seletor").on("click", ".spColor", function () {
                                        //移除其他颜色的选中样式
                                        $(".spColor").removeClass("checkSp");
                                        $(this).addClass("checkSp");
                                        color = $(this).html();
                                        //先将所有尺寸设置不可选, 并清除选中样式
                                        $(".spSize").addClass("notCheckSp");
                                        $(".spSize").removeClass("checkSp");
                                        for(var i = 0; i < $(".spSize").length; i++){
                                            if(mp.get(color).get($(".spSize").eq(i).html()) != null) {
                                                $(".spSize").eq(i).removeClass("notCheckSp");
                                            }
                                        }

                                    });
                                    //绑定的尺寸点击事件
                                    $("#size").on("click", ".spSize", function () {
                                        if(!$(this).hasClass("notCheckSp")) {
                                            $(".spSize").removeClass("checkSp");
                                            $(this).addClass("checkSp");
                                            size = $(this).html();
                                            price = mp.get(color).get(size);
                                            $('#goodsPrice').html(price * num);
                                        }
                                    })

                                    $("#num").bind('input porpertychange',function(){
                                        num = $('#num').val();
                                        $('#goodsPrice').html(price * num);
                                    });
                                    $('#price .glyphicon-plus-sign').click(function () {
                                        num = $(this).siblings('input').val() - 0 + 1;
                                        $(this).siblings('input').val(num);
                                        $('#goodsPrice').html(price * num);
                                    });
                                    $('#price .glyphicon-minus-sign').click(function () {
                                        num = $(this).siblings('input').val() - 0;
                                        if(num == 1){
                                            $(this).siblings('input').val('1');
                                        }else {
                                            num = $(this).siblings('input').val() - 0 - 1;
                                            $(this).siblings('input').val(num);
                                            $('#goodsPrice').html(price * num);
                                        }
                                    });

                                    //2017.12.11 图文详情，单独取得
                                    // $('#description').html(res.data.description);

                                    //加入购物车
                                    $('#addBuyCar').click (function (){
                                        if(color == ''){
                                            layer.alert('请选择颜色',{icon:0})
                                        }else if(size == ''){
                                            layer.alert('请选择尺寸',{icon:0})
                                        }else{
                                            $.ajax({
                                                type: 'POST',
                                                url: '../shoppingCart/shopcartAdd',
                                                contentType: 'application/json; charset=utf-8',
                                                data: JSON.stringify({pid: res.data.id,num: $('#num').val(), color:color, size:size}),
                                                headers: {
                                                    'Accept': 'application/json; charset=utf-8',
                                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                                },
                                                success: function (res) {
                                                    if(res.stateCode == 100){
                                                        $('#myModal3').modal('show');
                                                        $('#myModal3 .sure').click(function () {
                                                            $('#myModal3').modal('hide');
                                                        })
                                                    }else {
                                                        layer.alert(res.message,{icon:0})
                                                    }
                                                },
                                                error: function (err) {
                                                    layer.alert(err,{icon:0})
                                                }
                                            });
                                        }
                                    });
                            //立即下单
                                 $('#xiadan').click(function () {
                                     if(color == ''){
                                           layer.alert('请选择颜色',{icon:0})
                                     }else if(size == ''){
                                            layer.alert('请选择尺寸',{icon:0})
                                     }else{
                                         $('#homePage').css('display','none');
                                         $('#xiadans').css('display','block');
                                         $('#detail').css('display','none');
                                         $('#buyCars').css('display','none');
                                         $('#sample-table-1 #xiadanInform').html('');
                                         $('#customer').html('');
                                         $('#xiadanTbody').html('');
                                         var totalPrice = 0;
                                         totalPrice = $('#goodsPrice').html() / $('#num').val();
                                         $('#customer').append('<tr><td class="information_container"><div>' +
                                             '<div class="information"><img src="'+imgSrc+'" alt=""></div>' +
                                             '<div class="introduce_container">' +
                                             '<div class="chinese_introduce">'+res.data.name+'</div>' +
                                             '<span class="color">颜色：'+color+' &nbsp;&nbsp; 尺寸：'+size+'</span></div></div>' +
                                             '</td><td>'+ totalPrice+'</td><td>'+$('#num').val()+'</td><td>'+$('#goodsPrice').html()+'</td></tr><tr>' +
                                             '<td class="information_container"></td><td></td><td>数量总计：<span>'+$('#num').val()+'件</span></td>' +
                                             '<td>货品金额总计：<span>'+$('#goodsPrice').html()+'元</span></td></tr>');
                                         $('#sample-table #xiadanInforms').html('');
                                         $('#sample-table #xiadanInforms').append(' <th class="center">商品名称</th><th class="center">商品ID</th>' +
                                             '<th class="center">分类</th><th class="center">颜色</th>' +
                                             '<th class="center">尺寸</th><th class="center">肩宽</th><th class="center">胸围</th>' +
                                             '<th class="center">腰围</th><th class="center">臀围</th><th class="center">身高</th>' +
                                             '<th class="center">体重</th><th class="center">喉到地</th><th class="center">其他</th>');
                                         $('#xiadanTbody').append(' <tr class="custormInformation">' +
                                             '<td>'+res.data.name+'</td><td>'+res.data.code+'</td><td>'+res.data.categoryName+'</td><td>'+color+'</td>' +
                                             '<td>'+size+'</td><td><input type="number" id="shoulder"></td><td><input id="bust" type="number"></td>' +
                                             '<td><input id="waist" type="number"></td><td><input id="hipline" type="number"></td>' +
                                             '<td><input id="height" type="number"></td><td><input id="weight" type="number"></td>' +
                                             '<td><input id="throatheight" type="number"></td><td><input id="other" type="text"></td></tr>');
                                         $('#submitOrders').click(function () {
                                          if($('#textarea').val() == ''){
                                                 layer.alert('请输入地址',{icon:0});
                                          }else if($('#receive').val() == 0){
                                              layer.alert('收件人不能为空',{icon:0});
                                          }else if($('#receivedTel').val() == 0){
                                              layer.alert('收件人联系电话不能为空',{icon:0});
                                          }else if($('.receiver-validate-mobile').css('display') == 'inline'){
                                              layer.alert('手机号码不正确',{icon:0});
                                          } else{
                                              var data = {
                                                  province: $("#cmbProvince").find('option:selected').val(),
                                                  provinceName: $("#cmbProvince").find("option:selected").text(),
                                                  city: $("#cmbCity").find('option:selected').val(),
                                                  cityName: $("#cmbCity").find("option:selected").text(),
                                                  zone: $("#cmbArea").find('option:selected').val(),
                                                  zoneName: $("#cmbArea").find("option:selected").text(),
                                                  receiver: $('#receive').val(),
                                                  mobile: $('#receivedTel').val(),
                                                  address: $('#textarea').val(),
                                                  remarks: $('#remark').val(),
                                                  expectsendTime: $('#datetimepicker').val(),
                                                  orderDetail: [{
                                                      shopcartId: '',
                                                      pid: res.data.id,
                                                      color: color,
                                                      size: size,
                                                      num: $('#num').val(),
                                                      shoulder: $('#shoulder').val(),
                                                      bust: $('#bust').val(),
                                                      waist: $('#waist').val(),
                                                      hipline: $('#hipline').val(),
                                                      height: $('#height').val(),
                                                      weight: $('#weight').val(),
                                                      throatheight: $('#throatheight').val(),
                                                      other: $('#other').val()
                                                  }]
                                              };
                                              $.ajax({
                                                  type: 'POST',
                                                  url: '../salesProductOrder/submitOrder',
                                                  contentType: 'application/json; charset=utf-8',
                                                  headers: {
                                                      'Accept': 'application/json; charset=utf-8',
                                                      'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                                  },
                                                  data: JSON.stringify(data),
                                                  success: function (res) {
                                                      if (res.stateCode == 100) {
                                                          $('#myModal4').modal('show');
                                                          $('#myModal4 .sure').click(function () {
                                                              location.reload();
                                                          })
                                                      } else {
                                                          layer.alert(res.message,{icon:0})
                                                      }

                                                  },
                                                  error: function (err) {
                                                      layer.alert(err.message,{icon:0})
                                                  }
                                              })
                                          }
                                         })
                                     }
                                 })
                                 //下单结束
                                }else {
                                    layer.alert(res.message,{icon:0});
                                }
                            },
                            error: function (err) {
                                layer.alert(err.message,{icon:0})
                            }
                        })
                    }
                    $('#container .col').click(function () {
                        id = $(this).attr('id');
                        $('#homePage').css('display','none');
                        $('#xiadans').css('display','none');
                        $('#detail').css('display','block');
                        $('#buyCars').css('display','none');
                        detail(id)
                        //2017.12.11 图文详情单独显示
                        getProductDescription(id)
                    });
                }
             }else {
                 $('#container').html('');
                 $(".tcdPageCode").css('display', 'none');
                 layer.alert(res.message,{icon:0})
             }
             },
             error: function (err) {
                 layer.alert(err.message,{icon:0});
             }
         })
     }
        //分页
        $(".tcdPageCode").createPage({
            pageCount:10,//总共的页码
            current:1,//当前页
            backFn:function(p){//p是点击的页码
                data.pageNum = $("#page").val(p);
                ajax();
            }
        });
        ajax();
        //导航处理
        $('#sidebar ul .sec').click(function () {
            $(this).siblings().removeClass('sec_active');
            $(this).addClass('sec_active');
            data.categoryId = $(this).attr('id');
            $('#homePage').css('display','block');
            $('#xiadans').css('display','none');
            $('#detail').css('display','none');
            $('#buyCars').css('display','none');
            ajax();
        });
        $('#synthesise').click(function () {
            data.sortType = 1;
            ajax();
        });
        $('#salesVolume').click(function () {
            data.sortType = 2;
            ajax();
        });
        $('#prices').click(function () {
            data.sortType = 3;
            ajax();
        });
        $('#new').click(function () {
            data.sortType = 4;
            ajax();
        });
        $('#search').click(function () {
            data.startPrice = $('#startPrice').val();
            data.endPrice = $('#endPrice').val();
            ajax();
        })
    },
    error: function (err) {
        layer.alert(err,{icon:0})
    }
});

//2017.12.11 图文详情单独显示
function getProductDescription(id) {
    $.ajax({
        type: 'POST',
        url:  '../storeHome/getProductDescription',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#description').html(res.data.description);
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    });
}

//购物车
$('#buyCar').click(function () {
    $('#homePage').css('display', 'none');
    $('#xiadans').css('display', 'none');
    $('#detail').css('display', 'none');
    $('#buyCars').css('display', 'block');
    $('#tbody').html('');
    $('#sample-table-2 thead').html('');
    var th = '';
    th = ' <tr style="color: rgb(25,98,155)"><th class="center">商品信息</th><th class="center">单价</th>' +
        '<th class="hidden-480 center">数量</th>' +
        '<th class="center">实付款</th><th class="hidden-480 center">操作</th></tr>';
    $('#sample-table-2 thead').append(th);
    $.ajax({
        type: 'POST',
        url: '../shoppingCart/shopcartList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for(var i = 0; i < res.data.length; i++){
                var tr = '';
                tr += '<tr id="'+res.data[i].id+'"><td class="information_container"><div>' +
                    '<div class="information"><img src="'+res.data[i].imgUrl+'" alt=""></div>' +
                    '<div class="introduce_container">' +
                    '<div class="chinese_introduce">'+res.data[i].name+'</div>' +
                    '<span class="color">颜色：'+res.data[i].color+' &nbsp;&nbsp; 尺寸：'+res.data[i].size+'</span></div>' +
                    '</div></td><td>'+res.data[i].price+'</td><td><span class="glyphicon glyphicon-plus-sign"></span><input class="number" type="number" value="'+res.data[i].number+'" onkeyup="this.value=(this.value.match(/^[1-9]\\d*$/)||[\'\'])[0]">' +
                    '<span class="glyphicon glyphicon-minus-sign"></span></td>' +
                    '<td>'+res.data[i].total+'</td><td class="hidden-480 delete_a">' +
                    '<span class="delete" data-toggle="modal" id="'+res.data[i].id+'">删除</span></td></tr>';
                $('#tbody').append(tr);
            }
 //表格格式化
            function initTableCheckbox(){
                var $thr = '';
                var $thr = $('#sample-table-2 thead tr');
                var $checkAllTh = '';
                var $checkAllTh = $('<th class="center"><input type="checkbox" id="checkAll" name="checkAll" />全选</th>');
                /*将全选/反选复选框添加到表头最前，即增加一列*/
                $thr.prepend($checkAllTh);
                /*“全选/反选”复选框*/
                var $checkAll = $thr.find('input');
                $checkAll.click(function (event) {
                    /*将所有行的选中状态设成全选框的选中状态*/
                    $tbr.find('input').prop('checked', $(this).prop('checked'));
                    /*并调整所有选中行的CSS样式*/
                    if ($(this).prop('checked')) {
                        $tbr.find('input').parent().parent().addClass('xuanzhong');
                    } else {
                        $tbr.find('input').parent().parent().removeClass('xuanzhong');
                    }
                    /*阻止向上冒泡，以防再次触发点击操作*/
                    event.stopPropagation();
                    var xuanz = $('#sample-table-2 .xuanzhong');
                    var nums = 0;
                    var price = 0;
                    for(var i=0; i < xuanz.length; i++){
                        price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                        nums += xuanz[i].children[3].children[1].value - 0;
                    };
                   $('#goodsNums').html(nums);
                   $('#money').html(price);
                });
                /*点击全选框所在单元格时也触发全选框的点击操作*/
                $checkAllTh.click(function () {
                    $(this).find('input').click();

                });
                var $tbr = $('#sample-table-2 tbody tr');
                var $checkItemTd = $('<td><input type="checkbox" name="checkItem" /></td>');
                /*每一行都在最前面插入一个选中复选框的单元格*/
                $tbr.prepend($checkItemTd);
                /*点击每一行的选中复选框时*/
                $tbr.find('input').click(function (event) {
                    /*调整选中行的CSS样式*/
                    $(this).parent().parent().toggleClass('xuanzhong');
                    /*如果已经被选中行的行数等于表格的数据行数，将全选框设为选中状态，否则设为未选中状态*/
                    $checkAll.prop('checked', $tbr.find('input:checked').length == $tbr.length ? true : false);
                    /*阻止向上冒泡，以防再次触发点击操作*/
                    event.stopPropagation();
                    var xuanz = $('#sample-table-2 .xuanzhong');
                    var nums = 0;
                    var price = 0;
                    for(var i=0; i < xuanz.length; i++){
                        price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                        nums += xuanz[i].children[3].children[1].value - 0;
                    };
                    $('#goodsNums').html(nums);
                    $('#money').html(price);
                });
                //删除每一项
                $('.delete_a .delete').click(function () {
                    var _this = $(this);
                    $('#myModal').modal('show');
                    $('#myModal .sure').click(function () {
                        $.ajax({
                            type: 'POST',
                            url: '../shoppingCart/shopcartRemove',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({id: _this.attr('id')}),
                            success: function (res) {
                                _this.parent().parent().remove();
                                $('#myModal').modal('hide')
                                var xuanz = $('#sample-table-2 .xuanzhong');
                                var nums = 0;
                                var price = 0;
                                for(var i=0; i < xuanz.length; i++){
                                    price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                                    nums += xuanz[i].children[3].children[1].value - 0;
                                };
                                $('#goodsNums').html(nums);
                                $('#money').html(price);
                            },
                            error: function (err) {
                             layer.alert(err.message,{icon:0});
                                $('#myModal').modal('hide')
                            }
                        })
                        })

                })
                //清空购物车
                $('#footer .delete_all').click(function () {
                    $('#myModal2').modal('show');
                    $('#myModal2 .sure').click(function () {
                        $.ajax({
                            type: 'POST',
                            url: '../shoppingCart/shopcartRemove',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({id: ''}),
                            success: function (res) {
                                $('#myModal2').modal('hide');
                                $('#tbody .delete').parent().parent().remove();
                                var xuanz = $('#sample-table-2 .xuanzhong');
                                var nums = 0;
                                var price = 0;
                                for(var i=0; i < xuanz.length; i++){
                                    price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                                    nums += xuanz[i].children[3].children[1].value - 0;
                                };
                                $('#goodsNums').html(nums);
                                $('#money').html(price);
                            },
                            error: function (err) {
                                layer.alert(err.message,{icon:0});
                                $('#myModal2').modal('hide');
                            }
                        })
                    })
                });
                //商品数量加减
                $('#tbody td .glyphicon-plus-sign').click(function () {
                    var num = $(this).siblings('input').val() - 0 + 1;
                    $(this).siblings('input').val(num);
                    $(this).parent().parent().children()[4].innerHTML = ($(this).parent().parent().children()[2].innerHTML - 0) * num
                    $.ajax({
                        type: 'POST',
                        url: '../shoppingCart/shopcartUpdateNum',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({id: $(this).parent().parent().attr('id'),num: num}),
                        success: function (res) {
                        },
                        error: function (err) {
                             layer.alert(err.message,{icon:0})
                        }
                    })
                    var xuanz = $('#sample-table-2 .xuanzhong');
                    var nums = 0;
                    var price = 0;
                    for(var i=0; i < xuanz.length; i++){
                        price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                        nums += xuanz[i].children[3].children[1].value - 0;
                    };
                    $('#goodsNums').html(nums);
                    $('#money').html(price);
                });
                $('#tbody td .glyphicon-minus-sign').click(function () {
                    var num = $(this).siblings('input').val() - 0;
                    if (num == 1) {
                        $(this).siblings('input').val('1');
                    } else {
                        num = $(this).siblings('input').val() - 0 - 1;
                        $(this).siblings('input').val(num);
                        $(this).parent().parent().children()[4].innerHTML = ($(this).parent().parent().children()[2].innerHTML - 0) * num
                        $.ajax({
                            type: 'POST',
                            url: '../shoppingCart/shopcartUpdateNum',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({id: $(this).parent().parent().attr('id'),num: num}),
                            success: function (res) {
                            },
                            erroe: function (err) {
                                layer.alert(err.message,{icon:0})
                            }
                        })
                        var xuanz = $('#sample-table-2 .xuanzhong');
                        var nums = 0;
                        var price = 0;
                        for(var i=0; i < xuanz.length; i++){
                            price += (xuanz[i].children[2].innerHTML - 0) * (xuanz[i].children[3].children[1].value - 0);
                            nums += xuanz[i].children[3].children[1].value - 0;
                        };
                        $('#goodsNums').html(nums);
                        $('#money').html(price);
                    }
                });
                //结算
                $('#check').click(function () {
                    var arr = $('#sample-table-2 .xuanzhong');
                    var arr1 = {shopcartIds: []};
                    for(var i = 0; i < arr.length; i++){
                        arr1.shopcartIds.push({id: arr[i].id})
                    }
                    $.ajax({
                        type: 'POST',
                        url: '../salesProductOrder/salesOrder',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify(arr1),
                        success: function (res) {
                            if(res.stateCode == 100){
                                $('#homePage').css('display','none');
                                $('#xiadans').css('display','block');
                                $('#detail').css('display','none');
                                $('#buyCars').css('display','none');
                                $('#sample-table-1 #xiadanInform').html('');
                                $('#customer').html('');
                                $('#xiadanTbody').html('');
                                var totalNums = 0;
                                var totalPri = 0;
                            for(var i = 0; i < res.data.length; i++){
                                var th1 = '';
                                th1 += '<tr><td class="information_container"><div>' +
                                    '<div class="information"><img src="'+res.data[i].href+'" alt=""></div>' +
                                    '<div class="introduce_container">' +
                                    '<div class="chinese_introduce">'+res.data[i].name+'</div>' +
                                    '<span class="color">颜色：'+res.data[i].color+' &nbsp;&nbsp; 尺寸：'+res.data[i].size+'</span></div></div>' +
                                    '</td><td>'+res.data[i].price+'</td><td>'+res.data[i].num+'</td><td>'+res.data[i].total+'</td></tr>';
                                $('#customer').append(th1);
                                totalNums += res.data[i].num;
                                totalPri += res.data[i].total;

                                var th2 = '';
                                th2 += '<tr class="custormInformation">' +
                                    '<td>'+res.data[i].name+'</td><td>'+res.data[i].code+'</td><td>'+res.data[i].categoryName+'</td><td>'+res.data[i].color+'</td>' +
                                    '<td>'+res.data[i].size+'</td><td><input class="shoulder" type="number"></td><td><input class="bust" type="number"></td>' +
                                    '<td><input type="number" class="waist"></td><td><input class="hipline" type="number"></td>' +
                                    '<td><input class="height" type="number"></td><td><input class="weight" type="number"></td>' +
                                    '<td><input class="throatheight" type="number"></td><td><input class="other" type="text"></td></tr>';
                                $('#xiadanTbody').append(th2);

                            };
                            $('#customer').append('<tr>' +
                                '<td class="information_container"></td><td></td><td>数量总计：<span>'+totalNums+'件</span></td>' +
                                '<td>货品金额总计：<span>'+totalPri+'元</span></td></tr>');
                                $('#sample-table #xiadanInforms').html('');
                                $('#sample-table #xiadanInforms').append(' <th class="center">商品名称</th><th class="center">商品ID</th>' +
                                    '<th class="center">分类</th><th class="center">颜色</th>' +
                                    '<th class="center">尺寸</th><th class="center">肩宽</th><th class="center">胸围</th>' +
                                    '<th class="center">腰围</th><th class="center">臀围</th><th class="center">身高</th>' +
                                    '<th class="center">体重</th><th class="center">喉到地</th><th class="center">其他</th>');
                                $('#submitOrders').click(function () {
                                    var arr3 = [];
                                    for(var i = 0; i < res.data.length; i++){
                                       var obj1 = {
                                            shopcartId: res.data[i].id,
                                            pid: res.data[i].pid,
                                            num: res.data[i].num,
                                            color: res.data[i].color,
                                            size: res.data[i].size,
                                            shoulder:$('.shoulder').eq(i).val(),
                                            bust: $('.bust').eq(i).val(),
                                            waist: $('.waist').eq(i).val(),
                                            hipline: $('.hipline').eq(i).val(),
                                            height: $('.height').eq(i).val(),
                                            weight: $('.weight').eq(i).val(),
                                            throatheight: $('.throatheight').eq(i).val(),
                                            other: $('.other').eq(i).val()
                                        }
                                        arr3.push(obj1)
                                    }
                                    var data = {
                                        province: $("#cmbProvince").val(),
                                        provinceName: $("#cmbProvince").find("option:selected").text(),
                                        city: $("#cmbCity").val(),
                                        cityName: $("#cmbCity").find("option:selected").text(),
                                        zone: $("#cmbArea").val(),
                                        zoneName: $("#cmbArea").find("option:selected").text(),
                                        receiver: $('#receive').val(),
                                        mobile: $('#receivedTel').val(),
                                        address: $('#textarea').val(),
                                        remarks: $('#remark').val(),
                                        expectsendTime: $('#datetimepicker').val(),
                                        orderDetail: arr3
                                    };
                                    $.ajax({
                                        type: 'POST',
                                        url: '../salesProductOrder/submitOrder',
                                        contentType: 'application/json; charset=utf-8',
                                        headers: {
                                            'Accept': 'application/json; charset=utf-8',
                                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                        },
                                        data: JSON.stringify(data),
                                        success: function (res) {
                                            $('#myModal4').modal('show');
                                            $('#myModal4 .sure').click(function () {
                                                location.reload();
                                            })
                                        },
                                        error: function (err) {
                                            layer.alert(err.message,{icon:0})
                                        }
                                    })
                                })
                            }else {
                                layer.alert(res.message,{icon:0});
                            }
                        },
                        error: function (err) {
                             layer.alert(err.message,{icon:0});
                        }
                    })
                })

            }
            initTableCheckbox();
        },
        error: function (err) {
            layer.alert(err.message,{icon:0});
        }
    })
});

// 验证手机号码
function checkTel() {
    var pattern = /^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\d{8}$/;
    var phone = document.getElementById('receivedTel').value;
    if(!(pattern.test(phone))){
        $('.receiver-validate-mobile').show();
        return false;
    } else {
        $('.receiver-validate-mobile').hide();
        return false;
    }
}


