package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.LikeMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.Like;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 点赞服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 文章类型常量
     */
    private static final Integer TARGET_TYPE_ARTICLE = 1;

    /**
     * 评论类型常量
     */
    private static final Integer TARGET_TYPE_COMMENT = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean likeArticle(Long articleId, HttpServletRequest request) {
        return like(articleId, TARGET_TYPE_ARTICLE, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlikeArticle(Long articleId, HttpServletRequest request) {
        return unlike(articleId, TARGET_TYPE_ARTICLE, request);
    }

    @Override
    public Boolean checkArticleLikeStatus(Long articleId, HttpServletRequest request) {
        return checkLikeStatus(articleId, TARGET_TYPE_ARTICLE, request);
    }

    @Override
    public Long getArticleLikeCount(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return 0L;
        }

        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_id", articleId)
                   .eq("target_type", TARGET_TYPE_ARTICLE);
        return this.count(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean like(Long targetId, Integer targetType, HttpServletRequest request) {
        // 1. 参数校验
        if (targetId == null || targetId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标ID不能为空");
        }
        if (targetType == null || (targetType != TARGET_TYPE_ARTICLE && targetType != TARGET_TYPE_COMMENT)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不正确");
        }

        // 2. 获取当前登录用户
        User currentUser = getCurrentLoginUser(request);
        Long userId = currentUser.getId();

        // 3. 如果是文章点赞，检查文章是否存在和状态
        if (TARGET_TYPE_ARTICLE.equals(targetType)) {
            Article article = articleMapper.selectById(targetId);
            if (article == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
            }
            if (article.getStatus() != 2) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章未发布，无法点赞");
            }
        }

        // 4. 检查是否已经点赞
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_id", targetId)
                   .eq("target_type", targetType);
        Like existingLike = this.getOne(queryWrapper);
        
        if (existingLike != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您已经点赞过了");
        }

        // 5. 创建点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setTargetId(targetId);
        like.setTargetType(targetType);
        like.setCreateTime(new Date());

        boolean saveResult = this.save(like);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "点赞失败");
        }

        // 手动更新点赞数
        if (TARGET_TYPE_ARTICLE.equals(targetType)) {
            updateArticleLikeCount(targetId);
        }

        log.info("用户点赞成功，用户ID：{}，目标ID：{}，目标类型：{}", userId, targetId, targetType);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unlike(Long targetId, Integer targetType, HttpServletRequest request) {
        // 1. 参数校验
        if (targetId == null || targetId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标ID不能为空");
        }
        if (targetType == null || (targetType != TARGET_TYPE_ARTICLE && targetType != TARGET_TYPE_COMMENT)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不正确");
        }

        // 2. 获取当前登录用户
        User currentUser = getCurrentLoginUser(request);
        Long userId = currentUser.getId();

        // 3. 检查是否已经点赞
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_id", targetId)
                   .eq("target_type", targetType);
        Like existingLike = this.getOne(queryWrapper);
        
        if (existingLike == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "您还没有点赞");
        }

        // 4. 删除点赞记录
        boolean removeResult = this.remove(queryWrapper);
        if (!removeResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消点赞失败");
        }

        // 手动更新点赞数
        if (TARGET_TYPE_ARTICLE.equals(targetType)) {
            updateArticleLikeCount(targetId);
        }

        log.info("用户取消点赞成功，用户ID：{}，目标ID：{}，目标类型：{}", userId, targetId, targetType);
        return true;
    }

    @Override
    public Boolean checkLikeStatus(Long targetId, Integer targetType, HttpServletRequest request) {
        // 1. 参数校验
        if (targetId == null || targetId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标ID不能为空");
        }
        if (targetType == null || (targetType != TARGET_TYPE_ARTICLE && targetType != TARGET_TYPE_COMMENT)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "目标类型不正确");
        }

        // 2. 获取当前登录用户
        User currentUser = getCurrentLoginUser(request);
        Long userId = currentUser.getId();

        // 3. 检查点赞状态
        QueryWrapper<Like> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("target_id", targetId)
                   .eq("target_type", targetType);
        Like existingLike = this.getOne(queryWrapper);
        
        return existingLike != null;
    }

    /**
     * 获取当前登录用户（已验证权限）
     * 
     * @param request HTTP请求对象
     * @return 当前登录用户
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
     * 更新文章点赞数
     *
     * @param articleId 文章ID
     */
    private void updateArticleLikeCount(Long articleId) {
        if (articleId == null || articleId <= 0) {
            return;
        }

        // 统计当前文章的点赞数
        Long likeCount = getArticleLikeCount(articleId);

        // 更新文章表中的点赞数
        Article updateArticle = new Article();
        updateArticle.setId(articleId);
        updateArticle.setLikeCount(likeCount.intValue());
        updateArticle.setUpdateTime(new Date());

        articleMapper.updateById(updateArticle);

        log.debug("更新文章点赞数，文章ID：{}，点赞数：{}", articleId, likeCount);
    }
}
