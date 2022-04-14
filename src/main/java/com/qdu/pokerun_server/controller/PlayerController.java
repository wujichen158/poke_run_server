package com.qdu.pokerun_server.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.qdu.pokerun_server.lib.LibMisc;
import com.qdu.pokerun_server.repository.PlayerRepository;
import com.qdu.pokerun_server.api.annotation.ParamHexAnnotation.ParamHex;
import com.qdu.pokerun_server.api.annotation.ParamLenAnnotation.ParamLen;
import com.qdu.pokerun_server.api.annotation.ParamNotExistAnnotation.ParamNotExist;
import com.qdu.pokerun_server.api.annotation.ParamRegexAnnotation.ParamRegex;
import com.qdu.pokerun_server.api.exception.ErrorCode;
import com.qdu.pokerun_server.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.qdu.pokerun_server.api.annotation.CheckListAnnotation.CheckList;

import javax.servlet.http.HttpServletResponse;

import static com.qdu.pokerun_server.lib.LibMisc.EMAIL_REGEX;
import static com.qdu.pokerun_server.utils.SpringUtil.genPageRequest;

@RestController
public class PlayerController {
    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private PlayerRepository playerRepository;

    static class unameConstraint {
        @ParamLen(min = 2, max = 30)
        public String playerName;
    }

    static class pwdConstraint {
//        @ParamHex
        @ParamLen(min = 6, max = 40)
        public String pwd;
    }

    static class emailConstraint {
        @ParamRegex(value = EMAIL_REGEX, errCode = ErrorCode.NOT_EMAIL)
        @ParamNotExist(MethodName = "findByEmail", BeanName = "userRepository")
        public String email;
    }

    @PostMapping(path = "/api/login/email", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(Player.WithoutPassword.class)
    @Transactional
    public Player loginEmail(
            @RequestParam String email,
            @RequestParam @CheckList(pwdConstraint.class) String pwd
    ) {
        Player u = playerRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"email not exist"));
//        byte[] pwd = ParamHexAnnotation.decodeHex(password);
        if (!u.getPwd().equals(pwd)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
//        try {
//            userRepository.updateLastLogin(u.getUuid(), new Date());
//        } catch (Exception e) {
//            log.error(String.valueOf(e));
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        session.setAttribute("user", u);
//        log.info(session.getId());
        return u;
    }

    @PostMapping(path = "/api/login/playerName", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(Player.WithoutPassword.class)
    @Transactional
    public Player loginPlayerName(
            @RequestParam String playerName,
            @RequestParam @CheckList(pwdConstraint.class) String pwd
    ) {
        Player u = playerRepository.findByPlayerName(playerName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND ,"player not exist"));
        if (!u.getPwd().equals(pwd)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return u;
    }

    /*
    public static class alterUserConstraint {
        @ParamOptional @CheckList(unameConstraint.class)
        public String name;
        @ParamOptional @CheckList(passwordConstraint.class)
        public String password;
        @ParamOptional @CheckList(emailConstraint.class)
        public String email;
        @ParamOptional @ParamLen(max = 200)
        public String avator;
    }


    @PatchMapping(path = "/api/user/{uid}", produces = LibMisc.PRODUCE_TYPE)
    @Transactional
    @Permission(Least = 254, Auth = UserCheck.class)
    @JsonView(User.WithoutPassword.class)
    public void alterUser(
            @RequestBody Map<String, Object> properties,
            @PathVariable @ParamOCheck long uid,
            HttpServletResponse response
    ) throws Exception {

        CheckListAnnotation.Check(new Class[]{alterUserConstraint.class}, properties, FilterPoilcy.FILTE_NOT_EXIST_NULL);
        Optional<User> newUser = userRepository.findById(uid);
        SpringUtil.CopyProperties(properties, newUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")));

        try {
            userRepository.save(newUser.get());
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/api/user/{uid}", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(User.WithoutPassword.class)
    @Permission(Least = 1)
    public User getUser(@PathVariable long uid) {
        return userRepository.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/api/user/favorite/{postid}")
    @Permission(Least = 1)
    @Transactional
    public void newUserFavorite(@PathVariable long postid, HttpSession session) {
        var user = Optional.ofNullable((User) session.getAttribute("user")).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userRepository.updateUserFavorite(user.getUuid(), postid);
    }

    @PersistenceContext
    private EntityManager entityManager;
    @GetMapping(path = "/api/user/{uid}/favorite", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    public List<Post> getUserFavorite(
            @PathVariable long uid,
            @RequestParam long pageIndex,
            @RequestParam @Contains({"30", "50", "100"}) String pageSize,
            @RequestParam @Contains({"asc", "desc"}) String order
    ) {
        var user = userRepository.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Long ps = Long.parseLong(pageSize);
        Long offest = ps * pageIndex;
        var entity = entityManager;
        var result = entity.createNativeQuery(
                "select post0_.* from post post0_ inner join user_favorite userfavori2_ on post0_.postid = userfavori2_.postid" +
                        " inner join user user1_ on user1_.uid = userfavori2_.uid" +
                        " where user1_.uid="+ uid +" order by post0_.create_time " + order +" limit " +
                        offest + "," + ps
                , Post.class).getResultList();
        entity.close();
        return result;
    }
    @DeleteMapping(path = "/api/user/{uid}/favorite/{postid}")
    @Transactional
    public void deleteUserFavorite(
            @PathVariable long uid,
            @PathVariable long postid,
            HttpServletResponse response
    ) {
        var user = userRepository.findById(uid).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userRepository.deleteUserFavorite(user.getUuid(), postid);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
    */

}
