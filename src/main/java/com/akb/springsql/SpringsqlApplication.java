package com.akb.springsql;

import com.akb.springsql.Servlet.AddServlet;
import com.akb.springsql.Servlet.DeleteServlet;
import com.akb.springsql.Servlet.LoginServlet;
import com.akb.springsql.Servlet.UpdateServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ServletComponentScan
public class SpringsqlApplication {



    public static void main(String[] args) {
        SpringApplication.run(SpringsqlApplication.class, args);
    }

}
