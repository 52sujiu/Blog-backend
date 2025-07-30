package com.sujiu.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sujiu.blog.model.dto.system.SystemConfigUpdateRequest;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.system.SystemConfigVO;
import com.sujiu.blog.service.SystemConfigService;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * 管理员系统配置控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminSystemConfigControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private SystemConfigService systemConfigService;

    @MockBean
    private UserService userService;

    @Resource
    private ObjectMapper objectMapper;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setNickname("管理员");
        adminUser.setRole("admin");
    }

    @Test
    void testGetSystemConfigByAdmin() throws Exception {
        // Mock管理员用户
        when(userService.getCurrentLoginUser(any())).thenReturn(adminUser);

        // Mock系统配置
        SystemConfigVO mockConfig = new SystemConfigVO();
        mockConfig.setSiteName("管理员博客系统");
        mockConfig.setSiteDescription("管理员描述");
        mockConfig.setCommentNeedAudit(false);

        when(systemConfigService.getSystemConfig()).thenReturn(mockConfig);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/system/config"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.siteName").value("管理员博客系统"));
    }

    @Test
    void testUpdateSystemConfig() throws Exception {
        // Mock管理员用户
        when(userService.getCurrentLoginUser(any())).thenReturn(adminUser);

        // 创建更新请求
        SystemConfigUpdateRequest updateRequest = new SystemConfigUpdateRequest();
        updateRequest.setSiteName("新的网站名称");
        updateRequest.setSiteDescription("新的网站描述");
        updateRequest.setCommentNeedAudit(false);

        // Mock更新后的配置
        SystemConfigVO updatedConfig = new SystemConfigVO();
        updatedConfig.setSiteName("新的网站名称");
        updatedConfig.setSiteDescription("新的网站描述");
        updatedConfig.setCommentNeedAudit(false);

        when(systemConfigService.updateSystemConfig(any(), any())).thenReturn(updatedConfig);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/system/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("系统配置更新成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.siteName").value("新的网站名称"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.siteDescription").value("新的网站描述"));
    }

    @Test
    void testRefreshConfigCache() throws Exception {
        // Mock管理员用户
        when(userService.getCurrentLoginUser(any())).thenReturn(adminUser);

        // Mock刷新缓存
        doNothing().when(systemConfigService).refreshConfigCache();

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/system/config/refresh"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("配置缓存刷新成功"));
    }

    @Test
    void testUpdateSystemConfigWithEmptyRequest() throws Exception {
        // Mock管理员用户
        when(userService.getCurrentLoginUser(any())).thenReturn(adminUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/system/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000));
    }
}
