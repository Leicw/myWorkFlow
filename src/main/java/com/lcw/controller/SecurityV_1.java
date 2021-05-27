package com.lcw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityV_1 {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/test")
    public String test(){
        return "=======";
    }

}
