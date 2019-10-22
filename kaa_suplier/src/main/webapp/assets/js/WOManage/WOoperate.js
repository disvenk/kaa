var suplierId;
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
        $('#type').append('<option id="">全部</option>');
        for (var i = 0; i < res.data.length; i++) {
            var options = '';
            options += '<option id="' + res.data[i].id + '">' + res.data[i].name + '</option>';
            $('#type').append(options);
        }
    },
    error: function (err) {
        alert(err.message);
    }
});
var pageNum = 1;
var data = {
    pageNum: pageNum,
    code: '',
    name: '',
    phone: '',
    stationType: ''
}
function list() {
    $.ajax({
        type: 'POST',
        url: '../suplierWorker/workerList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            suplierId = res.data.suplierId;
            if (res.stateCode == 100) {
                $('#tbody').html('');
                if (res.data.worklist.length < 1) {
                    $('#display').css('display', 'block');
                    $('.tcdPageCode').html('');
                }else {
                    //分页
                    $('#display').css('display', 'none');
                    $('.tcdPageCode').html('');
                    $(".tcdPageCode").createPage({
                        pageCount: res.pageSum,//总共的页码
                        current: 1,//当前页
                        backFn: function (p) {//p是点击的页码
                            data.pageNum = $(".current").html();
                            ajax();
                        }
                    });
                    ajax();
                }
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
}
list();
function ajax() {
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../suplierWorker/workerList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.close(index);
                 for(var i = 0; i < res.data.worklist.length; i++){
                     var tt = '';
                     var tr = '';
                     tr += '   <tr class="'+res.data.worklist[i].id+'">\n' +
                         '                                <td>'+res.data.worklist[i].code+'</td>\n' +
                         '                                <td>'+res.data.worklist[i].name+'</td>\n';
                     for(var j = 0; j < res.data.worklist[i].stationTypeList.length; j++){
                          // tt += res.data.worklist[i].stationTypeList[j].stationType + ' ';
                          tt += res.data.worklist[i].stationTypeList[j].stationName + ' ';
                      }
                       tr += '<td>'+tt+'</td>';
                       tr +=  '<td>'+res.data.worklist[i].phone+'</td>' +
                         '                                <td>'+res.data.worklist[i].updateDate+'</td>\n' +
                         '                                <td>'+res.data.worklist[i].remark+'</td>\n' +
                         '                                <td>\n' +
                         '                                    <div class="local-edit" onclick="edit('+res.data.worklist[i].id+')">编辑</div>\n' +
                         '                                    <div class="local-del" onclick="deleteWorker('+res.data.worklist[i].id+')">删除</div>\n' +
                           ' <div class="local-del" onclick="resertPassword('+res.data.worklist[i].id+')">重置密码</div>';
                         '                                </td>\n' +
                         '                            </tr>';
                     $('#tbody').append(tr);
                 }
            } else {
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
};
//搜索
$('#btnQuery').click(function () {
    data = {
        pageNum: pageNum,
        code: $('#workerNo').val(),
        name: $('#workerName').val(),
        phone: $('#phoneNum').val(),
        stationType: $('#type').find("option:checked").attr("id")
    };
   list();
});
//重置
$('#resert').click(function () {
    $('#workerNo').val('');
    $('#workerName').val('');
    $('#phoneNum').val('');
    $('#type').val('');
    data = {
        pageNum: 1,
        code: '',
        name: '',
        phone: '',
        stationType: ''
    };
    list();
});
//新增
$('#btnAdd').click(function () {
    location.href='../WOManage/WOoperateAddHtml';
});
//编辑
function edit(id) {
    location.href='../WOManage/WOoperateEditHtml?id='+id+'';
}
//删除
function deleteWorker(id) {
    $('#myModal').modal('show');
    $('#suredelete').click(function () {
        $.ajax({
            type: 'POST',
            url: '../suplierWorker/removeSuplierWorker',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#myModal').modal('hide');
                    list();
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    });
}
function keyLogin(){
    if (event.keyCode==13)  //回车键的键值为13
        $('#btnQuery').click(); //调用登录按钮的登录事件
}
//重置密码
function resertPassword(id) {
    $('#resertModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../suplierWorker/resetPassword',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#resertModal').modal('hide');
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
}