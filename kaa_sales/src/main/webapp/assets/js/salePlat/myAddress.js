// made by ZXF
var url = window.location.href;
var id = url.split('=')[1];
var isDefault;
var editId='';


function pro() {
    $.ajax({
        type: 'POST',
        url: '../area/findProvince',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for (var i = 0; i < res.data.length; i++) {
                var province = '';
                province += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                $('#cmbProvince').append(province);
            }
            code = $('#cmbProvince option').eq(0).val();
            provinceCity();
            $("#cmbProvince").change(function () {
                $('#cmbCity').html('');
                $('#cmbArea').html('');
                code = $("#cmbProvince").val();
                provinceCity()
            });

            function provinceCity() {
                $.ajax({
                    type: 'POST',
                    url: '../area/findCity',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({code: code}),
                    success: function (res) {
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity = '';
                            cmbCity += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        //区
                        code = $('#cmbCity option').eq(0).val();
                        area();
                        $("#cmbCity").change(function () {
                            code = $("#cmbCity").val();
                            area();
                        });

                        function area() {
                            $('#cmbArea').html('');
                            $.ajax({
                                type: 'POST',
                                url: '../area/findCity',
                                contentType: 'application/json; charset=utf-8',
                                headers: {
                                    'Accept': 'application/json; charset=utf-8',
                                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                },
                                data: JSON.stringify({code: code}),
                                success: function (res) {
                                    $('#cmbArea').html('');
                                    for (var i = 0; i < res.data.length; i++) {
                                        var cmbArea = '';
                                        cmbArea += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                                        $('#cmbArea').append(cmbArea)
                                    }
                                },
                                error: function (err) {
                                    alert(err.message)
                                }
                            })
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        },
        error: function (err) {
            alert(err.message)
        }
    });
}

pro();

function ajaxList() {
    $.ajax({
        type: 'POST',
        url: '../userAddress/findUserAddressList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            //清空原表格内容
            $('#tbody').html('');
            //绑定产生表格数据
            var tr = '';
            $('#tbody').html('');
            for (var i = 0; i < res.data.length; i++) {
                tr += '<tr class="' + res.data[i].id + '">' +
                    '<td>' + res.data[i].receiver + '</td>' +
                    '<td>' + res.data[i].cityName + '</td>' +
                    '<td>' + res.data[i].address + '</td>' +
                    '<td>' + res.data[i].mobile + '</td>' +
                    '<td>';
                    if(res.data[i].Default) {
                        tr += '<img class="setting-btn default" src="../assets/img/salePlat/set-default.png" alt="" title="设为默认">';
                    } else {
                        tr += '<img class="setting-btn default" src="../assets/img/salePlat/no-default.png" alt="" title="设为默认">';
                    }
                tr += '</td>' +
                    '<td>' +
                    '<img class="setting-btn edit" src="../assets/img/salePlat/edit-png.png" alt="" title="编辑">' +
                    '<img class="setting-btn delete ml10" src="../assets/img/salePlat/del-png.png" alt="" title="删除">' +
                    '</td>' +
                    '</tr>';
            }
            $('#tbody').append(tr);


            //删除
            $('.delete').click(function () {
                $('#deleteModal').modal('show');
                var _this = $(this);
                var id = _this.parent().parent().attr('class');
                $('#suredelete').click(function () {
                    $.ajax({
                        type: 'POST',
                        url: '../userAddress/removeUserAddress',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({id: id}),
                        success: function (res) {
                            $('#deleteModal').modal('hide');
                            location.reload();
                        },
                        error: function (err) {
                            alert(err.message);
                        }
                    });
                })
            });
            //编辑
            $('.edit').click(function () {
                var _this = $(this);
                id = _this.parent().parent().attr('class');
                editId = id;
                $.ajax({
                    type: 'POST',
                    url: '../userAddress/userAddressDetail',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({id: id}),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            // console.log(res.data)
                            $("#addressId").attr("class", res.data.id);
                            $("#storeName").val(res.data.receiver);
                            $("#telephone").val(res.data.mobile);
                            $('#address').val(res.data.address);
                            $('#cmbProvince').find('option:selected').text(res.data.provinceName);
                            $('#cmbCity').find('option:selected').text(res.data.cityName);
                            $('#cmbArea').find('option:selected').text(res.data.zoneName);
                            if (_this.parent().prev().children().attr('src') == '../assets/img/salePlat/set-default.png') {
                                isDefault == true;
                                $('.default-input').attr('checked',true);
                            } else {
                                isDefault == false;
                                $('.default-input').attr('checked',false);
                            }

                        } else {
                            layer.alert(res.message, {icon:0});
                        }
                    },
                    error: function (err) {
                        layer.alert(res.message, {icon:0});
                    }
                })
            });
            // 设为默认
            $('.default').click(function () {
                var _this = $(this);
                var id = _this.parent().parent().attr('class');
                $.ajax({
                    type: 'POST',
                    url: '../userAddress/setDefaultAddress',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({id: id}),
                    success: function (res) {
                        // console.log(res);
                        $('.default').attr('src', '../assets/img/salePlat/no-default.png');
                        _this.attr('src', '../assets/img/salePlat/set-default.png');
                    }
                })
            })
        }

    })
}

ajaxList();

$('#btnSave').click(function () {
    var receiver = $('#storeName').val();
    var phone = $('#telephone').val();
    var address = $('#address').val();
    if ($('.default-input').is(':checked')) {
        isDefault = true;
    } else {
        isDefault = false;
    }
    var data = {
        receiver: receiver,
        address: address,
        mobile: phone,
        province: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        city: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zone: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text(),
        isDefault: isDefault,
        id: editId
    };
    if(receiver==''){
        layer.alert('收件人名称必填！', {icon:0});
    } else if(phone==''){
        layer.alert('手机号码必填！', {icon:0});
    } else if(address==''){
        layer.alert('请填写详细地址！', {icon:0});
    }else {
        // 新增的保存
        $('.spinner-wrap').show();
        if (editId == '') {
            $.ajax({
                type: 'POST',
                url: '../userAddress/saveUserAddress',
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
                        editId == '';
                        ajaxList();
                    } else {
                        layer.alert(res.message, {icon:0});
                    }
                },
                error: function (err) {
                    layer.alert(res.message, {icon:0});
                }
            })
        } else {
            // 编辑的保存
            $('.spinner-wrap').show();
            $.ajax({
                type: 'POST',
                url: '../userAddress/updateUserAddress',
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
                        editId = '';
                        $('#storeName').val('');
                        $('#telephone').val('');
                        $('#address').val('');
                        ajaxList();
                    } else {
                        alert(res.message);
                    }
                },
                error: function (err) {
                    alert(err.message)
                }
            })
        }
    }


})