package com.akb.springsql.Servlet;

import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/admin/addstu")
public class AddServlet extends HttpServlet {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Resource
    RedisTemplate redisTemplate;
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

        response.setContentType("application/json;charset=utf-8");

        inputJson=JSONObject.parseObject(sb.toString());
        System.out.println(inputJson);

        JSONObject resultJSON=new JSONObject();
        if(jdbcTemplate.update("insert into student (id,name,phone,qq,email) values (?,?,?,?,?) "
                ,new Object[]{
                        inputJson.get("nid"),inputJson.get("nname"),inputJson.get("nphone"),
                        inputJson.get("nqq"), inputJson.get("nemail"),

        })>0){
            resultJSON.put("code",1);
            resultJSON.put("descript","success insert");
            //更新缓存
            Map<String, Student> stumap=(HashMap)redisTemplate.opsForValue().get("stusMap");
            if(stumap!=null){
                stumap.put((String)inputJson.get("nid"),new Student((String)inputJson.get("nid"),(String)inputJson.get("nname"),
                        (String)inputJson.get("nphone"),(String)inputJson.get("nqq"),(String)inputJson.get("nemail")));
                redisTemplate.opsForValue().set("stusMap",stumap);
            }

            pw.write(resultJSON.toJSONString());
        }else{
            resultJSON.put("code",701);
            resultJSON.put("descript","insert fail");
            pw.write(resultJSON.toJSONString());
        }

    }
}
