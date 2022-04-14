package com.qdu.pokerun_server.api.ownerChecks;

import com.qdu.pokerun_server.entity.Player;
import com.qdu.pokerun_server.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

@Component
public class CommentCheck implements OwnerCheck {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public boolean Check(HttpSession session, Map<String, Object> params) {
        var u = Optional.ofNullable((Player) session.getAttribute("user"));
        if (u.isEmpty()) {
            System.out.println("no login");
            return false;
        }

        var uid = u.get().getUuid();
        var cid = Long.parseLong(Optional.of((String) params.get("cid")).get());
        var comment = commentRepository.findById(cid);
        if (!comment.isPresent()) {
            return false;
        }

        System.out.println("comment uid " + comment.get().getCreaterUid() + " uid " + uid);
        return comment.get().getCreaterUid().longValue() == uid;
    }
}
