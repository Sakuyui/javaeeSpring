package com.akb.springsql.Servlet;


import com.akb.springsql.Tools;
import com.akb.springsql.tools.RSACoder;
import net.sf.json.JSONObject;
import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/dovote/vote")
public class VoteServlet extends HttpServlet {
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //客户端提供的数据 ip,key1(日期加密过的key),key2(手机号加密过的key)
        //ip合法性检验 filter已经解决
        //对时间进行数字签名
        PrintWriter pw=response.getWriter();
        Date now=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=formatter.format(now);
        String sign= RSACoder.sign(date,privateKey,"utf-8");
        JSONObject resultJSON=new JSONObject();


        //读取数据部分
        com.alibaba.fastjson.JSONObject inputJson=null;
        BufferedReader br=new BufferedReader(new InputStreamReader(((HttpServletRequest)request).getInputStream()));
        String line;

        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }

        inputJson= com.alibaba.fastjson.JSONObject.parseObject(sb.toString());
        System.out.println(inputJson);
        String key1="";
        String key2="";
        String phone="";


        Cookie[] cookies=request.getCookies();
        for (Cookie cookie:cookies){
            if(cookie.getName().equals("key1")) key1=cookie.getValue();
            if(cookie.getName().equals("key2")) key2=cookie.getValue();
        }

        phone=inputJson.getString("phone");

        if(key1==null || key2==null || phone==null || "".equals(key1) || "".equals(key2) || "".equals(phone)){
            resultJSON.put("code",-1);
            resultJSON.put("result","Exception happened");
            return;
        }


        //请求参数
        Map<String,String> params=new HashMap<>();
        params.put("sign",sign);   //数字签名
        params.put("time",date);   //时间戳
        params.put("key1",key1);   //key1
        params.put("key2",key2);   //key2
        params.put("phone",phone);   //phone

        String result="";
        //发送请求
        try {
            result=Tools.https("https://localhost:9239/dovote", params);
        }catch (Exception e){
            resultJSON.put("code",-1);
            resultJSON.put("result","Exception happened");
            e.printStackTrace();
            return;
        }
        com.alibaba.fastjson.JSONObject respondJSON=com.alibaba.fastjson.JSONObject.parseObject(result);



        //Cipher.getInstance(.r)

    }


}
