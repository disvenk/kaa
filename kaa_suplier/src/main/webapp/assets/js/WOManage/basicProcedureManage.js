//新增
$('#btnAdd').click(function () {
    location.href='../WOManage/basicProcedureAddHtml';

});

var urlle = sessionStorage.getItem('urllen');
var url = window.location.href;
var type = url.split('=')[1];
var ids = [];


//初始化加载所有列表数据
var pageNum = 1;
var data = {
    pageNum: pageNum
};

//搜索

$('#btnQuery').click(function () {
    $('.spinner').show();
    var name = $('#cateName').val();
    data = {
        name: name,
        pageNum: pageNum
    };
    initData(data)
});

//重置
$('#resert').click(function () {
    $('#cateName').val("");
    data = {
        name: '',
        pageNum: 1
    };
    initData(data);
})

function initData() {
    window.parent.platform();
    var index = layer.load(2, {shade: [0.6,'#000']});
    $.ajax({
        type: 'POST',
        url: '../procedure/findProcedureList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            layer.close(index);
            //清空原表格内容
            $('#tbody').html('');
            if(res.data.length < 1){
                $('.tcdPageCode').html('');
                $('#sample-table-2').css('display', 'none');
                $('#display').css('display', 'block');
                return;
            }
            $('#sample-table-2').css('display', '');
            $('#display').css('display', 'none');
            load(res);
            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount:res.pageSum,//总共的页码
                current:1,//当前页
                backFn:function(p){//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum=pageNum;
                    queryData({type:1,pageNum:pageNum});
                }
            });
        },

        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}
initData();
function queryData(data) {
    $.ajax({
        type: 'POST',
        url: '../procedure/findProcedureList',
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
}
function load(res) {
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('#display').css('display', 'block');
        $('.tcdPageCode').html('');
        $('#sample-table-2').css('display', 'none');
        return;
    }
    $('#sample-table-2').css('display', '');
    $('#display').css('display', 'none');
    $('#sample-table-2 thead').html('');
        $('#sample-table-2 thead').append(' <tr> <th>工序名称</th> <th>工价</th>'+
            '<th>更新时间</th><th>排序</th> <th>备注</th><th>操作</th> </tr>');
    //绑定产生表格数据
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        var sort = i+1;
        var tr = '';
        tr+='<tr id="'+res.data[i].id+'">' +
            '<td>'+res.data[i].name+'</td>' +
            '<td>'+res.data[i].price+'</td>' +
            '<td>'+res.data[i].updateDate+'</td>' +
            '<td>'+sort+'</td>' +
            '<td>'+(res.data[i].remarks==null?"":res.data[i].remarks)+'</td>' +
            '<td style="min-width: 80px">' +
            '<div class="edit setting-btn" sort="'+sort+'">编辑</div>' +
            '<div class="delete setting-btn">删除</div>' +
            '</tr>'
        $('#tbody').append(tr);
    }
    //编辑
    $('.edit').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        var sort = $(this).attr("sort")
        location.href = '../WOManage/basicProcedureEditorHtml?id=' + id +"="+ sort;
    });

//删除
    var id;
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
         id = _this.parent().parent().attr('id');
    });

    $('#suredelete').click(function () {
        $.ajax({
            type: 'POST',
            url: urlle + 'procedure/deleteProductProcedure',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode == 100){
                    location.reload();
                }else{
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(res.message, {icon: 0});
            }
        });
    })

}

