var url = window.location.href;
var id = url.split('=')[1];

var worderOpts = '';
$.ajax({
    type: 'POST',
    url: '../produceRecord/getAllWorkerName',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        if(res.stateCode == 100){
            // $('.recordWorker').html('');
            // $('.recordWorker').append('<option class=""></option>');
            worderOpts += '<option class=""></option>';
            for(var i = 0; i < res.data.length; i++){
                // var option = '';
                worderOpts += '<option class="'+res.data[i].workId+'">'+res.data[i].name+'</option>';
                // $('.recordWorker').append(option);
            }
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        alert(err.message);
    }
});

var detailProcedureIds = new Array();
$.ajax({
    type: 'POST',
    url: '../produceRecord/produceRecordDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            // suplierId = res.data.suplierId;
            $('#orderNo').html(res.data.orderNo);
            $('#productCode').html(res.data.pno);
            $('#img2').attr('src', res.data.href);
            $('#categoryName').html(res.data.categoryName);
            $('#color').html(res.data.color);
            $('#qty').html(res.data.qty);
            $('#producedStatusName').html(res.data.producedStatusName);
            for(var i = 0; i < res.data.procedureTypeList.length; i++){

                var div = '';
                div += '<div class="order-detail-wrap clearfix">\n' +
                    '                                <div class="order-detail w17 l">'+ res.data.procedureTypeList[i].procedureName +'</div>\n' +
                    '                                <div class="order-detail w81 r" id="pro'+ res.data.procedureTypeList[i].detailProcedureId +'">\n' +
                    '                                    <select class="recordWorker">\n' +
                    worderOpts +
                    '                                    </select>\n' +
                    '                                    <input type="text" class="workerDate">\n' +
                    '                                </div>\n' +
                    '                            </div>';
                $('#procedures').append(div);

                detailProcedureIds[i] = res.data.procedureTypeList[i].detailProcedureId;

                $('#pro' + res.data.procedureTypeList[i].detailProcedureId + ' .recordWorker').val(res.data.procedureTypeList[i].workerName);
                $('#pro' + res.data.procedureTypeList[i].detailProcedureId + ' .workerDate').val(res.data.procedureTypeList[i].productionDate);
            }


            laydate.render({
                elem: '.workerDate',
                type: 'datetime'
            });

        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        layer.alert(err.message, {icon: 0});
    }
});


//提交保存
$('#confirm').click(function () {
    var produceRecordStationTypeList = [];
    for(var i = 0; i < detailProcedureIds.length; i++){
        var obj = {};
        obj = {
            detailProcedureId: detailProcedureIds[i],
            updateDate: $('#pro' + detailProcedureIds[i] + ' .workerDate').val(),
            workerName:  $('#pro' + detailProcedureIds[i] + ' .recordWorker').find("option:checked").text(),
            workerId: $('#pro' + detailProcedureIds[i] + ' .recordWorker').find("option:checked").attr("class")
        };
        produceRecordStationTypeList[i] = obj;
    }
    var data = {
        id: id,
        produceRecordStationTypeList: produceRecordStationTypeList
    };
    $.ajax({
        type: 'POST',
        url: '../produceRecord/saveproduceRecord',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                layer.alert(res.message, {icon: 1});
                setTimeout(function () {
                    location.href = '../WOManage/WOrecordHtml';
                },1500);
            }else{
                layer.alert(res.message, {icon: 0})
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
});