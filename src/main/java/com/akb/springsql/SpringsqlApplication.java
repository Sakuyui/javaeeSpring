package com.akb.springsql;

import com.akb.springsql.Servlet.AddServlet;
import com.akb.springsql.Servlet.DeleteServlet;
import com.akb.springsql.Servlet.LoginServlet;
import com.akb.springsql.Servlet.UpdateServlet;
import com.akb.springsql.mapper.StudentMapper;
import com.akb.springsql.pojo.Student;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@ServletComponentScan

public class SpringsqlApplication {



    public static void main(String[] args) {
        SpringApplication.run(SpringsqlApplication.class, args);

    }



}
