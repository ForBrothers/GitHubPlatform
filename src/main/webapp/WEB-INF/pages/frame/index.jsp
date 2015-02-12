<%@ page import="com.platform.frame.user.bean.SysUser" %>
<%@ page import="com.platform.frame.common.util.CommonConstants" %>
<%@ page import="com.platform.frame.common.util.ErrorCode" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String userName = "";
    SysUser loginUser = (SysUser)session.getAttribute(CommonConstants.USER_SESSION_ATTR);
    if(loginUser != null) {
        userName = loginUser.getUserName();
    }
%>
<!doctype html>
<html>
<head>
    <%@include file="/WEB-INF/pages/frame/common/common.jsp" %>
    <style type="text/css">
        .navbar-default .navbar-brand, .navbar-default .navbar-brand:hover {
            color: #fff;
        }
    </style>
</head>
<body class=" theme-blue">
<script type="text/javascript">
    //处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外
    function forbidBackSpace(e) {
        var ev = e || window.event; //获取event对象
        var obj = ev.target || ev.srcElement; //获取事件源
        var t = obj.type || obj.getAttribute('type'); //获取事件源类型
        //获取作为判断条件的事件类型
        var vReadOnly = obj.readOnly;
        var vDisabled = obj.disabled;
        //处理undefined值情况
        vReadOnly = (vReadOnly == undefined) ? false : vReadOnly;
        vDisabled = (vDisabled == undefined) ? true : vDisabled;
        //当敲Backspace键时，事件源类型为密码或单行、多行文本的，
        //并且readOnly属性为true或disabled属性为true的，则退格键失效
        var flag1 = ev.keyCode == 8 && (t == "password" || t == "text" || t == "textarea") && (vReadOnly == true || vDisabled == true);
        //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效
        var flag2 = ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea";
        //判断
        if (flag2 || flag1) return false;
    }
    //禁止后退键 作用于Firefox、Opera
    document.onkeypress = forbidBackSpace;
    //禁止后退键  作用于IE、Chrome
    document.onkeydown = forbidBackSpace;
    $.basePath = "<%=path%>";
    $.ajaxSetup({
        contentType : "application/x-www-form-urlencoded;charset=utf-8",
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            $.sysAjaxError(XMLHttpRequest, textStatus, errorThrown);
        }
    });
    $.sysAjaxError=function(XMLHttpRequest, textStatus, errorThrown)
    {
        //前台超时
        if("timeout "== textStatus)
        {
            showErrorInfo("响应超时");
            return;
        }
        var result = $.parseJSON(XMLHttpRequest.responseText);
        //session超时
        if(result && <%=ErrorCode.LOGIN_TIMEOUT_CODE%> == result.errorCode)
        {
            // 如果超时就处理 ，指定要跳转的页面
            var options = {"text":"登录超时，请重新登录","close":function(){window.document.location.href = $.basePath + "/frame/login"}};
            showToastWithOptions(options);
        }
        //数据库错误
        else if(result && <%=ErrorCode.DATABASE_ERROR_CODE%> ==result.errorCode)
        {
            showErrorInfo(result.errorMsg);
        }
        //系统普通错误
        else if(result && <%=ErrorCode.SYSTEM_ERROR_CODE%> ==result.errorCode)
        {
            showErrorInfo(result.errorMsg);
        }
        else {
            showErrorInfo("未知错误");
        }
    }
    $(function () {
        var match = document.cookie.match(new RegExp('color=([^;]+)'));
        if (match) var color = match[1];
        if (color) {
            $('body').removeClass(function (index, css) {
                return (css.match(/\btheme-\S+/g) || []).join(' ')
            })
            $('body').addClass('theme-' + color);
        }
        $('[data-popover="true"]').popover({html: true});
    });
    $(function () {
        var uls = $('.sidebar-nav > ul > *').clone();
        uls.addClass('visible-xs');
        $('#main-menu').append(uls.clone());
    });
</script>
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<!--[if lt IE 7 ]>
<body class="ie ie6"> <![endif]-->
<!--[if IE 7 ]>
<body class="ie ie7 "> <![endif]-->
<!--[if IE 8 ]>
<body class="ie ie8 "> <![endif]-->
<!--[if IE 9 ]>
<body class="ie ie9 "> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<!--<![endif]-->
<div class="navbar navbar-default" role="navigation">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="" href="<%=path%>/frame/index"><span class="navbar-brand"><span class="fa fa-paper-plane"></span> Manager System</span></a>
    </div>
    <div class="navbar-collapse collapse" style="height: 1px;">
        <ul id="main-menu" class="nav navbar-nav navbar-right">
            <li class="dropdown hidden-xs">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                    <span class="glyphicon glyphicon-user padding-right-small" style="position:relative;top: 3px;"></span> <%=userName%>
                    <i class="fa fa-caret-down"></i>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="./">My Account</a></li>
                    <li class="divider"></li>
                    <li><a tabindex="-1" href="<%=path%>/frame/loginOut">Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>
</div>
<div class="sidebar-nav">
    <ul>
        <li>
            <a href="javascript:void(0)" data-target=".dashboard-menu" class="nav-header" data-toggle="collapse">
                <i class="fa fa-fw fa-dashboard"></i> 系统管理<i class="fa fa-collapse"></i>
            </a>
        </li>
        <li>
            <ul class="dashboard-menu nav nav-list collapse in">
                <li><a href="javascript:void(0)" onclick="loadPageContent(this)" url="/user/userManagePage"><span class="fa fa-caret-right"></span> 用户管理</a></li>
                <li><a href=""><span class="fa fa-caret-right"></span> 菜单管理</a></li>
                <li><a href=""><span class="fa fa-caret-right"></span> 部门管理</a></li>
                <li><a href=""><span class="fa fa-caret-right"></span> 权限管理</a></li>
                <li><a href=""><span class="fa fa-caret-right"></span> 参数管理</a></li>
            </ul>
        </li>
    </ul>
</div>
<div class="content" >
<div class="header" style="height: 30px">
    <div class="stats">
        <p class="stat"><span class="label label-info">0</span> Tickets</p>
        <p class="stat"><span class="label label-success">0</span> Tasks</p>
        <p class="stat"><span class="label label-danger">0</span> Overdue</p>
    </div>
    <ul class="breadcrumb">
        <li><a href="<%=path%>/frame/index">Home</a></li>
        <%--<li class="active">Dashboard</li>--%>
    </ul>
</div>
<div class="main-content" id="indexPageContent" style="margin-top:2px">
</div>
</div>
</body>
<script type="text/javascript">
    function loadPageContent(linkObject){
        if($(linkObject).length > 0) {
            var url = $.basePath + $(linkObject).attr("url");
            if(url){
               try{
                   $("#indexPageContent").load(url);
                }
                catch(e){
                    showErrorInfo("加载页面异常");
                }
            }
            else {
                showErrorInfo("菜单URL为空");
            }
        }
    }
</script>
</html>
