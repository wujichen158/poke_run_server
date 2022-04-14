package com.qdu.pokerun_server.api.annotation;

import com.qdu.pokerun_server.utils.SpringUtil;
import com.qdu.pokerun_server.api.exception.ExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class ParamNotExistAnnotation {
    private static final Logger log = LoggerFactory.getLogger(ParamNotExistAnnotation.class);
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ParamNotExist {
        public String MethodName();
        public String BeanName();
        public String message() default "key is exist";
    }

    static public void Check(ParamNotExist a, String param, String arg) {
        Method m = null;
        Object obj = null;
        try {
            System.out.println(a.MethodName());
            obj = SpringUtil.getBean(a.BeanName());
            Class<?> clazz = obj.getClass();
            System.out.println(clazz.getName());
            m = clazz.getDeclaredMethod(a.MethodName(), String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (m == null || obj == null) {
            log.error("annotation method " + a.MethodName() + " or beans " + a.BeanName() + " can't be found");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Optional<?> o = (Optional<?>)m.invoke(obj, arg);
            if (o.isPresent())
                throw new ExistException(param, a.message());
        } catch (IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
