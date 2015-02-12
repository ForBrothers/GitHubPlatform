<%@ page language="java"  pageEncoding="UTF-8"%>
<%
    String localPath = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<base href="<%=basePath%>">
<title>Platform</title>
<meta charset="utf-8">
<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="Platform">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="<%=localPath%>/scripts/bootstrap/css/bootstrap.css">
<link rel="stylesheet" type="text/css" href="<%=localPath%>/css/font-awesome/css/font-awesome.css">
<link rel="stylesheet" type="text/css" href="<%=localPath%>/css/default/theme.css">
<link rel="stylesheet" type="text/css" href="<%=localPath%>/css/default/premium.css">
<link rel="stylesheet" type="text/css" href="<%=localPath%>/scripts/toastmessage/css/jquery.toastmessage.css">
<script type="text/javascript" src="<%=localPath%>/scripts/jQuery/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="<%=localPath%>/scripts/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=localPath%>/scripts/toastmessage/jquery.toastmessage.js"></script>
<script type="text/javascript" src="<%=localPath%>/scripts/common/common.js"></script>


<link rel="stylesheet" type="text/css" href="<%=localPath%>/scripts/bootstrap-table/bootstrap-table.min.css">
    <script type="text/javascript" src="<%=localPath%>/scripts/bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="<%=localPath%>/scripts/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>