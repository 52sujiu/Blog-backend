package com.sujiu.blog.controller;

import com.sujiu.blog.model.dto.admin.CommentAuditRequest;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.dto.comment.AdminCommentQueryRequest;
import com.sujiu.blog.model.dto.comment.CommentRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.entity.Comment;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.article.ArticleVO;
import com.sujiu.blog.model.vo.comment.AdminCommentVO;
import com.sujiu.blog.model.vo.comment.CommentVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.ArticleService;
import com.sujiu.blog.service.CommentService;
import com.sujiu.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 管理员评论管理控制器测试
 *
 * @author sujiu
 */
@SpringBootTest
public class AdminCommentControllerTest {

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @Resource
    private ArticleService articleService;

    @Resource
    private AdminCommentController adminCommentController;

    private MockHttpServletRequest request;
    private Long adminUserId;
    private Long authorUserId;
    private Long testArticleId;
    private Long testCommentId;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        
        // 创建管理员用户
        int randomSuffix = (int)(Math.random() * 1000);
        UserRegisterRequest adminRegisterRequest = new UserRegisterRequest();
        adminRegisterRequest.setUsername("commentadmin" + randomSuffix);
        adminRegisterRequest.setEmail("commentadmin" + randomSuffix + "@test.com");
        adminRegisterRequest.setPassword("123456");
        adminRegisterRequest.setConfirmPassword("123456");
        
        adminUserId = userService.userRegister(adminRegisterRequest, request);
        
        // 设置管理员角色
        User adminUser = userService.getById(adminUserId);
        adminUser.setRole("admin");
        userService.updateById(adminUser);
        
        // 创建作者用户
        UserRegisterRequest authorRegisterRequest = new UserRegisterRequest();
        authorRegisterRequest.setUsername("commentauthor" + randomSuffix);
        authorRegisterRequest.setEmail("commentauthor" + randomSuffix + "@test.com");
        authorRegisterRequest.setPassword("123456");
        authorRegisterRequest.setConfirmPassword("123456");
        
        authorUserId = userService.userRegister(authorRegisterRequest, request);
        
        // 切换到作者身份创建文章
        User authorUser = userService.getById(authorUserId);
        request.getSession().setAttribute("user_login", authorUser);
        
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setTitle("测试文章标题");
        articleRequest.setSummary("这是一篇测试文章的摘要");
        articleRequest.setContent("# 测试文章\n\n这是测试文章的内容。");
        articleRequest.setStatus(2); // 已发布状态
        
        ArticleVO articleVO = articleService.publishArticle(articleRequest, request);
        testArticleId = articleVO.getId();
        
        // 创建测试评论
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("这是一条测试评论");
        commentRequest.setArticleId(testArticleId);
        
        CommentVO commentVO = commentService.createComment(commentRequest, request);
        testCommentId = commentVO.getId();

        // 将评论状态设置为待审核（0）以便测试审核功能
        Comment comment = commentService.getById(testCommentId);
        comment.setStatus(0);
        commentService.updateById(comment);

        // 切换回管理员身份
        request.getSession().setAttribute("user_login", adminUser);
    }

    @Test
    void testListComments() {
        // 测试获取评论列表
        AdminCommentQueryRequest queryRequest = new AdminCommentQueryRequest();
        queryRequest.setCurrent(1);
        queryRequest.setSize(10);
        
        PageVO<AdminCommentVO> result = commentService.listCommentsByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 1); // 至少有测试评论
    }

    @Test
    void testListCommentsWithKeyword() {
        // 测试关键词搜索
        AdminCommentQueryRequest queryRequest = new AdminCommentQueryRequest();
        queryRequest.setKeyword("测试评论");
        
        PageVO<AdminCommentVO> result = commentService.listCommentsByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getRecords().size() >= 1);
        
        // 验证搜索结果包含关键词
        boolean found = result.getRecords().stream()
            .anyMatch(comment -> comment.getContent().contains("测试评论"));
        assertTrue(found);
    }

    @Test
    void testListCommentsByArticle() {
        // 测试按文章ID筛选
        AdminCommentQueryRequest queryRequest = new AdminCommentQueryRequest();
        queryRequest.setArticleId(testArticleId);
        
        PageVO<AdminCommentVO> result = commentService.listCommentsByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        
        // 验证所有结果都属于指定文章
        boolean allBelongToArticle = result.getRecords().stream()
            .allMatch(comment -> comment.getArticle().getId().equals(testArticleId));
        assertTrue(allBelongToArticle);
    }

    @Test
    void testListCommentsByStatus() {
        // 测试按状态筛选（待审核）
        AdminCommentQueryRequest queryRequest = new AdminCommentQueryRequest();
        queryRequest.setStatus(0); // 待审核
        
        PageVO<AdminCommentVO> result = commentService.listCommentsByAdmin(queryRequest);
        
        assertNotNull(result);
        assertNotNull(result.getRecords());
        
        // 验证所有结果都是待审核状态
        boolean allPending = result.getRecords().stream()
            .allMatch(comment -> comment.getStatus() == 0);
        assertTrue(allPending);
    }

    @Test
    void testAuditCommentApprove() {
        // 测试审核通过评论
        CommentAuditRequest auditRequest = new CommentAuditRequest();
        auditRequest.setStatus(1); // 审核通过
        auditRequest.setAuditReason("内容合规，审核通过");
        
        Boolean result = commentService.auditComment(testCommentId, auditRequest, request);
        
        assertTrue(result);
        
        // 验证评论状态已更新
        Comment updatedComment = commentService.getById(testCommentId);
        assertEquals(1, updatedComment.getStatus());
        assertEquals("内容合规，审核通过", updatedComment.getAuditReason());
    }

    @Test
    void testAuditCommentReject() {
        // 测试审核拒绝评论
        CommentAuditRequest auditRequest = new CommentAuditRequest();
        auditRequest.setStatus(2); // 审核拒绝
        auditRequest.setAuditReason("内容违规");
        
        Boolean result = commentService.auditComment(testCommentId, auditRequest, request);
        
        assertTrue(result);
        
        // 验证评论状态已更新
        Comment updatedComment = commentService.getById(testCommentId);
        assertEquals(2, updatedComment.getStatus());
        assertEquals("内容违规", updatedComment.getAuditReason());
    }

    @Test
    void testDeleteComment() {
        // 测试删除评论
        Boolean result = commentService.deleteCommentByAdmin(testCommentId, request);
        
        assertTrue(result);
        
        // 验证评论已被逻辑删除
        Comment deletedComment = commentService.getById(testCommentId);
        assertNull(deletedComment); // 逻辑删除后查询不到
    }
}
