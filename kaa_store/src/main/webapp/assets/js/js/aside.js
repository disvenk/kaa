$(function () {
    var sides = '<div id="sidebar" class="sidebar" style="position: fixed;top: 0;left: 0;height: 100vh">' +
        '<ul class="nav nav-list" style="background-color: rgb(19, 95, 154)">' +
        '<div><img id="logo" src="../assets/img/logo.png" alt="">' +
        '</div></ul></div>';
    $('#main-container').append(sides);
    $.ajax({
        type: 'POST',
        url: '../storeHome/productCategoryList',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            for (var i = 0; i < res.data.length; i++) {
                var side = '';
                side += '<div class="sec" id="'+res.data[i].id+'">' + res.data[i].name + '</div>';
                $('#sidebar ul').append(side);
            }
            $('#sidebar ul .sec').click(function () {
                $(this).siblings().removeClass('sec_active');
                $(this).addClass('sec_active');
                location.href = '/kaa_store/storeHome/indexHtml?store=1-1&type='+$(this).attr('id')+'';
            })
            GetRequest();
            if (type == '' || type == undefined || type == null || type == 'undefined') {
                $('#sidebar ul div').eq(1).addClass('sec_active');

            }else {
                   var sec = $('#sidebar div')
                for(var i = 0; i< sec.length; i++){
                       if(sec[i].id == type){
                           sec.eq(i).addClass('sec_active');
                       }
                }
            }
        },
        error: function (err) {
            alert(err)
        }
    });
    function GetRequest() {
        var url = location.search; //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for (var i = 0; i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
            }
        }
        type = theRequest.type;
    }
})

