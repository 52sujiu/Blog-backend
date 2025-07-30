package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.admin.CommentAuditRequest;
import com.sujiu.blog.model.dto.comment.AdminCommentQueryRequest;
import com.sujiu.blog.model.vo.comment.AdminCommentVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员评论管理控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/admin/comments")
@Slf4j
@Tag(name = "管理员评论管理", description = "管理员评论管理相关接口")
public class AdminCommentController {

    @Resource
    private CommentService commentService;

    /**
     * 获取所有评论列表
     *
     * @param adminCommentQueryRequest 评论查询请求
     * @return 评论列表
     */
    @Operation(summary = "获取所有评论列表", description = "管理员获取所有评论列表，支持分页、搜索、筛选")
    @RequireAdmin
    @GetMapping
    public BaseResponse<PageVO<AdminCommentVO>> listComments(AdminCommentQueryRequest adminCommentQueryRequest) {
        if (adminCommentQueryRequest == null) {
            adminCommentQueryRequest = new AdminCommentQueryRequest();
        }

        PageVO<AdminCommentVO> result = commentService.listCommentsByAdmin(adminCommentQueryRequest);
        return ResultUtils.success(result, "获取评论列表成功");
    }

    /**
     * 审核评论
     *
     * @param commentId 评论ID
     * @param commentAuditRequest 评论审核请求
     * @param request HTTP请求对象
     * @return 审核结果
     */
    @Operation(summary = "审核评论", description = "管理员审核评论（通过/拒绝）")
    @RequireAdmin
    @PutMapping("/{commentId}/audit")
    public BaseResponse<Boolean> auditComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId,
            @RequestBody CommentAuditRequest commentAuditRequest,
            HttpServletRequest request) {
        
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (commentAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = commentService.auditComment(commentId, commentAuditRequest, request);
        return ResultUtils.success(result, "评论审核成功");
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除评论", description = "管理员删除评论（逻辑删除）")
    @RequireAdmin
    @DeleteMapping("/{commentId}")
    public BaseResponse<Boolean> deleteComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId,
            HttpServletRequest request) {
        
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        Boolean result = commentService.deleteCommentByAdmin(commentId, request);
        return ResultUtils.success(result, "评论删除成功");
    }
}
