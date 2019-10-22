$("#type").val("");

function footerContentEdit(id){
    location.href='../salesHeadCategory/bottomVideoEditHtml?='+id;
}

$('#add').click(function () {
    location.href='../salesHeadCategory/bottomVideoAddHtml';
})

var pageNum = 1;
var data = {
    pageNum: pageNum
};
//搜索
$('#orderSearch').click(function () {
    $('.spinner').show();
    var name = $('#orderBuyer').val();
    var kind = parseInt($("#type option:selected").attr("value"));
    data = {
        name: name,
        kind:kind,
        pageNum: pageNum
    };
    list()
});

//重置
$('#orderReset').click(function () {
    $('.spinner').show();
    var name = $('#orderBuyer').val("");
    var kind = $("#type").val("")
    data = {
        name: "",
        pageNum: pageNum
    };
    list()
});



//展示列表
function list() {
    $.ajax({
        type: 'POST',
        url: '../footerContent/findFooterContentList',
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
        url: '../footerContent/findFooterContentList',
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
        var kind;
        console.log(sort);
      switch(res.data[i].kind)
        {
            case 1:
              kind= "主播推荐";
                break;
            case 2:
                kind="新娘课堂";
                break;
            case 3:
                kind="体验师";
                break;
            default:
                break;
        }
        var tr = '';
        tr += '<tr id="'+res.data[i].id+'">\n' +
            '                                <td>'+res.data[i].title+'</td>\n' +
            '                                <td>'+kind+'</td>\n' +
            '                                <td>'+res.data[i].updateDate+'</td>\n' +
            '                                <td>'+sort+'</td>\n' +
            '                                <td>'+ (res.data[i].isShow ? "是" : "否") +'</td>\n' +
            '                                <td>'+res.data[i].watch+'</td>\n' +
            '                                <td>' +
            (res.data[i].isShow ? "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", false)'>隐藏</span>"
                : "<span class=\"storage-operate\" onclick='isShow("+res.data[i].id+", true)'>展示</span>") +
            '                                   <span class="storage-operate ml10p" onclick="footerContentEdit('+res.data[i].id+')">编辑</span>' +
            '                                   <span class="storage-operate ml10p" onclick="footerContentDelete('+res.data[i].id+')">删除</span>' +
            // '                                   <span class="storage-operate ml10p">商品信息</span>' +
            '                                </td>\n' +
            '                            </tr>';
        $('#tbody').append(tr);
    }
}

//删除
function footerContentDelete(id) {
    $('#myModal').modal('show');
    $('#sure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../footerContent/footerContentRemove',
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

//修改展示状态
function isShow(id, isTrue) {
    $.ajax({
        type: 'POST',
        url: '../footerContent/isShowUpdate',
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