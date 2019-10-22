var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');


//供应商
$.ajax({
    type: 'POST',
    url: urlle + 'product/suplierNameList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    success: function (res) {
        for(var i = 0; i < res.data.length; i++){
            var productSupplierName = '';
            productSupplierName += '<option id="'+res.data[i].id+'" value="'+res.data[i].suplierName+'">'+res.data[i].suplierName+'</option>';
            $('#supplierList').append(productSupplierName);
        }
    },
    error: function (err) {
        alert(err.message)
    }
});

$.ajax({
    type: 'POST',
    url: '../suplier/suplierGoodsEditListDetail',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({id: id}),
    success:function (res) {
        $('.img').attr('src',res.data[0].mainHref);
        $('.supplier-id').val(res.data[0].productCode);
        $('.supplier-pno').val(res.data[0].pno);
        $('.supplier-name').val(res.data[0].name);
        $('#supplierList').val(res.data[0].suplierName);
        $('.supplier-years').val(res.data[0].suplierDay);
        $('.supplier-prices').val(res.data[0].suplierPrice);
        $('.order-textarea').val(res.data[0].suplierRemark);
    }
})

// 提交
$('#submit').click(function () {
    var supplierName=$('.supplier-name').val();
    var mainHref=$('.img_container > div img').attr('src');
    var supplierSelect= $('#supplierList').find('option:selected').attr('id');
    var supplierYears=$('.supplier-years').val();
    var supplierPrice=$('.supplier-prices').val();
    var text=$('.order-textarea').val();
    var pno=$('.supplier-pno').val();

    var data={
        id:id,
        pno:pno,
        name:supplierName,
        href:mainHref,
        SupplierId:supplierSelect,
        suplierDay:supplierYears,
        suplierPrice:supplierPrice,
        Remark:text
    }

    $.ajax({
        type: 'POST',
        url: '../suplier/updateSuplierGoodsList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            $('.spinner-wrap').hide();
            if (res.stateCode == 100) {
                $('#myModal').modal('show');
                $('#sure').click(function () {
                    $('#myModal').modal('hide');
                    setTimeout(function () {
                        location.href = '../suplier/supplierGoodsListManageHtml';
                    }, 1500)
                })
            } else {
                // alert(res.message)
                layer.alert(res.message, {icon: 0});
            }
        },
        error: function (err) {
            // alert(err.message)
            layer.alert(err.message, {icon: 0});
        }
    })
})








