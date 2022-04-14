package com.qdu.pokerun_server.api.exception;

public class HexException extends ApiException {
    public HexException(String key, String msg) {
        super(key, ErrorCode.KEY_NOT_HEX, msg);
    }
}
