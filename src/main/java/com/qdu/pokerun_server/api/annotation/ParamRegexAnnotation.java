package com.qdu.pokerun_server.api.annotation;
import com.qdu.pokerun_server.api.exception.ApiException;
import com.qdu.pokerun_server.api.exception.ErrorCode;
import com.qdu.pokerun_server.api.exception.ExistException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;


public class ParamRegexAnnotation {
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamRegex {
        public String value();
        public String message() default "param is not match pattern";
        public ErrorCode errCode();
    }

    static public void Check(ParamRegex a, String param, String arg) throws ExistException {
        if (!Pattern.compile(a.value()).matcher(arg).matches()) {
            throw new ApiException(param, a.errCode(), a.message());
        }
    }
}




