package com.lcw.sercurity;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ManGo
 */
@Controller
public class LoginLimitController {

    @RequestMapping("/login")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String login(){

        return "未登录";
    }

    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFoundPage(){

        return "页面未找到";
    }

}
