package com.lcw.sercurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcw.util.R;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    private ObjectMapper objectMapper;

//    表单登录处理器
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println("================================");
    }

//    ajax/url请求登录处理
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().println(authentication.getName() + ": 登录成功");
        response.getWriter().println(
                objectMapper.writeValueAsString(R.success("登录成功：===" + authentication.getName()))
        );

    }
}

