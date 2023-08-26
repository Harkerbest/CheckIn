<%@ page import="cn.harkerBest.checkInBaseServer.CheckInServlet" pageEncoding="UTF-8"%>
<%@ page import="cn.harkerBest.checkInBaseServer.TrafficLogger" %>
<html>
<head>
    <title>CheckIn</title>
    <link rel="stylesheet"  type="text/css"  href="index.css"/>
</head>
<body>
<div id="mainDiv">
    <div id="questionsDiv"><%
    if (request.getMethod().equals("GET")){
        try {
            TrafficLogger trafficLogger = TrafficLogger.getInstance();
            trafficLogger.logOnce();
        } catch (Exception e) {
            e.printStackTrace();
        }%>
        
        <form id="mainForm" action="<%=request.getRequestURI()%>" method="post" >
                "<input type="hidden" name="start" value="true" >
                        "<p class="text" id="title">
                        电教委交流群入群测试V3.1
        </p>
        <hr/>
        <pre class="text" id="preface">
                        让我猜猜，你是一位资深的电教委?
                        或者每天来到班里，看到的全是“电脑砖家”，找不到志同道合的网友交流见解?
                        欢迎来到希沃售后业绩冲击部！
                        本次入群考试在上次的基础上大幅增加了题目多样性。大部分题目都是常识级别，欢迎各位来试试水！
                        注意:
                                1.本试题完全开卷，答题时间不限。可能需要查阅资料，请各位在开始答题前确保设备与环境合适。
                                2.每道题2分，分数线为60分，满分100，多选题必须选出全部正确答案才能得分。
                                3.答题次数不限，答案顺序随机，100分进群可获得群内专属头衔。
                                4.本题目检验的并不完全是个人水平，也包括查找搜寻资料的学习能力，所以一定记得善用开卷考试的特点！！！
        </pre>
        <div id="QQlogin" class="text">
        以后这里写QQ登录
        </div>
            <input id="submitButton1" type="submit" value="开始答题">
        </form >
    <% } else if(request.getParameter("start")!=null){ %>
        <script src='https://js.hcaptcha.com/1/api.js' async defer></script>
        <form id = "mainForm" action = "<%=request.getRequestURI()%>" method="post">
        <% for (int i = 0; i < 20; i++) { %>
            <p class="text">
            这是第<%=i%>题
            <div>
            <% boolean single = (int) (Math.random() * 2) == 0; %>
            <% String type = single ? "radio" : "checkbox";%>
            <% for (int j = 0; j < 4; j++) { %>
                <div style="display: flex;">
                    <input class="option" type="<%=type%>" name = "question_<%=i%>" value = <%=j%> id="question_<%=i%>_option_<%=j%>">
                    <label for="question_<%=i%>_option_<%=j%>" class="text"> 选项<%=(1+j)%></label>
                    <br/>
                </div>
            <% } %>
            </div>
            <br/>
            <br/>
        <%}%>
        
                <div id = "h-captcha" data-callback = "captchaCallback" data-sitekey = "558405d8-cc09-4bfe-b7a3-0754a2f880e8" data-theme="dark" class="mt-4 h-captcha" ></div >
                <input id = "submitButton" type = "submit" value = "提交" >
                <input type = "hidden" name = "submitAnswers" value = "true">
                </form>
    <% } else if (request.getParameter("submitAnswers")!=null) {
        CheckInServlet checkInServlet = new CheckInServlet();
        checkInServlet.doPost(request,response);%>
        <div class="text">完成作答,<%=checkInServlet.result.toString()%></div>
        <%}%>
</div>
</div>
</body>
</html>
