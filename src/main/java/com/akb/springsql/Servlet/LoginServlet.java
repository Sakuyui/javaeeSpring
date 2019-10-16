package com.akb.springsql.Servlet;

import com.akb.springsql.Tools;
import com.akb.springsql.pojo.Adminstrator;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.javassist.tools.rmi.ObjectNotFoundException;
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


        //获取传入的账号与密码
        String ac=json.getString("ac");
        String pwd=json.getString("pwd");
        if("".equals(ac) || "".equals(pwd) ||ac==null ||pwd==null){
            result.put("code",301);
            result.put("descript","information error");
            pw.write(result.toJSONString());
            return;
        }


        boolean isAdmin=true;

        //查看是否是管理员
        String sql="select * from jadmin where account=?";

        String key="";
        RowMapper<Adminstrator> rowMapper=new BeanPropertyRowMapper<Adminstrator>(Adminstrator.class);
        System.out.println(jdbcTemplate);
        Adminstrator adm;
        Student stu;
        try{
            adm= jdbcTemplate.queryForObject(sql, rowMapper,ac);
        }catch (EmptyResultDataAccessException e){
            adm=null;
        }
        System.out.println(adm);

        if(adm==null || !pwd.equals(adm.getPwd())){
            //管理员验证失败，尝试验证是否是学生账号

            key=Tools.KeyValue_lenght(64);
            isAdmin=false;
            //这里是验证策略
            if(pwd.equals(ac)){
                try {
                    System.out.println("尝试更新stu");
                    if(jdbcTemplate.update("update student set ckey='"+key+"' where id="+ac)<=0){
                        System.out.println("更新失败");
                        throw new Exception();
                    }
                }catch (Exception e){
                    result.put("code",301);
                    result.put("descript","information error");
                    pw.write(result.toJSONString());
                    System.out.println("验证失败 "+e.toString());
                    return;
                }

            }
            //验证失败
            else{
                result.put("code",301);
                result.put("descript","information error");
                pw.write(result.toJSONString());
                System.out.println("验证失败");
                return;
            }


        }
        result.put("code",0);
        result.put("descript","success");

        //随机生成key
        key="".equals(key)?(Tools.KeyValue_lenght(64)):key;
        //result.put("key",key);

        jdbcTemplate.update("update jadmin set cookiesk='"+key+"'");
        Cookie cookie1=new Cookie("key",key);
        cookie1.setMaxAge(60*60);
        Cookie cookie2=new Cookie("account",isAdmin?adm.getAccount():ac);
        cookie2.setMaxAge(60*60);
        System.out.println("是否是管理员"+isAdmin);
        Cookie cookie3=new Cookie("isAdmin",(isAdmin?"1":"0"));
        cookie2.setMaxAge(60*60);
        System.out.println("验证成功，写入cookies:"+key);
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.addCookie(cookie3);

        pw.write(result.toJSONString());


    }
}
