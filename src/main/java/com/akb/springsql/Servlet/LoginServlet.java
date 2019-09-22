package com.akb.springsql.Servlet;

import com.akb.springsql.Tools;
import com.akb.springsql.pojo.Adminstrator;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/login",name = "LoginServlet")
public class LoginServlet extends HttpServlet {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject json;
        JSONObject result=new JSONObject();

        //读取json数据部分
        BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        PrintWriter pw=response.getWriter();

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }
        json=JSONObject.parseObject(sb.toString());

        System.out.println(json);
        String ac=json.getString("ac");
        String pwd=json.getString("pwd");
        if("".equals(ac) || "".equals(pwd) ||ac==null ||pwd==null){
            result.put("code",301);
            result.put("descript","information error");
            pw.write(result.toJSONString());
            return;
        }

        String sql="select * from jadmin where account=?";

        RowMapper<Adminstrator> rowMapper=new BeanPropertyRowMapper<Adminstrator>(Adminstrator.class);
        System.out.println(jdbcTemplate);
        Adminstrator adm;
        try{
            adm= jdbcTemplate.queryForObject(sql, rowMapper,ac);
        }catch (EmptyResultDataAccessException e){
            adm=null;
        }
        System.out.println(adm);

        if(adm==null || !pwd.equals(adm.getPwd())){
            result.put("code",301);
            result.put("descript","information error");
            pw.write(result.toJSONString());
            System.out.println("验证失败");
            return;
        }

        result.put("code",0);
        result.put("descript","success");
        String key=Tools.KeyValue_lenght(64);
        jdbcTemplate.update("update jadmin set cookiesk='"+key+"'");
        Cookie cookie1=new Cookie("adminkey",key);
        cookie1.setMaxAge(60*60);
        Cookie cookie2=new Cookie("adminaccout",adm.getAccount());
        cookie2.setMaxAge(60*60);
        System.out.println("验证成功，写入cookies:"+key);
        response.addCookie(cookie1);
        response.addCookie(cookie2);

        pw.write(result.toJSONString());
    }
}
