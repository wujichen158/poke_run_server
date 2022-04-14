package com.qdu.pokerun_server.api.ownerChecks;

import com.qdu.pokerun_server.entity.Player;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

@Component
public class UserCheck implements OwnerCheck {

    @Override
    public boolean Check(HttpSession session, Map<String, Object> params) {
        long targetUid = Long.parseLong((String) params.get("uid"));
        if (targetUid == 0) {
            return true;
        }

        var u = Optional.ofNullable((Player) session.getAttribute("user"));
        if (u.isEmpty()) {
            System.out.println("no login");
            return false;
        }
        System.out.println("targetId " + targetUid + " uid " + u.get().getUuid());
        return ((Player) u.get()).getUuid() == targetUid;
    }
}
