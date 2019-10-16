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

@WebServlet(urlPatterns = "/admin/deletestu")
public class DeleteServlet extends HttpServlet {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Resource
    RedisTemplate redisTemplate;
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("DeleteServlet In");
        PrintWriter pw=response.getWriter();
        response.setContentType("application/json;charset=utf-8");
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

        jdbcTemplate.execute("delete from student where id='"+inputJson.get("nid")+"'");
        JSONObject resultJSON=new JSONObject();

        //更新缓存
        //更新缓存
        Map<String, Student> stumap=(HashMap)redisTemplate.opsForValue().get("stusMap");
        if(stumap!=null){
            try{
                stumap.remove(inputJson.get("nid"));
                redisTemplate.opsForValue().set("stusMap",stumap);
            }catch (Exception e){}


        }

        resultJSON.put("code",1);
        resultJSON.put("descript","delete finished");
        pw.write(resultJSON.toJSONString());

    }
}
