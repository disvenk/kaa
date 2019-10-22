var url = window.location.href;
var id = url.split('=')[1];
var urlle = sessionStorage.getItem('urllen');
var proSelect;
var reg = /^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$/;
$(function () {
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
                proSelect = $('#cmbProvince').find('option')
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
});
//用户身份
$.ajax({
    type: 'POST',
    url: urlle + '/base/getBaseDataList',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },

    data: JSON.stringify({parameterType: 4}),
    success: function (res) {
        for (var i = 0; i < res.data.length; i++) {
            var userType = '';
            userType += '<option id="' + (1 + i) + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            $('#userType').append(userType);
        }
    },

    error: function (err) {
        alert(err.message)
    }
});

function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: urlle + 'channel/channelDetail',
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
                $('#storeName').val('' + res.data.name + '');
                $('#createTime').html('' + res.data.createTime + '');
                $('#contactPhone').val('' + res.data.telephone + '');
                $('#contact').val('' + res.data.contact + '');
                $('#address').val('' + res.data.address + '');

                for (var i = 0; i < $('#userType').find('option').length; i++) {
                    if ($('#userType').find('option').eq(i).val() == res.data.userType) {
                        $('#userType').find('option').eq(i).attr('selected', 'selected')
                    }
                }
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
                            proSelect = $('#cmbProvince').find('option')
                        }
                        for (var i = 0; i < proSelect.length; i++) {
                            if (proSelect.eq(i).text() == provin) {
                                proSelect.eq(i).attr('selected', 'selected');
                            }
                        }
                    }
                })
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
                setTimeout(function () {
                    code = areaCitId;
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
                },500);
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

//提交编辑信息
$('#confirm').click(function () {
    var userName = $('#storeName').val();
    var userType = $('#userType option:selected').val();
    var address = $('#address').val();
    var contact = $('#contact').val();
    var contactPhone = $('#contactPhone').val();

    var data = {
        id : id,
        name: userName,
        type: userType,
        address: address,
        contact: contact,
        telephone: contactPhone,
        province: $('#cmbProvince').find('option:selected').val(),
        provinceName: $('#cmbProvince').find('option:selected').text(),
        city: $('#cmbCity').find('option:selected').val(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zone: $('#cmbArea').find('option:selected').val(),
        zoneName: $('#cmbArea').find('option:selected').text(),
    };

    if (userName == '') {
        layer.alert('门店名称为必填！', {icon: 0});
    } else if (address == '') {
        layer.alert('请输入详细地址！', {icon: 0});
    } else if (contact==''){
        layer.alert('联系人必填！', {icon: 0});
    }else if (!reg.test(contactPhone)){
        layer.alert('联系人电话错误！', {icon: 0});
    } else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'channel/channelUpdate',
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
                        setTimeout(function () {
                            location.href = '../channel/channelHtml';
                        }, 500)
                } else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }


});