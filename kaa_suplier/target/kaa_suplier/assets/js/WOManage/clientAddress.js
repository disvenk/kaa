var url = window.location.href;
//客户id
var id = url.split('=')[1];
$('#btnAdd').click(function () {
    location.href = '../WOManage/clientAddressAddHtml?id='+id+'';
})
var data = {
    id: id
}
function ajax() {
    var index = layer.load(2, {shade: [0.6, '#000']});
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/supplierCustomerAddressList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('#tbody').html('');
            if (res.stateCode == 100) {
                layer.close(index);
                for (var i = 0; i < res.data.length; i++) {
                    var tr = '';
                    tr += '<tr><td>' + res.data[i].receiver +'</td><td>' + res.data[i].mobile + '</td><td>' + res.data[i].address + '</td>' + '<td>\n' +
                        '<div class="local-edit" onclick="editAddress(' + res.data[i].id + ')">编辑</div>\n' +
                        '<div class="local-edit" onclick="deleteAddress(' + res.data[i].id + ')">删除</div>\n' +
                    '</td>\n' +
                    '</tr>';
                    $('#tbody').append(tr);
                }
            } else {
                layer.alert(res.message, {icon: 0});
                layer.close(index);
            }
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0})
        }
    })
};
ajax();

//编辑
function editAddress(ids) {
    //地址id和客户id
    location.href = '../WOManage/clientAddressEditHtml?id='+ids+"="+id;
}

// 删除
function deleteAddress(id) {
    $('#myModal').modal('show');
    $('#suredelete').click(function () {
        $.ajax({
            type: 'POST',
            url: '../supplierCustomer/removeSupplierCustomerAddress',
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
                    ajax();
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon:0});
            }
        });
    });
}