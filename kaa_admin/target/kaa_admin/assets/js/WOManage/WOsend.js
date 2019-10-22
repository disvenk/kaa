var url = window.location.href;
var type = url.split('=')[1];
$('#wo1').focus();
function keyLogin(){
    //回车键的键值为13
    var wo1 = $('#wo1').val();
    var wo2 = $('#wo2').val();
    if (event.keyCode==13) {
      if(wo1 == ''){
          $('#wo1').focus();
      }else{
          $('#wo1').blur();
          $('#wo2').focus();
          if(wo2 == ''){
              $('#wo2').focus();
          }else if(wo2 != wo1){
              layer.alert('工单号不一致', {icon: 0})
              $('#wo2').focus();
          }else{
              $.ajax({
                  type: 'POST',
                  url: '../supOrderOperation/selectWorkOrder',
                  dataType: 'json',
                  contentType: 'application/json; charset=utf-8',
                  headers: {
                      'Accept': 'application/json; charset=utf-8',
                      'Authorization': 'Basic ' + sessionStorage.getItem('token')
                  },
                  data: JSON.stringify({orderNo: wo2}),
                  success: function (res) {
                      if(res.stateCode == 100){
                          $('#tbody').append('<tr><td class="wo2">'+wo2+'</td><td style="cursor: pointer" onclick="$(this).parent().remove()">删除</td></tr>');
                          $('#wo1').val('');
                          $('#wo2').val('');
                          $('#wo1').focus();
                          $('#wo2').blur();
                      }else{
                          layer.alert(res.message, {icon: 0})
                      }
                  },error: function (err) {
                      layer.alert(err.message, {icon: 0})
                  }
              })
          }
      }
    }else{
        return;
    }
}
$('#download').click(function () {
    var orderNoList = [];
    if($('#tbody tr').length == 0){
        layer.alert('工单号为空',{icon:0});
    }else {
        for(var i = 0; i < $('#tbody tr').length; i++){
           var obj = {orderNo: ''};
           obj.orderNo = $('#tbody tr .wo2').eq(i).html();
           orderNoList[i] = obj;
        }
        $.ajax({
            type: 'POST',
            url: '../supOrderOperation/saveWorkOrder',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({orderNoList:orderNoList}),
            success: function (res) {
                   if(res.stateCode == 100){
                       layer.alert('成功！', {icon: 1});
                       location.href = '../WOManage/WOoperateHtml?type='+type+'';
                   }else {
                       layer.alert(res.message, {icon: 0})
                   }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
    }
});
