package com.akb.springsql.Servlet;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/admin/addstu")
public class AddServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("AddServlet In");
        PrintWriter pw=response.getWriter();
        JSONObject inputJson;
        //读取json数据部分
        BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }

        
        inputJson=JSONObject.parseObject(sb.toString());
        System.out.println(inputJson);


    }
}
