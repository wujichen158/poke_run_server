package com.qdu.pokerun_server.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qdu.pokerun_server.api.annotation.ParamOCheck;
import com.qdu.pokerun_server.api.annotation.Permission;
import com.qdu.pokerun_server.api.ownerChecks.CommentCheck;
import com.qdu.pokerun_server.api.ownerChecks.OwnerCheck;
import com.qdu.pokerun_server.api.ownerChecks.UserCheck;
import com.qdu.pokerun_server.utils.GetMethodAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.*;

import com.qdu.pokerun_server.entity.Player;

@Component
public class PermissionCheckInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PermissionCheckInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        var method = ((HandlerMethod)handler).getMethod();
        log.info("PermissionCheckInterceptor.preHandle() " + method);
        if (method.isAnnotationPresent(Permission.class)) {
            Permission permission = (Permission) method.getAnnotation(Permission.class);
            // 这个if判断条件想了半天...
            boolean result;
            if (permission.Auth() == Permission.class) {
                result = permissionCheck(permission, request, response);
                log.info("no auth");
            } else {
                result = checkOwner(handler, permission, request, response) || permissionCheck(permission, request, response);
            }
            if (!result) {
                log.info("forbidden");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        log.info("ok");
        return true;
    }

    @Autowired
    private UserCheck userCheck;
    @Autowired
    private CommentCheck commentCheck;


    boolean checkOwner(Object handler, Permission p, HttpServletRequest req, HttpServletResponse resp) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        OwnerCheck ownerChecks[] = {
                userCheck,
                commentCheck,
        };
        var clazz = p.Auth();
        Optional<OwnerCheck> oCheck = Optional.ofNullable(null);
        for (var c: ownerChecks) {
            if (clazz.isInstance(c)) {
                oCheck = Optional.of(c);
                break;
            }
        }

        Map<String, Annotation[]> annotations = GetMethodAnnotations.Do(((HandlerMethod) handler).getMethod());

        Map<String, Object> params = new HashMap<>();
        Map pVarMap = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        for (Map.Entry<String, Annotation[]> entry: annotations.entrySet()) {
            boolean isAuthParam = false;
            boolean isPathVar = false;
            for (Annotation a: entry.getValue()) {
                if (a instanceof ParamOCheck) {
                    isAuthParam = true;
                } else if (a instanceof PathVariable) {
                    isPathVar = true;
                }
            }
            if (!isAuthParam) {
                continue;
            }
            Object val = null;
            if (isPathVar) {
                val = pVarMap.get(entry.getKey());
            } else {
                val = req.getParameter(entry.getKey());
            }
            params.put(entry.getKey(), Objects.requireNonNull(val));
        }

        return oCheck.get().Check(req.getSession(), params);
    }

    boolean permissionCheck(Permission p, HttpServletRequest req, HttpServletResponse resp) throws NullPointerException {
        var user = Optional.ofNullable(req.getSession().getAttribute("user"));
        short uPermission = 0;
        if (user.isPresent()) {
            uPermission = ((Player) user.get()).getPermission();
        }
        log.info("permission " + uPermission + " least " + p.Least());
        return uPermission >= p.Least();
    }
}
