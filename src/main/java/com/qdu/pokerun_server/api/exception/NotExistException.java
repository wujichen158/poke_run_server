package com.qdu.pokerun_server.api.exception;

public class NotExistException extends ApiException {
    public NotExistException(String key, String msg) {
        super(key, ErrorCode.KEY_NOT_EXIST, msg);
    }
}
