package com.sujiu.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureWebMvc
public class UserControllerTest {

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    public void testUserRegister() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建注册请求
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser456");
        request.setEmail("test456@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // 发送POST请求
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    public void testUserRegisterWithInvalidData() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 测试密码不一致的情况
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser789");
        request.setEmail("test789@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password456");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        // 发送POST请求，期望返回错误
        mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(40000)); // PARAMS_ERROR
    }
}
