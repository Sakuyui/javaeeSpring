package com.akb.springsql;


import com.akb.springsql.pojo.FaceSearchResult;
import com.akb.springsql.tools.RSACoder;
import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import sun.misc.BASE64Decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


import java.util.*;

public class Test {
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



    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }





    public static void main(String[] args){
        Test t=new Test();
        try {



            Map<String,String> params=new HashMap<>();

            Date now=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date=formatter.format(now);
            String sign= RSACoder.sign(date,privateKey,"utf-8");

            params.put("sign",sign);
            params.put("time",date);
            params.put("key1","aaa");
            String key2= Base64.byteArrayToBase64(Tools.encrypt("18260829239".getBytes(),"akbsakuy"));
            System.out.println("18260829239".getBytes());
            params.put("key2",key2);
            params.put("phone","18260829239");
            params.put("tid","3");
            params.put("vcode","9239");



            String result=Tools.https("https://localhost:8081/dovote", params);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}


/*class AuthService {

    *//**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     *//*
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = "百度云应用的AK";
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = "百度云应用的SK";
        return getAuth(clientId, clientSecret);
    }

    *//**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     *//*
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            *//**
             * 返回结果示例
             *//*
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

}*/


/**
        * 人脸注册
        */
/*class FaceAdd {

    *//**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     *//*
    public static String add() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image",
                    Base64Util.encode(FileUtil.readFileByBytes("E:\\eresources\\faces\\EEG638pVAAEbUaQ.jpg"))
            );
            map.put("group_id", "group_1");
            map.put("user_id", "201792015");
            map.put("user_info", "abc");
            map.put("liveness_control", "NORMAL");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth("cnWam0xMCGiyORboZwcGvn7h","YVCeEXI4hVjmgeYw00FGwbCK3orH5L4t");

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        FaceAdd.add();
    }
}*/



/**
 * 人脸搜索
 */
class FaceSearch {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    /*public static String search() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image",
                    Base64Util.encode(FileUtil.
                            readFileByBytes("C:\\Users\\akb\\Desktop\\twitter pic\\QQ截图20190907180228.png")));
            map.put("liveness_control", "NORMAL");
            map.put("group_id_list", "group_repeat,group_233");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken =  AuthService.getAuth("cnWam0xMCGiyORboZwcGvn7h","YVCeEXI4hVjmgeYw00FGwbCK3orH5L4t");

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static void main(String[] args) {
        //FaceSearch.search();
    }
}