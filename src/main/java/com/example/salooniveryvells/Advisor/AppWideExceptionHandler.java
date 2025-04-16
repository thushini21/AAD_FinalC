package com.example.salooniveryvells.Advisor;

import com.example.salooniveryvells.Util.ResponseUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@CrossOrigin
public class AppWideExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseUtil exceptionHandler(Exception ex) {
        return new ResponseUtil(500, ex.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseUtil resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return new ResponseUtil(404, ex.getMessage(), null);
    }
}
