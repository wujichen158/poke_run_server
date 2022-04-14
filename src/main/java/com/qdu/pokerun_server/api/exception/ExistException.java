package com.qdu.pokerun_server.api.exception;

public class ExistException extends ApiException {
    public ExistException(String key, String msg) {
        super(key, ErrorCode.KEY_IS_EXIST, msg);
    }
}
