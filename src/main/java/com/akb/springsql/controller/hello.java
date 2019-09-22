package com.akb.springsql.controller;

import com.akb.springsql.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class hello {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @RequestMapping(value="/hello", method= RequestMethod.GET)
    public String index() {

        String sql="select * from student";

        RowMapper<Student> rowMapper=new BeanPropertyRowMapper<Student>(Student.class);
        List<Student> stus= jdbcTemplate.query(sql, rowMapper);
        for (Student stu : stus) {
            System.out.println(stu);
        }
        return "Hello ";
    }
}
