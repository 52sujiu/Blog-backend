package com.sujiu.blog.service;

import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 关注服务测试
 *
 * @author sujiu
 */
@SpringBootTest
public class FollowServiceTest {

    @Resource
    private FollowService followService;

    @Resource
    private UserService userService;

    @Test
    public void testFollowUser() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 注册两个用户
        UserRegisterRequest registerRequest1 = new UserRegisterRequest();
        registerRequest1.setUsername("follower1");
        registerRequest1.setEmail("follower1@example.com");
        registerRequest1.setPassword("password123");
        registerRequest1.setConfirmPassword("password123");
        
        Long followerId = userService.userRegister(registerRequest1, httpRequest);

        UserRegisterRequest registerRequest2 = new UserRegisterRequest();
        registerRequest2.setUsername("following1");
        registerRequest2.setEmail("following1@example.com");
        registerRequest2.setPassword("password123");
        registerRequest2.setConfirmPassword("password123");
        
        Long followingId = userService.userRegister(registerRequest2, httpRequest);

        // 登录第一个用户
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("follower1");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 测试关注
        Boolean result = followService.followUser(followingId, httpRequest);
        assertTrue(result);

        // 验证关注关系
        Boolean isFollowing = followService.isFollowing(followerId, followingId);
        assertTrue(isFollowing);

        // 验证关注数和粉丝数
        Integer followingCount = followService.getFollowingCount(followerId);
        assertEquals(1, followingCount);

        Integer followerCount = followService.getFollowerCount(followingId);
        assertEquals(1, followerCount);
    }

    @Test
    public void testUnfollowUser() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 注册两个用户
        UserRegisterRequest registerRequest1 = new UserRegisterRequest();
        registerRequest1.setUsername("follower2");
        registerRequest1.setEmail("follower2@example.com");
        registerRequest1.setPassword("password123");
        registerRequest1.setConfirmPassword("password123");
        
        Long followerId = userService.userRegister(registerRequest1, httpRequest);

        UserRegisterRequest registerRequest2 = new UserRegisterRequest();
        registerRequest2.setUsername("following2");
        registerRequest2.setEmail("following2@example.com");
        registerRequest2.setPassword("password123");
        registerRequest2.setConfirmPassword("password123");
        
        Long followingId = userService.userRegister(registerRequest2, httpRequest);

        // 登录第一个用户
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("follower2");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 先关注
        followService.followUser(followingId, httpRequest);

        // 测试取消关注
        Boolean result = followService.unfollowUser(followingId, httpRequest);
        assertTrue(result);

        // 验证关注关系已取消
        Boolean isFollowing = followService.isFollowing(followerId, followingId);
        assertFalse(isFollowing);
    }

    @Test
    public void testGetFollowingList() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("follower3");
        registerRequest.setEmail("follower3@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        Long followerId = userService.userRegister(registerRequest, httpRequest);

        // 登录用户
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("follower3");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 获取关注列表
        PageVO<UserVO> result = followService.getFollowingList(followerId, 1, 10);
        assertNotNull(result);
        assertNotNull(result.getRecords());
    }

    @Test
    public void testFollowSelf() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("selffollow");
        registerRequest.setEmail("selffollow@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        Long userId = userService.userRegister(registerRequest, httpRequest);

        // 登录用户
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("selffollow");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 测试关注自己（应该失败）
        assertThrows(BusinessException.class, () -> {
            followService.followUser(userId, httpRequest);
        });
    }
}
