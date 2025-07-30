package com.sujiu.blog.controller;

import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 文件上传控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class FileUploadControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("测试用户");
        testUser.setRole("user");
    }

    @Test
    void testUploadImageSuccess() throws Exception {
        // Mock登录用户
        when(userService.getCurrentLoginUser(any())).thenReturn(testUser);

        // 创建模拟图片文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/image")
                        .file(file)
                        .param("type", "avatar"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("图片上传成功"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.originalName").value("test.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.type").value("image/jpeg"));
    }

    @Test
    void testUploadImageWithoutLogin() throws Exception {
        // Mock未登录状态
        when(userService.getCurrentLoginUser(any())).thenThrow(new BusinessException(ErrorCode.NOT_LOGIN_ERROR));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/image")
                        .file(file)
                        .param("type", "avatar"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40100));
    }

    @Test
    void testUploadImageInvalidType() throws Exception {
        when(userService.getCurrentLoginUser(any())).thenReturn(testUser);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/image")
                        .file(file)
                        .param("type", "invalid"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000));
    }

    @Test
    void testUploadFileSuccess() throws Exception {
        when(userService.getCurrentLoginUser(any())).thenReturn(testUser);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test pdf content".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/file")
                        .file(file)
                        .param("type", "document"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("文件上传成功"));
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        when(userService.getCurrentLoginUser(any())).thenReturn(testUser);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0] // 空文件
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload/image")
                        .file(file)
                        .param("type", "avatar"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(40000));
    }
}
