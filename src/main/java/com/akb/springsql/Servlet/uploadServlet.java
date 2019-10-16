package com.akb.springsql.Servlet;

import com.akb.springsql.Base64Util;
import com.akb.springsql.FileUtil;
import com.akb.springsql.Tools;
import com.akb.springsql.pojo.Adminstrator;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@WebServlet(urlPatterns = {"/student/upimg","/admin/upimg"})
public class uploadServlet extends HttpServlet {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Resource
    RedisTemplate redisTemplate;
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter pw=response.getWriter();
        request.setCharacterEncoding("utf-8");
        String filepath="E:\\eresources\\faces\\";
        JSONObject jsonObject=new JSONObject();
        //DiskFileItemFactory factory = new DiskFileItemFactory();
        //ServletFileUpload upload = new ServletFileUpload(factory);
        String id="";
        String imgname="";
        String base64="";
        try {
            List<FileItem> items= (List<FileItem>) request.getSession().getAttribute("uploaddata");
            for(FileItem item: items) {
                System.out.println(filepath+item.getName());
                if(!item.isFormField()) {

                    item.write(new File(filepath+item.getName()));
                    imgname=item.getName();
                    base64= Base64Util.encode(FileUtil.readFileByBytes(filepath+item.getName()));
                }
                if(item.isFormField()){
                    if("id".equals(item.getFieldName())){
                        id=item.getString("utf-8");
                    }
                }
            }




            if("".equals(id)) throw new Exception("ID未知错误");
            if("".equals(base64)) throw new Exception("图片上传错误");

            //读取学生信息
            String sql="select * from student where id=?";


            RowMapper<Student> rowMapper=new BeanPropertyRowMapper<Student>(Student.class);

            Student stu;
            try{
                stu= jdbcTemplate.queryForObject(sql, rowMapper,id);
            }catch (EmptyResultDataAccessException e){
                stu=null;
            }

            if(stu==null){
                throw new Exception("找不到该学生");
            }



            //上传到腾讯云
            try{
                Tools.addMember(stu.getName(),1,stu.getId(),"2",base64);
            }catch (Exception e){
                System.out.println(e.toString());
                jsonObject.put("code",501);
                jsonObject.put("descript","上传到云服务器出错");
                pw.write(jsonObject.toJSONString());
                return;
            }




            //写入数据库
            if(jdbcTemplate.update("update student set img=? where id=?",imgname,id)>0){
                System.out.println("更新成功");
                jsonObject.put("code","1");
                jsonObject.put("descript","success");
            }else{
                throw new Exception("更新错误(更新数据库失败)");
            }

        } catch (FileUploadException e) {
            jsonObject.put("code",500);
            jsonObject.put("descript",e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            jsonObject.put("code",500);
            jsonObject.put("descript",e.toString());
            e.printStackTrace();
        }

        pw.write(jsonObject.toJSONString());

    }
}
