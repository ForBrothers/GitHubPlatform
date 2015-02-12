<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
    <%@include file="/WEB-INF/pages/frame/common/common.jsp" %>
    <style type="text/css">
        #line-chart {
            height: 300px;
            width: 800px;
            margin: 0px auto;
            margin-top: 1em;
        }
        .navbar-default .navbar-brand, .navbar-default .navbar-brand:hover {
            color: #fff;
        }
    </style>
</head>
<body class="theme-blue">
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
        <a class="" href="index.html"><span class="navbar-brand"><span
                class="fa fa-paper-plane"></span>Manage System</span></a>
    </div>
    <div class="navbar-collapse collapse" style="height: 1px;">
    </div>
</div>
</div>
<div class="dialog">
    <div class="panel panel-default">
        <p class="panel-heading no-collapse" style=""><span class=" glyphicon glyphicon-log-in"></span>  登录系统</p>
        <div class="panel-body">
            <form>
                <div class="form-group">
                    <label class="control-label" >用户名</label>
                    <input type="text" id="userName" name="userName" placeholder="请输入用户名" value="admin" class="form-control" >
                </div>
                <div class="form-group">
                    <label class="control-label">密    码</label>
                    <input type="password" id="userPassword"  class="form-control" value="admin" placeholder="请输入密码">
                </div>
                <a href="javascript:void(0)" id="signIn" class="btn btn-primary pull-right">登 录</a>
                <label class="remember-me"><input type="checkbox"> 记住我</label>
                <div class="clearfix"></div>
            </form>
        </div>
    </div>
    <p class="pull-right" style=""></p>
    <p><a href="reset-password.html">忘记密码?</a></p>
</div>
<script type="text/javascript">
    $(function () {

       $("#signIn").click(function(){
           var userName = $.trim($("#userName").val());
           var userPassword = $("#userPassword").val();
           $.ajax( {
               type : "POST",
               url : "<%=path%>/frame/signIn",
               data : {
                 'userName' : userName,
                 'userPassword' : userPassword
                },
               dataType: "json",
               success : function(data) {
                   if(data.result){
                       window.location.href = "<%=path%>/frame/index";
                   }
                   else{
                       alert(data.msg);
                   }
               },
               error :function(){

               }
           });
       });
        document.onkeydown = function (event) {
                    var e = event || window.event || arguments.callee.caller.arguments[0];
                    if (e && e.keyCode == 13) {
                        $("#signIn").click();
                    }
                };
    });

</script>
</body>
</html>
