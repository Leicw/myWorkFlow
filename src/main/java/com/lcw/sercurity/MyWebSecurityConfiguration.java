package com.lcw.sercurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.annotation.Resource;

/**
 * @author ManGo
 */
//@Configuration
public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private SuccessHandler successHandler;
    @Resource
    private FailHandler failHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//实现请求登录
//                .loginPage("/login")//未登录，请求需要登录资源 重定向到的路径（前后端分离通常重定向到一个接口，接口返回json），如果不指定，则使用security默认的页面
                .loginProcessingUrl("/login")//登录的接口（即登录页面表单的action
                .successHandler(successHandler)//登录成功后调用的处理器
                .failureHandler(failHandler)//登录失败后调用的处理器
                .and()
                .authorizeRequests()//权限验证部分
                .antMatchers("/login").permitAll()//允许登录页面的请求，防止一直重定向
                .anyRequest().authenticated()//其余页面都需要登录
                .and()
                .logout().permitAll()
                .and()
                .csrf().disable()//禁用CSRF防护功能，使得ajax可以成功调用
                .headers().frameOptions().disable();

    }

}
