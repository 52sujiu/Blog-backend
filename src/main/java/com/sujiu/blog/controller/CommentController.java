package com.sujiu.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sujiu.blog.annotation.RequireLogin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.comment.CommentRequest;
import com.sujiu.blog.model.vo.comment.CommentVO;
import com.sujiu.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 评论管理控制器
 *
 * @author sujiu
 */
@RestController
@Slf4j
@Tag(name = "评论管理", description = "评论管理相关接口")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 发表评论
     *
     * @param articleId 文章ID
     * @param commentRequest 评论创建请求
     * @param request HTTP请求对象
     * @return 评论详情
     */
    @Operation(summary = "发表评论", description = "在指定文章下发表评论，需要登录")
    @RequireLogin
    @PostMapping("/articles/{articleId}/comments")
    public BaseResponse<CommentVO> createComment(@Parameter(description = "文章ID") @PathVariable Long articleId,
                                                 @RequestBody CommentRequest commentRequest,
                                                 HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (commentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 设置文章ID
        commentRequest.setArticleId(articleId);

        CommentVO commentVO = commentService.createComment(commentRequest, request);
        return ResultUtils.success(commentVO, "评论发表成功");
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除评论", description = "删除指定评论，需要登录且为评论作者或管理员")
    @RequireLogin
    @DeleteMapping("/comments/{commentId}")
    public BaseResponse<Boolean> deleteComment(@Parameter(description = "评论ID") @PathVariable Long commentId,
                                               HttpServletRequest request) {
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        Boolean result = commentService.deleteComment(commentId, request);
        return ResultUtils.success(result, "评论删除成功");
    }

    /**
     * 获取评论列表
     *
     * @param articleId 文章ID
     * @param current 当前页码，默认1
     * @param size 每页大小，默认10
     * @param sortOrder 排序方式（asc, desc）
     * @param request HTTP请求对象
     * @return 评论分页列表
     */
    @Operation(summary = "获取评论列表", description = "获取指定文章的评论列表，支持分页和排序")
    @GetMapping("/articles/{articleId}/comments")
    public BaseResponse<Page<CommentVO>> getCommentList(
            @Parameter(description = "文章ID") @PathVariable Long articleId,
            @Parameter(description = "当前页码，默认1") @RequestParam(required = false, defaultValue = "1") Integer current,
            @Parameter(description = "每页大小，默认10") @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Page<CommentVO> commentPage = commentService.getCommentPage(articleId, current, size, sortOrder, request);
        return ResultUtils.success(commentPage, "获取成功");
    }

    /**
     * 评论点赞
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    @Operation(summary = "评论点赞", description = "对指定评论进行点赞，需要登录")
    @RequireLogin
    @PostMapping("/comments/{commentId}/like")
    public BaseResponse<Boolean> likeComment(@Parameter(description = "评论ID") @PathVariable Long commentId,
                                             HttpServletRequest request) {
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        Boolean result = commentService.likeComment(commentId, request);
        return ResultUtils.success(result, "点赞成功");
    }

    /**
     * 取消评论点赞
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 取消点赞结果
     */
    @Operation(summary = "取消评论点赞", description = "取消对指定评论的点赞，需要登录")
    @RequireLogin
    @DeleteMapping("/comments/{commentId}/like")
    public BaseResponse<Boolean> unlikeComment(@Parameter(description = "评论ID") @PathVariable Long commentId,
                                               HttpServletRequest request) {
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        Boolean result = commentService.unlikeComment(commentId, request);
        return ResultUtils.success(result, "取消点赞成功");
    }
}
