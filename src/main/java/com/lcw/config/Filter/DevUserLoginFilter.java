package com.lcw.config.Filter;

import com.lcw.sercurity.SecurityUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ManGo
 */
@Component
public class DevUserLoginFilter implements HandlerInterceptor {
    @Resource
    private SecurityUtil securityUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        securityUtil.logInAs("l");
        return true;
    }
}


