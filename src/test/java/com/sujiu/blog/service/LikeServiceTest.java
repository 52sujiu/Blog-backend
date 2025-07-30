package com.sujiu.blog.service;

import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.vo.article.ArticleVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 点赞服务测试
 *
 * @author sujiu
 */
@SpringBootTest
public class LikeServiceTest {

    @Resource
    private LikeService likeService;

    @Resource
    private ArticleService articleService;

    @Resource
    private UserService userService;

    @Test
    public void testArticleLike() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("likeuser");
        registerRequest.setEmail("likeuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("likeuser");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 发布一篇文章
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("点赞测试文章");
        articleRequest.setContent("这是一篇用于测试点赞功能的文章");
        articleRequest.setStatus(2);

        ArticleVO article = articleService.publishArticle(articleRequest, httpRequest);
        assertNotNull(article);

        Long articleId = article.getId();

        // 测试点赞
        Boolean likeResult = likeService.likeArticle(articleId, httpRequest);
        assertTrue(likeResult);

        // 检查点赞状态
        Boolean likeStatus = likeService.checkArticleLikeStatus(articleId, httpRequest);
        assertTrue(likeStatus);

        // 获取点赞数
        Long likeCount = likeService.getArticleLikeCount(articleId);
        assertEquals(Long.valueOf(1), likeCount);

        // 测试重复点赞（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.likeArticle(articleId, httpRequest);
        });

        // 测试取消点赞
        Boolean unlikeResult = likeService.unlikeArticle(articleId, httpRequest);
        assertTrue(unlikeResult);

        // 检查点赞状态（应该为false）
        Boolean likeStatusAfterUnlike = likeService.checkArticleLikeStatus(articleId, httpRequest);
        assertFalse(likeStatusAfterUnlike);

        // 获取点赞数（应该为0）
        Long likeCountAfterUnlike = likeService.getArticleLikeCount(articleId);
        assertEquals(Long.valueOf(0), likeCountAfterUnlike);

        // 测试重复取消点赞（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.unlikeArticle(articleId, httpRequest);
        });
    }

    @Test
    public void testLikeWithoutLogin() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 测试未登录时点赞（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.likeArticle(1L, httpRequest);
        });

        // 测试未登录时取消点赞（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.unlikeArticle(1L, httpRequest);
        });

        // 测试未登录时检查点赞状态（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.checkArticleLikeStatus(1L, httpRequest);
        });
    }

    @Test
    public void testLikeNonExistentArticle() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("likeuser2");
        registerRequest.setEmail("likeuser2@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("likeuser2");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 测试点赞不存在的文章（应该失败）
        assertThrows(BusinessException.class, () -> {
            likeService.likeArticle(999999L, httpRequest);
        });
    }

    @Test
    public void testGenericLikeMethods() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // 先注册并登录用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("genericlikeuser");
        registerRequest.setEmail("genericlikeuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        
        userService.userRegister(registerRequest, httpRequest);

        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setAccount("genericlikeuser");
        loginRequest.setPassword("password123");
        
        userService.userLogin(loginRequest, httpRequest);

        // 发布一篇文章
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("通用点赞测试文章");
        articleRequest.setContent("这是一篇用于测试通用点赞功能的文章");
        articleRequest.setStatus(2);

        ArticleVO article = articleService.publishArticle(articleRequest, httpRequest);
        assertNotNull(article);

        Long articleId = article.getId();

        // 测试通用点赞方法
        Boolean likeResult = likeService.like(articleId, 1, httpRequest); // 1表示文章
        assertTrue(likeResult);

        // 检查点赞状态
        Boolean likeStatus = likeService.checkLikeStatus(articleId, 1, httpRequest);
        assertTrue(likeStatus);

        // 测试通用取消点赞方法
        Boolean unlikeResult = likeService.unlike(articleId, 1, httpRequest);
        assertTrue(unlikeResult);

        // 检查点赞状态（应该为false）
        Boolean likeStatusAfterUnlike = likeService.checkLikeStatus(articleId, 1, httpRequest);
        assertFalse(likeStatusAfterUnlike);
    }
}
