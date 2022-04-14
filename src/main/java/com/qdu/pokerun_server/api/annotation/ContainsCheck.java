package com.qdu.pokerun_server.api.annotation;

import com.qdu.pokerun_server.api.exception.ApiException;
import com.qdu.pokerun_server.api.exception.ErrorCode;

import java.util.Arrays;

public class ContainsCheck {
    public static void Check(Contains a, String param, String arg) {
        for (var i: a.value()) {
            if (arg.equals(i)) {
                return;
            }
        }
        throw new ApiException(param, ErrorCode.KEY_ILLEGAL, "key is illegal, not at set " + Arrays.toString(a.value()));
    }
}
