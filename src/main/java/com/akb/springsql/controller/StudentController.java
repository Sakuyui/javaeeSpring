package com.akb.springsql.controller;

import com.akb.springsql.mapper.StudentMapper;
import com.akb.springsql.pojo.Student;
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;

@Controller
public class StudentController {
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    //添加页面
    @RequestMapping("add")
    public String add() {
        return "add";
    }

    //查找(用于查询)
    @RequestMapping("getStudent")
    public String getStudent(String id, Model model) throws Exception {
        Student student = studentMapper.getStudent(id);
        model.addAttribute("Student", student);
        return "studentShow";
    }

    //添加
    @RequestMapping("addStudent")
    public String listStudent(Student student, BindingResult bindingResult) throws Exception {
        boolean flag = studentMapper.add(student) > 0;
        return "redirect:listStudent";
    }

    //删除
    @RequestMapping("deleteStudent")
    public String deleteStudent(Student stu) throws Exception {
        studentMapper.del(stu.getId());
        return "redirect:listStudent";
    }

    //修改
    @RequestMapping("updateStudent")
    public String updateStudent(Student Users) throws Exception {
        boolean flag = studentMapper.update(Users) > 0;
        return "redirect:listUser";
    }

    //查找(用于修改)
    @RequestMapping("findStudent")
    public String findStudent(String id, Model model) throws Exception {
        Student stu = studentMapper.getStudent(id);
        model.addAttribute("Student", stu);
        return "modify";
    }

   /* //遍历
    @RequestMapping({"/","listStudent"})
    public String listStudent(@RequestParam(value = "name",defaultValue = "") String name,
                           Model model, @RequestParam(value = "start", defaultValue = "1") int start,
                           @RequestParam(value = "size", defaultValue = "2") int size) throws Exception {
        PageHelper.startPage(start, size);
        List<Student> stuList = studentMapper.getAllStudentList(name);
        PageInfo<Student> page = new PageInfo<>(stuList);
        model.addAttribute("pages", page);
        model.addAttribute("name", name);
        return "index";
    }*/


    //遍历
    @RequestMapping("searchstu")
    public String searchStudent(@RequestParam(value = "str",defaultValue = "") String str,
                           Model model, @RequestParam(value = "start", defaultValue = "1") int start,
                           @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {

        Integer curp=start;
        model.addAttribute("currpage",curp);
        model.addAttribute("searchstr",str);
        model.addAttribute("pzsize",size);
        PageHelper.startPage(start, size);
        PageHelper.orderBy("id asc");

        List<Student> stuList=null;
        Map<String,Student> stumap=null;
        final ObjectMapper mapper = new ObjectMapper();



        PageInfo<Student> page=null;




        stumap =(HashMap)redisTemplate.opsForValue().get("stusMap");
        if(stumap==null){
            System.out.println("从数据库读取");
            Map<String,Student> studentHashMap=new HashMap<>();
            List<Map<String, Object>> rs = jdbcTemplate.queryForList("select * from student");


            for(Map<String,Object> m:rs){
                Student s=new Student();
                for(String k:m.keySet()){
                    switch (k){
                        case "id":{
                            s.setId((String)m.get(k));
                            break;
                        }
                        case "name":{
                            s.setName((String)m.get(k));
                            break;
                        }
                        case "phone":{
                            s.setPhone((String)m.get(k));
                            break;
                        }
                        case "qq":{
                            s.setQq((String)m.get(k));
                            break;
                        }
                        case "email":{
                            s.setEmail((String)m.get(k));
                            break;
                        }
                        case "img":{
                            s.setImg((String)m.get(k));
                        }
                    }
                }
                System.out.println("缓存"+s);
                studentHashMap.put(s.getId(),s);
            }
            //stuList = studentMapper.getAllStudent();



            redisTemplate.opsForValue().set("stuslist",new ArrayList<>());

            redisTemplate.opsForValue().set("stusMap",studentHashMap);
            stuList = studentMapper.getFuzzyQueryStudentList(str);
            System.out.println(stuList+"  "+stuList.size());
            page = new PageInfo<>(stuList);
        }else{
            System.out.println("从缓存读取");
            redisTemplate.delete("stusMap");
            stuList=new ArrayList<>();
            for(String key:stumap.keySet()){
                //System.out.println(key);
                Student cur=stumap.get(key);
                if(cur.isMatchee_V(str)){
                    stuList.add(cur);
                }
            }

            Collections.sort(stuList);

            //设置page
            Page p=new Page();
            p.setCount(true);
            int max=(int)Math.ceil(stuList.size()/(double)size);
            p.setPages(max);
            p.setPageSize(size);
            p.setPageNum(start>max?max:(start<1?1:start));
            p.setStartRow((p.getPageNum()-1)*size);
            p.setEndRow(p.getPageNum()*size);
            p.setTotal(stuList.size());
            p.setCountSignal(false);
            p.setOrderByOnly(false);
            //stuList = studentMapper.getFuzzyQueryStudentList(str);
            //System.out.println(stuList+"  "+stuList.size());
            page = new PageInfo<>(p);
            page.setList(stuList.subList((start-1)*size,start*size>=stuList.size()?stuList.size():start*size));
        }




        model.addAttribute("pages", page);
        model.addAttribute("str", str);

        return "search";
    }



}
