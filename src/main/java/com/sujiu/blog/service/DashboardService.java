package com.sujiu.blog.service;

import com.sujiu.blog.model.dto.admin.StatisticsQueryRequest;
import com.sujiu.blog.model.vo.admin.ArticleStatisticsVO;
import com.sujiu.blog.model.vo.admin.DashboardOverviewVO;
import com.sujiu.blog.model.vo.admin.UserStatisticsVO;
import com.sujiu.blog.model.vo.admin.ViewStatisticsVO;

/**
 * 仪表板服务
 *
 * @author sujiu
 */
public interface DashboardService {

    /**
     * 获取系统概览数据
     *
     * @return 系统概览数据
     */
    DashboardOverviewVO getDashboardOverview();

    /**
     * 获取用户统计数据
     *
     * @param request 统计查询请求
     * @return 用户统计数据
     */
    UserStatisticsVO getUserStatistics(StatisticsQueryRequest request);

    /**
     * 获取文章统计数据
     *
     * @param request 统计查询请求
     * @return 文章统计数据
     */
    ArticleStatisticsVO getArticleStatistics(StatisticsQueryRequest request);

    /**
     * 获取访问统计数据
     *
     * @param request 统计查询请求
     * @return 访问统计数据
     */
    ViewStatisticsVO getViewStatistics(StatisticsQueryRequest request);
}
