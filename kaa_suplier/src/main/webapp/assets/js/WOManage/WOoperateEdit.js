//所属工位
$.ajax({
    type: 'POST',
    // url: '../base/getAllSupWorkerStationType',
    url: '../procedure/findProcedureListCombox',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for (var i = 0; i < res.data.length; i++) {
            var options = '';
            options += '<input type="checkbox" name="opera" value="' + res.data[i].id + '">' + res.data[i].name;
            $('#type').append(options);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});

var url = window.location.href;
var id = url.split('=')[1];
var reg =/^[1][3,4,5,7,8][0-9]{9}$/;
var suplierId;
$.ajax({
    type: 'POST',
    url: '../suplierWorker/suplierWorkerDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if(res.stateCode == 100){
            suplierId = res.data.suplierId;
           $('#workerNo').val(res.data.code);
            $('#workerName').val(res.data.name);
            $('#workerPhone').val(res.data.phone);
            $('#remark').val(res.data.remark);
            $('#data').html(res.data.updateDate);
            if (res.data.stationTypeList.length == 0) {
            } else {
                for (var i = 0; i < res.data.stationTypeList.length; i++) {
                    $('#type input[value=' + res.data.stationTypeList[i].stationType + ']').attr('checked', true);
                }
            }
        }else{
            layer.alert(res.message, {icon: 0})
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
//提交保存
$('#confirm').click(function () {
    var object = document.getElementsByName("opera");
    var check_val = [];
    for (var i = 0; i < object.length; i++) {
        if (object[i].checked) {
            check_val.push({workerStationType: object[i].value})
        }
    }
   var data = {
       id: id,
       suplierId: suplierId,
       code: $('#workerNo').val(),
       name: $('#workerName').val(),
       phone: $('#workerPhone').val(),
       remark: $('#remark').val(),
       updateDate: $('#data').html(),
       workerStationTypeList: check_val
   };
    if(data.code == ''){
        layer.alert('工人编号不能为空', {icon: 0});
    }else if(data.name == ''){
        layer.alert('工人名称不能为空', {icon: 0});
    }else if($('#workerPhone').val() == '' || !reg.test($('#workerPhone').val())){
        layer.alert('手机号格式不正确', {icon: 0});
    }else if(data.workerStationTypeList.length == 0){
        layer.alert('请选择所属工位', {icon: 0});
    }else if(!reg.test(data.phone)){
        layer.alert('手机号码错误', {icon: 0});
    }else {
        $.ajax({
            type: 'POST',
            url: '../suplierWorker/saveSuplierWorker',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#myModal').modal('show');
                    $('#sure').click(function () {
                        $('#myModal').modal('hide');
                        location.href = '../WOManage/WOoperateHtml';
                    });
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon:0});
            }
        });
    }
});