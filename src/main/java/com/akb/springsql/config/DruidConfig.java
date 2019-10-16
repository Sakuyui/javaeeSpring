package com.akb.springsql.config;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * druid 性能检测
 *
 * @author Administrator
 * @date 2017/12/21 0021
 */
@Configuration
public abstract class DruidConfig  {

    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }


    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        servletRegistrationBean.addUrlMappings("/druid/*");



        HashMap<String, String> initParameters = new HashMap<>();
        // 用户名
        initParameters.put("loginUsername", "druid");
        // 密码
        initParameters.put("loginPassword", "druid");
        /*禁止html页面上的reset all  功能*/
        initParameters.put("resetEnable", "false");
        //ip 白名单,没有配置或者为空，则允许所有的访问
        initParameters.put("allow", "127.0.0.1");
        //ip黑名单,当两种共存的时候deny优先于allow
        /*initParameters.put("deny","125.23.23.1");*/
        servletRegistrationBean.setInitParameters(initParameters);

        return servletRegistrationBean;
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();

        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

    @Bean(value = "druid-interceptor")
    public DruidStatInterceptor DruidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }


    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setProxyTargetClass(true);
        //设置要监控的bean的id
        /*beanNameAutoProxyCreator.setBeanNames("sysRoleMapper","loginController");*/
        beanNameAutoProxyCreator.setInterceptorNames("druid-interceptor");
        return beanNameAutoProxyCreator;
    }
}
