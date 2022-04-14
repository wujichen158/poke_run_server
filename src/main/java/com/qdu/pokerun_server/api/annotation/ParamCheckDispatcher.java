package com.qdu.pokerun_server.api.annotation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;

public class ParamCheckDispatcher {
    public static void Dispatch(Annotation a, String param, Object arg) throws Exception {
        try {
            if (a instanceof ParamLenAnnotation.ParamLen) {
                ParamLenAnnotation.Check((ParamLenAnnotation.ParamLen) a, param, (String) arg);
            } else if (a instanceof ParamHexAnnotation.ParamHex) {
                ParamHexAnnotation.Check((ParamHexAnnotation.ParamHex) a, param, (String) arg);
            } else if (a instanceof ParamRegexAnnotation.ParamRegex) {
                ParamRegexAnnotation.Check((ParamRegexAnnotation.ParamRegex) a, param, (String) arg);
            } else if (a instanceof ParamNotExistAnnotation.ParamNotExist) {
                ParamNotExistAnnotation.Check((ParamNotExistAnnotation.ParamNotExist) a, param, (String) arg);
            } else if (a instanceof CheckListAnnotation.CheckList) {
                CheckListAnnotation.Check(((CheckListAnnotation.CheckList) a).value(), param, arg);
            } else if (a instanceof Contains) {
                ContainsCheck.Check((Contains) a, param, (String) arg);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "param type error");
        }
    }
}
