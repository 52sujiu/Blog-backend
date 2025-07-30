package com.sujiu.blog.controller;

import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.model.vo.system.SystemConfigVO;
import com.sujiu.blog.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 系统配置控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/system")
@Slf4j
@Tag(name = "系统配置", description = "系统配置相关接口")
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    @Operation(summary = "获取系统配置", description = "获取系统的基本配置信息，无需登录")
    @GetMapping("/config")
    public BaseResponse<SystemConfigVO> getSystemConfig() {
        log.info("获取系统配置");
        
        SystemConfigVO systemConfig = systemConfigService.getSystemConfig();
        return ResultUtils.success(systemConfig, "获取系统配置成功");
    }
}
