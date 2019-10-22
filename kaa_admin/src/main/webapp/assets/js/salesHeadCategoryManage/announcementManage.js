var pageNum = 1;

//搜索
$('#btnQuery').click(function () {
    $('.spinner').show();
    var name = $('#txtName').val();
    data = {
        name: name,
        pageNum: pageNum
    };
    list()
});
//重置
/*$('#resert').click(function () {
    $('#name').val("");
    $('#phone').val("");
    data = {
        name: '',
        phone:  '',
        pageNum: 1
    };
    list()
})*/


var data = {
    pageNum: pageNum
};
//展示列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../announcementManage/findCmsList',
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
                    data.pageNum = pageNum;
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
        url: '../announcementManage/findCmsList',
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
    var sort = i+1;
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'">\n' +
            '                                <td>'+res.data[i].title+'</td>\n' +
            '                                <td>'+res.data[i].style+'</td>\n' +
            '                                <td>'+res.data[i].updateDate+'</td>\n' +
            '                                <td>'+sort+'</td>\n' +
            '                                <td>'+ (res.data[i].isShow ? "是" : "否") +'</td>\n' +
            '                                <td>' +
            (res.data[i].isShow ? "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", false)'>隐藏</span>"
                : "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", true)'>展示</span>") +
            '                                   <span class="storage-operate ml10p" onclick="cmsContentAdd('+res.data[i].id+')">编辑</span>' +
            '                                   <span class="storage-operate ml10p" onclick="deleteCmsContent('+res.data[i].id+')">删除</span>' +
            // '                                   <span class="storage-operate ml10p">商品信息</span>' +
            '                                </td>\n' +
            '                            </tr>';
        $('#tbody').append(tr);
    }
}

//新增
$('#btnAdd').click(function () {
    location.href = '../announcementManage/announcementAddHtml';
});

// 编辑
function  cmsContentAdd(id) {
    location.href = '../announcementManage/announcementEditHtml?id=' + id;
}


//删除
function deleteCmsContent(id) {
    $('#deleteModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../announcementManage/cmsContentRemove',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id:id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#deleteModal').modal('hide');
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

//修改展示状态
function isShow(id, isTrue) {
    $.ajax({
        type: 'POST',
        url: '../announcementManage/isShowUpdate',
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