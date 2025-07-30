package com.sujiu.blog.controller;

import com.sujiu.blog.model.dto.user.UserQueryRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.dto.user.UserRoleUpdateRequest;
import com.sujiu.blog.model.dto.user.UserStatusUpdateRequest;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.user.AdminUserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员用户管理控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
public class AdminUserControllerTest {

    @Resource
    private UserService userService;

    @Resource
    private AdminUserController adminUserController;

    private MockHttpServletRequest request;
    private Long adminUserId;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();

        // 创建管理员用户（使用随机用户名避免冲突）
        int randomSuffix = (int)(Math.random() * 1000);
        UserRegisterRequest adminRegisterRequest = new UserRegisterRequest();
        adminRegisterRequest.setUsername("admin" + randomSuffix);
        adminRegisterRequest.setEmail("admin" + randomSuffix + "@test.com");
        adminRegisterRequest.setPassword("123456");
        adminRegisterRequest.setConfirmPassword("123456");

        adminUserId = userService.userRegister(adminRegisterRequest, request);

        // 设置管理员角色
        User adminUser = userService.getById(adminUserId);
        adminUser.setRole("admin");
        userService.updateById(adminUser);

        // 模拟管理员登录
        request.getSession().setAttribute("user_login", adminUser);

        // 创建测试用户
        UserRegisterRequest testRegisterRequest = new UserRegisterRequest();
        testRegisterRequest.setUsername("testuser" + randomSuffix);
        testRegisterRequest.setEmail("test" + randomSuffix + "@test.com");
        testRegisterRequest.setPassword("123456");
        testRegisterRequest.setConfirmPassword("123456");

        testUserId = userService.userRegister(testRegisterRequest, request);
    }

    @Test
    void testListUsers() {
        // 测试获取用户列表
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setCurrent(1);
        queryRequest.setSize(10);
        
        PageVO<AdminUserVO> result = userService.listUsersByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 2); // 至少有管理员和测试用户
    }

    @Test
    void testListUsersWithKeyword() {
        // 测试关键词搜索
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setKeyword("testuser");

        PageVO<AdminUserVO> result = userService.listUsersByAdmin(queryRequest);

        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getRecords().size() >= 1);

        // 验证搜索结果包含关键词
        boolean found = result.getRecords().stream()
            .anyMatch(user -> user.getUsername().contains("testuser"));
        assertTrue(found);
    }

    @Test
    void testUpdateUserStatus() {
        // 测试更新用户状态
        UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest();
        statusRequest.setStatus(0); // 禁用
        statusRequest.setReason("测试禁用");
        
        Boolean result = userService.updateUserStatus(testUserId, statusRequest, request);
        
        assertTrue(result);
        
        // 验证状态已更新
        User updatedUser = userService.getById(testUserId);
        assertEquals(0, updatedUser.getStatus());
    }

    @Test
    void testUpdateUserRole() {
        // 测试更新用户角色
        UserRoleUpdateRequest roleRequest = new UserRoleUpdateRequest();
        roleRequest.setRole("ban");
        
        Boolean result = userService.updateUserRole(testUserId, roleRequest, request);
        
        assertTrue(result);
        
        // 验证角色已更新
        User updatedUser = userService.getById(testUserId);
        assertEquals("ban", updatedUser.getRole());
    }

    @Test
    void testDeleteUser() {
        // 测试删除用户
        Boolean result = userService.deleteUser(testUserId, request);
        
        assertTrue(result);
        
        // 验证用户已被逻辑删除
        User deletedUser = userService.getById(testUserId);
        assertNull(deletedUser); // 逻辑删除后查询不到
    }
}
