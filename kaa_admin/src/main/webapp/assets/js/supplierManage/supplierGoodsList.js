var urlle = sessionStorage.getItem('urllen');
var pageNum = 1;
var data = {
    pageNum: pageNum,

};

$('#btnAdd').click(function () {
    location.href = '../suplier/supplierGoodsListAddHtml';
});

//调用OCUpload插件的方法
$("#uploadExcel").upload({
    action:'../suplier/uploadSupplierManage',//表单提交的地址
    name:"myFile",

    onComplete:function (data) { //提交表单之后
        $('.spinner-wrap').hide();
        if (JSON.parse(data).stateCode == 100) {
            $('#myModal1').modal('show');
            $('#sure1').click(function () {
                $('#myModal1').modal('hide');
                setTimeout(function () {
                    window.location.reload();
                }, 1500)
            })
        } else {
            alert(JSON.parse(data).message);
            $('#myModal2').modal('show');
            $('#sure2').click(function () {
                $('#myModal2').modal('hide');
                setTimeout(function () {
                    window.location.reload();
                }, 1500)
            })
        }
    },
    onSelect: function() {//当用户选择了一个文件后触发事件
        $('.spinner-wrap').show();
        //当选择了文件后，关闭自动提交
        this.autoSubmit=false;
        //校验上传的文件名是否满足后缀为.xls或.xlsx
        var regex =/^.*\.(?:xls|xlsx)$/i;
        //this.filename()返回当前选择的文件名称 (ps：我使用这个方法没好使，自己写了一个获取文件的名的方法) $("[name = '"+this.name()+"']").val())
        //alert(this.filename());
        if(regex.test($("[name = '"+this.name()+"']").val())){
            //通过校验
            this.submit();
        }else{
            $('.spinner-wrap').hide();
            //未通过
            alert("文件格式不正确，必须以.xls或.xlsx结尾");
            $("#myModal3").modal('show'); //错误提示框，文件格式不正确，必须以.xls或.xlsx结尾
        }
    }
});


//初始化加载所有列表数据
initData();

function initData() {
    $.ajax({
        type: 'POST',
        url: '../suplier/suplierGoodsListManage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            //清空原表格内容
            $('#tbody').html('');
            if (res.data.length < 1) {
                $('.tcdPageCode').html('');
                return;
            }

            load(res);

            //分页
            $('.tcdPageCode').html('');
            $(".tcdPageCode").createPage({
                pageCount: res.pageSum,//总共的页码
                current: 1,//当前页
                backFn: function (p) {//p是点击的页码
                    pageNum = $(".current").html();
                    data.pageNum = pageNum;
                    queryData();
                }
            });
        },

        error: function (err) {
            $('.tcdPageCode').html('');
            alert(err.message);
        }
    })
}

function queryData() {
    $.ajax({
        type: 'POST',
        url: '../suplier/suplierGoodsListManage',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner').hide();
            load(res);
        }
    })
}
//搜索
$('#search').click(function () {
    $('.spinner').show();
    var productName = $('#productName').val();
    var suplierName = $('#suplierName').val();
    var productCode = $('#productCode').val();
    data = {
        name: productName,
        productCode: productCode,
        suplierName: suplierName,
        pageNum: 1
    };
    queryData();
});
//重置
$('#resert').click(function () {

    window.location.reload();
})


function load(res) {
    //清空原表格内容
    $('#tbody').html('');
    if (res.data.length < 1) {
        $('.tcdPageCode').html('');
        return;
    }

    //绑定产生表格数据
    var tr = '';
    $('#tbody').html('');
    for (var i = 0; i < res.data.length; i++) {
        tr += ' <tr id="' + res.data[i].id + '">' +
            '<td>' + res.data[i].productCode + '</td>' +
            '<td>' + res.data[i].name + '</td>' +
            '<td>' + res.data[i].pno + '</td>' +
            '<td><img style="width: 65px;height: 65px;" src="' + res.data[i].mainpicHref + '" alt=""></td>' +
            '<td>' + res.data[i].suplierName + '</td>' +
            '<td>' + res.data[i].suplierDay + '</td>' +
            '<td>' + res.data[i].price + '</td>' +
            '<td>' + res.data[i].updateDate + '</td>' +
            '<td>' + res.data[i].remark + '</td>' +
            '<td>' +
            '<div class="edit setting-btn">编辑</div>' +
            '<div class="delete setting-btn">删除</div>' +
            '</td>' +
            '</tr>';
    }
    $('#tbody').append(tr);

    // 删除
    $('.delete').click(function () {
        $('#myModal').modal('show');
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        $('#sure').click(function () {
            $.ajax({
                type: 'POST',
                url: urlle + 'suplier/deleteSuplierGoodsListGoods',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify({id: id}),
                success: function (res) {
                    $('#myModal').modal('hide');
                    location.reload();
                },
                error: function (err) {
                    alert(err.message);
                }
            });
        })
    });

    // 编辑
    $('.edit').click(function () {
        var _this = $(this);
        var id = _this.parent().parent().attr('id');
        var userId = _this.parent().parent().attr('id');
        location.href = '../suplier/supplierGoodsListEditHtml?id=' + id + '';
    });


}