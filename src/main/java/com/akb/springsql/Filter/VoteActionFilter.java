package com.akb.springsql.Filter;

import com.akb.springsql.Tools;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@WebFilter(urlPatterns = "/dovote/*",filterName = "VoteActioNFilter")
public class VoteActionFilter  implements Filter {
    @Resource
    RedisTemplate redisTemplate;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //ip访问频率控制，并且进行ip识别

        //读取json数据部分
        JSONObject inputJson=null;
        BufferedReader br=new BufferedReader(new InputStreamReader(((HttpServletRequest)request).getInputStream()));
        String line;

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }
        inputJson= JSONObject.parseObject(sb.toString());
        System.out.println(inputJson);

        JSONObject result=new JSONObject();
        String ip="";
        //首先对IP进行匹配
        try {
            ip= Tools.https("https://pv.sohu.com/cityjson?ie=utf-8", new HashMap<>());
            if("".equals(ip)) throw new Exception();
            JSONObject ipresult=JSONObject.parseObject( ip.substring(ip.indexOf('{'),ip.length()-1));
            ip=ipresult.getString("cip");
        }catch (Exception e){
            result.put("code","-1");
            result.put("result","unknow error");
            e.printStackTrace();
            PrintWriter pw=((HttpServletResponse)response).getWriter();
            pw.write(result.toJSONString());
            return;
        }


        System.out.println("in filter: input ip="+inputJson.getString("ip")+" real ip="+ip);

        //post带的ip和访问ip不同. 可能该请求由程序伪造。拒绝访问
        if(!(ip.equals(inputJson.getString("ip")))){

            result.put("code","-1");
            result.put("result","Access Dined");
            PrintWriter pw=((HttpServletResponse)response).getWriter();
            pw.write(result.toJSONString());
            return;
        }
        System.out.println("check ip.... pass");
        System.out.print("check multiaccess.... ");
        //查看是否ip已在一个时间段内访问访问
        // redisTemplate.delete(ip);
        if(redisTemplate.opsForValue().get(ip)!=null){
            result.put("code","-2");
            result.put("result","Access Dined");
            PrintWriter pw=((HttpServletResponse)response).getWriter();
            pw.write(result.toJSONString());
            System.out.println("false");
            return;
        }else{
            System.out.println("true");
            //同ip 5秒内只能进行一次操作
            System.out.println("Set cache");
            redisTemplate.opsForValue().set(ip,1,5, TimeUnit.SECONDS);
        }

        System.out.print("check vote record...");
        //查看cookies是否存在投票记录
        Cookie[] cookies=((HttpServletRequest) request).getCookies();
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("voteRecord")){
                result.put("code","-2");
                result.put("result","Access Dined");
                System.out.println("false");
                PrintWriter pw=((HttpServletResponse)response).getWriter();
                pw.write(result.toJSONString());
                return;
            }
        }
        System.out.println("true\r\n pass to servlet");
        //放行
        chain.doFilter(request,response);


    }

}

