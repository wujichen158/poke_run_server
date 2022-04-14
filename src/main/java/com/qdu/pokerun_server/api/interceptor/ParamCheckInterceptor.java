package com.qdu.pokerun_server.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.qdu.pokerun_server.api.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;


@Component
public class ParamCheckInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ParamCheckInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Parameter[] params = ((HandlerMethod) handler).getMethod().getParameters();
        Annotation[][] pa = ((HandlerMethod) handler).getMethod().getParameterAnnotations();
        boolean result = true;
        for (int i = 0; i < pa.length && result; ++i) {
            String param = params[i].getName();
            String arg = request.getParameter(param);
            if (arg == null) {
                continue;
            }
            log.info("param: " + arg + " name: " + param);
            for (Annotation a : pa[i]) {
                ParamCheckDispatcher.Dispatch(a, param, arg);
                log.info(String.valueOf(a.annotationType()));
            }
        }

        return result;
    }
}
