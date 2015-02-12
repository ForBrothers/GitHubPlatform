function showToastWithOptions(options){
    $().toastmessage('showToast', options);
}
function showErrorInfo(info){
    $().toastmessage("showErrorToast",info);
}
function showSuccessInfo(info){
    $().toastmessage("showSuccessToast",info);
}
function showNoticeInfo(info){
    $().toastmessage("showNoticeToast",info);
}
function showWarningInfo(info){
    $().toastmessage("showWarningToast",info);
}