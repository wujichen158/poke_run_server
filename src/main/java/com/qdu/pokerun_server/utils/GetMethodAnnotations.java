package com.qdu.pokerun_server.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.TreeMap;

public class GetMethodAnnotations {

    static public Map<String, Annotation[]> Do(Method m) {
        Parameter[] params = m.getParameters();
        Annotation[][] annotations = m.getParameterAnnotations();
        Map<String, Annotation[]> ret = new TreeMap<>();
        for (int i = 0; i < params.length; ++i) {
            ret.put(params[i].getName(), annotations[i]);
        }
        return ret;
    }
}
