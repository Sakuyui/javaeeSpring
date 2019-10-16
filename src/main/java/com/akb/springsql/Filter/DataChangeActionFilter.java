package com.akb.springsql.Filter;

import com.akb.springsql.pojo.Adminstrator;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import java.util.List;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebFilter(urlPatterns = {"/student/*"},filterName = "datachangeFliter")
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
        PrintWriter pw=response.getWriter();


        JSONObject inputJson=null;
        if(((HttpServletRequest) reqest).getRequestURL().indexOf("upimg")>=0){
            reqest.setCharacterEncoding("utf-8");
            DiskFileItemFactory factory = new DiskFileItemFactory();
            org.apache.commons.fileupload.servlet.ServletFileUpload upload = new ServletFileUpload(factory);
            String ctype;
            String targetacc;
            inputJson=new JSONObject();
            try {
                List<FileItem> items= upload.parseRequest((HttpServletRequest)reqest);
                ((HttpServletRequest) reqest).getSession().setAttribute("uploaddata",items);
                for(FileItem item: items) {
                    String fn=item.getFieldName();
                    System.out.println(fn);
                    if("id".equals(fn)){

                        targetacc=item.getString();
                        System.out.println("tacc="+targetacc);
                        inputJson.put("targetacc",targetacc);

                    }
                }
                inputJson.put("ctype","upload");


            } catch (Exception e) {

            }
        }else {
            //读取json数据部分
            inputJson = new JSONObject();
            BufferedReader br = new BufferedReader(new InputStreamReader(reqest.getInputStream()));
            String line;

            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            inputJson = JSONObject.parseObject(sb.toString());
        }

        //首先获取cookies
        Cookie[] cookies = ((HttpServletRequest) reqest).getCookies();//创建一个cookie集合并拿到cookie放入创建好的cookie集合里面
        //遍历cookie集合并判断是否有自己想要的指定cookie如果有则返回指定cookie的值，如果没有则返回空字符串
        String acc="";
        String key_cookie="";
        String isAdmin="";
        if(cookies!=null){
            for (Cookie cookie:cookies){
                if ("key".equals(cookie.getName())){
                    key_cookie=cookie.getValue();
                }else if("account".equals(cookie.getName())){
                    acc=cookie.getValue();
                }else if("isAdmin".equals(cookie.getName())){
                    isAdmin=cookie.getValue();
                }
            }
        }

        System.out.println("\nFilter获取到cookies: acc="+acc+",key="+key_cookie+",isAdmin:"+isAdmin);


        //判断权限


        if(!acc.equals(inputJson.get("targetacc"))){   //当前cookies 账号与目标账号不匹配
            System.out.println("权限不正确 targer="+inputJson.get("targetacc")+" curr="+acc);
            JSONObject jobj=new JSONObject();
            jobj.put("code",210);
            jobj.put("descript","student not have permission");
            pw.write(jobj.toJSONString());
            return;
        }
        if("0".equals(isAdmin)){
            System.out.println("检测到学生登入状态");
            String ctype=inputJson.getString("ctype");
            String url=((HttpServletRequest) reqest).getRequestURL().toString();
            if(!("upload".equals(ctype))){
                System.out.println("权限不正确");
                JSONObject jobj=new JSONObject();
                jobj.put("code",210);
                jobj.put("descript","student not have permission");
                pw.write(jobj.toJSONString());
                return;
            }

            String sql="select * from student where id=?";
            RowMapper<Student> rowMapper=new BeanPropertyRowMapper<Student>(Student.class);
            System.out.println(jdbcTemplate);

            Student stu;
            try{
                stu= jdbcTemplate.queryForObject(sql, rowMapper,acc);
            }catch (EmptyResultDataAccessException e){
                stu=null;
            }

            //System.out.println("Cookies: "+acc+","+adminkey_cookie+"  ["+adm+"]");
            if(!(stu==null ||stu.getCkey()==null ||
                    !stu.getCkey().equals(key_cookie))){
                System.out.println("cookies验证成功");
                //检测cookies成功
                JSONObject jobj=new JSONObject();

                if(((HttpServletRequest) reqest).getRequestURL().toString().indexOf("change")<0){
                    chain.doFilter(reqest,response);
                }else{
                    jobj.put("code",0);
                    jobj.put("descript","cookies exist");
                    pw.write(jobj.toJSONString());
                }

                return;
            }

        }


        //从管理员数据库中查key
        if("1".equals(isAdmin)){
            String sql="select * from jadmin where account=?";
            RowMapper<Adminstrator> rowMapper=new BeanPropertyRowMapper<Adminstrator>(Adminstrator.class);
            System.out.println(jdbcTemplate);

            Adminstrator adm;
            try{
                adm= jdbcTemplate.queryForObject(sql, rowMapper,acc);
            }catch (EmptyResultDataAccessException e){
                adm=null;
            }

            //System.out.println("Cookies: "+acc+","+adminkey_cookie+"  ["+adm+"]");
            if(!(adm==null ||adm.getCookiesk()==null ||
                    !adm.getCookiesk().equals(key_cookie))){
                System.out.println("cookies验证成功");
                //检测cookies成功
                JSONObject jobj=new JSONObject();

                if(((HttpServletRequest) reqest).getRequestURL().toString().indexOf("change")<0){
                    System.out.println("redrect:"+((HttpServletRequest) reqest).getRequestURL().toString());

                    chain.doFilter(reqest,response);
                }else{
                    jobj.put("code",0);
                    jobj.put("descript","cookies exist");
                    pw.write(jobj.toJSONString());
                }

                return;
            }
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
