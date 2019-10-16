package com.akb.springsql.Servlet;

import com.akb.springsql.Tools;
import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


//进行短信验证
@WebServlet("/vote/verify")
public class VoteVerifyServlet extends HttpServlet {
    private Random random=new Random();
    @Resource
    RedisTemplate redisTemplate;
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //客户端发来的是手机号，还有带有key1的cookies.

        boolean hasKey1=false;
        JSONObject jsonObject=new JSONObject();
        JSONObject inputJSON;
        PrintWriter pw=response.getWriter();

        System.out.println("===========================================");
        System.out.println("In verify Servlet");
        System.out.println("Get key1=");
        //查看日期加密过的key1
        String key1=(String)request.getSession().getAttribute("key1");
        if(!(key1==null || "".equals(key1))){
            System.out.println(key1);
            byte[] ciphertext= Base64.base64ToByteArray(key1);
            try{
                System.out.print("decrypt: date=");
                String dstr= new String(Tools.decrypt(ciphertext,"akbsakuy"));
                SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d=f.parse(dstr);
                System.out.println(d);
                System.out.print("Check if key1 out of date...");
                if(((new Date()).getTime()-d.getTime())<180*1000){
                    System.out.println("pass");
                }else{throw new Exception("key1 out of date");}

            }catch (Exception e){
                jsonObject.put("code",-1);
                jsonObject.put("result","Key1 Verify failed");
                pw.write(jsonObject.toJSONString());
                System.out.println("false");
                return;
            }
        }else{
            jsonObject.put("code",-1);
            jsonObject.put("result","Key1 Verify failed");
            pw.write(jsonObject.toJSONString());
            return;
        }





        //查看请求类型
        //读取json数据部分
        BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }
        inputJSON=JSONObject.parseObject(sb.toString());


        String phoneNum=inputJSON.getString("phone");
        System.out.println("get phone="+phoneNum);

        String ctype=inputJSON.getString("ctype");

        if(ctype==null || "".equals(ctype) || phoneNum==null || "".equals(phoneNum)){
            jsonObject.put("code",-1);
            jsonObject.put("result","Infromation Error");
            pw.write(jsonObject.toJSONString());
            return;
        }

        if(ctype.equals("sendMessage")){
            //要求发送短信

            //防止发送过多，查询是否1分钟内已发送
            System.out.print("check if send frequently...");
            if(redisTemplate.opsForValue().get("sendMsg:"+phoneNum)!=null){
                jsonObject.put("code",-3);
                jsonObject.put("result","Frequent send msg");
                pw.write(jsonObject.toJSONString());
                System.out.println("false");
                return;
            }
            System.out.println("pass");

            //TODO
            int randomcode=random.nextInt(900000)+100000;
            System.out.println("Generated code="+randomcode);

            System.out.print("check if phone has voted....");
            //Test
            //检测手机号是否已投过票
            if(redisTemplate.opsForValue().get("voted:"+phoneNum)!=null){
                jsonObject.put("code",-2);
                jsonObject.put("result","Voted Phone");
                System.out.println("false");
                pw.write(jsonObject.toJSONString());
                return;
            }

            System.out.println("pass");




            System.out.println("set random code to cache,life=120s");
            redisTemplate.opsForValue().set("votekey:"+phoneNum,randomcode,120, TimeUnit.SECONDS);  //2分钟时间限制



            System.out.print("send msg...");
            //发送短信的代码




            System.out.println("sucess");

            System.out.println("write cache make this user can't send msg in 1 minute");
            //防止60秒内重新发送
            redisTemplate.opsForValue().set("sendMsg:"+phoneNum,"1",60,TimeUnit.SECONDS);

            //发送成功后的代码



            //写入key2
            System.out.print("Generate Key2=");
            byte[] ciphertext=Tools.encrypt(phoneNum.getBytes(),"akbsakuy");
            System.out.println(Base64.byteArrayToBase64(ciphertext));
            request.getSession().setAttribute("key2",Base64.byteArrayToBase64(ciphertext));
            System.out.println("Wrtire key2 to session and cache,set valid time=120s");

            redisTemplate.opsForValue().set("key2:"+phoneNum,"1",2,TimeUnit.MINUTES);


            //设置key2的生存时间

            //key2.setMaxAge(60*2);


            System.out.println("all success...");
            System.out.println("===========================================");

            jsonObject.put("code",0);
            jsonObject.put("result","success");
            pw.write(jsonObject.toJSONString());


        }


    }
}
