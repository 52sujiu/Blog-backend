package com.sujiu.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.entity.Like;

import javax.servlet.http.HttpServletRequest;

/**
 * 点赞服务
 *
 * @author sujiu
 */
public interface LikeService extends IService<Like> {

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    Boolean likeArticle(Long articleId, HttpServletRequest request);

    /**
     * 取消文章点赞
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 取消点赞结果
     */
    Boolean unlikeArticle(Long articleId, HttpServletRequest request);

    /**
     * 检查文章点赞状态
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 是否已点赞
     */
    Boolean checkArticleLikeStatus(Long articleId, HttpServletRequest request);

    /**
     * 获取文章点赞数
     *
     * @param articleId 文章ID
     * @return 点赞数
     */
    Long getArticleLikeCount(Long articleId);

    /**
     * 通用点赞方法
     *
     * @param targetId 目标ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    Boolean like(Long targetId, Integer targetType, HttpServletRequest request);

    /**
     * 通用取消点赞方法
     *
     * @param targetId 目标ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param request HTTP请求对象
     * @return 取消点赞结果
     */
    Boolean unlike(Long targetId, Integer targetType, HttpServletRequest request);

    /**
     * 通用检查点赞状态方法
     *
     * @param targetId 目标ID
     * @param targetType 目标类型（1-文章，2-评论）
     * @param request HTTP请求对象
     * @return 是否已点赞
     */
    Boolean checkLikeStatus(Long targetId, Integer targetType, HttpServletRequest request);
}
