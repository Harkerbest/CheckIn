<%@ page import="java.util.List" %>
<%@ page import="java.time.Instant" %>
<%@ page import="cn.harkerBest.checkInBaseServer.TrafficLogger" %>
<%@ page import="cn.harkerBest.checkInBaseServer.dataFormat.Question" %>
<%@ page import="cn.harkerBest.checkInBaseServer.io.QuestionData" %>
<%@ page import="cn.harkerBest.checkInBaseServer.dataFormat.Choice" %>
<%@ page %><%--
  Created by IntelliJ IDEA.
  User: etern
  Date: 2023/8/20
  Time: 15:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>CheckIn管理页</title>
    <link rel="stylesheet" type="text/css" href="manage.css"/>
    <script src="https://cdn.staticfile.org/echarts/4.3.0/echarts.min.js"></script>
</head>
<body>
<%
    if (request.getMethod().equals("POST")) {
        if (request.getParameter("username") != null && request.getParameter("password") != null &&
                request.getParameter("username").equals("admin") && request.getParameter("password").equals("admin")) {
            Cookie loginCookie = new Cookie("login", request.getRequestURI());
            loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);
            response.sendRedirect(request.getRequestURI());
        } else if (request.getParameter("logout") != null) {
            Cookie loginCookie = new Cookie("login", request.getRequestURI());
            loginCookie.setHttpOnly(true);
            loginCookie.setMaxAge(0);
            response.addCookie(loginCookie);
            response.sendRedirect(request.getRequestURI());
        } else {
            response.sendRedirect(request.getRequestURI() + "?error=1");
        }
    } else {
        boolean logon = false;
        final String requestURI = request.getRequestURI();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("login") && cookie.getValue().equals(requestURI)) {
                    logon = true;
                }
            }
            Cookie loginCookie = new Cookie("login", requestURI);
            loginCookie.setHttpOnly(true);
        }
%>
<% if (logon) { %>
<div style="flex-direction: row;display: flex;">
    <div id="shrink" class="pageChoose" style="text-align: center;background: #1f2023;width: 50px;height: 50px"
         onclick="function shrink() {
                var left = document.getElementById('left');
                var right = document.getElementById('right');
                var shrink = document.getElementById('shrink');
                var logout = document.getElementById('logout');
                if (left.style.width === '10%') {
                    left.style.width = '0%';
                    left.style.borderRadius = '0px';
                    left.style.padding = '0px';
                    right.style.width = '100%';
                    right.style.marginLeft = '0%';
                    shrink.style.left = '0%';
                    shrink.innerHTML = '<p class=&quot;shrink&quot;>展开</p>';
                    logout.style.visibility = 'hidden';
                } else {
                    left.style.width = '10%';
                    left.style.borderRadius = '15px';
                    left.style.padding = '20px';
                    right.style.width = '88%';
                    right.style.marginLeft = '1%';
                    shrink.style.left = '10%';
                    shrink.innerHTML = '<p class=&quot;shrink&quot;>收起</p>';
                    logout.style.visibility = 'visible';
                }
        }
        shrink()"><p class="shrink">收起</p></div>
    <h1 class="text" style="margin-left: 4px">CheckIn管理页</h1>
</div>
<div id="main">
    <div id="left">
        <%
            String[] tabNames = {"流量", "题库", "添加题目", "设置", "账户设置", "", "", "", "", ""};
            int i = 0;
            for (String tabName : tabNames) {
                String selectedStyle = "";
                if (request.getParameter("page") != null && request.getParameter("page").equals(String.valueOf(i))) {
                    selectedStyle = "style=\"background-color: #414244\"";
                }
                out.println("<div class=\"pageChoose\" " + selectedStyle + " onclick=\"window.open('" + request.getRequestURL() + "?page=" + i + "','_self')\"><p class=\"tabText\">" + tabName + "</p></div>");
                i++;
            } %>
        <form method="POST" action="<%=requestURI%>">
            <input type="hidden" name="logout" value="1"/>
            <input class="button" id="logout" type="submit" value="退出" style="width: 100%; margin-top: 10px"/>
        </form>
    </div>
    <div id="right">
        <% if (request.getParameter("page") != null)
            if (request.getParameter("page").equals("0")) { %>
        <div id="chart" style="width: 100%;height: 300px;"></div>
        <script type="text/javascript">
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('chart'));

            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '流量',
                    textStyle: {
                        color: '#d3d3d3'
                    }
                },
                tooltip: {},
                legend: {
                    data: ['流量'],
                    textStyle: {
                        color: '#d3d3d3'
                    }
                },
                xAxis: {
                    data: [<%for (int d=0;d<=7;d++){
                        out.print("\""+Instant.now().minusSeconds(d*24*60*60).toString().substring(0,10)+"\",");
                    }%>],
                    axisLabel: {
                        textStyle: {
                            color: '#d3d3d3'
                        }
                    }
                },
                yAxis: {
                    axisLabel: {
                        textStyle: {
                            color: '#d3d3d3'
                        }
                    }
                },
                series: [{
                    name: '访问次数',
                    type: 'line',
                    data: [<%for (int d=0;d<=7;d++){
                        out.print("\""+TrafficLogger.getInstance().getAmountOf(Instant.now().minusSeconds(d*24*60*60).toString().substring(0,10),"mainCount")+"\",");
                    }%>],

                }]
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        </script>
        <% } else if (request.getParameter("page").equals("1")) {
            QuestionData instance = QuestionData.getInstance();
            instance.readFromFile();%>
        <div id="overView">
            <table>
                <tr>
                    <th>题目描述</th>
                    <th>选项</th>
                    <th>分区</th>
                    <th>Hash</th>
                </tr>
                <%
                    try {
                        for (Question question : instance.getQuestionsByTypeZone(QuestionData.TypeZone.UNDEFINED)) {
                            final List<Choice> choices = question.getChoices();%>
                <tr>
                    <td rowspan="<%=choices.size()%>" style="width:50%;"><%=question.getContent()%>
                    </td>
                    <%
                        boolean first = true;
                        for (Choice choice : choices) {
                            if (!first) {%>
                </tr>
                <tr>
                    <%}%>
                    <td style="background:<%=(choice.isCorrect()?"#324132":"#422b2b")%>;width: 40px"><%=choice%>
                    </td>
                    <%
                            if (first) {
                                first = false;
                                %>
                    <td rowspan="<%=choices.size()%>" style="width:8%"><%=question.getTypeZone()%>
                    <td rowspan="<%=choices.size()%>" style="width:10%"><%=QuestionData.MD5Of(question)%>
                    </td>
                    <%
                            }
                        }
                    %>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                %>
            </table>
        </div>
        <%
                } else if (request.getParameter("page").equals("3")) {

                }%>
    </div>
        <% } else { %>
    <div id="login">
        <h1 id="loginTitle" class="text">CheckIn管理页</h1>
        <form method="post" action="<%out.println(requestURI);%>">
            <div class="margin"><label>
                <input class="textInput" type="text" name="username" placeholder="用户名"/>
            </label></div>
            <div class="margin"><label>
                <input class="textInput" type="password" name="password" placeholder="密码"/>
            </label></div>
            <div id="buttonDiv" class="margin"><input class="button" type="submit" value="登录"/></div>
        </form>
            <% if (request.getParameter("error") != null){ %>
        <p class="text" style="font-size: 10px">用户名或密码错误</p>
            <%}
        }
    }%>
</body>
</html>
