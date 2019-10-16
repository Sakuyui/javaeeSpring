package com.akb.springsql.Servlet;

import com.akb.springsql.Base64Util;
import com.akb.springsql.Tools;
import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(urlPatterns = "/dovote/prevote")
public class PreVoteServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse res) throws IOException {
        //获取日期并加密
        Date now=new Date();
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String DesdateStr= Base64.byteArrayToBase64(Tools.encrypt(f.format(now).getBytes(),"akbsakuy"));


        System.out.println("date="+f.format(now));
        request.getSession().setAttribute("key1",DesdateStr);
        System.out.println("in prevote servlet: write key1="+DesdateStr);

        //key1.setMaxAge(60*3);  //后续操作要在3分钟内完成

        PrintWriter pw=res.getWriter();
        JSONObject resultJSON=new JSONObject();
        resultJSON.put("code",0);
        resultJSON.put("status","success");
        pw.write(resultJSON.toJSONString());
    }
}
