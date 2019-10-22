
var urlle = sessionStorage.getItem('urllen');
var url = window.location.href;
var id = url.split('=')[1];
var sort = url.split('=')[2];
var ids = [];

$.ajax({
    type: 'POST',
    url: '../procedure/checkProductProcedureDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id:id}),
    success: function (res) {
        $("#cateName").val(res.data.name);
        $("#price").val(res.data.price);
        $("#sort").val(sort);
        $("#remark").val(res.data.remarks);

        $("#sign").val(res.data.name);
    },
    error: function (err) {
        alert(err.message);
    }
})

$("#confirm").click(function(){
    var name = $("#cateName").val();
    var price = $("#price").val();
    var sort = $("#sort").val();
    var remarks = $("#remark").val();
   var sign = $("#sign").val();
    var data = {
        id:id,
        name:name,
        price:price,
        sort:sort,
        remarks:remarks
    }

    if(id && (name==sign)){
        $("#alert").html("工序名可用");
        $("#alert").css("color","green")

        addCate(data);
        return;
    }

    if(name=="") {
        layer.alert('名称不能为空！', {icon: 0});

    }else if(price=="" || price==0 || price<0){
        layer.alert('工价不能为空或0或负数！', {icon: 0});

    }else if(sort=="" || sort==0 || sort<0){
        layer.alert('排序号不能为空或0或负数！', {icon: 0});
    }else {
        $.ajax({
            type: 'POST',
            url: '../procedure/checkUniqueProcedure',
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
                        $("#alert").html("工序名已存在");
                        $("#alert").css("color","red")

                    } else {
                        $("#alert").html("工序名可用");
                        $("#alert").css("color","green")
                        addCate(data);
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


})


function addCate(data){
    $.ajax({
        type: 'POST',
        url: '../procedure/editorProductProcedure',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode==100){
                window.location.href="../WOManage/basicProcedureHtml";
            }
        },
        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}
