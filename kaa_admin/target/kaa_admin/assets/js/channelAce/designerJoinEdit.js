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
function detail() {
    //详情
    $.ajax({
        type: 'POST',
        url: urlle + 'channel/channelDesEdit',
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
                $('#name').val(res.data.name);
                $('#telephone').val(res.data.telephone);
                $('#address').val(res.data.address);

                for (var i = 0; i < $('#type').find('option').length; i++) {
                    if ($('#type').find('option').eq(i).text() == res.data.type) {
                        $('#type').find('option').eq(i).attr('selected', 'selected')
                    }
                }
                //地址
                var provin = res.data.provinceName;
                var areaCit = res.data.cityName;
                var areaZoom = res.data.zoneName;
                var cityCode = res.data.cityCode;
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
                });
                var code = res.data.provinceCode;
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
                      code = cityCode;
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
                  },500)
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
    var name = $('#name').val();
    var type = $('#type option:selected').text();
    var address = $('#address').val();
    var telephone = $('#telephone').val();
    var data = {
        id : id,
        name: name,
        type: type,
        address: address,
        telephone: telephone,
        provinceName: $('#cmbProvince').find('option:selected').text(),
        cityName: $('#cmbCity').find('option:selected').text(),
        zoneName: $('#cmbArea').find('option:selected').text()
    };

    if (name == '') {
        layer.alert('名称为必填！', {icon: 0});
    } else if (address == '') {
        layer.alert('请输入详细地址！', {icon: 0});
    } else if (!reg.test(telephone)){
        layer.alert('联系方式不正确！', {icon: 0});
    }else {
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url: urlle + 'channel/channelDesUpdate',
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
                            location.href = '../channel/designerJoinHtml';
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