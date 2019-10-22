var data = {
    pageNum: 1
};

//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    var name = $('#name').val();
    var phone = $('#phone').val();
    data = {
        name: name,
        phone: phone,
        pageNum: 1
    };
    list()
});
//重置
$('#resert').click(function () {
    $('#name').val("");
    $('#phone').val("");
    data = {
        name: '',
        phone:  '',
        pageNum: 1
    };
    list()
})


//列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../designer/designerList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
            }
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    ajax();
                }
            });
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}
list();
function ajax() {
    $.ajax({
        type: 'POST',
        url: '../designer/designerList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        },
        error: function (err) {
            alert(err.message);
            $('.tcdPageCode').html('');
        }
    });
}

//加载数据
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    for(var i = 0; i < res.data.length; i++){
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'">\n' +
            '                                <td>'+res.data[i].phone+'</td>\n' +
            '                                <td><img style="width: 100px;height: 100px;" src="'+res.data[i].icon+'" alt=""></td>\n' +
            '                                <td>'+res.data[i].name+'</td>\n' +
            '                                <td>'+res.data[i].sex+'</td>\n' +
            '                                <td>'+res.data[i].type+'</td>\n' +
            '                                <td>'+res.data[i].city+'</td>\n' +
            '                                <td>'+res.data[i].remarks+'</td>\n' +
            '                                <td>'+ (res.data[i].isShow ? "是" : "否") +'</td>\n' +
            '                                <td>' +
            (res.data[i].isShow ? "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", false)'>隐藏</span>"
                : "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", true)'>展示</span>") +
            '                                   <span class="storage-operate ml10p" onclick="designerAdd('+res.data[i].id+')">编辑</span>' +
            '                                   <span class="storage-operate ml10p" onclick="deleteDesigner('+res.data[i].id+')">删除</span>' +
            // '                                   <span class="storage-operate ml10p">商品信息</span>' +
            '                                </td>\n' +
            '                            </tr>';
        $('#tbody').append(tr);
    }
}


//修改展示状态
function isShow(id, isTrue) {
    $.ajax({
        type: 'POST',
        url: '../designer/isShowUpdate',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id, isTrue: isTrue}),
        success: function (res) {
            if(res.stateCode == 100){
                alert("修改成功");
                ajax();
            }else{
                alert(res.message);
            }
        },
        error: function (err) {
            alert(err.message);
        }
    });
}

//删除
function deleteDesigner(id) {
    $('#myModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../designer/designerDelete',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id:id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#myModal').modal('hide');
                    ajax();
                }else{
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message);
            }
        });
    });
}

$("#designAdd").click(function () {
    designerAdd('');
})

function designerAdd(id) {
    location.href = "../designer/designerAddHtml?id=" + id;
}