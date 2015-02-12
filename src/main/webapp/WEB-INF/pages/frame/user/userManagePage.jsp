<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
%>
<!doctype html>
<html>
<head>
    <title>用户管理</title>
    <%@include file="/WEB-INF/pages/frame/common/common.jsp" %>
    <link rel="stylesheet" type="text/css" href="<%=path%>/scripts/bootstrap-table/bootstrap-table.min.css">
    <script type="text/javascript" src="<%=path%>/scripts/bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="<%=path%>/scripts/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
</head>
<body>
<div class="row">
    <table data-toggle="table"
           data-url="<%=path%>/user/queryUserList"
           data-classes="table table-hover table-condensed"
           data-striped="true"
           data-show-toggle="true"
           data-show-columns="true"
           data-show-export="true"
           data-pagination="true"
           data-click-to-select="true"
           data-select-item-name="myRadioName"
           data-row-style="rowStyle"
           data-side-pagination="server" >
        <thead>
        <tr>
            <th data-field="userId" data-radio="true"></th>
            <th data-field="userName">用户姓名</th>
            <th data-field="loginAccount">登录帐号</th>
            <th data-field="deptName">所属部门</th>
            <th data-field="userStatus">状态</th>
            <th data-field="userSex">性别</th>
            <th data-field="userPhone">手机号码</th>
            <th data-field="userDesc">备注</th>
        </tr>
        </thead>
    </table>
</div>
</body>
<script type="text/javascript">
    function rowStyle(row, index) {
        var classes = ['active', 'info'];
        return { classes: classes[index % 2] };;
    }
    $(function () {
         alert(123);
    });
</script>
</html>
