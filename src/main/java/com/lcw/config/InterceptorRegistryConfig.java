package com.lcw.config;

import com.lcw.config.Filter.DevUserLoginFilter;
import com.lcw.util.GlobalConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author ManGo
 */
@Configuration
public class InterceptorRegistryConfig implements WebMvcConfigurer {
    @Resource
    private DevUserLoginFilter devUserLoginFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> strings = Arrays.asList("/login", "/static/**", "/resources/**");
        if(GlobalConfig.Test) {
            registry.addInterceptor(devUserLoginFilter).addPathPatterns("/**").excludePathPatterns(strings);

            WebMvcConfigurer.super.addInterceptors(registry);
        }
    }
}
