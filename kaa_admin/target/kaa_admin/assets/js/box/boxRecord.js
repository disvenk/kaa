var url = window.location.href;
var type = url.split('=')[1];
laydate.render({
    elem: '#placeOrder',range: true
});
laydate.render({
    elem: '#give',range: true
});

var data = {
    pageNum: 1,

};

//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    var username = $('#username').val();
    var status = $('#status').find("option:checked").attr("value");

    data = {
        username: username,
        status: status,
        startTime: $('#placeOrder').val().split(' ')[0]?$('#placeOrder').val().split(' ')[0]: '',
        endTime: $('#placeOrder').val().split(' ')[2]?$('#placeOrder').val().split(' ')[2]: '',
        pageNum: 1
    };
    list()
});
//重置
$('#orderReset').click(function () {
    $('.spinner').show();
    $('#username').val("");
    $('#placeOrder').val("")
    $('#status').val("");

    data = {
        username: '',
        status:  '',
        startTime:  '',
        endTime:  '',
        pageNum: 1
    };
    list()
});
//列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../boxManage/boxManageRecordList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum=pageNum;
                    ajax();
                }
            });
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../boxManage/boxManageRecordList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
//加载数据
function load(res) {
    $('.spinner').hide();
    var tr = '';
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        tr += '<tr class="sales-order-state" style="background-color:rgb(243,242,244);height: 50px;">\n' +
            '                                            <td style="border: none"><span class="color-blue">记录号:</span>'+res.data[i].orderNo+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">时间：</span>'+res.data[i].createTime+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">客户地址：</span>'+res.data[i].address+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">收件人：</span>'+res.data[i].receiver+'</td>\n' +
            '                                            <td style="border: none"><span class="color-blue">收件人电话：</span>'+res.data[i].receiveTel+'</td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                            <td style="border: none"></td>\n' +
            '                                        </tr>\n';
        for (var j = 0; j < res.data[i].boxRecordList.length; j++) {
            var product = res.data[i].boxRecordList[j]
            tr += '                                        <tr class="'+res.data[i].id+'">\n' +
                '                                            <td>\n' +
                '                                                <div class="order-info-wrap">\n' +
                '                                                    <img class="record-img" src="'+product.href+'" alt="">\n' +
                '                                                    <div class="record-name" style="width: 160px">商品ID：'+product.productCode+'' +
                '                                                    </div>\n' +
                '                                                    <div class="record-name" style="width: 198px">供应商产品编号：'+product.pno+'' +
                '                                                    </div>\n' +
                '                                                    <div class="record-name">商品名称：'+product.name+'' +
                '                                                    </div>\n' +
                '                                                </div>\n' +
                '                                            </td>\n' +
                '                                            <td>￥'+product.price+'</td>\n' +
                '                                            <td>X'+product.count+'</td>\n' +
                '                                            <td>'+product.payStatusName+'</td>\n' +
                '                                                 <td>'+ res.data[i].statusName +'</td>\n'+
                '                                                 <td>'+ res.data[i].username+'</br>'+res.data[i].loginName+'</td>\'' +
                '                                           <td>' +
                '                                               <div class="qrcode setting-btn" onclick="findQrcode('+product.boxProductId+',\''+product.href+'\','+ product.id +',' + res.data[i].orderNo +')">查看二维码</div>' +
                '                                           </td>';
            tr +=  '                                         </td>\n' +
                '                                           <td>\n';
            //状态  0：未发出    1：已发出   2：已退回   3：已完成
            if (res.data[i].status == 0) {
                tr += '                                          <div class="sales-order-operate" style="cursor: pointer" onclick="deliver('+res.data[i].id+')">寄出</div>\n';
                tr += '                                          <div class="sales-order-operate mt4" style="cursor: pointer" onclick="logisticsRecord('+res.data[i].id+')"><span>物流记录</span></div>\n' ;
            } else if (res.data[i].status == 2) {
                tr += '                                          <div class="sales-order-operate mt4" style="cursor: pointer" onclick="sure1('+res.data[i].id+')">确认</div>\n';
                tr += '                                          <div class="sales-order-operate mt4" style="cursor: pointer" onclick="logisticsRecord('+res.data[i].id+')">物流记录</div>\n';
            }else{
                tr += '                                          <div class="sales-order-operate" style="cursor: pointer" onclick="logisticsRecord('+res.data[i].id+')">物流记录</div>\n';
            }
        }
    }
    $('#tbody').append(tr);

    //合并列
    var tr = $('#tbody tr');
    for(var i = 0; i < tr.length; i++) {
        if (tr.eq(i).attr('class') == tr.eq(i + 1).attr('class')) {
            // tr.eq(i + 1).find('td').eq(5).remove();
            // tr.eq(i).find('td').eq(5).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);
            //
            // tr.eq(i + 1).find('td').eq(4).remove();
            // tr.eq(i).find('td').eq(4).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);
            // 记住，此方法，只适合合并最后一列单元格，请勿随意CV
            tr.eq(i + 1).find('td').eq(7).remove();
            tr.eq(i).find('td').eq(7).attr('rowspan', $('.' + tr.eq(i).attr('class') + '').length);
        }
    }
}

