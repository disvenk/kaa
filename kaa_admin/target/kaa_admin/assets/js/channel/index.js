var nameEl = document.getElementById('sel_city');
var province = document.getElementById('province');
var tel = document.getElementById('tel');
var tip = document.getElementsByClassName('tip')[0];
var zindex = document.getElementsByClassName('zindex')[0];
var sure = document.getElementsByClassName('sure')[0];
var tip_content = document.getElementsByClassName('tip_content')[0];
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
            province.innerHTML = text1+text2+text3;
            province.style.color = 'rgb(42, 46, 54)';
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
//提示框
function tishi(text){
    zindex.style.display = 'block';
    tip.style.display = 'block';
    tip_content.innerHTML = text;
}
//手机号判断
var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
//信息提交
function confirm(){
    var type = $("input[type='radio']:checked").val();
    if($('#name').val() == ''){
        tishi('企业名称不能为空');
    }else if($('#province').html() == '所在地区'){
        tishi('所在地区不能为空');
    }else if($('#city').val() == ''){
        tishi('详细地址不能为空');
    }else if($('#man').val() == ''){
        tishi('联系人不能为空');
    }else if(!reg.test(tel.value)){
        tishi('手机号码不正确')
    }else{
        var params = {
            name: $('#name').val(),
            type: type,
            zone: zoneCode,
            zoneName: text3,
            province: provinceCode,
            provinceName: text1,
            city: cityCode,
            cityName: text2,
            address: $('#city').val(),
            contact: $('#man').val(),
            telephone: $('#tel').val()
        }
        $.ajax({
            type : "POST",  //提交方式
            url : "../channel/saveChannel",//路径
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data : JSON.stringify(params),//数据，这里使用的是Json格式进行传输
            success : function(res) {//返回数据根据结果进行相应的处理
                if ( res.stateCode == 100 ) {
                    zindex.style.display = 'block';
                    sure.style.display = 'block';
                } else {
                    alert('添加失败，请重新添加');
                }
            },error: function (err) {
                alert(err.message);
            }
        });
    }
}
$('#close').click(function(){
    zindex.style.display = 'none';
    sure.style.display = 'none';
    $('#province').html('所在地区');
    $('#province').css('color','rgba(42, 46, 54, 0.5)');
    $('#city').val('');
    $('#name').val('');
    $('#tel').val('');
    $('#man').val('');
    $("input[name='sport']").get(0).checked=true;
})
$('#tip_sure').click(function(){
    zindex.style.display = 'none';
    tip.style.display = 'none';
});
