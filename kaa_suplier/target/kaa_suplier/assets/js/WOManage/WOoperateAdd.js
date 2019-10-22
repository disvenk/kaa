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
$('#confirm').click(function () {
    if($('#workerNo').val() == ''){
        layer.alert('工人编号不能为空', {icon: 0});
    }else if($('#workerName').val() == ''){
        layer.alert('工人名称不能为空', {icon: 0});
    }else if($('#workerPhone').val() == '' || !reg.test($('#workerPhone').val())){
        layer.alert('手机号格式不正确', {icon: 0});
    }else if(!$("#type input[name=opera]").is(":checked")){
        layer.alert('请选择所属工位', {icon: 0});
    }else if(!reg.test($("#workerPhone").val())){
        layer.alert('手机号码错误', {icon: 0});
    }else {
        var object = document.getElementsByName("opera");
        var check_val = [];
        for (var i = 0; i < object.length; i++) {
            if (object[i].checked) {
                check_val.push({workerStationType: object[i].value})
            }
        }
        var data = {
            id: id,
            code: $('#workerNo').val(),
            name: $('#workerName').val(),
            phone: $('#workerPhone').val(),
            remark: $('#remark').val(),
            workerStationTypeList: check_val
        };
        $.ajax({
            type: 'POST',
            url: '../suplierWorker/addSuplierWorker',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    location.href = '../WOManage/WOoperateHtml';
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