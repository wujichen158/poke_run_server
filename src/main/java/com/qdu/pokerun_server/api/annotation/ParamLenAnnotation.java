package com.qdu.pokerun_server.api.annotation;

import com.qdu.pokerun_server.api.exception.ApiException;
import com.qdu.pokerun_server.api.exception.ErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ParamLenAnnotation {
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamLen {
        public long min() default 0;
        public long max() default -1;
        public String message() default "key len don't match";
    }

    static public void Check(ParamLen a, String param, String arg) {
        if (arg.length() < a.min() || (a.max() != -1 && arg.length() > a.max())) {
            throw new ApiException(param, ErrorCode.KEY_LEN, a.message());
        }
    }
}
