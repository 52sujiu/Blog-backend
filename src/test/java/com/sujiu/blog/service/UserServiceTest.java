package com.sujiu.blog.service;

import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.user.PasswordUpdateRequest;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.dto.user.UserUpdateRequest;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.user.LoginUserVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author sujiu
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testUserRegister() {
        // 测试正常注册
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser123");
        request.setEmail("test123@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        
        Long userId = userService.userRegister(request, httpRequest);
        assertNotNull(userId);
        assertTrue(userId > 0);

        // 验证用户是否保存成功
        User user = userService.getById(userId);
        assertNotNull(user);
        assertEquals("testuser123", user.getUsername());
        assertEquals("test123@example.com", user.getEmail());
        assertEquals("user", user.getRole());
        assertEquals(1, user.getStatus());
    }

    @Test
    public void testUserRegisterWithInvalidParams() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 测试空参数
        assertThrows(BusinessException.class, () -> {
            userService.userRegister(null, httpRequest);
        });

        // 测试用户名为空
        UserRegisterRequest request1 = new UserRegisterRequest();
        request1.setEmail("test@example.com");
        request1.setPassword("password123");
        request1.setConfirmPassword("password123");
        
        assertThrows(BusinessException.class, () -> {
            userService.userRegister(request1, httpRequest);
        });

        // 测试密码不一致
        UserRegisterRequest request2 = new UserRegisterRequest();
        request2.setUsername("testuser");
        request2.setEmail("test@example.com");
        request2.setPassword("password123");
        request2.setConfirmPassword("password456");
        
        assertThrows(BusinessException.class, () -> {
            userService.userRegister(request2, httpRequest);
        });

        // 测试用户名格式错误
        UserRegisterRequest request3 = new UserRegisterRequest();
        request3.setUsername("ab"); // 太短
        request3.setEmail("test@example.com");
        request3.setPassword("password123");
        request3.setConfirmPassword("password123");
        
        assertThrows(BusinessException.class, () -> {
            userService.userRegister(request3, httpRequest);
        });

        // 测试邮箱格式错误
        UserRegisterRequest request4 = new UserRegisterRequest();
        request4.setUsername("testuser");
        request4.setEmail("invalid-email");
        request4.setPassword("password123");
        request4.setConfirmPassword("password123");
        
        assertThrows(BusinessException.class, () -> {
            userService.userRegister(request4, httpRequest);
        });
    }

    @Test
    public void testUserLogin() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册一个用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("logintest");
        registerRequest.setEmail("logintest@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        Long userId = userService.userRegister(registerRequest, httpRequest);
        assertNotNull(userId);

        // 测试用户名登录
        UserLoginRequest loginRequest1 = new UserLoginRequest();
        loginRequest1.setAccount("logintest");
        loginRequest1.setPassword("password123");
        loginRequest1.setRememberMe(false);

        LoginUserVO loginUserVO1 = userService.userLogin(loginRequest1, httpRequest);
        assertNotNull(loginUserVO1);
        assertEquals("logintest", loginUserVO1.getUsername());
        assertEquals("logintest@example.com", loginUserVO1.getEmail());

        // 测试邮箱登录
        UserLoginRequest loginRequest2 = new UserLoginRequest();
        loginRequest2.setAccount("logintest@example.com");
        loginRequest2.setPassword("password123");
        loginRequest2.setRememberMe(true);

        LoginUserVO loginUserVO2 = userService.userLogin(loginRequest2, httpRequest);
        assertNotNull(loginUserVO2);
        assertEquals("logintest", loginUserVO2.getUsername());
    }

    @Test
    public void testUserLoginWithInvalidCredentials() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 测试用户不存在
        UserLoginRequest request1 = new UserLoginRequest();
        request1.setAccount("nonexistent");
        request1.setPassword("password123");

        assertThrows(BusinessException.class, () -> {
            userService.userLogin(request1, httpRequest);
        });

        // 先注册一个用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("logintest2");
        registerRequest.setEmail("logintest2@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        userService.userRegister(registerRequest, httpRequest);

        // 测试密码错误
        UserLoginRequest request2 = new UserLoginRequest();
        request2.setAccount("logintest2");
        request2.setPassword("wrongpassword");

        assertThrows(BusinessException.class, () -> {
            userService.userLogin(request2, httpRequest);
        });
    }

    @Test
    public void testUserLogout() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 测试未登录状态注销
        assertThrows(BusinessException.class, () -> {
            userService.userLogout(httpRequest);
        });

        // 先注册并登录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("logouttest");
        registerRequest.setEmail("logouttest@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("logouttest");
        loginRequest.setPassword("password123");

        userService.userLogin(loginRequest, httpRequest);

        // 测试注销
        Boolean result = userService.userLogout(httpRequest);
        assertTrue(result);
    }

    @Test
    public void testGetCurrentUser() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 测试未登录状态
        assertThrows(BusinessException.class, () -> {
            userService.getCurrentUser(httpRequest);
        });

        // 先注册并登录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("currenttest");
        registerRequest.setEmail("currenttest@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("currenttest");
        loginRequest.setPassword("password123");

        LoginUserVO loginUserVO = userService.userLogin(loginRequest, httpRequest);
        assertNotNull(loginUserVO);

        // 测试获取当前用户信息
        LoginUserVO currentUser = userService.getCurrentUser(httpRequest);
        assertNotNull(currentUser);
        assertEquals("currenttest", currentUser.getUsername());
        assertEquals("currenttest@example.com", currentUser.getEmail());
        assertEquals("user", currentUser.getRole());
        assertNotNull(currentUser.getId());
        assertNotNull(currentUser.getCreatedTime());
    }

    @Test
    public void testUpdateProfile() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("profiletest");
        registerRequest.setEmail("profiletest@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("profiletest");
        loginRequest.setPassword("password123");

        userService.userLogin(loginRequest, httpRequest);

        // 测试更新个人信息
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setNickname("新昵称");
        updateRequest.setPhone("13900139000");
        updateRequest.setGender(1);
        updateRequest.setBirthday("1990-01-01");
        updateRequest.setBio("这是更新后的个人简介");
        updateRequest.setWebsite("https://example.com");
        updateRequest.setLocation("北京");

        Boolean result = userService.updateProfile(updateRequest, httpRequest);
        assertTrue(result);

        // 验证更新结果
        LoginUserVO updatedUser = userService.getCurrentUser(httpRequest);
        assertEquals("新昵称", updatedUser.getNickname());
        assertEquals("13900139000", updatedUser.getPhone());
        assertEquals(Integer.valueOf(1), updatedUser.getGender());
        assertEquals("这是更新后的个人简介", updatedUser.getBio());
        assertEquals("https://example.com", updatedUser.getWebsite());
        assertEquals("北京", updatedUser.getLocation());
    }

    @Test
    public void testUpdatePassword() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("passwordtest");
        registerRequest.setEmail("passwordtest@example.com");
        registerRequest.setPassword("oldpassword123");
        registerRequest.setConfirmPassword("oldpassword123");

        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("passwordtest");
        loginRequest.setPassword("oldpassword123");

        userService.userLogin(loginRequest, httpRequest);

        // 测试修改密码
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest();
        passwordRequest.setOldPassword("oldpassword123");
        passwordRequest.setNewPassword("newpassword123");
        passwordRequest.setConfirmPassword("newpassword123");

        Boolean result = userService.updatePassword(passwordRequest, httpRequest);
        assertTrue(result);

        // 验证新密码可以登录
        userService.userLogout(httpRequest);

        UserLoginRequest newLoginRequest = new UserLoginRequest();
        newLoginRequest.setAccount("passwordtest");
        newLoginRequest.setPassword("newpassword123");

        LoginUserVO loginResult = userService.userLogin(newLoginRequest, httpRequest);
        assertNotNull(loginResult);
        assertEquals("passwordtest", loginResult.getUsername());
    }

    @Test
    public void testUpdatePasswordWithWrongOldPassword() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("passwordtest2");
        registerRequest.setEmail("passwordtest2@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");

        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("passwordtest2");
        loginRequest.setPassword("password123");

        userService.userLogin(loginRequest, httpRequest);

        // 测试使用错误的原密码
        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest();
        passwordRequest.setOldPassword("wrongpassword");
        passwordRequest.setNewPassword("newpassword123");
        passwordRequest.setConfirmPassword("newpassword123");

        assertThrows(BusinessException.class, () -> {
            userService.updatePassword(passwordRequest, httpRequest);
        });
    }
}
