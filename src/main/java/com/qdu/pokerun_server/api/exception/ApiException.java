package com.qdu.pokerun_server.api.exception;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage"})
public class ApiException extends RuntimeException {
    private String key;
    private int errcode;
    private String message;


    public ApiException(String key, ErrorCode errcode, String msg) {
        super(msg);
        this.key = key;
        this.errcode = errcode.ordinal();
        this.message = msg;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String toJson() {
        return "{\"key\":\"" + key + "\",\"errcode\":" + errcode + ",\"msg\":\"" + message + "\"}";
    }
}
