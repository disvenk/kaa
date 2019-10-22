var url = window.location.href;
//地址id
var id = url.split('=')[1];
var cusId = url.split('=')[2];
//省市区
var proSelect;
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
                proSelect = $('#cmbProvince').find('option');
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

function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: '../supplierCustomer/supplierCustomerAddressDetail',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            $('.spinner').hide();
            if (res.stateCode == 100) {
                //详情信息
                $('#receiver').val('' + res.data.receiver + '');
                $('#mobile').val('' + res.data.mobile + '');
                $('#address').val('' + res.data.address + '');

                //地址
                var provin = res.data.provinceName;
                var areaCit = res.data.cityName;
                var areaZoom = res.data.zoneName;
                var areaCitId = res.data.cityId;
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
                            proSelect = $('#cmbProvince').find('option');
                            for (var i = 0; i < proSelect.length; i++) {
                                if (proSelect.eq(i).text() == provin) {
                                    proSelect.eq(i).attr('selected', 'selected');
                                }
                            }
                        }
                    },
                        error: function (err) {
                        }
                    });
                var code = res.data.provinceId;
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
                        $('#cmbCity').html('');
                        for (var i = 0; i < res.data.length; i++) {
                            var cmbCity = '';
                            cmbCity += '<option value="' + res.data[i].code + '">' + res.data[i].name + '</option>';
                            $('#cmbCity').append(cmbCity)
                        }
                        var citySelect = $('#cmbCity').find('option');
                        for (var i = 0; i < citySelect.length; i++) {
                            if (citySelect.eq(i).text() == areaCit) {
                                citySelect.eq(i).attr('selected', 'selected')
                            }
                        }
                        code = areaCitId;
                        $('#cmbArea').html('');
                      setTimeout(function () {
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
                                  var areaSelect = $('#cmbArea').find('option');
                                  for (var i = 0; i < areaSelect.length; i++) {
                                      if (areaSelect.eq(i).text() == areaZoom) {
                                          areaSelect.eq(i).attr('selected', 'selected')
                                      }
                                  }
                              },
                              error: function (err) {
                                  alert(err.message)
                              }
                          })
                      },200)
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
        } else {
            alert(res.message);
}
},
    error: function (err) {
        alert(err.message);
    }
});
}

detail();


//编辑保存
$('#confirm').click(function () {
    if($('#mobile').val() != '' && !(/^[1][3,4,5,7,8][0-9]{9}$/.test($('#mobile').val()))){
        layer.alert('手机号不正确',{icon:0})
    }else {
        var data = {
            id:id,
            receiver: $('#receiver').val(),
            mobile: $('#mobile').val(),
            province: $('#cmbProvince').find('option:selected').val(),
            provinceName: $('#cmbProvince').find('option:selected').text(),
            city: $('#cmbCity').find('option:selected').val(),
            cityName: $('#cmbCity').find('option:selected').text(),
            zone: $('#cmbArea').find('option:selected').val(),
            zoneName: $('#cmbArea').find('option:selected').text(),
            address: $('#address').val()
        };
        $.ajax({
            type: 'POST',
            url: '../supplierCustomer/saveSupplierCustomerAddress',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if (res.stateCode == 100) {
                    layer.alert('保存成功!', {icon: 1});
                    location.href = '../WOManage/clientAddressHtml?id=' + cusId + '';
                } else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(res.message, {icon: 0});
            }
        });
    }
});
