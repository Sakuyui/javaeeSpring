package com.akb.springsql.controller;

import com.akb.springsql.mapper.StudentMapper;
import com.akb.springsql.pojo.Student;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class StudentController {
    @Resource
    private StudentMapper studentMapper;

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
        //System.out.println(str);
        Integer curp=start;
        model.addAttribute("currpage",curp);
        model.addAttribute("searchstr",str);
        model.addAttribute("pzsize",size);
        PageHelper.startPage(start, size);
        List<Student> stuList = studentMapper.getFuzzyQueryStudentList(str);
        PageInfo<Student> page = new PageInfo<>(stuList);
        model.addAttribute("pages", page);
        model.addAttribute("str", str);

        return "search";
    }
}
