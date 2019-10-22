function getRootPath_dc() {
    var pathName = window.location.pathname.substring(1);
    var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/'));
    // console.log(pathName)
    // console.log(webName)
    if (webName == "" || webName != "kaa_admin") {
        return window.location.protocol + '//' + window.location.host;
    } else {
        return window.location.protocol + '//' + window.location.host + '/' + webName;
    }
}

UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
UE.Editor.prototype.getActionUrl = function(action) {
    if (action == "uploadimage") {
//        return "${contextPath}/ueditor/uploadImage";
//        return "../../uploadImage";
//        console.log(window.UEDITOR_HOME_URL)
//        return "${contextPath}/../../../uploadImage";
//         return "http://kaasales.free.ngrok.cc/kaa_admin/ueditor/uploadImage";
        return getRootPath_dc() + "/ueditor/uploadImage";
    } else if (action == "uploadvideo") {
        return getRootPath_dc() + "/ueditor/uploadVideo";
    } else {
        return this._bkGetActionUrl.call(this, action);
    }
};





