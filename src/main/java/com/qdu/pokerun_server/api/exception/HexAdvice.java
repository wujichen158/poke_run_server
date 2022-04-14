package com.qdu.pokerun_server.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class HexAdvice {
    @ResponseBody
    @ExceptionHandler(HexException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    HexException Handler(HexException ex) { return ex; }
}