//快递信息
function logisticsRecord(id) {
    $.ajax({
        type: 'POST',
        url: '../boxManage/boxRecordDeliveryList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('#deliveryModal').modal('show');
            $('#deliveryModal iframe').attr('src', '');
            $('#deliveryModal tbody').html('');
            if(res.stateCode == 100){
                for(var i = 0; i < res.data.length; i++){
                    var ts = '';
                    ts += '<tr><td>'+res.data[i].createTime+'</td><td>'+res.data[i].deliveryCompanyName+'</td>' +
                        '<td>'+res.data[i].deliveryNo+'</td><td class="'+res.data[i].deliveryCompany+'" style="cursor: pointer" onclick="check($(this),'+res.data[i].deliveryNo+')">查看物流</td></tr>';
                    $('#deliveryModal tbody').append(ts);
                }
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}
//获取快递信息
function check(_this, no) {
    $.ajax({
        type: 'POST',
        url: '../express/searchExpress',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({com: _this.attr('class'), nu: no}),
        success: function (res) {
            if(res.stateCode == 100){
                $('#deliveryModal iframe').attr('src', res.data.result);
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    })
}

//发货
function deliver(id) {
    $('#sendModal').modal('show');
    //快递公司
    $.ajax({
        type: 'POST',
        url: '../base/getBaseDataList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({parameterType: 5}),
        success: function (res) {
            if (res.stateCode == 100) {
                for (var i = 0; i < res.data.length; i++) {
                    var option = '';
                    option += '<option id="' + res.data[i].id + '" value=' + res.data[i].name + '>' + res.data[i].name + '</option>';
                    $('#company').append(option);
                }
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    });

    $('#sure1').unbind('click');
    $('#sure1').one('click',function () {
        var ship = {
            deliveryCompany: $('#company').find('option:checked').attr('id'),
            deliveryCompanyName: $('#company').val(),
            deliveryNo: $('#orderNo').val(),
            id: id
        };
        $.ajax({
            type: 'POST',
            url: '../boxManage/boxRecordDeliver',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ship),
            success: function (res) {
                $('#sendModal').modal('hide');
                if(res.stateCode == 100){
                    list();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    })
}


//确认收货
function sure1(id) {
    $.ajax({
        type: 'POST',
        url: '../boxManage/boxRecordPayStatus',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                ajax();
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}



//查看二维码
function findQrcode(id, src, orderId, orderNo) {
    console.log(src);
    $('#myModal3').modal('show');
    $('#qrCode').html('');
    var qrcode = new QRCode(document.getElementById('qrCode'), {
        width : 300,//设置宽高
        height : 300
    });
    qrcode.makeCode("http://s.heyizhizao.com/boxMobile/weixinAuthorizationHtml?id="+id+"&type=2&orderId=" + orderId + "&orderNo=" + orderNo);
    var img = '<img style="width: 65px;height: 65px;position: absolute;left: 133px;top: 140px;right: 0px;bottom: 0px;border: 5px solid #fff" ' +
        'src="'+src+'" alt="">';
    $("#qrCode").append(img);
}

