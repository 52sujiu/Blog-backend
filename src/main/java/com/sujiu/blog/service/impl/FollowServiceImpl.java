package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.FollowMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.entity.Follow;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 关注服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Boolean followUser(Long followingId, HttpServletRequest request) {
        // 1. 参数校验
        if (followingId == null || followingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User currentUser = getCurrentLoginUser(request);
        Long followerId = currentUser.getId();

        // 3. 不能关注自己
        if (followerId.equals(followingId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能关注自己");
        }

        // 4. 检查被关注用户是否存在
        User followingUser = userMapper.selectById(followingId);
        if (followingUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "被关注用户不存在");
        }

        // 5. 检查是否已经关注
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                   .eq("following_id", followingId);
        Follow existingFollow = this.getOne(queryWrapper);
        if (existingFollow != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经关注该用户");
        }

        // 6. 创建关注关系
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setCreateTime(new Date());

        boolean result = this.save(follow);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "关注失败，数据库异常");
        }

        log.info("用户关注成功，关注者ID：{}，被关注者ID：{}", followerId, followingId);
        return true;
    }

    @Override
    public Boolean unfollowUser(Long followingId, HttpServletRequest request) {
        // 1. 参数校验
        if (followingId == null || followingId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User currentUser = getCurrentLoginUser(request);
        Long followerId = currentUser.getId();

        // 3. 查找关注关系
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                   .eq("following_id", followingId);
        Follow follow = this.getOne(queryWrapper);
        if (follow == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未关注该用户");
        }

        // 4. 删除关注关系
        boolean result = this.removeById(follow.getId());
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "取消关注失败，数据库异常");
        }

        log.info("用户取消关注成功，关注者ID：{}，被关注者ID：{}", followerId, followingId);
        return true;
    }

    @Override
    public PageVO<UserVO> getFollowingList(Long userId, int current, int size) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 分页查询关注关系
        Page<Follow> page = new Page<>(current, size);
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId)
                   .orderByDesc("created_time");
        
        IPage<Follow> followPage = this.page(page, queryWrapper);

        // 3. 获取被关注用户ID列表
        List<Long> followingIds = followPage.getRecords().stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());

        // 4. 查询用户信息
        List<UserVO> userVOList;
        if (followingIds.isEmpty()) {
            userVOList = List.of();
        } else {
            List<User> users = userMapper.selectBatchIds(followingIds);
            userVOList = users.stream().map(this::convertToUserVO).collect(Collectors.toList());
        }

        // 5. 构建分页结果
        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(userVOList);
        pageVO.setTotal(followPage.getTotal());
        pageVO.setSize(followPage.getSize());
        pageVO.setCurrent(followPage.getCurrent());
        pageVO.setPages(followPage.getPages());

        return pageVO;
    }

    @Override
    public PageVO<UserVO> getFollowerList(Long userId, int current, int size) {
        // 1. 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        // 2. 分页查询关注关系
        Page<Follow> page = new Page<>(current, size);
        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("following_id", userId)
                   .orderByDesc("created_time");
        
        IPage<Follow> followPage = this.page(page, queryWrapper);

        // 3. 获取关注者用户ID列表
        List<Long> followerIds = followPage.getRecords().stream()
                .map(Follow::getFollowerId)
                .collect(Collectors.toList());

        // 4. 查询用户信息
        List<UserVO> userVOList;
        if (followerIds.isEmpty()) {
            userVOList = List.of();
        } else {
            List<User> users = userMapper.selectBatchIds(followerIds);
            userVOList = users.stream().map(this::convertToUserVO).collect(Collectors.toList());
        }

        // 5. 构建分页结果
        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(userVOList);
        pageVO.setTotal(followPage.getTotal());
        pageVO.setSize(followPage.getSize());
        pageVO.setCurrent(followPage.getCurrent());
        pageVO.setPages(followPage.getPages());

        return pageVO;
    }

    @Override
    public Boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }

        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                   .eq("following_id", followingId);
        
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Integer getFollowingCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", userId);
        
        return Math.toIntExact(this.count(queryWrapper));
    }

    @Override
    public Integer getFollowerCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("following_id", userId);
        
        return Math.toIntExact(this.count(queryWrapper));
    }

    /**
     * 转换为UserVO
     */
    private UserVO convertToUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setCreatedTime(user.getCreatedTime());
        
        // 设置统计信息
        userVO.setFollowingCount(getFollowingCount(user.getId()));
        userVO.setFollowerCount(getFollowerCount(user.getId()));
        // TODO: 设置文章数量，需要文章服务
        userVO.setArticleCount(0);
        
        return userVO;
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
}
