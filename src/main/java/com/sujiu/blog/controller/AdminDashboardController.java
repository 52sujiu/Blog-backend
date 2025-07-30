package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.model.dto.admin.StatisticsQueryRequest;
import com.sujiu.blog.model.vo.admin.ArticleStatisticsVO;
import com.sujiu.blog.model.vo.admin.DashboardOverviewVO;
import com.sujiu.blog.model.vo.admin.UserStatisticsVO;
import com.sujiu.blog.model.vo.admin.ViewStatisticsVO;
import com.sujiu.blog.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 管理员仪表板控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Tag(name = "管理员仪表板", description = "管理员仪表板和系统统计相关接口")
public class AdminDashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取系统概览
     *
     * @return 系统概览数据
     */
    @Operation(summary = "获取系统概览", description = "获取系统总体数据概览，包括用户、文章、评论、浏览量等统计信息")
    @RequireAdmin
    @GetMapping("/dashboard/overview")
    public BaseResponse<DashboardOverviewVO> getDashboardOverview() {
        log.info("管理员获取系统概览数据");
        
        DashboardOverviewVO overview = dashboardService.getDashboardOverview();
        return ResultUtils.success(overview, "获取系统概览成功");
    }

    /**
     * 获取用户统计
     *
     * @param type 统计类型（daily, weekly, monthly）
     * @param days 统计天数，默认30
     * @return 用户统计数据
     */
    @Operation(summary = "获取用户统计", description = "获取用户相关统计数据，支持按时间范围查询")
    @RequireAdmin
    @GetMapping("/statistics/users")
    public BaseResponse<UserStatisticsVO> getUserStatistics(
            @Parameter(description = "统计类型：daily-按日，weekly-按周，monthly-按月") 
            @RequestParam(value = "type", required = false, defaultValue = "daily") String type,
            @Parameter(description = "统计天数，默认30天") 
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        
        log.info("管理员获取用户统计数据，类型：{}，天数：{}", type, days);
        
        StatisticsQueryRequest request = new StatisticsQueryRequest();
        request.setType(type);
        request.setDays(days);
        
        UserStatisticsVO statistics = dashboardService.getUserStatistics(request);
        return ResultUtils.success(statistics, "获取用户统计成功");
    }

    /**
     * 获取文章统计
     *
     * @param type 统计类型（daily, weekly, monthly）
     * @param days 统计天数，默认30
     * @return 文章统计数据
     */
    @Operation(summary = "获取文章统计", description = "获取文章相关统计数据，支持按时间范围查询")
    @RequireAdmin
    @GetMapping("/statistics/articles")
    public BaseResponse<ArticleStatisticsVO> getArticleStatistics(
            @Parameter(description = "统计类型：daily-按日，weekly-按周，monthly-按月") 
            @RequestParam(value = "type", required = false, defaultValue = "daily") String type,
            @Parameter(description = "统计天数，默认30天") 
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        
        log.info("管理员获取文章统计数据，类型：{}，天数：{}", type, days);
        
        StatisticsQueryRequest request = new StatisticsQueryRequest();
        request.setType(type);
        request.setDays(days);
        
        ArticleStatisticsVO statistics = dashboardService.getArticleStatistics(request);
        return ResultUtils.success(statistics, "获取文章统计成功");
    }

    /**
     * 获取访问统计
     *
     * @param type 统计类型（daily, weekly, monthly）
     * @param days 统计天数，默认30
     * @return 访问统计数据
     */
    @Operation(summary = "获取访问统计", description = "获取网站访问相关统计数据，支持按时间范围查询")
    @RequireAdmin
    @GetMapping("/statistics/views")
    public BaseResponse<ViewStatisticsVO> getViewStatistics(
            @Parameter(description = "统计类型：daily-按日，weekly-按周，monthly-按月") 
            @RequestParam(value = "type", required = false, defaultValue = "daily") String type,
            @Parameter(description = "统计天数，默认30天") 
            @RequestParam(value = "days", required = false, defaultValue = "30") Integer days) {
        
        log.info("管理员获取访问统计数据，类型：{}，天数：{}", type, days);
        
        StatisticsQueryRequest request = new StatisticsQueryRequest();
        request.setType(type);
        request.setDays(days);
        
        ViewStatisticsVO statistics = dashboardService.getViewStatistics(request);
        return ResultUtils.success(statistics, "获取访问统计成功");
    }
}
