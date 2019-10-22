$.ajax({
    type: 'POST',
    url:  '../boxHome/boxInfo',
    contentType: 'application/json; charset=utf-8',
    dataType: 'json',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if(res.stateCode == 100){
                if(res.data.id == null){
                    $('.state1').css('display', 'none');
                    $('.state2').css('display', 'block');
                    $('.tcdPageCode').css('display', 'none');
                }else{
                    $('.state1').css('display', 'block');
                    $('.state2').css('display', 'none');
                    $('.box-time').html(res.data.count);
                    $('.box-money').html(res.data.deposit);
                    $('.box-date').html(res.data.termTime);
                }
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0})
    }
});
$('.forbid-btn').click(function () {
    window.open('../salesHome/boxJoinHtml?boxIds=');
});
var pageNum = 1;
var data = {
    pageNum: pageNum
};
function list() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url:  '../boxHome/boxUseLogList',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            layer.close(index);
            if(res.stateCode == 100){
                $('#list').html('');
                if(res.data.length < 1){
                    $('.tcdPageCode').css('display', 'none');
                }else{
                    load(res);
                    //分页
                    $('.tcdPageCode').css('display', 'block');
                    $('.tcdPageCode').html('');
                    $(".tcdPageCode").createPage({
                        pageCount:res.pageSum,//总共的页码
                        current:1,//当前页
                        backFn:function(p){//p是点击的页码
                            data.pageNum = $(".current").html();
                            ajax();
                        }
                    });
                }
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },
        error: function (err) {
            $('.spinner').hide();
            layer.alert(err.message, {icon: 0})
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../boxHome/boxUseLogList',
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
    $('#list').html('');
    for(var i = 0; i < res.data.length; i++){
        var tr = '';
        tr += '<table class="table table-bordered mt10"><tbody>'+
            '<tr> <td colspan="6" style="text-align: left;background-color: #f3f2f4;">'+
            '时间 <span class="record-time"style="margin-right: 30px">'+res.data[i].createTime+'</span>记录号: <span class="record-no">'+res.data[i].orderNo+'</span>'+
        '</td></tr>';
        for(var j = 0; j < res.data[i].productList.length; j++){
            tr += '<tr><td style="width: 30%;min-width: 284px;"><img class="record-img" src="'+res.data[i].productList[j].href+'" alt="">'+
                '<div class="record-name">'+res.data[i].productList[j].name+'</div> </td> <td style="width: 14%">￥'+res.data[i].productList[j].price+'</td>'+
                '<td style="width: 14%">'+res.data[i].productList[j].count+'件</td> <td style="width: 14%">'+res.data[i].productList[j].statusName+'</td> ' +
                '<td style="width: 14%">'+res.data[i].statusName+'</td>';
            if(res.data[i].status == 1){
                tr += ' <td style="width: 14%;color: rgb(101,161,199);"><div style="cursor: pointer" class="'+res.data[i].deliveryCom+'" value="'+res.data[i].deliveryCompanyName+'" onclick="delivery($(this), '+res.data[i].deliveryNo+')">查看物流</div>' +
                    '<div style="cursor: pointer" class="'+res.data[i].deliveryCom+'" value="'+res.data[i].deliveryCompanyName+'" onclick="back('+res.data[i].id+')">退回</div></td></tr>';
            }else if(res.data[i].status == 2){
                tr += ' <td style="width: 14%;color: rgb(101,161,199);"><div style="cursor: pointer" class="'+res.data[i].deliveryCom+'" value="'+res.data[i].deliveryCompanyName+'" onclick="delivery($(this), '+res.data[i].deliveryNo+')">查看物流</div></td></tr>';
            } else{
                tr += '<td style="width: 14%;">无</td></tr>';
            }
        }
          tr +=  '</tbody></table>';
        $('#list').append(tr);
    }
    for(var i = 0; i < $('tbody').length; i++){
        if($('tbody').eq(i).find('tr').length > 2){
            for(var j = 1; j < $('tbody').eq(i).find('tr').length; j++){
                $('tbody').eq(i).find('tr').eq(j + 1).find('td').eq(5).remove();
                $('tbody').eq(i).find('tr').eq(j).find('td').eq(5).attr('rowspan', $('tbody').eq(i).find('tr').length - 1);
            }
        }else{

        }
    }
}
//物流
function delivery(it,no) {
    $('#sendInformation').modal('show');
            $('#deliveryNum').html(no);
            $('#deliveryCom').html(it.attr('value'));
            $.ajax({
                type: 'POST',
                url: '../express/searchExpress',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({com: it.attr('class'), nu: no}),
                success: function (res) {
                    $('#iframe').attr('src', res.data.result)
                },
                error: function (err) {
                    alert(err.message);
                }
            })
}
//退回
function back(id) {
    $('#sendNow').modal('show');
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
                    option += '<option id="'+ res.data[i].id +'" value=' + res.data[i].name + '>' + res.data[i].name + '</option>';
                    $('#deliveryCompany').append(option);
                }
                //直接发货
                $('#sendN').click(function () {
                    var param = {
                        id: id,
                        deliveryCompanyName: $('#deliveryCompany').val(),
                        deliveryNo: $('#deliveryNo').val(),
                        deliverCompanyId: $('#deliveryCompany').find('option:checked').attr('id')
                    };
                    if($('#deliveryNo').val() == ''){
                        alert('请填写单号')
                    }else{
                        $.ajax({
                            url: '../boxHome/boxUseLogReturn',
                            type: 'POST',
                            dataType: 'json',
                            data: JSON.stringify(param),
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            success: function (res) {
                                if (res.stateCode == 100) {
                                    $('#sendNow').modal('hide');
                                    list();
                                } else {
                                    layer.alert(res.message,{icon:0})
                                }
                            }
                        })
                    }
                })

            } else {
                layer.alert(err.message,{icon:0})
            }
        },
        error: function (err) {
            layer.alert(err.message,{icon:0})
        }
    });
}