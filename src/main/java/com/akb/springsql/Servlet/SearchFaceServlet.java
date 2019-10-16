package com.akb.springsql.Servlet;

import com.akb.springsql.Base64Util;
import com.akb.springsql.FileUtil;
import com.akb.springsql.Tools;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = "/searchface")
public class SearchFaceServlet  extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String base64="";
        JSONObject jsonObject=new JSONObject();
        PrintWriter pw=response.getWriter();
        DiskFileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        try {
            List<FileItem> items= (List<FileItem>) upload.parseRequest(request);
            for(FileItem item: items) {
                if(!item.isFormField()) {
                    InputStream in=item.getInputStream();
                    int available=in.available();
                    byte[] data = new byte[available];
                    in.read(data);
                    in.close();


                    base64= Base64Util.encode(data);
                }

            }



            if("".equals(base64)) throw new Exception("图片上传错误");
            String result=Tools.searchMember(base64,1);
            pw.write(result);
            System.out.println("result="+result);
            System.out.println("sid="+request.getSession().getId());
            request.getSession().setAttribute("searchresult",result);
            request.getSession().setAttribute("sourceimg",base64);
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
    }
}
