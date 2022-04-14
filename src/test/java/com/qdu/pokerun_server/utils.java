package com.qdu.pokerun_server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qdu.pokerun_server.entity.Player;
import com.qdu.pokerun_server.repository.PlayerRepository;
import org.apache.commons.codec.DecoderException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class utils {

    public static Map<String, Object> responseJson(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        var mapper = new ObjectMapper();
        return mapper.readValue(actions.andReturn().getResponse().getContentAsString(), Map.class);
    }

    public static List<Object> responseJList(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        var mapper = new ObjectMapper();
        return mapper.readValue(actions.andReturn().getResponse().getContentAsString(), List.class);
    }

    public static ResultMatcher except(Function<Exception, Boolean> f) {
        return (result) -> {
            if (result.getResolvedException() == null) {
                throw new RuntimeException("no exception");
            }
            result.getResolvedException().printStackTrace();
            assertTrue(f.apply(result.getResolvedException()));
        };
    }

    public static ResultActions request(
            MockHttpSession session,
            MockMvc mockMvc,
            MockHttpServletRequestBuilder r,
            Map<String, Object> params,
            boolean inBody
    ) throws Exception {
        r = r.contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8").session(session)
        ;

        if (inBody) {
            String content = new ObjectMapper().writeValueAsString(params);
            r = r.content(content);
        } else {
            for (var entry : params.entrySet()) {
                r = r.param((String) entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
            }
        }
        return mockMvc.perform(r).andDo(print());
    }

    public static BiFunction<Integer, Integer, Integer> genRandInt() {
        Random r = new Random();
        return (var min, var max) -> {
            return r.nextInt(max - min) + min;
        };
    }

    public static String randStr(String t, int min, int max) {
        StringBuilder sb = new StringBuilder();
        var rand = genRandInt();
        int len = rand.apply(min, max);
        for (int i = 0; i < len; ++i) {
            sb.append(t.charAt(rand.apply(0, t.length() - 1)));
        }
        return sb.toString();
    }

    static String alphabet = "abcdefghijklmnopqrstuvwxyz";
    public static String cc = "义已逝吾亦逝忆旧倚酒跋夷陵";
    public static String taggg = "戳啦极霸矛嘛";

    public static Player newPlayer(PlayerRepository userRepository, short permission) throws DecoderException {
        StringBuilder name = new StringBuilder();
        var email = new StringBuilder();

        var rand = genRandInt();
        int namelen = rand.apply(3, 20);
        for (int i = 0; i < namelen; ++i) {
            name.append(cc.charAt(rand.apply(0, cc.length() - 1)));
        }
        int elen = rand.apply(3, 10);
        for (int i = 0; i < elen; ++i) {
            email.append(alphabet.charAt(rand.apply(0, alphabet.length() - 1)));
        }
        email.append("@");
        elen = rand.apply(3, 10);
        for (int i = 0; i < elen; ++i) {
            email.append(alphabet.charAt(rand.apply(0, alphabet.length() - 1)));
        }

        Player u = new Player();
        u.setPlayerName(name.toString());
        u.setEmail(email.toString());
        u.setPwd("ffffff");
        u.setPermission(permission);
        return userRepository.save(u);
    }

}
