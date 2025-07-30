package com.sujiu.blog.controller;

import com.sujiu.blog.model.vo.system.SystemConfigVO;
import com.sujiu.blog.service.SystemConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static org.mockito.Mockito.when;

/**
 * 系统配置控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SystemConfigControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private SystemConfigService systemConfigService;

    @Test
    void testGetSystemConfig() throws Exception {
        // Mock系统配置
        SystemConfigVO mockConfig = new SystemConfigVO();
        mockConfig.setSiteName("测试博客系统");
        mockConfig.setSiteDescription("测试描述");
        mockConfig.setSiteKeywords("测试,博客");
        mockConfig.setCommentNeedAudit(true);
        mockConfig.setArticleNeedAudit(false);
        mockConfig.setUploadMaxSize(10485760L);
        mockConfig.setUserDefaultAvatar("/static/images/default-avatar.png");

        when(systemConfigService.getSystemConfig()).thenReturn(mockConfig);

        mockMvc.perform(MockMvcRequestBuilders.get("/system/config"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("获取系统配置成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.siteName").value("测试博客系统"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.siteDescription").value("测试描述"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.commentNeedAudit").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.articleNeedAudit").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.uploadMaxSize").value(10485760));
    }
}
