package com.akb.springsql.dao;

import com.akb.springsql.pojo.Adminstrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class AdminstratorDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Adminstrator get(String ac) {
        String sql = "select id,name,deptid from jadmin where account=?";
        RowMapper<Adminstrator> rowMapper = new BeanPropertyRowMapper<Adminstrator>(Adminstrator.class);
        return jdbcTemplate.queryForObject(sql, rowMapper, ac);
    }
}
