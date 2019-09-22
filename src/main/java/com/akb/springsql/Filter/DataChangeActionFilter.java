package com.akb.springsql.Filter;

import com.akb.springsql.pojo.Adminstrator;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;


@WebFilter(urlPatterns = {"/admin/*"},filterName = "datachangeFliter")
public class DataChangeActionFilter implements Filter {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("init-----------filter");
    }
    @Override
    public void doFilter(ServletRequest reqest, ServletResponse response, FilterChain chain) throws ServletException,IOException{
        System.out.println(((HttpServletRequest)reqest).getRequestURL());
        System.out.println("doFilter!!!");

        //首先获取cookies
        Cookie[] cookies = ((HttpServletRequest) reqest).getCookies();//创建一个cookie集合并拿到cookie放入创建好的cookie集合里面
        //遍历cookie集合并判断是否有自己想要的指定cookie如果有则返回指定cookie的值，如果没有则返回空字符串
        String acc="";
        String adminkey_cookie="";
        for (Cookie cookie:cookies){
            if ("adminkey".equals(cookie.getName())){
                adminkey_cookie=cookie.getValue();
            }else if("adminaccout".equals(cookie.getName())){
                acc=cookie.getValue();
            }
        }

        String sql="select * from jadmin where account=?";
        RowMapper<Adminstrator> rowMapper=new BeanPropertyRowMapper<Adminstrator>(Adminstrator.class);
        System.out.println(jdbcTemplate);
        PrintWriter pw=response.getWriter();
        Adminstrator adm;
        try{
            adm= jdbcTemplate.queryForObject(sql, rowMapper,acc);
        }catch (EmptyResultDataAccessException e){
            adm=null;
        }

        //System.out.println("Cookies: "+acc+","+adminkey_cookie+"  ["+adm+"]");
        if(!(adm==null ||adm.getCookiesk()==null ||
                !adm.getCookiesk().equals(adminkey_cookie))){
            System.out.println("cookies验证成功");
            //检测cookies成功
            JSONObject jobj=new JSONObject();
            jobj.put("code",0);
            jobj.put("descript","cookies exist");
            pw.write(jobj.toJSONString());
            if(((HttpServletRequest) reqest).getRequestURL().toString().indexOf("change")<0){
                chain.doFilter(reqest,response);
            }

            return;
        }

        HttpSession session=((HttpServletRequest) reqest).getSession();
        String account=(String)session.getAttribute("ac");
        String passwd=(String)session.getAttribute("pwd");

        if(account== null || passwd==null || "".equals(account) || "".equals(passwd)){
            JSONObject jobj=new JSONObject();
            jobj.put("code",210);
            jobj.put("descript","not permission");
            pw.write(jobj.toJSONString());
        }

    }
}
