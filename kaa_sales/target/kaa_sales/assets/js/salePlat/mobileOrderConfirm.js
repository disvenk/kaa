var url = window.location.href;
var array = url.split('=');
var ids = array[1].split(',');
var params = [];
for(var i = 0; i < ids.length; i++){
    var obj = {
        id: ''
    };
    obj.id = ids[i];
    params[i] = obj
}
//商品信息
$.ajax({
    type: 'POST',
    url: '../boxHome/boxUseLogCheck',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify({ids:params}),
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0 ;i < res.data.length; i++){
                var ul = '';
                ul += '  <div class="order-list clearfix">\n' +
                    '            <img class="order-list-img" src="'+res.data[i].href+'" alt="">\n' +
                    '            <div class="order-list-detail">\n' +
                    '                <div class="order-name">'+res.data[i].name+'</div>\n' +
                    '                <div class="order-count">共 <span class="order-num">'+res.data[i].count+'</span>件商品  价格 <span class="order-price">￥'+res.data[i].price+'</span> </div>\n' +
                    '            </div>\n' +
                    '        </div>';
                $('.order-list-wrap').append(ul);
            }
        }else{
            layer.msg(res.message);
            setTimeout(function () {
                location.href = '../boxMobile/mobileHomeHtml';
            },2000)
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});
//手机号判断
var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
var nameEl = document.getElementById('sel_city');
var first = []; /* 省，直辖市 */
var second = []; /* 市 */
var third = []; /* 镇 */
var selectedIndex = [0, 0, 0]; /* 默认选中的地区 */
var provinceCode;
var cityCode;
var zoneCode;
var checked = [0, 0, 0]; /* 已选选项 */
var city;
var text1;
var text2;
var text3;
$.ajax({
    type: 'POST',
    url: '../area/findProvinceCityZone',
    dataType: 'json',
    contentType: 'application/json; charset=utf-8',
    success: function (res) {
        city = res.data;
        creatList(city, first);
        if (city[selectedIndex[0]].hasOwnProperty('sub')) {
            creatList(city[selectedIndex[0]].sub, second);
        } else {
            second = [{text: '', value: 0}];
        }

        if (city[selectedIndex[0]].sub[selectedIndex[1]].hasOwnProperty('sub')) {
            creatList(city[selectedIndex[0]].sub[selectedIndex[1]].sub, third);
        } else {
            third = [{text: '', value: 0}];
        }

        var picker = new Picker({
            data: [first, second, third],
            selectedIndex: selectedIndex,
            title: '请选择地址'
        });

        picker.on('picker.select', function (selectedVal, selectedIndex) {
            text1 = first[selectedIndex[0]].text;
            text2 = second[selectedIndex[1]].text;
            text3 = third[selectedIndex[2]] ? third[selectedIndex[2]].text : '';
            provinceCode = first[selectedIndex[0]].value;
            cityCode = second[selectedIndex[1]].value;
            zoneCode = third[selectedIndex[2]] ? third[selectedIndex[2]].value : '';
            $('#sel_city').val(text1+text2+text3);
        });

        picker.on('picker.change', function (index, selectedIndex) {
            if (index === 0){
                firstChange();
            } else if (index === 1) {
                secondChange();
            }

            function firstChange() {
                second = [];
                third = [];
                checked[0] = selectedIndex;
                var firstCity = city[selectedIndex];
                if (firstCity.hasOwnProperty('sub')) {
                    creatList(firstCity.sub, second);

                    var secondCity = city[selectedIndex].sub[0]
                    if (secondCity.hasOwnProperty('sub')) {
                        creatList(secondCity.sub, third);
                    } else {
                        third = [{text: '', value: 0}];
                        checked[2] = 0;
                    }
                } else {
                    second = [{text: '', value: 0}];
                    third = [{text: '', value: 0}];
                    checked[1] = 0;
                    checked[2] = 0;
                }

                picker.refillColumn(1, second);
                picker.refillColumn(2, third);
                picker.scrollColumn(1, 0)
                picker.scrollColumn(2, 0)
            }

            function secondChange() {
                third = [];
                checked[1] = selectedIndex;
                var first_index = checked[0];
                if (city[first_index].sub[selectedIndex].hasOwnProperty('sub')) {
                    var secondCity = city[first_index].sub[selectedIndex];
                    creatList(secondCity.sub, third);
                    picker.refillColumn(2, third);
                    picker.scrollColumn(2, 0)
                } else {
                    third = [{text: '', value: 0}];
                    checked[2] = 0;
                    picker.refillColumn(2, third);
                    picker.scrollColumn(2, 0)
                }
            }

        });
// picker.on('picker.valuechange', function (selectedVal, selectedIndex) {
//   console.log(selectedVal);
//   console.log(selectedIndex);
// });

        nameEl.addEventListener('click', function () {
            picker.show();
        });
    },
    error: function (err) {
        alert(err.message);
    }
})
function creatList(obj, list){
    obj.forEach(function(item, index, arr){
        var temp = new Object();
        temp.text = item.name;
        temp.value = item.code;
        list.push(temp);
    })
}
//提交
$('.order-btn').click(function () {
    if($('#receiver').val() == ''){
        layer.msg('收件人不能为空');
    }else if(!reg.test($('#phone').val())){
        layer.msg('手机号码不正确');
    }else if(text1 == undefined){
        layer.msg('地址不能为空');
    }else if($('#address').val() == ''){
        layer.msg('请填写详细地址');
    }else{
        var data = {
            province: provinceCode,
            provinceName: text1,
            city: cityCode,
            cityName: text2,
            zone: zoneCode,
            zoneName: text3,
            receiver: $('#receiver').val(),
            mobile: $('#phone').val(),
            address: $('#address').val(),
            ids:params
        };
        $.ajax({
            type: 'POST',
            url: '../boxHome/boxUseLogSave',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    location.href='../boxMobile/mobileSuccessHtml';
                }else{
                    layer.close(index);
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.close(index);
                layer.msg(err.message);
            }
        });
    }
});
