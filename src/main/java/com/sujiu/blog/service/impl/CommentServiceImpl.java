package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.CommentMapper;
import com.sujiu.blog.mapper.LikeMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.admin.CommentAuditRequest;
import com.sujiu.blog.model.dto.comment.AdminCommentQueryRequest;
import com.sujiu.blog.model.dto.comment.CommentQueryRequest;
import com.sujiu.blog.model.dto.comment.CommentRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.Comment;
import com.sujiu.blog.model.entity.Like;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.enums.ArticleStatusEnum;
import com.sujiu.blog.model.enums.CommentStatusEnum;
import com.sujiu.blog.model.vo.comment.AdminCommentVO;
import com.sujiu.blog.model.vo.comment.CommentVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private LikeMapper likeMapper;

    /**
     * 获取当前登录用户
     */
    private User getCurrentLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取当前登录用户
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 从数据库查询最新的用户信息
        Long userId = currentUser.getId();
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        return user;
    }

    /**
     * 获取当前登录用户（可选）
     */
    private User getCurrentLoginUserOptional(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        try {
            return getCurrentLoginUser(request);
        } catch (BusinessException e) {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(CommentRequest commentRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (commentRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户
        User user = getCurrentLoginUser(request);

        // 3. 详细参数校验
        validateCommentRequest(commentRequest);

        // 4. 检查文章是否存在且已发布
        Article article = articleMapper.selectById(commentRequest.getArticleId());
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }
        if (!ArticleStatusEnum.PUBLISHED.getValue().equals(article.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章未发布，无法评论");
        }

        // 5. 检查父评论是否存在（如果有）
        if (commentRequest.getParentId() != null && commentRequest.getParentId() > 0) {
            Comment parentComment = this.getById(commentRequest.getParentId());
            if (parentComment == null || !CommentStatusEnum.APPROVED.getValue().equals(parentComment.getStatus())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论不存在或未审核");
            }
            if (!parentComment.getArticleId().equals(commentRequest.getArticleId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论与文章不匹配");
            }
        }

        // 6. 检查回复目标评论是否存在（如果有）
        if (commentRequest.getReplyToId() != null && commentRequest.getReplyToId() > 0) {
            Comment replyToComment = this.getById(commentRequest.getReplyToId());
            if (replyToComment == null || !CommentStatusEnum.APPROVED.getValue().equals(replyToComment.getStatus())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复目标评论不存在或未审核");
            }
            if (!replyToComment.getArticleId().equals(commentRequest.getArticleId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复目标评论与文章不匹配");
            }
        }

        // 7. 创建评论实体
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentRequest, comment);
        
        // 设置默认值
        comment.setAuthorId(user.getId());
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }
        comment.setLikeCount(0);
        comment.setStatus(CommentStatusEnum.APPROVED.getValue()); // 默认审核通过
        
        // 设置IP地址和用户代理
        comment.setIpAddress(getClientIpAddress(request));
        comment.setUserAgent(request.getHeader("User-Agent"));
        
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());

        // 8. 保存到数据库
        boolean result = this.save(comment);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评论发表失败");
        }

        // 9. 更新文章评论数量
        updateArticleCommentCount(commentRequest.getArticleId(), 1);

        // 10. 记录日志
        log.info("用户 {} 在文章 {} 发表了评论：{}", user.getId(), commentRequest.getArticleId(), comment.getId());

        // 11. 返回评论详情
        return convertToVO(comment, user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long commentId, HttpServletRequest request) {
        // 1. 参数校验
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        // 2. 获取当前登录用户
        User user = getCurrentLoginUser(request);

        // 3. 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 4. 检查权限
        if (!hasDeletePermission(comment, user.getId(), user.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限删除此评论");
        }

        // 5. 检查是否有子评论
        QueryWrapper<Comment> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parent_id", commentId);
        childWrapper.eq("status", CommentStatusEnum.APPROVED.getValue());
        long childCount = this.count(childWrapper);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该评论下还有回复，无法删除");
        }

        // 6. 执行逻辑删除
        boolean result = this.removeById(commentId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "评论删除失败");
        }

        // 7. 更新文章评论数量
        updateArticleCommentCount(comment.getArticleId(), -1);

        // 8. 记录日志
        log.info("用户 {} 删除了评论：{}", user.getId(), commentId);

        return true;
    }

    @Override
    public Page<CommentVO> getCommentPage(Long articleId, Integer current, Integer size, String sortOrder, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 2. 设置默认值
        if (current == null || current <= 0) {
            current = 1;
        }
        if (size == null || size <= 0) {
            size = 10;
        }
        if (size > 50) {
            size = 50; // 限制最大页面大小
        }

        // 3. 获取当前用户（可选）
        User currentUser = getCurrentLoginUserOptional(request);
        Long userId = currentUser != null ? currentUser.getId() : null;

        // 4. 构建查询条件：只查询顶级评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        queryWrapper.eq("parent_id", 0);
        queryWrapper.eq("status", CommentStatusEnum.APPROVED.getValue());
        
        // 排序
        if ("asc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByAsc("created_time");
        } else {
            queryWrapper.orderByDesc("created_time");
        }

        // 5. 分页查询
        Page<Comment> commentPage = this.page(new Page<>(current, size), queryWrapper);

        // 6. 构建评论树结构
        return buildCommentTree(commentPage, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeComment(Long commentId, HttpServletRequest request) {
        // 1. 参数校验
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        // 2. 获取当前登录用户
        User user = getCurrentLoginUser(request);

        // 3. 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 4. 检查是否已点赞
        if (isCommentLiked(commentId, user.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经点赞过了");
        }

        // 5. 创建点赞记录
        Like like = new Like();
        like.setUserId(user.getId());
        like.setTargetId(commentId);
        like.setTargetType(2); // 2表示评论
        like.setCreateTime(new Date());

        boolean result = likeMapper.insert(like) > 0;
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "点赞失败");
        }

        // 6. 更新评论点赞数量
        updateCommentLikeCount(commentId, 1);

        // 7. 记录日志
        log.info("用户 {} 点赞了评论：{}", user.getId(), commentId);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeComment(Long commentId, HttpServletRequest request) {
        // 1. 参数校验
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        // 2. 获取当前登录用户
        User user = getCurrentLoginUser(request);

        // 3. 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 4. 检查是否已点赞
        if (!isCommentLiked(commentId, user.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "还未点赞");
        }

        // 5. 删除点赞记录
        QueryWrapper<Like> likeWrapper = new QueryWrapper<>();
        likeWrapper.eq("user_id", user.getId());
        likeWrapper.eq("target_id", commentId);
        likeWrapper.eq("target_type", 2);

        boolean result = likeMapper.delete(likeWrapper) > 0;
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "取消点赞失败");
        }

        // 6. 更新评论点赞数量
        updateCommentLikeCount(commentId, -1);

        // 7. 记录日志
        log.info("用户 {} 取消点赞了评论：{}", user.getId(), commentId);

        return true;
    }

    @Override
    public void validateCommentRequest(CommentRequest commentRequest) {
        String content = commentRequest.getContent();
        Long articleId = commentRequest.getArticleId();

        // 评论内容校验
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (content.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能超过1000个字符");
        }

        // 文章ID校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 父评论ID校验
        if (commentRequest.getParentId() != null && commentRequest.getParentId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论ID不能为负数");
        }

        // 回复目标ID校验
        if (commentRequest.getReplyToId() != null && commentRequest.getReplyToId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回复目标ID不能为负数");
        }
    }

    @Override
    public Boolean hasDeletePermission(Comment comment, Long userId, String userRole) {
        if (comment == null || userId == null) {
            return false;
        }

        // 管理员可以删除任何评论
        if ("admin".equals(userRole)) {
            return true;
        }

        // 评论作者可以删除自己的评论
        return comment.getAuthorId().equals(userId);
    }

    @Override
    public Boolean isCommentLiked(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            return false;
        }

        QueryWrapper<Like> likeWrapper = new QueryWrapper<>();
        likeWrapper.eq("user_id", userId);
        likeWrapper.eq("target_id", commentId);
        likeWrapper.eq("target_type", 2);

        return likeMapper.selectCount(likeWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommentLikeCount(Long commentId, Integer increment) {
        if (commentId == null || increment == null || increment == 0) {
            return;
        }

        Comment comment = this.getById(commentId);
        if (comment == null) {
            return;
        }

        int newCount = Math.max(0, comment.getLikeCount() + increment);
        comment.setLikeCount(newCount);
        comment.setUpdateTime(new Date());

        this.updateById(comment);

        log.debug("更新评论 {} 的点赞数量：{} -> {}", commentId, comment.getLikeCount() - increment, newCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleCommentCount(Long articleId, Integer increment) {
        if (articleId == null || increment == null || increment == 0) {
            return;
        }

        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return;
        }

        int newCount = Math.max(0, article.getCommentCount() + increment);
        article.setCommentCount(newCount);
        article.setUpdateTime(new Date());

        articleMapper.updateById(article);

        log.debug("更新文章 {} 的评论数量：{} -> {}", articleId, article.getCommentCount() - increment, newCount);
    }

    @Override
    public QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        if (commentQueryRequest == null) {
            return queryWrapper;
        }

        Long articleId = commentQueryRequest.getArticleId();
        Long authorId = commentQueryRequest.getAuthorId();
        Long parentId = commentQueryRequest.getParentId();
        Integer status = commentQueryRequest.getStatus();
        String keyword = commentQueryRequest.getKeyword();
        String sortOrder = commentQueryRequest.getSortOrder();

        // 文章ID筛选
        if (articleId != null) {
            queryWrapper.eq("article_id", articleId);
        }

        // 评论者ID筛选
        if (authorId != null) {
            queryWrapper.eq("user_id", authorId);
        }

        // 父评论ID筛选
        if (parentId != null) {
            queryWrapper.eq("parent_id", parentId);
        }

        // 状态筛选
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("content", keyword);
        }

        // 排序
        if ("asc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByAsc("created_time");
        } else {
            queryWrapper.orderByDesc("created_time");
        }

        return queryWrapper;
    }

    @Override
    public CommentVO convertToVO(Comment comment, Long userId) {
        if (comment == null) {
            return null;
        }

        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        commentVO.setCreatedTime(comment.getCreateTime());

        // 设置评论者信息
        User author = userMapper.selectById(comment.getAuthorId());
        if (author != null) {
            CommentVO.AuthorInfo authorInfo = new CommentVO.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            commentVO.setAuthor(authorInfo);
        }

        // 设置回复目标信息
        if (comment.getReplyToId() != null && comment.getReplyToId() > 0) {
            Comment replyToComment = this.getById(comment.getReplyToId());
            if (replyToComment != null) {
                User replyToUser = userMapper.selectById(replyToComment.getAuthorId());
                if (replyToUser != null) {
                    CommentVO.ReplyToInfo replyToInfo = new CommentVO.ReplyToInfo();
                    replyToInfo.setId(replyToUser.getId());
                    replyToInfo.setNickname(replyToUser.getNickname());
                    commentVO.setReplyTo(replyToInfo);
                }
            }
        }

        // 设置是否已点赞
        commentVO.setIsLiked(userId != null && isCommentLiked(comment.getId(), userId));

        return commentVO;
    }

    @Override
    public Page<CommentVO> buildCommentTree(Page<Comment> comments, Long userId) {
        Page<CommentVO> commentVOPage = new Page<>(comments.getCurrent(), comments.getSize(), comments.getTotal());

        if (comments.getRecords().isEmpty()) {
            commentVOPage.setRecords(new ArrayList<>());
            return commentVOPage;
        }

        // 获取所有顶级评论的回复
        List<Long> parentIds = comments.getRecords().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        QueryWrapper<Comment> replyWrapper = new QueryWrapper<>();
        replyWrapper.in("parent_id", parentIds);
        replyWrapper.eq("status", CommentStatusEnum.APPROVED.getValue());
        replyWrapper.orderByAsc("created_time");

        List<Comment> replies = this.list(replyWrapper);

        // 按父评论ID分组
        Map<Long, List<Comment>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 转换为VO并构建树结构
        List<CommentVO> commentVOList = comments.getRecords().stream()
                .map(comment -> {
                    CommentVO commentVO = convertToVO(comment, userId);

                    // 添加回复列表
                    List<Comment> commentReplies = replyMap.get(comment.getId());
                    if (commentReplies != null && !commentReplies.isEmpty()) {
                        List<CommentVO.ReplyComment> replyComments = commentReplies.stream()
                                .map(reply -> {
                                    CommentVO.ReplyComment replyComment = new CommentVO.ReplyComment();
                                    replyComment.setId(reply.getId());
                                    replyComment.setContent(reply.getContent());
                                    replyComment.setLikeCount(reply.getLikeCount());
                                    replyComment.setCreatedTime(reply.getCreateTime());
                                    replyComment.setIsLiked(userId != null && isCommentLiked(reply.getId(), userId));

                                    // 设置回复者信息
                                    User replyAuthor = userMapper.selectById(reply.getAuthorId());
                                    if (replyAuthor != null) {
                                        CommentVO.AuthorInfo authorInfo = new CommentVO.AuthorInfo();
                                        authorInfo.setId(replyAuthor.getId());
                                        authorInfo.setUsername(replyAuthor.getUsername());
                                        authorInfo.setNickname(replyAuthor.getNickname());
                                        authorInfo.setAvatar(replyAuthor.getAvatar());
                                        replyComment.setAuthor(authorInfo);
                                    }

                                    // 设置回复目标信息
                                    if (reply.getReplyToId() != null && reply.getReplyToId() > 0) {
                                        Comment replyToComment = this.getById(reply.getReplyToId());
                                        if (replyToComment != null) {
                                            User replyToUser = userMapper.selectById(replyToComment.getAuthorId());
                                            if (replyToUser != null) {
                                                CommentVO.ReplyToInfo replyToInfo = new CommentVO.ReplyToInfo();
                                                replyToInfo.setId(replyToUser.getId());
                                                replyToInfo.setNickname(replyToUser.getNickname());
                                                replyComment.setReplyTo(replyToInfo);
                                            }
                                        }
                                    }

                                    return replyComment;
                                })
                                .collect(Collectors.toList());

                        commentVO.setReplies(replyComments);
                    }

                    return commentVO;
                })
                .collect(Collectors.toList());

        commentVOPage.setRecords(commentVOList);
        return commentVOPage;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    // ==================== 管理员评论管理接口实现 ====================

    @Override
    public PageVO<AdminCommentVO> listCommentsByAdmin(AdminCommentQueryRequest adminCommentQueryRequest) {
        // 1. 参数校验
        if (adminCommentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 构建查询条件
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        // 关键词搜索（评论内容）
        String keyword = adminCommentQueryRequest.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("content", keyword);
        }

        // 状态筛选
        Integer status = adminCommentQueryRequest.getStatus();
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 文章ID筛选
        Long articleId = adminCommentQueryRequest.getArticleId();
        if (articleId != null) {
            queryWrapper.eq("article_id", articleId);
        }

        // 评论者ID筛选
        Long authorId = adminCommentQueryRequest.getAuthorId();
        if (authorId != null) {
            queryWrapper.eq("author_id", authorId);
        }

        // 时间范围筛选
        if (adminCommentQueryRequest.getStartTime() != null) {
            queryWrapper.ge("created_time", adminCommentQueryRequest.getStartTime());
        }
        if (adminCommentQueryRequest.getEndTime() != null) {
            queryWrapper.le("created_time", adminCommentQueryRequest.getEndTime());
        }

        // 父评论ID筛选
        Long parentId = adminCommentQueryRequest.getParentId();
        if (parentId != null) {
            if (parentId == 0) {
                // 只查询顶级评论
                queryWrapper.isNull("parent_id").or().eq("parent_id", 0);
            } else {
                queryWrapper.eq("parent_id", parentId);
            }
        }

        // IP地址筛选
        String ipAddress = adminCommentQueryRequest.getIpAddress();
        if (StringUtils.isNotBlank(ipAddress)) {
            queryWrapper.eq("author_ip", ipAddress);
        }

        // 排序
        String sortField = adminCommentQueryRequest.getSortField();
        String sortOrder = adminCommentQueryRequest.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if ("asc".equals(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("created_time");
        }

        // 3. 分页查询
        Page<Comment> page = new Page<>(adminCommentQueryRequest.getCurrent(), adminCommentQueryRequest.getSize());
        Page<Comment> commentPage = this.page(page, queryWrapper);

        // 4. 转换为VO
        List<AdminCommentVO> adminCommentVOList = commentPage.getRecords().stream()
            .map(this::convertToAdminCommentVO)
            .collect(Collectors.toList());

        // 5. 构建分页结果
        return new PageVO<>(adminCommentVOList, commentPage.getTotal(),
            commentPage.getCurrent(), commentPage.getSize());
    }

    @Override
    public Boolean auditComment(Long commentId, CommentAuditRequest commentAuditRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }
        if (commentAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Integer status = commentAuditRequest.getStatus();
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态参数错误");
        }

        // 2. 获取当前操作用户
        User currentUser = getCurrentLoginUser(request);
        log.info("管理员 {} 审核评论 {}，状态：{}，原因：{}",
            currentUser.getId(), commentId, status, commentAuditRequest.getAuditReason());

        // 3. 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }

        // 4. 检查评论状态（只有待审核的评论才能审核）
        if (comment.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不在待审核状态");
        }

        // 5. 更新评论状态
        Comment updateComment = new Comment();
        updateComment.setId(commentId);
        updateComment.setStatus(status);
        updateComment.setAuditReason(commentAuditRequest.getAuditReason());
        updateComment.setUpdateTime(new Date());

        boolean result = this.updateById(updateComment);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评论审核失败");
        }

        return true;
    }

    @Override
    public Boolean deleteCommentByAdmin(Long commentId, HttpServletRequest request) {
        // 1. 参数校验
        if (commentId == null || commentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID不能为空");
        }

        // 2. 获取当前操作用户
        User currentUser = getCurrentLoginUser(request);
        log.info("管理员 {} 删除评论 {}", currentUser.getId(), commentId);

        // 3. 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论不存在");
        }

        // 4. 逻辑删除评论
        boolean result = this.removeById(commentId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评论删除失败");
        }

        // 5. 更新文章评论数量
        updateArticleCommentCount(comment.getArticleId(), -1);

        return true;
    }

    /**
     * 转换Comment实体为AdminCommentVO
     *
     * @param comment 评论实体
     * @return AdminCommentVO
     */
    private AdminCommentVO convertToAdminCommentVO(Comment comment) {
        if (comment == null) {
            return null;
        }

        AdminCommentVO adminCommentVO = new AdminCommentVO();
        BeanUtils.copyProperties(comment, adminCommentVO);
        adminCommentVO.setCreateTime(comment.getCreateTime());
        adminCommentVO.setUpdateTime(comment.getUpdateTime());

        // 设置文章信息
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null) {
            AdminCommentVO.ArticleInfo articleInfo = new AdminCommentVO.ArticleInfo();
            articleInfo.setId(article.getId());
            articleInfo.setTitle(article.getTitle());
            articleInfo.setStatus(article.getStatus());
            adminCommentVO.setArticle(articleInfo);
        }

        // 设置评论者信息
        User author = userMapper.selectById(comment.getAuthorId());
        if (author != null) {
            AdminCommentVO.AuthorInfo authorInfo = new AdminCommentVO.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            authorInfo.setEmail(author.getEmail());
            authorInfo.setStatus(author.getStatus());
            adminCommentVO.setAuthor(authorInfo);
        }

        // 设置回复目标用户信息
        if (comment.getReplyToId() != null) {
            User replyToUser = userMapper.selectById(comment.getReplyToId());
            if (replyToUser != null) {
                AdminCommentVO.ReplyToInfo replyToInfo = new AdminCommentVO.ReplyToInfo();
                replyToInfo.setId(replyToUser.getId());
                replyToInfo.setUsername(replyToUser.getUsername());
                replyToInfo.setNickname(replyToUser.getNickname());
                adminCommentVO.setReplyTo(replyToInfo);
            }
        }

        return adminCommentVO;
    }
}
