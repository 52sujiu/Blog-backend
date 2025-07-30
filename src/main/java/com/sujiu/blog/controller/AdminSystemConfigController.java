package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.system.SystemConfigUpdateRequest;
import com.sujiu.blog.model.vo.system.SystemConfigVO;
import com.sujiu.blog.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员系统配置控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/admin/system")
@Slf4j
@Tag(name = "管理员系统配置", description = "管理员系统配置管理相关接口")
public class AdminSystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 获取系统配置（管理员）
     *
     * @return 系统配置
     */
    @Operation(summary = "获取系统配置", description = "管理员获取系统配置信息")
    @RequireAdmin
    @GetMapping("/config")
    public BaseResponse<SystemConfigVO> getSystemConfig() {
        log.info("管理员获取系统配置");
        
        SystemConfigVO systemConfig = systemConfigService.getSystemConfig();
        return ResultUtils.success(systemConfig, "获取系统配置成功");
    }

    /**
     * 更新系统配置
     *
     * @param updateRequest 更新请求
     * @param request HTTP请求对象
     * @return 更新后的系统配置
     */
    @Operation(summary = "更新系统配置", description = "管理员更新系统配置")
    @RequireAdmin
    @PutMapping("/config")
    public BaseResponse<SystemConfigVO> updateSystemConfig(
            @RequestBody SystemConfigUpdateRequest updateRequest,
            HttpServletRequest request) {

        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新请求不能为空");
        }

        // 验证是否有有效的更新字段
        if (isEmptyUpdateRequest(updateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "至少需要提供一个有效的配置项进行更新");
        }

        // 验证上传文件大小限制
        if (updateRequest.getUploadMaxSize() != null && updateRequest.getUploadMaxSize() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件上传最大大小必须大于0");
        }

        log.info("管理员更新系统配置，请求参数：{}", updateRequest);

        SystemConfigVO result = systemConfigService.updateSystemConfig(updateRequest, request);
        return ResultUtils.success(result, "系统配置更新成功");
    }

    /**
     * 检查更新请求是否为空
     */
    private boolean isEmptyUpdateRequest(SystemConfigUpdateRequest request) {
        return request.getSiteName() == null &&
               request.getSiteDescription() == null &&
               request.getSiteKeywords() == null &&
               request.getCommentNeedAudit() == null &&
               request.getArticleNeedAudit() == null &&
               request.getUploadMaxSize() == null &&
               request.getUserDefaultAvatar() == null;
    }

    /**
     * 刷新配置缓存
     *
     * @return 操作结果
     */
    @Operation(summary = "刷新配置缓存", description = "管理员手动刷新系统配置缓存")
    @RequireAdmin
    @PostMapping("/config/refresh")
    public BaseResponse<String> refreshConfigCache() {
        log.info("管理员刷新配置缓存");
        
        systemConfigService.refreshConfigCache();
        return ResultUtils.success("配置缓存刷新成功");
    }
}
