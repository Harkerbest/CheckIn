<%@ page import="cn.harkerBest.checkInBaseServer.CheckInServlet" %>
<%@page pageEncoding="UTF-8"%>
<html>
<head>
    <title>CheckIn</title>
</head>
<body><%
    if (request.getMethod().equals("GET")){
        out.println(
                "<form id = \"mainForm\" action = \"" +
                        request.getRequestURI() +
                        "\" method = \"post\" >"+
                "<input type = \"hidden\" name = \"start\" value = \"true\" >"+
                "<input id = \"submitButton\" type = \"submit\" value = \"开始答题\" >"+
                "</form >"
                );
    } else if(request.getParameter("start")!=null){
        out.println("<script src='https://js.hcaptcha.com/1/api.js' async defer></script>");
        out.println("<form id = \"mainForm\" action = \""+ request.getRequestURI() +"\" method = \"post\" >");
        for (int i = 0; i < 20; i++) {
            out.println("<p>");
            out.println("这是第" + i + "题");
            out.println("<div>");
            for (int j = 0; j < 4; j++) {
                out.println("<input type = \"radio\" name = \"question_"+i+"option_"+j+"\"value = \"" + j + "\" >");
                out.println("选项" + (1+j));
                out.println("<br>");
            }
            out.println("</div>");
            out.println("</p>");
        }
        out.println(
                "<div id = \"h-captcha\" data - callback = \"captchaCallback\" data-sitekey = \"558405d8-cc09-4bfe-b7a3-0754a2f880e8\" class=\"mt-4 h-captcha\" ></div >\n"+
                "<input id = \"submitButton\" type = \"submit\" value = \"提交\" >\n"+
                "<input type = \"hidden\" name = \"submitAnswers\" value = \"true\" >"+
                "</form >\"\n"
        );
        out.println("<iframe src=\"\" name=\"submitFrame\" width=\"200\" height=\"50\"></iframe>");
    } else if (request.getParameter("submitAnswers")!=null) {
        CheckInServlet checkInServlet = new CheckInServlet();
//        request.setAttribute("h-captcha-response",response.getParameter("h-captcha-response"));
        checkInServlet.doPost(request,response);
        out.println("<div>完成作答,"+ checkInServlet.result.toString() +"</div>");
    }
%>
<%--<div id="result"></div>--%>
<%--<script> function submitForm() { //创建XMLHttpRequest对象
    var xhr = new XMLHttpRequest(); //设置请求方法和URL
    xhr.open("post", "${pageContext.request.contextPath}/submit"); //设置请求头
    xhr.setRequestHeader("Content-Type", "application/x-www-form.xml-urlencoded"); //获取表单数据
    var formData = new FormData(document.getElementById("mainForm")); //发送请求
    xhr.send(formData); //注册回调函数
    xhr.onreadystatechange = function() { //判断请求状态和响应状态
        if (xhr.readyState === 4 && xhr.status === 200) { //获取响应文本
            //将响应文本显示在result元素中
            // document.getElementById("result").innerHTML = xhr.responseText;
            print("success")
            document.getElementById("submitButton").disabled = "true"
        }
    }
}
</script>--%>
</body>
</html>
