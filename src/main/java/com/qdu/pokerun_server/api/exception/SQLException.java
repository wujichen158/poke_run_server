package com.qdu.pokerun_server.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage", "key"})
public class SQLException extends ApiException {
    public SQLException(String msg) {
        super("", ErrorCode.SQL_RETRY, msg);
    }
}
