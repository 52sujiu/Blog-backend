package com.sujiu.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 管理员仪表板控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminDashboardControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private MockHttpSession adminSession;

    @BeforeEach
    void setUp() {
        // 创建管理员用户
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole("admin");
        adminUser.setStatus(1);

        // 模拟管理员登录
        when(userService.getLoginUserPermitNull(any())).thenReturn(adminUser);

        // 创建管理员会话
        adminSession = new MockHttpSession();
        adminSession.setAttribute("user_login_state", adminUser);
    }

    @Test
    void testGetDashboardOverview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard/overview")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取系统概览成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userCount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.articleCount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentCount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.viewCount").exists());
    }

    @Test
    void testGetUserStatistics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/statistics/users")
                        .param("type", "daily")
                        .param("days", "30")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取用户统计成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.items").isArray());
    }

    @Test
    void testGetArticleStatistics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/statistics/articles")
                        .param("type", "daily")
                        .param("days", "30")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取文章统计成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.items").isArray());
    }

    @Test
    void testGetViewStatistics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/statistics/views")
                        .param("type", "daily")
                        .param("days", "30")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取访问统计成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.items").isArray());
    }

    @Test
    void testGetDashboardOverviewWithoutAdmin() throws Exception {
        // 测试非管理员访问
        User normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("user");
        normalUser.setRole("user");
        normalUser.setStatus(1);

        when(userService.getLoginUserPermitNull(any())).thenReturn(normalUser);

        MockHttpSession userSession = new MockHttpSession();
        userSession.setAttribute("user_login_state", normalUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/dashboard/overview")
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40101)); // NEED_ADMIN_ERROR
    }

    @Test
    void testGetUserStatisticsWithoutLogin() throws Exception {
        // 测试未登录访问
        when(userService.getLoginUserPermitNull(any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/statistics/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40100)); // NOT_LOGIN_ERROR
    }
}
