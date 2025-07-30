package com.sujiu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.user.PasswordUpdateRequest;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserQueryRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.dto.user.UserRoleUpdateRequest;
import com.sujiu.blog.model.dto.user.UserStatusUpdateRequest;
import com.sujiu.blog.model.dto.user.UserUpdateRequest;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.user.AdminUserVO;
import com.sujiu.blog.model.vo.user.LoginUserVO;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author sujiu
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @param request HTTP请求对象
     * @return 用户ID
     */
    Long userRegister(UserRegisterRequest userRegisterRequest, HttpServletRequest request);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request HTTP请求对象
     * @return 登录用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request HTTP请求对象
     * @return 注销结果
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     *
     * @param request HTTP请求对象
     * @return 当前登录用户信息
     */
    LoginUserVO getCurrentUser(HttpServletRequest request);

    /**
     * 更新个人信息
     *
     * @param userUpdateRequest 用户信息更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    Boolean updateProfile(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 修改密码
     *
     * @param passwordUpdateRequest 密码修改请求
     * @param request HTTP请求对象
     * @return 修改结果
     */
    Boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest, HttpServletRequest request);

    /**
     * 获取用户公开信息
     *
     * @param userId 用户ID
     * @return 用户公开信息
     */
    UserVO getUserPublicInfo(Long userId);

    /**
     * 获取当前登录用户（已验证权限）
     *
     * @param request HTTP请求对象
     * @return 当前登录用户
     */
    User getCurrentLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request HTTP请求对象
     * @return 当前登录用户信息，未登录时返回null
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    // ==================== 管理员用户管理接口 ====================

    /**
     * 管理员获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户列表
     */
    PageVO<AdminUserVO> listUsersByAdmin(UserQueryRequest userQueryRequest);

    /**
     * 管理员更新用户状态
     *
     * @param userId 用户ID
     * @param userStatusUpdateRequest 用户状态更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    Boolean updateUserStatus(Long userId, UserStatusUpdateRequest userStatusUpdateRequest, HttpServletRequest request);

    /**
     * 管理员更新用户角色
     *
     * @param userId 用户ID
     * @param userRoleUpdateRequest 用户角色更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    Boolean updateUserRole(Long userId, UserRoleUpdateRequest userRoleUpdateRequest, HttpServletRequest request);

    /**
     * 管理员删除用户
     *
     * @param userId 用户ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteUser(Long userId, HttpServletRequest request);

}
