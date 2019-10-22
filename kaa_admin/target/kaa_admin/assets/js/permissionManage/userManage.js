/**
 * Created by 12452 on 2017/10/20.
 */

var categoryid = '';
var pageNum = 1;
var data = {
    pageNum: pageNum
};
var ss;
var urlle = sessionStorage.getItem('urllen');
var productIds = '';

// ajax加载列表
    $.ajax({
        type: 'POST',
        url: '../user/getUserListAllByUser',
        dataType: 'json',

        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        }
    })

var id;
//新增用户
$('#addCategory').click(function () {
    $('#approveModal').modal('show');
    $("#approveModalLabel").html("新增用户")
    $('#userForm')[0].reset();
    $(".roleTD1").html("")

    $.ajax({
        url : '../role/getRoleListAllByUser',
        type : 'POST',
        dataType : 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data:JSON.stringify({pageNum:1}),
        success : function(res) {
            $(res.data).each(function(index){
                //生成CheckBox
                var checkbox=$("<input type='checkbox' name='roleIds1'/>");
                checkbox.val(this.id);
                if(index==0){
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");
                }else if(index%4==0){
                    $(".roleTD1").append("</br>");
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");
                }else{
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");


                }

            });
        },
        error : function(msg) {
            alert('加载异常!');
        }
    });
})

$('#addSure').click(function (){
    var tel = $('#tel').val().replace(/[ ]/g,"");
    var userName = $('#userName').val().replace(/[ ]/g,"");
    var password = $("#password").val().replace(/[ ]/g,"");
    var relName = $("#relName").val().replace(/[ ]/g,"");
    var roleArr= new Array();
    $('input:checkbox[name=roleIds1]:checked').each(function(i){
        console.log(3);
        roleArr.push($(this).val())
    });
    var roleStr = roleArr.join(",");
    ss = {
        tel:tel,
        userName: userName,
        password:password,
        relName:relName,
        roleStr:roleStr,
    }
    var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
    if (tel == ''){
        layer.alert('手机号不能为空！', {icon: 0});
    }else if(!myreg.test(tel)){
        layer.alert('手机号格式不正确！', {icon: 0});
    }else if(userName==""){
        layer.alert('用户名不能为空！', {icon: 0});
    }else if(password==""){
        layer.alert('密码不能为空！', {icon: 0});
    }else if(relName==""){
        layer.alert('真实姓名不能为空！', {icon: 0});
    }else {
        $.ajax({
            type: 'POST',
            url: '../user/newUserAddManage',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ss),
            success: function (res) {
                if (res.stateCode == 100) {
                    window.location.reload();
                } else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
})


function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    var table1 = '';
        for (var i = 0; i< res.data.length; i++) {
            table1 += '<tr id="' + res.data[i].id + '">' +
                '<td>' + res.data[i].tel+ '</td>' +
                '<td>' + res.data[i].userName + '</td>' +
                '<td>' + res.data[i].relName + '</td>' +
                '<td style="width:100px"><span style="padding-right: 5px" class="editReset checkModal setting-btn">密码重置</span><span id="storageEdit" class="edit2 editModal setting-btn">编辑</span><span style="padding-left: 5px" class="delete setting-btn">删除</span></td></tr>';
        }
        $('#tbody').append(table1);


    // 编辑
    $('#tbody .editModal').click(function(){
        $('#approveModal2').modal('show');
        $(".roleTD2").html("")
        $("#pressent").html("");
        $.ajax({
            url : '../role/getRoleListAllByUser',
            type : 'POST',
            dataType : 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data:JSON.stringify({pageNum:1}),
            success : function(res) {
                $(res.data).each(function(index){
                    //生成CheckBox
                    var checkbox=$("<input type='checkbox' name='roleIds2'/>");

                    checkbox.val(this.id);
                    if(index==0){
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }else if(index%4==0){
                        $(".roleTD2").append("</br>");
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }else{
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }

                });
                $.ajax({
                    type: 'POST',
                    url: '../user/userCheckDetail',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({id: id}),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            $('#tel2').val(res.data.tel)
                            $('#userName2').val(res.data.userName);
                            $("#relName2").val(res.data.relName);
                            $(res.data.role).each(function (index, element) {
                                $("input:checkbox[value=" + element.id + "][name=roleIds2]").prop("checked", "checked");
                            });
                        } else {
                            alert(res.message);
                        }

                    },
                    error: function (err) {
                        alert(err.message);
                    }
                })
            },
            error : function(msg) {
                alert('加载异常!');
            }
        });
        id = $(this).parent().parent().attr('id');

         });

        $('#editorSure').click(function (){
            var tel = $('#tel2').val().replace(/[ ]/g,"");
            var userName = $('#userName2').val().replace(/[ ]/g,"");
            var password = $("#password2").val().replace(/[ ]/g,"");
            var relName = $("#relName2").val().replace(/[ ]/g,"");
            var roleArr= new Array();
            $('input:checkbox[name=roleIds2]:checked').each(function(i){
                roleArr.push($(this).val())
            });
            var roleStr = roleArr.join(",");
            ss = {
                id:id,
                tel:tel,
                userName: userName,
                password:password,
                relName:relName,
                roleStr:roleStr
            }
            var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
            if (tel == ''){
                layer.alert('手机号不能为空！', {icon: 0});
            }else if(!myreg.test(tel)){
                layer.alert('手机号格式不正确！', {icon: 0});
            }else if(userName==""){
                layer.alert('用户名不能为空！', {icon: 0});
            }else if(relName==""){
                layer.alert('真实姓名不能为空！', {icon: 0});
            }else {
                $.ajax({
                    type: 'POST',
                    url: '../user/newUserAddManage',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify(ss),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            window.location.reload();
                        } else {
                            alert(res.message)
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        })


    // 用户删除
    $('.delete').click(function(){
        $('#myModal').modal('show');
        id = $(this).parent().parent().attr('id');
    })

    $('#delSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../user/deleteUser',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode != 100){
                    alert(res.message);
                }
                window.location.reload();
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })

    var resetId;
    $('#tbody .editReset').click(function(){
        $('#myModalReset').modal('show');
        resetId = $(this).parent().parent().attr('id');
    });

    $('#resetSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../user/resetUserPass',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: resetId}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode != 100){
                    alert(res.message);
                }
                window.location.reload();
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })
    }


