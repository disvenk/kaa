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
        url: '../suplierHome/supOrderWorkerList',
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
                        category += ' <tr><td>'+res.data[i].name+'</td>'+
                            '<td class="modal-show" value="'+res.data[i].id+'" data="'+res.data[i].name+'" style="color: rgb(101,161,199);cursor: pointer">'+res.data[i].orderCount+'</td> <td>'+res.data[i].priceSum+'</td></tr>';
                        $('#tbody').append(category);
                    }
                    $('.modal-show').click(function () {
                        $('#recordModal').modal('show');
                        findWorkerSupOrder($(this).attr('value'),$(this).attr('data'));
                    })
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
$('#check').click(function () {
    data = {
        dateStr: $('#placeOrder').val().split(' ')[0] + ' 00:00:00'?$('#placeOrder').val().split(' ')[0] + ' 23:59:59': '',
        dateEnd: $('#placeOrder').val().split(' ')[2] + ' 00:00:00'?$('#placeOrder').val().split(' ')[2] + ' 23:59:59': ''
    };
    ajax();
});
function findWorkerSupOrder(id,name) {
    $.ajax({
        type: 'POST',
        url: '../workerHome/findWorkerSupOrder',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({workerId: id}),
        success: function (res) {
            if (res.stateCode == 100){
                $("#modalTbody").html("");
                for (var i=0; i<res.data.length; i++) {
                    if (res.data[i].productionDate == null) res.data[i].productionDate = '';
                    var tr = '<tr>\n' +
                        '                <td ';
                    if (res.data[i].urgent == 2) tr += 'class="flag"';
                    tr +='>'+ res.data[i].orderNo +'</td>\n' +
                        '                <td>'+ res.data[i].procedureName +'</td>\n' +
                        '                <td>'+ res.data[i].price +'</td>\n' +
                        '                <td>'+ name +'</td>\n' +
                        '                <td>'+ res.data[i].productionDate +'</td>\n' +
                        '            </tr>';
                    $("#modalTbody").append(tr);
                }
            } else {
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err)
        }
    });
}

