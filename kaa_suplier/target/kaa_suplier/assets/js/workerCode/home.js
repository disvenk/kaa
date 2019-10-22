$('.toDetail').click(function () {
    location.href = '../suplierHome/workerDetailHtml'
})
$('#logOut').click(function () {
    location.href = '../suplierHome/loginHtml'
})



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





// 工资饼图
var chart = document.getElementById('salary');
var myChart = echarts.init(chart);
var tips = 0; //总金额
var orderAccount = 0; //总工单数
function loadingOption() {
    var option = {
        title: [{
            text: '￥' + tips,
            x: 'center',
            y: '124px',
            textStyle: {
                color: '#333',
                fontSize: 30,
            }
        }, {
            text: '合计工单:' + orderAccount,
            x: 'center',
            y: '164px',
            textStyle: {
                color: '#333',
                fontSize: 14,
                fontWeight: 100
            }
        }],

        series: [{
            name: 'loading',
            type: 'pie',
            radius: ['58%', '66%'],
            hoverAnimation: false,
            label: {
                normal: {
                    show: false,
                }
            },
            data: [{
                value: 10000 - tips < 0 ? 10000 : tips,
                itemStyle: {
                    normal: {
                        color: '#00c7ab',
                    }
                }
            }, {
                value: 10000 - tips < 0 ? 0 : 10000 - tips,
                itemStyle: {
                    normal: {
                        color: '#465262',
                    }
                }
            }],
        }]
    };
    return option;
}

//加载六个月的数据
var monthMax = 6;
var lastMonthList = new Array(monthMax);// 用于我的收入 月份展示
var dateList = new Array(monthMax);//用于获取我的收入 接口数据
//加载工资饼图月份列表
loadSalaryMonth();
function loadSalaryMonth() {
    for (var i=monthMax-1; i >= 0; i--) {
        var monthDate = new Date();
        monthDate.setDate(1);
        monthDate.setMonth(monthDate.getMonth()-i);
        var lastMonth = monthDate.getMonth() + 1;
        var dateStr = (monthDate.Format("yyyy-MM-dd"));

        //记录我的收入的月份
        lastMonthList[monthMax-1-i] = (lastMonth + "月");
        dateList[monthMax-1-i] = ({date: monthDate.Format("yyyy-MM")});

        monthDate.setMonth(monthDate.getMonth()+1);
        var dateEnd = (monthDate.Format("yyyy-MM-dd"));

        var li = '<li class="date-item" onclick="findWorkerSalaryMonth(\''+dateStr+'\',\''+dateEnd+'\')">'+lastMonth+'月</li>'
        if (i == 0){
            li = '<li class="date-item br0 active" onclick="findWorkerSalaryMonth(\''+dateStr+'\',\''+dateEnd+'\')">当月</li>'
            //获取工资拼图数据
            findWorkerSalaryMonth(dateStr, dateEnd);

        }
        $("#salaryMonth").append(li);

    }
    $('.date-item').click(function () {
        $('.date-item').removeClass('active');
        $(this).addClass('active');
    })
}



//获取工资饼图数据
function findWorkerSalaryMonth(dateStr, dateEnd) {
    chart.removeAttribute("_echarts_instance_");//初始化控件
    myChart = echarts.init(chart);
    myChart.showLoading();//增加loading样式
    $.ajax({
        type: 'POST',
        url: '../workerHome/findWorkerOrder',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({dateStr:dateStr, dateEnd:dateEnd}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100){
                tips = res.data.priceSum;
                orderAccount = res.data.orderCount;

                // 使用刚指定的配置项和数据显示图表。
                myChart.hideLoading();
                myChart.setOption(loadingOption());
            } else {
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err)
        }
    });

}





// 工资折线图
var chart1 = document.getElementById('salaryTrend');
var myChart1 = echarts.init(chart1);
var seriesData = new Array();
function loadingOption1() {
    var option1 = {
        toolbox: {
            show: true,
            y:-8,
            feature: {
                magicType: {type: ['line', 'bar']},
            }
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            // data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            data: lastMonthList
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}'
            }
        },
        series: [
            {
                name: '工资',
                type: 'line',
                // data: [5, 6.5, 3.1, 4.5, 5.0, 90.1, 5.5, 4.0, 6.5, 8.0, 7.0, 5.0],
                data: seriesData,
                markPoint: {
                    data: [
                        {type: 'max', name: '最大值'},
                        {type: 'min', name: '最小值'}
                    ]
                },
                markLine: {
                    data: [
                        {type: 'average', name: '平均值'}
                    ]
                }
            }
        ]
    };
    return option1;
}


//获取我的收入 数据
findWorkerMonthOrder();
function findWorkerMonthOrder() {
    // chart1.removeAttribute("_echarts_instance_");//初始化控件
    // myChart1 = echarts.init(chart1);
    myChart1.showLoading();//增加loading样式
    $.ajax({
        type: 'POST',
        url: '../workerHome/findWorkerMonthOrder',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({dateList: dateList}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100){
                for (var i=0; i<res.data.length; i++) {
                    seriesData[i] = res.data[i].priceSum <= 0 ? 0 : res.data[i].priceSum;
                }
                // 使用刚指定的配置项和数据显示图表。
                myChart1.hideLoading();
                myChart1.setOption(loadingOption1());
            } else {
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err)
        }
    });


}



//获取工人已扫码的工单
findWorkerSupOrder();
function findWorkerSupOrder() {
    $.ajax({
        type: 'POST',
        url: '../workerHome/findWorkerSupOrder',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify({}),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100){
                for (var i=0; i<res.data.length; i++) {
                    if (res.data[i].deliveryDate == null) res.data[i].deliveryDate = '';
                    var tr = '<tr>\n' +
                        '                <td ';
                    if (res.data[i].urgent == 2) tr += 'class="flag"';
                    tr +='>'+ res.data[i].orderNo +'</td>\n' +
                        '                <td>'+ res.data[i].procedureName +'</td>\n' +
                        '                <td>'+ res.data[i].productionDate +'</td>\n' +
                        '                <td>'+ res.data[i].deliveryDate +'</td>\n' +
                        '            </tr>';
                    $("#tbody").append(tr);
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



//扫一扫
$(function () {
    var  url = window.location.href.split('#')[0];
    var  encodeURI = encodeURIComponent(url);
    $.ajax({
        type: 'GET',
        url: '../suplierHome/getshareinfo?url='+encodeURI,
        dataType: 'json',
        async: false,
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic "
        },
        success: function(res){
            if(res.stateCode == 100){
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: res.data.appid, // 必填，公众号的唯一标识
                    timestamp: res.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: res.data.noncestr, // 必填，生成签名的随机串
                    signature: res.data.signature,// 必填，签名，见附录1
                    jsApiList: ['scanQRCode'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
                wx.error(function(res){
                    layer.msg('配置错误')
                })
            }else{
                layer.msg(res.message);
            }
        },
        error:function (err) {
            layer.msg(err.message);
        }
    });
});
$("#scan").click(function() {
    wx.scanQRCode({
        // 默认为0，扫描结果由微信处理，1则直接返回扫描结果
        needResult : 1,
        desc : 'scanQRCode desc',
        success : function(res) {
            //扫码后获取结果参数赋值给Input
            var url = res.resultStr;
            location.href = url;
            // //商品条形码，取","后面的
            // if(url.indexOf(",")>=0){
            //     var tempArray = url.split(',');
            //     var tempNum = tempArray[1];
            //     $("#id_securityCode_input").val(tempNum);
            // }else{
            //     $("#id_securityCode_input").val(url);
            // }
        }
    });
});