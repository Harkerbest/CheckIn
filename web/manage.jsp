<%--
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
    <link rel="stylesheet"  type="text/css"  href="manage.css"/>
</head>
<body>
<%
    if (request.getMethod().equals("POST")){

        if (request.getParameter("username")!=null && request.getParameter("password") != null &&
                request.getParameter("username").equals("admin") && request.getParameter("password").equals("admin")){
            Cookie loginCookie = new Cookie("login",request.getRequestURI());
            loginCookie.setHttpOnly(true);
            response.addCookie(loginCookie);
            response.sendRedirect(request.getRequestURI());
        } else if (request.getParameter("logout") != null) {
            Cookie loginCookie = new Cookie("login",request.getRequestURI());
            loginCookie.setHttpOnly(true);
            loginCookie.setMaxAge(0);
            response.addCookie(loginCookie);
            response.sendRedirect(request.getRequestURI());
        } else {
            response.sendRedirect(request.getRequestURI()+"?error=1");
        }
    } else {
        boolean logon = false;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies() ) {
                if (cookie.getName().equals("login") && cookie.getValue().equals(request.getRequestURI())) {
                    logon = true;
                }
            }
            Cookie loginCookie = new Cookie("login",request.getRequestURI());
            loginCookie.setHttpOnly(true);
        }

        if (logon){
            out.println("<h1 class=\"text\">CheckIn管理页</h1>");
            out.println("<div id=\"main\">");
            out.println("<div id=\"left\">");
            for (int i = 0; i < 10; i++) {
                String selectedStyle = "";
                if (request.getParameter("page") != null && request.getParameter("page").equals(String.valueOf(i))) {
                    selectedStyle = "style=\"background-color: #414244\"";
                }
                out.println("<div class=\"pageChoose\" "+selectedStyle +" onclick=\"window.open('"+request.getRequestURL()+"?page=" + i + "','_self')\"></div>");
            }
            out.println("<form action = \""+ request.getRequestURI() +"\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"logout\" value=\"1\"/>");
            out.println("<input class=\"button\" type=\"submit\" value=\"退出\"/>");
            out.println("</form>");
            out.println("</div>");
            out.println("<div id=\"right\">");
            out.println("</div>");
        } else {
            out.println("<div id=\"login\">");
            out.println("<h1 id=\"loginTitle\" class=\"text\">CheckIn管理页</h1>");
            out.println("<form action = \""+ request.getRequestURI() +"\" method=\"post\">");
            out.println("<div class=\"margin\"><input class=\"textInput\" type=\"text\" name=\"username\" placeholder=\"用户名\"/></div>");
            out.println("<div class=\"margin\"><input class=\"textInput\" type=\"password\" name=\"password\" placeholder=\"密码\"/></div>");
            out.println("<div id=\"buttonDiv\" class=\"margin\"><input class=\"button\" type=\"submit\" value=\"登录\"/></div>");
            out.println("</form>");
            if (request.getParameter("error") != null) out.println("<p class=\"text\">用户名或密码错误</p>");
        }
    }


%>
</body>
</html>
