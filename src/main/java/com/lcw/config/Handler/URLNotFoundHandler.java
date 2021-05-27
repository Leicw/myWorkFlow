package com.lcw.config.Handler;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @author ManGo
 */
@Configuration
public class URLNotFoundHandler implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {


    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
        factory.addErrorPages(error404Page);
    }
}
