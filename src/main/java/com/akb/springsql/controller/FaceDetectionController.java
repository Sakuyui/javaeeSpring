package com.akb.springsql.controller;


import com.akb.springsql.pojo.FaceSearchResult;
import com.akb.springsql.pojo.Student;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FaceDetectionController{
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    public static List<FaceSearchResult> parseResult(String json){
        JSONObject jsonObject=JSONObject.parseObject(json);
        JSONArray results=jsonObject.getJSONArray("Results").getJSONObject(0).getJSONArray("Candidates");


        List<FaceSearchResult> faceSearchResults=new ArrayList<>();
        for(int i=0;i<results.size();i++){
            String score=(results.getJSONObject(i)).getDouble("Score").toString();
            String personid=(results.getJSONObject(i)).getInteger("PersonId").toString();
            System.out.println("["+personid+","+score+"]");
            faceSearchResults.add(new FaceSearchResult(personid,score));
        }
        return faceSearchResults;
    }

    @RequestMapping("faceresult")
    public String getfaceresult(HttpSession session, Model model, @RequestParam(value = "size", defaultValue = "5") int size,@RequestParam(value = "start", defaultValue = "1") int start) throws Exception{
        System.out.println("尝试获取结果数据...");
        System.out.println("session="+session.getId());
        String re=(String)session.getAttribute("searchresult");
        System.out.println(re);
        List<FaceSearchResult> results=parseResult(re);
        String sql = "select * from student where id=?";
        RowMapper<Student> rowMapper = new BeanPropertyRowMapper<Student>(Student.class);

        Map<String,Student> stumap=null;
        stumap =(HashMap)redisTemplate.opsForValue().get("stusMap");


        for(int i=0;i<results.size();i++){

            //没有缓存
            if(stumap==null){
                System.out.println("从数据库读取");
                Map<String,Student> studentHashMap=new HashMap<>();
                Student rs = jdbcTemplate.queryForObject(sql,rowMapper,results.get(i));

                results.get(i).setEntity(rs);
            }else{
                results.get(i).setEntity(stumap.get( results.get(i).getPersonId()));
            }

            System.out.println(results.get(i).getEntity());
        }

        PageInfo<FaceSearchResult> page=null;
        if(!(0==results.size())){
            Page p=new Page();
            p.setCount(true);
            int max=(int)Math.ceil(results.size()/(double)size);
            p.setPages(max);
            p.setPageSize(size);
            p.setPageNum(start>max?max:(start<1?1:start));
            p.setStartRow((p.getPageNum()-1)*size);
            p.setEndRow(p.getPageNum()*size);
            p.setTotal(results.size());
            p.setCountSignal(false);
            p.setOrderByOnly(false);
            //stuList = studentMapper.getFuzzyQueryStudentList(str);
            //System.out.println(stuList+"  "+stuList.size());
            page = new PageInfo<>(p);
            page.setList(results.subList((start-1)*size,start*size>=results.size()?results.size():start*size));
        }

        model.addAttribute("pages", page);
        model.addAttribute("sourceimg",session.getAttribute("sourceimg"));
        System.out.println(re);
        return "FaceSearchResult";
    }
}
