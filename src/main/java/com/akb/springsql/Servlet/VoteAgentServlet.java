package com.akb.springsql.Servlet;

import com.akb.springsql.Tools;
import com.akb.springsql.tools.RSACoder;
import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
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

@WebServlet(urlPatterns = "/dorealvote")
public class VoteAgentServlet extends HttpServlet {
    private static String privateKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAN15xjXj6OOFOerH" +
            "h1EW8XH+ru08am7lSRpQdzo+QPKg8IkyuqMXFWjYReBJ9z4Lc5XHbU2tH49h9lC/" +
            "iBniAEyNuzXmh98UURpKVH4bfvCQjyWlKArZGmBE93lMPz1zTCPoBVUfBE38MnLi" +
            "stzUmZb03oMX+QbzRqik15TzbliBAgMBAAECgYAjL7GZ5jedzhDBiCmrbGW3oqrP" +
            "7gVK0QqoL5iBnFpdMcyFP5X/Sy+PlKylUZsCNHeMmD55VMPq17l2YreQxSMeusFy" +
            "R9gY5cOGwwAoPrzzRmLoRs0O3QS7TCAnl8Ag+EqKT9AOwg2R97CY8a4cbCAK39+9" +
            "7pl3+CO7YlA1GzSasQJBAPZjI9yOiFP07mEkpWxDhQbE2ZB/U3mfBhsj+Fxl5xFN" +
            "Gp9GVhfeHpeXz4vjdV7BWsxQTyh3JJfY4KOswXxWau8CQQDmHdKry+JC9LJKYaEL" +
            "8eeSSvgl1k8SrpVyOU0C73KyW9AWCd/MuOuAeMps9jMuukNwL9e50vVMfL3sOqyh" +
            "1TOPAkAPgwJY+mg+0ObJGuOHQ2D2oiIIZNu+hnJ99u/F8WxwvGf2qxj0e7l1Vctt" +
            "RS64fnfW8R9qrsWRAchyxYeQ6mflAkAfclvj1kzpUX874vObKke3Gj+nDA5qQylx" +
            "HpuDly1ZamqZWGgZFfw45kjjcxGzhQjKP/9/CXE0LqfVrH8C7pvJAkAJwIUM7+S7" +
            "BTDSWqF7ERxebYQBtYwoL0KuDaer/XjmPfYALCVUK+WSiOwguFv9l2W0INGeTyyv" +
            "NsHZKmxMpqey";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Map<String,String> params=new HashMap<>();

        PrintWriter pw=response.getWriter();
        JSONObject resultObject=new JSONObject();

net.sf.json.JSONObject resultJSON=new net.sf.json.JSONObject();


        //读取数据部分
        com.alibaba.fastjson.JSONObject inputJson=null;
        BufferedReader br=new BufferedReader(new InputStreamReader(((HttpServletRequest)request).getInputStream()));
        String line;

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }

        inputJson= com.alibaba.fastjson.JSONObject.parseObject(sb.toString());

        //String ctype=inputJson.getString("ctype");
        String phone=inputJson.getString("phone");
        String vcode=inputJson.getString("vcode");
        String tid=inputJson.getString("tid");
        String key1=(String)request.getSession().getAttribute("key1");
        String key2=(String)request.getSession().getAttribute("key2");



        //使用日期进行数字签名
        Date now=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=formatter.format(now);
        String sign= RSACoder.sign(date,privateKey,"utf-8");



        params.put("sign",sign);
        params.put("time",date);
        params.put("key1",key1);
        params.put("key2",key2);
        params.put("phone",phone);
        params.put("tid",tid);
        params.put("vcode",vcode);

        try{
            String result=Tools.https("https://localhost:8481/dovote",params);
            pw.write(result);
        }catch (Exception e){
            e.printStackTrace();
            resultObject.put("code",-500);
            resultObject.put("status","Exception happened");
            pw.write(resultObject.toJSONString());
        }

    }
}
