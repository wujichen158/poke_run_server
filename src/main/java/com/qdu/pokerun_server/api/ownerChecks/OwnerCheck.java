package com.qdu.pokerun_server.api.ownerChecks;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface OwnerCheck {
    public boolean Check(HttpSession session, Map<String, Object> params);
}
