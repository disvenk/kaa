laydate.render({
    elem: '#dateStart'
});
laydate.render({
    elem: '#dateEnd'
});

var data = {};
$(".toDetail").click(function () {
    data = {
        dateStr : $("#dateStart").val(),
        dateEnd : $("#dateEnd").val()
    }
    findWorkerSupOrder();
});

//获取工人已扫码的工单
findWorkerSupOrder();
function findWorkerSupOrder() {
    $.ajax({
        type: 'POST',
        url: '../workerHome/findWorkerSupOrder',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(data),
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            if (res.stateCode == 100){
                $("#tbody").html("");
                var sum = 0;
                for (var i=0; i<res.data.length; i++) {
                    if (res.data[i].productionDate == null) res.data[i].productionDate = '';
                    var tr = '<tr>\n' +
                        '                <td ';
                    if (res.data[i].urgent == 2) tr += 'class="flag"';
                    tr +='>'+ res.data[i].orderNo +'</td>\n' +
                        '                <td>'+ res.data[i].procedureName +'</td>\n' +
                        '                <td>'+ res.data[i].price +'</td>\n' +
                        '                <td>'+ res.data[i].productionDate +'</td>\n' +
                        '            </tr>';
                    $("#tbody").append(tr);
                    sum += res.data[i].price;
                }
                $('.number').html(res.data.length);
                $('.price').html(sum);

            } else {
                layer.msg(res.message)
            }
        },
        error: function (err) {
            layer.msg(err)
        }
    });
}