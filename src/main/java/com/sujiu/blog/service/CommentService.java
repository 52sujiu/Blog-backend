package com.sujiu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.admin.CommentAuditRequest;
import com.sujiu.blog.model.dto.comment.AdminCommentQueryRequest;
import com.sujiu.blog.model.dto.comment.CommentQueryRequest;
import com.sujiu.blog.model.dto.comment.CommentRequest;
import com.sujiu.blog.model.entity.Comment;
import com.sujiu.blog.model.vo.comment.AdminCommentVO;
import com.sujiu.blog.model.vo.comment.CommentVO;
import com.sujiu.blog.model.vo.common.PageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论服务
 *
 * @author sujiu
 */
public interface CommentService extends IService<Comment> {

    /**
     * 发表评论
     *
     * @param commentRequest 评论创建请求
     * @param request HTTP请求对象
     * @return 评论详情
     */
    CommentVO createComment(CommentRequest commentRequest, HttpServletRequest request);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteComment(Long commentId, HttpServletRequest request);

    /**
     * 获取评论列表（分页）
     *
     * @param articleId 文章ID
     * @param current 当前页码
     * @param size 每页大小
     * @param sortOrder 排序方式
     * @param request HTTP请求对象
     * @return 评论分页列表
     */
    Page<CommentVO> getCommentPage(Long articleId, Integer current, Integer size, String sortOrder, HttpServletRequest request);

    /**
     * 评论点赞
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    Boolean likeComment(Long commentId, HttpServletRequest request);

    /**
     * 取消评论点赞
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 取消点赞结果
     */
    Boolean unlikeComment(Long commentId, HttpServletRequest request);

    /**
     * 校验评论参数
     *
     * @param commentRequest 评论请求
     */
    void validateCommentRequest(CommentRequest commentRequest);

    /**
     * 检查用户是否有权限删除评论
     *
     * @param comment 评论实体
     * @param userId 用户ID
     * @param userRole 用户角色
     * @return 是否有权限
     */
    Boolean hasDeletePermission(Comment comment, Long userId, String userRole);

    /**
     * 检查用户是否已点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    Boolean isCommentLiked(Long commentId, Long userId);

    /**
     * 更新评论点赞数量
     *
     * @param commentId 评论ID
     * @param increment 增量（可为负数）
     */
    void updateCommentLikeCount(Long commentId, Integer increment);

    /**
     * 更新文章评论数量
     *
     * @param articleId 文章ID
     * @param increment 增量（可为负数）
     */
    void updateArticleCommentCount(Long articleId, Integer increment);

    /**
     * 获取查询包装器
     *
     * @param commentQueryRequest 查询请求
     * @return 查询包装器
     */
    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest);

    /**
     * 转换实体为VO
     *
     * @param comment 评论实体
     * @param userId 当前用户ID（用于判断是否已点赞）
     * @return 评论VO
     */
    CommentVO convertToVO(Comment comment, Long userId);

    /**
     * 构建评论树结构
     *
     * @param comments 评论列表
     * @param userId 当前用户ID
     * @return 评论树
     */
    Page<CommentVO> buildCommentTree(Page<Comment> comments, Long userId);

    // ==================== 管理员评论管理接口 ====================

    /**
     * 管理员获取评论列表
     *
     * @param adminCommentQueryRequest 管理员评论查询请求
     * @return 评论列表
     */
    PageVO<AdminCommentVO> listCommentsByAdmin(AdminCommentQueryRequest adminCommentQueryRequest);

    /**
     * 管理员审核评论
     *
     * @param commentId 评论ID
     * @param commentAuditRequest 评论审核请求
     * @param request HTTP请求对象
     * @return 审核结果
     */
    Boolean auditComment(Long commentId, CommentAuditRequest commentAuditRequest, HttpServletRequest request);

    /**
     * 管理员删除评论
     *
     * @param commentId 评论ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteCommentByAdmin(Long commentId, HttpServletRequest request);
}
