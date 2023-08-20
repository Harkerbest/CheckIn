<%@ page import="cn.harkerBest.checkInBaseServer.CheckInServlet" pageEncoding="UTF-8"%>
<html>
<head>
    <title>CheckIn</title>
    <link rel="stylesheet"  type="text/css"  href="index.css"/>
</head>
<body>
<div id="mainDiv">
    <div id="questionsDiv"><%
    if (request.getMethod().equals("GET")){
        out.println(
                "<form id = \"mainForm\" action = \"" +
                        request.getRequestURI() +
                        "\" method = \"post\" >"+
                "<input type = \"hidden\" name = \"start\" value = \"true\" >"+
                        "<p class=\"text\" id=\"title\">");
        out.println(
                        "电教委交流群入群测试V3.1\n"
        );
        out.println("</p>");
        out.println("<hr/>");
        out.println("<pre class=\"text\" id=\"preface\">");
        out.println(
                        "让我猜猜，你是一位资深的电教委?\n" +
                        "或者每天来到班里，看到的全是“电脑砖家”，找不到志同道合的网友交流见解?\n" +
                        "欢迎来到希沃售后业绩冲击部！\n" +
                        "本次入群考试在上次的基础上大幅增加了题目多样性。大部分题目都是常识级别，欢迎各位来试试水！\n" +
                        "注意:\n" +
                                "1.本试题完全开卷，答题时间不限。可能需要查阅资料，请各位在开始答题前确保设备与环境合适。\n" +
                                "2.每道题2分，分数线为60分，满分100，多选题必须选出全部正确答案才能得分。\n" +
                                "3.答题次数不限，答案顺序随机，100分进群可获得群内专属头衔。\n" +
                                "4.本题目检验的并不完全是个人水平，也包括查找搜寻资料的学习能力，所以一定记得善用开卷考试的特点！！！"
        );
        out.println("</pre>");
        out.println("<div id=\"QQlogin\" class=\"text\">");
        out.println("以后这里写QQ登录");
        out.println("</div>");
        out.println(
                "<input id = \"submitButton\" type = \"submit\" value = \"开始答题\" >"+
                "</form >"
                );
    } else if(request.getParameter("start")!=null){
        out.println("<script src='https://js.hcaptcha.com/1/api.js' async defer></script>");
        out.println("<form id = \"mainForm\" action = \""+ request.getRequestURI() +"\" method = \"post\" >");
        for (int i = 0; i < 20; i++) {
            out.println("<p class=\"text\">");
            out.println("这是第" + i + "题");
            out.println("<div>");
            boolean single = (int) (Math.random() * 2) == 0;
            String type = single ? "radio" : "checkbox";
            for (int j = 0; j < 4; j++) {
                out.println("<div style=\"display: flex;\">");
                    out.println("<input class=\"option\" type = \""+type+"\" name = \"question_"+i+"\" value = \"" + j + "\" id= \"question_"+i+"_option_"+j+"\"/>");
                    out.println("<label for=\"question_"+i+"_option_"+j+"\" class=\"text\"> 选项" + (1+j) + "</label>");
                    out.println("<br/>");
                out.println("</div>");
            }
            out.println("</div>");
            out.println("</p>");
            out.println("<br/>");
            out.println("<br/>");
        }
        out.println(
                "<div id = \"h-captcha\" data-callback = \"captchaCallback\" data-sitekey = \"558405d8-cc09-4bfe-b7a3-0754a2f880e8\" data-theme=\"dark\" class=\"mt-4 h-captcha\" ></div >\n"+
                "<input id = \"submitButton\" type = \"submit\" value = \"提交\" >\n"+
                "<input type = \"hidden\" name = \"submitAnswers\" value = \"true\" >"+
                "</form >\n"
        );
//        out.println("<iframe src=\"\" name=\"submitFrame\" width=\"200\" height=\"50\"></iframe>");
    } else if (request.getParameter("submitAnswers")!=null) {
        CheckInServlet checkInServlet = new CheckInServlet();
//        request.setAttribute("h-captcha-response",response.getParameter("h-captcha-response"));
        checkInServlet.doPost(request,response);
        out.println("<div class=\"text\">完成作答,"+ checkInServlet.result.toString() +"</div>");
    }
%>
</div>
</div>
</body>
</html>
