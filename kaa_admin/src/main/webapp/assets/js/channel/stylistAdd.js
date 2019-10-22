var sss = true;
var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
var tel = document.getElementById('tel');
var telTip = document.getElementById('telTip');
var tip = document.getElementsByClassName('tip')[0];
var zindex = document.getElementsByClassName('zindex')[0];
var tip_content = document.getElementsByClassName('tip_content')[0];
function phone(){
  if(!reg.test(tel.value)){
    telTip.style.display = 'block';
    tel.value = '';
  }else{
    telTip.style.display = 'none';
  }
}
//提示框
function tishi(text){
  zindex.style.display = 'block';
  tip.style.display = 'block';
  tip_content.innerHTML = text;
}
$('#confirm').click(function(){
    var name = $('#name').val();
    var select = $('#select option:selected');
    if(name == ''){
      tishi('姓名不能为空');
    }else if(!reg.test(tel.value)){
      tishi('手机号码不正确')
    }else{
      if(sss){
        sss = false;
       }else{
       }
    }
})
$('#tip_sure').click(function(){
  zindex.style.display = 'none';
  tip.style.display = 'none';
});