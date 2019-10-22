laydate.render({
    elem: '#placeOrder',range: true
});
var url = window.location.href;
var type = url.split('?')[1];
var data = {
    dateStr: '',
    dateEnd: ''
};
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};
function lastMonth() {
    var monthDate = new Date();
    monthDate.setDate(1);
    monthDate.setMonth(monthDate.getMonth()-1);
    var lastMonth = monthDate.getMonth() + 1;
    var dateStr = (monthDate.Format("yyyy-MM-dd"));
    var currentMonth=monthDate.getMonth();
    var nextMonth=++currentMonth;
    var nextMonthFirstDay=new Date(monthDate.getFullYear(),nextMonth,1);
    var oneDay=1000*60*60*24;
    var dateEnd = new Date(nextMonthFirstDay-oneDay).Format("yyyy-MM-dd");
    data.dateStr = dateStr + ' 00:00:00';
    data.dateEnd = dateEnd + ' 23:59:59';
    $('#placeOrder').val(dateStr+ ' - ' +dateEnd);
}
function currentMonth() {
    var monthDate = new Date();
    monthDate.setDate(1);
    monthDate.setMonth(monthDate.getMonth()-0);
    var lastMonth = monthDate.getMonth() + 1;
    var dateStr = (monthDate.Format("yyyy-MM-dd"));
    var currentMonth=monthDate.getMonth();
    var nextMonth=++currentMonth;
    var nextMonthFirstDay=new Date(monthDate.getFullYear(),nextMonth,1);
    var oneDay=1000*60*60*24;
    var dateEnd = new Date(nextMonthFirstDay-oneDay).Format("yyyy-MM-dd");
    data.dateStr = dateStr + ' 00:00:00';
    data.dateEnd = dateEnd + ' 23:59:59';
    $('#placeOrder').val(dateStr+ ' - ' +dateEnd);
}
if(type == 1){
    currentMonth();
    ajax();
}else{
    lastMonth();
    ajax();
}
function ajax() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../suplierHome/supOrderCustomerList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            layer.close(index);
            if(res.stateCode == 100){
                $('#tbody').html('');
                if(res.data.length > 0){
                    for(var i = 0; i < res.data.length; i++){
                        var category = '';
                        category += ' <tr><td>'+res.data[i].customer+'</td>'+
                            '<td>'+res.data[i].orderCount+'</td> <td>'+res.data[i].deliveryCount+'</td></tr>';
                        $('#tbody').append(category);
                    }
                }else{
                    $('#sample-table-2').hide();
                    $('#display').show();
                }
            }else{
                layer.alert(res.message, {icon: 0});
            }
        },error: function (err) {
            layer.close(index);
            layer.alert(err.message, {icon: 0});
        }
    });
}
//
$('#check').click(function () {
    data = {
        dateStr: $('#placeOrder').val().split(' ')[0] + ' 00:00:00'?$('#placeOrder').val().split(' ')[0] + ' 23:59:59': '',
        dateEnd: $('#placeOrder').val().split(' ')[2] + ' 00:00:00'?$('#placeOrder').val().split(' ')[2] + ' 23:59:59': ''
    };
    ajax();
});