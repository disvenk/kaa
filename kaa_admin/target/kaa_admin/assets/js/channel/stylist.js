var list = [
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白小纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:1},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白一纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:2},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白二纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:3},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白三纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:4},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白四纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:5},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白五纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:6},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白六纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:7},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白七纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:8},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白八纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:9},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白九纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:10},
    {headImg:'http://p0.so.qhimgs1.com/bdr/_240_/t0116b832af1505307c.jpg',name:'白十纯',profession:'一枚设计师',productions:30,views:1000,fork:1000,id:11}
  ] 
for(var i = 0; i< list.length; i++){
    var div = ' <a class="div" href="detail.html?'+list[i].id+'"><div class="headImg"><img id="headImg" style="width:100%;border-radius:50%" src="'+list[i].headImg+'" alt="头像"></div>'+
    '<span class="name">'+list[i].name+'</span><span class="profession">'+list[i].profession+'</span><span class="produces">作品 &nbsp;&nbsp;'+list[i].productions+'</span>'+
   '<span class="views"><img style="width:16pt" src="../../img/channel/eye2.png" alt="">'+list[i].views+'</span><span class="forks">'+
     '<img style="width:16pt" src="../../img/channel/zhuanfa2.png" alt="">'+list[i].fork+'</span></a>'
    $('#container').append(div)
}