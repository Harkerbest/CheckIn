package cn.harkerBest.checkInBaseServer;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class CheckInServlet extends HttpServlet {
    public StringBuilder result = new StringBuilder();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws java.io.IOException {
        resp.getWriter().println("收到提交");
    }
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws java.io.IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        resp.getWriter().println("收到提交");
//        result.append("收到提交");
        System.out.println("收到提交");
//        request.POST_DATA['h-captcha-response']
        final String hCaptchaResp = req.getParameter("h-captcha-response");
        String hCaptchaResult = SendHttpPOST("https://hcaptcha.com/siteverify","'response':"+ hCaptchaResp +", 'secret': 0x41CBe0fEf50608BC872869Bc68F43622fF9a8286");
        System.out.println(hCaptchaResp);
        System.out.println(hCaptchaResult);
        Gson gson = new Gson();
        Map resultMap = gson.fromJson(hCaptchaResult, Map.class);
        if (resultMap == null){
            resp.getWriter().println("验证失败");
            result.append("验证失败");
            System.out.println("验证失败");
            return;
        }
        if (resultMap.get("success").equals(true)) {
            resp.getWriter().println("验证成功");
            result.append("验证成功");
            System.out.println("验证成功");
        } else {
            if (((ArrayList<Object>)resultMap.get("error-codes")).get(0).equals("missing-input-response")){
                resp.getWriter().println("没有点击验证");
                result.append("没有点击验证");
                System.out.println("没有点击验证");
            } else {
                resp.getWriter().println("验证失败");
                result.append("验证失败");
                System.out.println("验证失败");
            }
        }
    }
    public String getInfo(){
        return result.toString();
    }
    public static String SendHttpPOST(String url, String data) throws IOException {
        StringBuilder result = null;
        //打开连接
        //要发送的POST请求url?Key=Value&amp;Key2=Value2&amp;Key3=Value3的形式
        //URL requestUrl = new URL(url + "?" + requestParam);
        
        URL requestUrl;
        try {
            requestUrl = new URI(url).toURL();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection httpConn = (HttpURLConnection)requestUrl.openConnection();
        
        //加入数据
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setRequestProperty("Content-Type", "application/x-www-form.xml-urlencoded");
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream(), StandardCharsets.UTF_8);
        
        //body参数在这里put到JSONObject中
        
        writer.write(data);
        writer.flush();
        //获取输入流
        BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8));
        int code = httpConn.getResponseCode();
        if (HttpURLConnection.HTTP_OK == code || HttpURLConnection.HTTP_CREATED == code){
            String temp = in.readLine();
            /*连接成一个字符串*/
            while (temp != null) {
                if (result != null) {
                    result.append(temp);
                }else {
                    result = new StringBuilder(temp);
                }
                temp = in.readLine();
            }
        }
        if (result != null) {
            return result.toString();
        } else {
            return null;
        }
    }
}
