package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireLogin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.user.PasswordUpdateRequest;
import com.sujiu.blog.model.dto.user.UserLoginRequest;
import com.sujiu.blog.model.dto.user.UserRegisterRequest;
import com.sujiu.blog.model.dto.user.UserUpdateRequest;
import com.sujiu.blog.model.vo.user.LoginUserVO;
import com.sujiu.blog.model.vo.user.UserRegisterVO;
import com.sujiu.blog.model.vo.user.UserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.FollowService;
import com.sujiu.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FollowService followService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @param request HTTP请求对象
     * @return 注册结果
     */
    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    public BaseResponse<UserRegisterVO> userRegister(@RequestBody UserRegisterRequest userRegisterRequest,
                                                     HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Long userId = userService.userRegister(userRegisterRequest, request);

        UserRegisterVO userRegisterVO = new UserRegisterVO();
        userRegisterVO.setUserId(userId);

        return ResultUtils.success(userRegisterVO, "注册成功");
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request HTTP请求对象
     * @return 登录结果
     */
    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                               HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(loginUserVO, "登录成功");
    }

    /**
     * 用户注销
     *
     * @param request HTTP请求对象
     * @return 注销结果
     */
    @Operation(summary = "用户注销", description = "用户注销接口")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        Boolean result = userService.userLogout(request);
        return ResultUtils.success(result, "注销成功");
    }

    /**
     * 获取当前用户信息
     *
     * @param request HTTP请求对象
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @RequireLogin
    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        LoginUserVO currentUser = userService.getCurrentUser(request);
        return ResultUtils.success(currentUser, "获取成功");
    }

    /**
     * 更新个人信息
     *
     * @param userUpdateRequest 用户信息更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息")
    @RequireLogin
    @PutMapping("/profile")
    public BaseResponse<Boolean> updateProfile(@RequestBody UserUpdateRequest userUpdateRequest,
                                               HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = userService.updateProfile(userUpdateRequest, request);
        return ResultUtils.success(result, "更新成功");
    }

    /**
     * 修改密码
     *
     * @param passwordUpdateRequest 密码修改请求
     * @param request HTTP请求对象
     * @return 修改结果
     */
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @RequireLogin
    @PutMapping("/password")
    public BaseResponse<Boolean> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                                HttpServletRequest request) {
        if (passwordUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = userService.updatePassword(passwordUpdateRequest, request);
        return ResultUtils.success(result, "密码修改成功");
    }

    /**
     * 关注用户
     *
     * @param userId 被关注用户ID
     * @param request HTTP请求对象
     * @return 关注结果
     */
    @Operation(summary = "关注用户", description = "关注指定用户")
    @RequireLogin
    @PostMapping("/{userId}/follow")
    public BaseResponse<Boolean> followUser(@PathVariable Long userId,
                                            HttpServletRequest request) {
        Boolean result = followService.followUser(userId, request);
        return ResultUtils.success(result, "关注成功");
    }

    /**
     * 取消关注
     *
     * @param userId 被关注用户ID
     * @param request HTTP请求对象
     * @return 取消关注结果
     */
    @Operation(summary = "取消关注", description = "取消关注指定用户")
    @RequireLogin
    @DeleteMapping("/{userId}/follow")
    public BaseResponse<Boolean> unfollowUser(@PathVariable Long userId,
                                              HttpServletRequest request) {
        Boolean result = followService.unfollowUser(userId, request);
        return ResultUtils.success(result, "取消关注成功");
    }

    /**
     * 获取关注列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 关注列表
     */
    @Operation(summary = "获取关注列表", description = "获取指定用户的关注列表")
    @GetMapping("/{userId}/following")
    public BaseResponse<PageVO<UserVO>> getFollowingList(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "1") int current,
                                                         @RequestParam(defaultValue = "10") int size) {
        PageVO<UserVO> result = followService.getFollowingList(userId, current, size);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 获取粉丝列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 粉丝列表
     */
    @Operation(summary = "获取粉丝列表", description = "获取指定用户的粉丝列表")
    @GetMapping("/{userId}/followers")
    public BaseResponse<PageVO<UserVO>> getFollowerList(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "1") int current,
                                                        @RequestParam(defaultValue = "10") int size) {
        PageVO<UserVO> result = followService.getFollowerList(userId, current, size);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 获取用户公开信息
     *
     * @param userId 用户ID
     * @return 用户公开信息
     */
    @Operation(summary = "获取用户公开信息", description = "获取指定用户的公开信息")
    @GetMapping("/{userId}")
    public BaseResponse<UserVO> getUserPublicInfo(@PathVariable Long userId) {
        UserVO userVO = userService.getUserPublicInfo(userId);
        return ResultUtils.success(userVO, "获取成功");
    }
}
