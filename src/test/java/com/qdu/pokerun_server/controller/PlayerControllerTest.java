package com.qdu.pokerun_server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qdu.pokerun_server.PokerunServerApplication;
import com.qdu.pokerun_server.api.WebConfig;
import com.qdu.pokerun_server.entity.Player;
import com.qdu.pokerun_server.repository.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;


import static com.qdu.pokerun_server.utils.newPlayer;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(
        classes = {PokerunServerApplication.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
@EnableAutoConfiguration
public class PlayerControllerTest {

    @Autowired
    private PlayerController controller;

    private MockHttpSession session;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        session = new MockHttpSession();
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository userRepository;

    @Test
    @Transactional
    public void loginTest() throws Exception {

        Map<String, Object> params = new HashMap<>();
        Player user = newPlayer(userRepository, (short) 1);
        System.out.println(new ObjectMapper().writeValueAsString(user));
        params.put("email", "asdasfkjasjhfkasjhfk");
        params.put("pwd", user.getPwd());
        System.out.println(user.getPwd());
        request(post("/api/login/email"), params, false).andExpect(
                status().isNotFound()
        );

        params.remove("email");
        params.put("playerName", "senbai");
        request(post("/api/login/playerName"), params, false).andExpect(
                status().isNotFound()
        );

        params.put("email", user.getEmail());
        params.put("pwd", "ccffff");
        request(post("/api/login/email"), params, false).andExpect(
                status().isUnauthorized()
        );
        params.put("pwd", user.getPwd());
        params = responseJson(request(post("/api/login/email"), params, false).andExpect(
                status().isOk()
        ));
        assertEquals(((Integer) params.get("uuid")).longValue(), user.getUuid());
        System.out.println(params);

        params.clear();
        params.put("playerName", user.getPlayerName());
        params.put("pwd", user.getPwd());
        params = responseJson(request(post("/api/login/playerName"), params, false).andExpect(
                status().isOk()
        ));
        assertEquals(((Integer) params.get("uuid")).longValue(), user.getUuid());
        // 这里返回的中文用户名是乱码，然而我用python测试返回的是正常的中文。怀疑是测试框架的问题
        // 指定charset之后ok
        System.out.println(params);
    }

    Map<String, Object> responseJson(ResultActions actions) throws UnsupportedEncodingException, JsonProcessingException {
        var mapper = new ObjectMapper();
        return mapper.readValue(actions.andReturn().getResponse().getContentAsString(), Map.class);
    }

    public ResultActions request(
            MockHttpServletRequestBuilder r,
            Map<String, Object> params,
            boolean inBody
    ) throws Exception {
        r = r.contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8").session(session);
        if (inBody) {
            String content = new ObjectMapper().writeValueAsString(params);
            r = r.content(content);
        } else {
            for (var entry: params.entrySet()) {
                r = r.param(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
            }
        }
        return mockMvc.perform(r).andDo(print());
    }
}
