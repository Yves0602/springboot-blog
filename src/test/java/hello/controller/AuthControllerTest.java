package hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.service.AuthService;
import hello.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp(){
        AuthService authService = new AuthService(userService);
        mvc = MockMvcBuilders
                .standaloneSetup(new AuthController(userService,authenticationManager,authService)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(mvcResult ->
                        Assertions.assertTrue(mvcResult.getResponse().getContentAsString().contains("用户没有登录")));
    }
    @Test
    void testLogin() throws Exception {

        mvc.perform(get("/auth")).andExpect(status().isOk())
                .andExpect(mvcResult ->
                        Assertions.assertTrue(mvcResult.getResponse().getContentAsString().contains("用户没有登录")));


        Map<String,String> usernamePassword = new HashMap<>();
        usernamePassword.put("username","myUser");
        usernamePassword.put("password","myPassword");


        Mockito.when(userService.loadUserByUsername("myUser"))
                .thenReturn(new User("myUser",bCryptPasswordEncoder.encode("myPassword"), Collections.emptyList()));
        Mockito.when(userService.getUserByUsername("myUser"))
                .thenReturn(new hello.Entity.User(123,"myUser",bCryptPasswordEncoder.encode("myPassword")));


        MvcResult response = mvc
                .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                .andExpect(status().isOk())
                .andExpect(result ->{
                        System.out.println("这是啥"+result.getResponse().getContentAsString());
                    Assertions.assertTrue(result.getResponse().getContentAsString().contains("登录成功"));})
                .andReturn();
        HttpSession session  = response.getRequest().getSession();
        mvc.perform(get("/auth").session((MockHttpSession) session)).andExpect(status().isOk())
                .andExpect(mvcResult ->
                        Assertions.assertTrue(mvcResult.getResponse().getContentAsString().contains("myUser")));
    }
}