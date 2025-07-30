package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.user.UserQueryRequest;
import com.sujiu.blog.model.dto.user.UserRoleUpdateRequest;
import com.sujiu.blog.model.dto.user.UserStatusUpdateRequest;
import com.sujiu.blog.model.vo.user.AdminUserVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员用户管理控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/admin/users")
@Slf4j
@Tag(name = "管理员用户管理", description = "管理员用户管理相关接口")
public class  AdminUserController {

    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户列表
     */
    @Operation(summary = "获取用户列表", description = "管理员获取用户列表，支持分页、搜索、筛选")
    @RequireAdmin
    @GetMapping
    public BaseResponse<PageVO<AdminUserVO>> listUsers(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            userQueryRequest = new UserQueryRequest();
        }

        PageVO<AdminUserVO> result = userService.listUsersByAdmin(userQueryRequest);
        return ResultUtils.success(result, "获取用户列表成功");
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param userStatusUpdateRequest 用户状态更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    @Operation(summary = "更新用户状态", description = "管理员更新用户状态（启用/禁用）")
    @RequireAdmin
    @PutMapping("/{userId}/status")
    public BaseResponse<Boolean> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @RequestBody UserStatusUpdateRequest userStatusUpdateRequest,
            HttpServletRequest request) {
        
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        if (userStatusUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = userService.updateUserStatus(userId, userStatusUpdateRequest, request);
        return ResultUtils.success(result, "用户状态更新成功");
    }

    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param userRoleUpdateRequest 用户角色更新请求
     * @param request HTTP请求对象
     * @return 更新结果
     */
    @Operation(summary = "更新用户角色", description = "管理员更新用户角色")
    @RequireAdmin
    @PutMapping("/{userId}/role")
    public BaseResponse<Boolean> updateUserRole(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @RequestBody UserRoleUpdateRequest userRoleUpdateRequest,
            HttpServletRequest request) {
        
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        if (userRoleUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = userService.updateUserRole(userId, userRoleUpdateRequest, request);
        return ResultUtils.success(result, "用户角色更新成功");
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "管理员删除用户（逻辑删除）")
    @RequireAdmin
    @DeleteMapping("/{userId}")
    public BaseResponse<Boolean> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            HttpServletRequest request) {
        
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }

        Boolean result = userService.deleteUser(userId, request);
        return ResultUtils.success(result, "用户删除成功");
    }
}
