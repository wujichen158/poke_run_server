package com.qdu.pokerun_server.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotExistAdvice {
    @ResponseBody
    @ExceptionHandler(NotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    NotExistException Handler(NotExistException ex) { return ex; }
}
