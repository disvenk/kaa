var img = [
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'},
    {href: '../assets/img/salePlat/1993.jpg'}
];
for(var i = 0; i < img.length; i++){
    var div = '';
    div += '  <div class="col-xs-2 col-md-2 col-sm-2 col-lg-2 col-ass-2">\n' +
        '                <a href="#" class="thumbnail">\n' +
        '                    <img src="../assets/img/salePlat/1993.jpg" alt="...">\n' +
        '                </a>\n' +
        '            </div>';
    $('.row').append(div);
}