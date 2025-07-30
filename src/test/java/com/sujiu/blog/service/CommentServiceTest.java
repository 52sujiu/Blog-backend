package com.sujiu.blog.service;

import com.sujiu.blog.model.dto.comment.CommentRequest;
import com.sujiu.blog.model.entity.Comment;
import com.sujiu.blog.model.vo.comment.CommentVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 评论服务测试
 *
 * @author sujiu
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceTest {

    @Resource
    private CommentService commentService;

    @Test
    public void testValidateCommentRequest() {
        // 测试正常情况
        CommentRequest validRequest = new CommentRequest();
        validRequest.setContent("这是一条测试评论");
        validRequest.setArticleId(1L);
        validRequest.setParentId(0L);

        assertDoesNotThrow(() -> commentService.validateCommentRequest(validRequest));

        // 测试内容为空
        CommentRequest emptyContentRequest = new CommentRequest();
        emptyContentRequest.setContent("");
        emptyContentRequest.setArticleId(1L);
        
        assertThrows(Exception.class, () -> commentService.validateCommentRequest(emptyContentRequest));

        // 测试内容过长
        CommentRequest longContentRequest = new CommentRequest();
        longContentRequest.setContent("a".repeat(1001));
        longContentRequest.setArticleId(1L);
        
        assertThrows(Exception.class, () -> commentService.validateCommentRequest(longContentRequest));

        // 测试文章ID为空
        CommentRequest nullArticleIdRequest = new CommentRequest();
        nullArticleIdRequest.setContent("测试评论");
        nullArticleIdRequest.setArticleId(null);
        
        assertThrows(Exception.class, () -> commentService.validateCommentRequest(nullArticleIdRequest));

        // 测试负数父评论ID
        CommentRequest negativeParentIdRequest = new CommentRequest();
        negativeParentIdRequest.setContent("测试评论");
        negativeParentIdRequest.setArticleId(1L);
        negativeParentIdRequest.setParentId(-1L);
        
        assertThrows(Exception.class, () -> commentService.validateCommentRequest(negativeParentIdRequest));
    }

    @Test
    public void testHasDeletePermission() {
        Comment comment = new Comment();
        comment.setAuthorId(1L);

        // 测试评论作者权限
        assertTrue(commentService.hasDeletePermission(comment, 1L, "user"));
        assertFalse(commentService.hasDeletePermission(comment, 2L, "user"));

        // 测试管理员权限
        assertTrue(commentService.hasDeletePermission(comment, 2L, "admin"));

        // 测试空参数
        assertFalse(commentService.hasDeletePermission(null, 1L, "user"));
        assertFalse(commentService.hasDeletePermission(comment, null, "user"));
    }

    @Test
    public void testIsCommentLiked() {
        // 测试空参数
        assertFalse(commentService.isCommentLiked(null, 1L));
        assertFalse(commentService.isCommentLiked(1L, null));

        // 测试不存在的点赞记录
        assertFalse(commentService.isCommentLiked(999999L, 999999L));
    }

    @Test
    public void testConvertToVO() {
        // 测试空对象
        assertNull(commentService.convertToVO(null, 1L));

        // 测试正常转换
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("测试评论");
        comment.setAuthorId(1L);
        comment.setParentId(0L);
        comment.setLikeCount(5);
        comment.setStatus(1);

        CommentVO vo = commentService.convertToVO(comment, 1L);
        assertNotNull(vo);
        assertEquals(comment.getId(), vo.getId());
        assertEquals(comment.getContent(), vo.getContent());
        assertEquals(comment.getParentId(), vo.getParentId());
        assertEquals(comment.getLikeCount(), vo.getLikeCount());
        assertEquals(comment.getStatus(), vo.getStatus());
        assertNotNull(vo.getIsLiked()); // 应该有值，即使是false
    }
}
