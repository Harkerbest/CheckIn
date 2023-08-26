<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%! int day = 3; %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>菜鸟教程(runoob.com)</title>
</head>
<body>
<h3>IF...ELSE 实例</h3>
<% if (day == 1 || day == 7) { %>
<p>今天是周末</p>
<% } else { %>
<p>今天不是周末</p>
<% } %>
</body>
</html>