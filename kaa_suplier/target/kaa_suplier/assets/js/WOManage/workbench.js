
//初始化加载所有列表数据
var pageNum = 1;
var data = {
    pageNum: pageNum
};

$.ajax({
    type: 'POST',
    url: '../suplierHome/supplierHomeInfo',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success:function (res) {
        // console.log(res);
        $('#currentSalesOrderCount').html(res.data.currentSalesOrderCount);
        $('#currentSalesOrderSum').html(res.data.currentSalesOrderSum);
        $('#lastSalesOrderCount').html(res.data.lastSalesOrderCount);
        $('#lastSalesOrderSum').html(res.data.lastSalesOrderSum);
        $('#salesOrderCountBy2').html(res.data.salesOrderCountBy2);
        $('#salesOrderCountBy3').html(res.data.salesOrderCountBy3);
        $('#orderUrgent').html(res.data.orderUrgent);
        $('#currentSalesOrderCountBy7').html(res.data.currentSalesOrderCountBy7);
        $('#lastSalesOrderCountBy7').html(res.data.lastSalesOrderCountBy7);
        $('#currentOrderCount').html(res.data.currentOrderCount);
        $('#currentOrderSum').html(res.data.currentOrderSum);
        $('#lastOrderCount').html(res.data.lastOrderCount);
        $('#lastOrderSum').html(res.data.lastOrderSum);
    }
})

//日期列表

function dateShow(type) {
    $.ajax({
        type: 'POST',
        url: '../suplierHome/supOrderDeliveryList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({type :type}),
        success:function (res) {
            if(res.data.length == 0){
                if(type==1){
                        $('#leftNum').html(0);
                        for(var i=0;i<2;i++){
                            var part='';
                            part+='<div class="right-item"><span class="right-item-span l brc"></span><span class="right-item-span r"></span></div>';
                            $('.part-a').append(part);
                        }
                } else if (type==2){
                        $('#rightNum').html(0);
                        for(var i=0;i<2;i++){
                            var part='';
                            part+='<div class="right-item"><span class="right-item-span l brc"></span><span class="right-item-span r"></span></div>';
                            $('.part-b').append(part);
                        }
                }
            }else{
                for(var i=0;i<res.data.length;i++){
                    var part='';
                    part+='<div class="right-item"><span class="right-item-span l brc">'+res.data[i].orderNo+'</span><span class="right-item-span r">'+res.data[i].deliveryDate+'</span></div>';
                    if(type==1){
                        $('#leftNum').html(res.data.length)
                        $('.part-a').append(part);
                    } else if (type==2){
                        $('#rightNum').html(res.data.length)
                        $('.part-b').append(part);
                    }
                }
            }

        }
    })
}
dateShow(1);
dateShow(2);
$('.tab').click(function () {
    $('.tab').removeClass('active');
    $(this).addClass('active');
})

$('.tab.l').click(function () {
    $('.part-a').css('display','block');
    $('.part-b').css('display','none');
    $('.part-a').html('');
    $('.part-b').html('');
    dateShow(1);
})

$('.tab.r').click(function () {
    $('.part-a').css('display','none');
    $('.part-b').css('display','block');
    $('.part-a').html('');
    $('.part-b').html('');
    dateShow(2);
})
