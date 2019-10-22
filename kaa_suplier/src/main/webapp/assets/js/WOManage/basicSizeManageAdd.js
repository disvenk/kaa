
var urlle = sessionStorage.getItem('urllen');
var url = window.location.href;
var id = url.split('=')[1];
var ids = [];
$("#cateName").val("");
$("#remark").val("");
$("#sign").val("");

//失焦事件查询
function onblur(data){
    var name = $("#cateName").val();
    var sign = $("#sign").val();
        if(id && (name==sign)){
            $("#alert").html("尺寸名可用");
            $("#alert").css("color","green")
            return;
        }
    $.ajax({
        type: 'POST',
        url: '../productBase/checkUniqueBase',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode==100){
                if (res.data) {
                    $("#alert").html("尺寸名已存在");
                    $("#alert").css("color", "red")
                }else if(!res.data){
                    $("#alert").html("尺寸名可用");
                    $("#alert").css("color","green")

                }
            }else if(res.stateCode==120){
                $("#alert").html("");

            }


        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })

}

$("#cateName").blur(function(){
    var data = {
        name: $("#cateName").val(),
        type:3
    }
    onblur(data);
});

if(id){
    $("#addSign").html("编辑尺寸");
    $.ajax({
        type: 'POST',
        url: '../productBase/checkProductBaseDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            if(res.stateCode==100){
               $("#cateName").val(res.data.name);
               $("#remark").val(res.data.remarks);
               $("#sign").val(res.data.name);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    })
}else {
    $("#addSign").html("新增尺寸");
}

$("#confirm").click(function(){

    var name = $("#cateName").val();
    var remarks = $("#remark").val();
    var sign = $("#sign").val();
    var data = {
        id:id,
        name:name,
        remarks:remarks,
        type:3
    }
    if(id && (name==sign)){
        $("#alert").html("尺寸名可用");
        $("#alert").css("color","green")

        addCate(data);
        return;
    }
    if(name=="") {
        layer.alert('名称不能为空！', {icon: 0});
        return;
    }

   $.ajax({
       type: 'POST',
       url: '../productBase/checkUniqueBase',
       dataType: 'json',
       contentType: 'application/json; charset=utf-8',
       headers: {
           'Accept': 'application/json; charset=utf-8',
           'Authorization': 'Basic ' + sessionStorage.getItem('token')
       },
       data: JSON.stringify(data),
       success: function (res) {
           if(res.stateCode==100){
               if (res.data) {
                   $("#alert").html("尺寸名已存在");
                   $("#alert").css("color","red")

               } else {
                   $("#alert").html("尺寸名可用");
                   $("#alert").css("color","green")
                   addCate(data);
               }
           }else if(res.stateCode==120){
               $("#alert").html("");
               flag=res.data;
           }


       },
       error: function (err) {
           $('.tcdPageCode').html('');
           alert(err.message);
       }
   })
})


function addCate(data){
    $.ajax({
        type: 'POST',
        url: '../productBase/addProductBase',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode==100){
                    window.location.href="../WOManage/basicSizeHtml";
            }
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}
