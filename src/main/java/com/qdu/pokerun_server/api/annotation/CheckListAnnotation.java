package com.qdu.pokerun_server.api.annotation;

import com.qdu.pokerun_server.api.exception.ApiException;
import com.qdu.pokerun_server.api.exception.ErrorCode;
import com.qdu.pokerun_server.api.exception.NotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import java.lang.annotation.Annotation;

import static com.qdu.pokerun_server.api.annotation.ParamCheckDispatcher.Dispatch;


public class CheckListAnnotation {
    private static final Logger log = LoggerFactory.getLogger(CheckListAnnotation.class);


    @Retention(RetentionPolicy.RUNTIME)
    public @interface CheckList {
        Class<?>[] value();
    }

    public enum FilterPoilcy {
      NONE,
      FILTE_NOT_EXIST,
      FILTE_NULL,
      FILTE_NOT_EXIST_NULL
    };

    static void dispatch(Annotation[] annotations, String name, Object arg, Class<?> clazz, FilterPoilcy policy) throws Exception {
        if (arg == null) {
            if (Arrays.stream(annotations).anyMatch(a -> a instanceof Nullable))
                return;
            throw new NotExistException(name, "key is null");
        }

        if (!clazz.isInstance(arg)) {
            log.info("name " + name + " " + clazz.getName());
            throw new ApiException(name, ErrorCode.TYPE_ERR, "type err");
        }

        Optional<Annotation> list = Optional.ofNullable(null);
        if (arg instanceof Map) {
            list = Arrays.stream(annotations).filter(a -> a instanceof CheckList).findFirst();
        }
        if (list.isPresent()) {
            annotations = Arrays.stream(annotations).filter(a -> !(a instanceof CheckList))
                    .toArray(Annotation[]::new);
        }
        System.out.println(name + " " + arg.getClass().getName());
        for (var annotation : annotations) {
            log.info(name + " " + annotation.toString());
            Dispatch(annotation, name, arg);
        }

        if (list.isPresent()) {
            Check(((CheckList) list.get()).value(), (Map<String, Object>) arg, policy);
        }
    }

    interface checkable {
        public boolean contains(String key);
        public Object get(String key);
    }

    static class checkMap implements checkable {
        private Map<String, Object> params;

        public checkMap(Map<String, Object> params) {
            this.params = params;
        }


        @Override
        public boolean contains(String key) {
            return params.containsKey(key);
        }

        @Override
        public Object get(String key) {
            return params.get(key);
        }
    }

    static class checkEntry implements checkable {
        private Object obj;
        private String name;

        public checkEntry(Object obj, String name) {
            this.obj = obj;
            this.name = name;
        }

        @Override
        public boolean contains(String key) {
            return key.equals(name);
        }

        @Override
        public Object get(String key) {
            return obj;
        }
    }


    static class withPolicy {
        private Object object;
        private FilterPoilcy poilcy;

        public withPolicy(Object object, FilterPoilcy poilcy) {
            this.object = object;
            this.poilcy = poilcy;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public FilterPoilcy getPoilcy() {
            return poilcy;
        }

        public void setPoilcy(FilterPoilcy poilcy) {
            this.poilcy = poilcy;
        }
    }

    static Set<String> process(Class<?> checkList, checkable checkItem, FilterPoilcy poilcy) throws Exception {
        System.out.println("all " + checkList.getFields().length);
        HashSet<String> existField = new HashSet<>();
        for (Field f : checkList.getFields()) {
            var annotations = f.getAnnotations();
            if (!checkItem.contains(f.getName())) {
                if (Arrays.stream(annotations).anyMatch((var __) -> __ instanceof ParamOptional)) {
                    continue;
                }
                throw new NotExistException(f.getName(), f.getName() + " is not exist in params");
            }

            if (poilcy == FilterPoilcy.FILTE_NOT_EXIST || poilcy == FilterPoilcy.FILTE_NOT_EXIST_NULL) {
                existField.add(f.getName());
            }

            var key = f.getName();
            var value = checkItem.get(key);

            if (value instanceof List) {
                var list = (List<Object>) value;
                for (var arg: list) {
                    dispatch(annotations, key, arg, (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0], poilcy);
                }
            } else {
                dispatch(annotations, key, value, f.getType(), poilcy);
            }
        }
        if (poilcy == FilterPoilcy.FILTE_NOT_EXIST || poilcy == FilterPoilcy.FILTE_NOT_EXIST_NULL) {
            return existField;
        } else {
            return null;
        }
    }

    static public void Check(Class<?>[] checkList, String paramName, Object arg) throws Exception {
        for (var list: checkList)
            process(list, new checkEntry(arg, paramName), FilterPoilcy.NONE);
    }

    static public void Check(Class<?>[] checkList, Map<String, Object> map, FilterPoilcy policy) throws Exception {
        for (var list : checkList) {
            Set<String> existParams = process(list, new checkMap(map), policy);
            paramFilter(map, existParams, policy);
        }
    }

    static public void Check(Class<?>[] checkList, Map<String, Object> map) throws Exception {
        for (var list : checkList) {
            Set<String> existParams = process(list, new checkMap(map), FilterPoilcy.NONE);
        }
    }

    static private void paramFilter(Map<String, ?> map, Set<String> exist, FilterPoilcy poilcy) {
        if (poilcy == FilterPoilcy.FILTE_NOT_EXIST || poilcy == FilterPoilcy.FILTE_NOT_EXIST_NULL) {
            map.entrySet().removeIf((var entry) -> !exist.contains(entry.getKey()));
        }
        if (poilcy == FilterPoilcy.FILTE_NULL || poilcy == FilterPoilcy.FILTE_NOT_EXIST_NULL) {
            map.entrySet().removeIf((var entry) -> entry.getValue() == null);
        }
    }
}
