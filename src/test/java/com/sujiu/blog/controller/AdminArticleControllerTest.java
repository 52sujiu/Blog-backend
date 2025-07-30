package com.sujiu.blog.controller;

import com.sujiu.blog.model.dto.article.AdminArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleAuditRequest;
import com.sujiu.blog.model.dto.article.ArticleOfflineRequest;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.article.AdminArticleVO;
import com.sujiu.blog.model.vo.article.ArticleVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.ArticleService;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员文章管理控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
public class AdminArticleControllerTest {

    @Resource
    private ArticleService articleService;

    @Resource
    private UserService userService;

    @Resource
    private AdminArticleController adminArticleController;

    private MockHttpServletRequest request;
    private Long adminUserId;
    private Long authorUserId;
    private Long testArticleId;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        
        // 创建管理员用户
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
        
        // 创建作者用户
        UserRegisterRequest authorRegisterRequest = new UserRegisterRequest();
        authorRegisterRequest.setUsername("author" + randomSuffix);
        authorRegisterRequest.setEmail("author" + randomSuffix + "@test.com");
        authorRegisterRequest.setPassword("123456");
        authorRegisterRequest.setConfirmPassword("123456");
        
        authorUserId = userService.userRegister(authorRegisterRequest, request);
        
        // 创建测试文章
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("测试文章标题");
        articleRequest.setSummary("这是一篇测试文章的摘要");
        articleRequest.setContent("# 测试文章\n\n这是测试文章的内容。");
        articleRequest.setStatus(1); // 审核中状态
        
        // 切换到作者身份创建文章
        User authorUser = userService.getById(authorUserId);
        request.getSession().setAttribute("user_login", authorUser);
        
        ArticleVO articleVO = articleService.publishArticle(articleRequest, request);
        testArticleId = articleVO.getId();
        
        // 切换回管理员身份
        request.getSession().setAttribute("user_login", adminUser);
    }

    @Test
    void testListArticles() {
        // 测试获取文章列表
        AdminArticleQueryRequest queryRequest = new AdminArticleQueryRequest();
        queryRequest.setCurrent(1);
        queryRequest.setSize(10);
        
        PageVO<AdminArticleVO> result = articleService.listArticlesByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 1); // 至少有测试文章
    }

    @Test
    void testListArticlesWithKeyword() {
        // 测试关键词搜索
        AdminArticleQueryRequest queryRequest = new AdminArticleQueryRequest();
        queryRequest.setKeyword("测试文章");
        
        PageVO<AdminArticleVO> result = articleService.listArticlesByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getRecords().size() >= 1);
        
        // 验证搜索结果包含关键词
        boolean found = result.getRecords().stream()
            .anyMatch(article -> article.getTitle().contains("测试文章"));
        assertTrue(found);
    }

    @Test
    void testListArticlesByStatus() {
        // 测试按状态筛选
        AdminArticleQueryRequest queryRequest = new AdminArticleQueryRequest();
        queryRequest.setStatus(1); // 审核中
        
        PageVO<AdminArticleVO> result = articleService.listArticlesByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        
        // 验证所有结果都是审核中状态
        boolean allReviewing = result.getRecords().stream()
            .allMatch(article -> article.getStatus() == 1);
        assertTrue(allReviewing);
    }

    @Test
    void testAuditArticleApprove() {
        // 测试审核通过文章
        ArticleAuditRequest auditRequest = new ArticleAuditRequest();
        auditRequest.setStatus(2); // 审核通过
        auditRequest.setAuditReason("内容质量良好，审核通过");
        
        Boolean result = articleService.auditArticle(testArticleId, auditRequest, request);
        
        assertTrue(result);
        
        // 验证文章状态已更新
        Article updatedArticle = articleService.getById(testArticleId);
        assertEquals(2, updatedArticle.getStatus());
        assertEquals("内容质量良好，审核通过", updatedArticle.getAuditReason());
        assertNotNull(updatedArticle.getPublishedTime());
    }

    @Test
    void testAuditArticleReject() {
        // 测试审核拒绝文章
        ArticleAuditRequest auditRequest = new ArticleAuditRequest();
        auditRequest.setStatus(3); // 审核拒绝
        auditRequest.setAuditReason("内容不符合规范");
        
        Boolean result = articleService.auditArticle(testArticleId, auditRequest, request);
        
        assertTrue(result);
        
        // 验证文章状态已更新
        Article updatedArticle = articleService.getById(testArticleId);
        assertEquals(3, updatedArticle.getStatus());
        assertEquals("内容不符合规范", updatedArticle.getAuditReason());
    }

    @Test
    void testOfflineArticle() {
        // 先审核通过文章
        ArticleAuditRequest auditRequest = new ArticleAuditRequest();
        auditRequest.setStatus(2);
        auditRequest.setAuditReason("审核通过");
        articleService.auditArticle(testArticleId, auditRequest, request);
        
        // 测试下架文章
        ArticleOfflineRequest offlineRequest = new ArticleOfflineRequest();
        offlineRequest.setReason("违规内容，需要下架");
        
        Boolean result = articleService.offlineArticle(testArticleId, offlineRequest, request);
        
        assertTrue(result);
        
        // 验证文章状态已更新为下架
        Article updatedArticle = articleService.getById(testArticleId);
        assertEquals(4, updatedArticle.getStatus());
        assertEquals("违规内容，需要下架", updatedArticle.getAuditReason());
    }

    @Test
    void testDeleteArticle() {
        // 测试删除文章
        Boolean result = articleService.deleteArticleByAdmin(testArticleId, request);
        
        assertTrue(result);
        
        // 验证文章已被逻辑删除
        Article deletedArticle = articleService.getById(testArticleId);
        assertNull(deletedArticle); // 逻辑删除后查询不到
    }
}
