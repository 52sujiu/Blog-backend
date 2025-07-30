package com.sujiu.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.entity.Follow;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 关注服务
 *
 * @author sujiu
 */
public interface FollowService extends IService<Follow> {

    /**
     * 关注用户
     *
     * @param followingId 被关注用户ID
     * @param request HTTP请求对象
     * @return 关注结果
     */
    Boolean followUser(Long followingId, HttpServletRequest request);

    /**
     * 取消关注
     *
     * @param followingId 被关注用户ID
     * @param request HTTP请求对象
     * @return 取消关注结果
     */
    Boolean unfollowUser(Long followingId, HttpServletRequest request);

    /**
     * 获取关注列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 关注列表
     */
    PageVO<UserVO> getFollowingList(Long userId, int current, int size);

    /**
     * 获取粉丝列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 粉丝列表
     */
    PageVO<UserVO> getFollowerList(Long userId, int current, int size);

    /**
     * 检查是否已关注
     *
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 是否已关注
     */
    Boolean isFollowing(Long followerId, Long followingId);

    /**
     * 获取关注数
     *
     * @param userId 用户ID
     * @return 关注数
     */
    Integer getFollowingCount(Long userId);

    /**
     * 获取粉丝数
     *
     * @param userId 用户ID
     * @return 粉丝数
     */
    Integer getFollowerCount(Long userId);
}
