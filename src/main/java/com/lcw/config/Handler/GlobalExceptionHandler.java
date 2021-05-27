package com.lcw.config.Handler;

import com.lcw.util.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ManGo
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public R<String> all(Exception e){
        e.printStackTrace();
        return R.failed(e.getMessage());
    }

}
