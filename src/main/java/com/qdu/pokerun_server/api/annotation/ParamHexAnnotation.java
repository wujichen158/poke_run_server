package com.qdu.pokerun_server.api.annotation;

import com.qdu.pokerun_server.api.exception.HexException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ParamHexAnnotation {
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamHex {
        public String message() default "key is not binary hex";
    }

    static public boolean Check(ParamHex a, String param, String arg) {
        try {
            Hex.decodeHex(arg);
        } catch (DecoderException e) {
            throw new HexException(param, a.message());
        }
        return true;
    }

    static public byte[] decodeHex(String hex) {
        try {
            return Hex.decodeHex(hex);
        } catch (DecoderException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, hex);
        }
    }
}
