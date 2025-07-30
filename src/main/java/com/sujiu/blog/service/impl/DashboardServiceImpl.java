package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.CommentMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.admin.StatisticsQueryRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.Comment;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.admin.ArticleStatisticsVO;
import com.sujiu.blog.model.vo.admin.DashboardOverviewVO;
import com.sujiu.blog.model.vo.admin.UserStatisticsVO;
import com.sujiu.blog.model.vo.admin.ViewStatisticsVO;
import com.sujiu.blog.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 仪表板服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public DashboardOverviewVO getDashboardOverview() {
        log.info("获取系统概览数据");

        DashboardOverviewVO overview = new DashboardOverviewVO();

        // 获取总数统计
        overview.setUserCount(getUserTotalCount());
        overview.setArticleCount(getArticleTotalCount());
        overview.setCommentCount(getCommentTotalCount());
        overview.setViewCount(getViewTotalCount());

        // 获取今日统计
        overview.setTodayUserCount(getTodayUserCount());
        overview.setTodayArticleCount(getTodayArticleCount());
        overview.setTodayCommentCount(getTodayCommentCount());
        overview.setTodayViewCount(getTodayViewCount());

        log.info("系统概览数据获取成功：用户总数={}, 文章总数={}, 评论总数={}, 浏览总数={}",
                overview.getUserCount(), overview.getArticleCount(),
                overview.getCommentCount(), overview.getViewCount());

        return overview;
    }

    @Override
    public UserStatisticsVO getUserStatistics(StatisticsQueryRequest request) {
        log.info("获取用户统计数据，请求参数：{}", request);

        // 参数校验和默认值设置
        if (request == null) {
            request = new StatisticsQueryRequest();
        }
        if (request.getDays() == null || request.getDays() <= 0) {
            request.setDays(30);
        }

        UserStatisticsVO statistics = new UserStatisticsVO();
        List<UserStatisticsVO.StatisticsItem> items = new ArrayList<>();

        // 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(request.getDays() - 1);

        // 按日期循环统计
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            UserStatisticsVO.StatisticsItem item = new UserStatisticsVO.StatisticsItem();
            item.setDate(date.format(DATE_FORMATTER));

            // 获取当日新增用户数
            item.setNewUserCount(getNewUserCountByDate(date));

            // 获取当日活跃用户数（登录用户数）
            item.setActiveUserCount(getActiveUserCountByDate(date));

            // 获取截止当日的累计用户数
            item.setTotalUserCount(getTotalUserCountByDate(date));

            items.add(item);
        }

        statistics.setItems(items);
        log.info("用户统计数据获取成功，共{}天数据", items.size());

        return statistics;
    }

    @Override
    public ArticleStatisticsVO getArticleStatistics(StatisticsQueryRequest request) {
        log.info("获取文章统计数据，请求参数：{}", request);

        // 参数校验和默认值设置
        if (request == null) {
            request = new StatisticsQueryRequest();
        }
        if (request.getDays() == null || request.getDays() <= 0) {
            request.setDays(30);
        }

        ArticleStatisticsVO statistics = new ArticleStatisticsVO();
        List<ArticleStatisticsVO.StatisticsItem> items = new ArrayList<>();

        // 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(request.getDays() - 1);

        // 按日期循环统计
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            ArticleStatisticsVO.StatisticsItem item = new ArticleStatisticsVO.StatisticsItem();
            item.setDate(date.format(DATE_FORMATTER));

            // 获取当日新增文章数
            item.setNewArticleCount(getNewArticleCountByDate(date));

            // 获取当日发布文章数
            item.setPublishedArticleCount(getPublishedArticleCountByDate(date));

            // 获取截止当日的累计文章数
            item.setTotalArticleCount(getTotalArticleCountByDate(date));

            // 获取截止当日的总浏览量
            item.setTotalViewCount(getTotalViewCountByDate(date));

            // 获取截止当日的总点赞数
            item.setTotalLikeCount(getTotalLikeCountByDate(date));

            items.add(item);
        }

        statistics.setItems(items);
        log.info("文章统计数据获取成功，共{}天数据", items.size());

        return statistics;
    }

    @Override
    public ViewStatisticsVO getViewStatistics(StatisticsQueryRequest request) {
        log.info("获取访问统计数据，请求参数：{}", request);

        // 参数校验和默认值设置
        if (request == null) {
            request = new StatisticsQueryRequest();
        }
        if (request.getDays() == null || request.getDays() <= 0) {
            request.setDays(30);
        }

        ViewStatisticsVO statistics = new ViewStatisticsVO();
        List<ViewStatisticsVO.StatisticsItem> items = new ArrayList<>();

        // 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(request.getDays() - 1);

        // 按日期循环统计
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            ViewStatisticsVO.StatisticsItem item = new ViewStatisticsVO.StatisticsItem();
            item.setDate(date.format(DATE_FORMATTER));

            // 获取当日页面浏览量（PV）
            item.setPageViews(getPageViewsByDate(date));

            // 获取当日独立访客数（UV）
            item.setUniqueVisitors(getUniqueVisitorsByDate(date));

            // 获取当日独立IP数
            item.setUniqueIps(getUniqueIpsByDate(date));

            // 获取截止当日的累计浏览量
            item.setTotalViews(getTotalViewsByDate(date));

            items.add(item);
        }

        statistics.setItems(items);
        log.info("访问统计数据获取成功，共{}天数据", items.size());

        return statistics;
    }

    // ==================== 私有方法：总数统计 ====================

    /**
     * 获取用户总数
     */
    private Long getUserTotalCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只统计正常状态的用户
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 获取文章总数
     */
    private Long getArticleTotalCount() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2); // 只统计已发布的文章
        return articleMapper.selectCount(queryWrapper);
    }

    /**
     * 获取评论总数
     */
    private Long getCommentTotalCount() {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只统计已审核的评论
        return commentMapper.selectCount(queryWrapper);
    }

    /**
     * 获取浏览总数
     */
    private Long getViewTotalCount() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2); // 只统计已发布的文章

        List<Article> articles = articleMapper.selectList(queryWrapper);
        if (articles == null || articles.isEmpty()) {
            return 0L;
        }

        return articles.stream()
                .filter(article -> article != null)
                .mapToLong(article -> article.getViewCount() != null ? article.getViewCount() : 0)
                .sum();
    }

    // ==================== 私有方法：今日统计 ====================

    /**
     * 获取今日新增用户数
     */
    private Long getTodayUserCount() {
        return getNewUserCountByDate(LocalDate.now());
    }

    /**
     * 获取今日新增文章数
     */
    private Long getTodayArticleCount() {
        return getNewArticleCountByDate(LocalDate.now());
    }

    /**
     * 获取今日新增评论数
     */
    private Long getTodayCommentCount() {
        return getNewCommentCountByDate(LocalDate.now());
    }

    /**
     * 获取今日浏览量
     */
    private Long getTodayViewCount() {
        return getPageViewsByDate(LocalDate.now());
    }

    // ==================== 私有方法：按日期统计 ====================

    /**
     * 获取指定日期新增用户数
     */
    private Long getNewUserCountByDate(LocalDate date) {
        Date startTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("created_time", startTime)
                   .lt("created_time", endTime);
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定日期活跃用户数（登录用户数）
     */
    private Long getActiveUserCountByDate(LocalDate date) {
        Date startTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("last_login_time", startTime)
                   .lt("last_login_time", endTime);
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 获取截止指定日期的累计用户数
     */
    private Long getTotalUserCountByDate(LocalDate date) {
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("created_time", endTime)
                   .eq("status", 1); // 只统计正常状态的用户
        return userMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定日期新增文章数
     */
    private Long getNewArticleCountByDate(LocalDate date) {
        Date startTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("created_time", startTime)
                   .lt("created_time", endTime);
        return articleMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定日期发布文章数
     */
    private Long getPublishedArticleCountByDate(LocalDate date) {
        Date startTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("published_time", startTime)
                   .lt("published_time", endTime)
                   .eq("status", 2); // 已发布状态
        return articleMapper.selectCount(queryWrapper);
    }

    /**
     * 获取截止指定日期的累计文章数
     */
    private Long getTotalArticleCountByDate(LocalDate date) {
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("created_time", endTime)
                   .eq("status", 2); // 只统计已发布的文章
        return articleMapper.selectCount(queryWrapper);
    }

    /**
     * 获取截止指定日期的总浏览量
     */
    private Long getTotalViewCountByDate(LocalDate date) {
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("created_time", endTime)
                   .eq("status", 2); // 只统计已发布的文章

        List<Article> articles = articleMapper.selectList(queryWrapper);
        return articles.stream()
                .mapToLong(article -> article.getViewCount() != null ? article.getViewCount() : 0)
                .sum();
    }

    /**
     * 获取截止指定日期的总点赞数
     */
    private Long getTotalLikeCountByDate(LocalDate date) {
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("created_time", endTime)
                   .eq("status", 2); // 只统计已发布的文章

        List<Article> articles = articleMapper.selectList(queryWrapper);
        return articles.stream()
                .mapToLong(article -> article.getLikeCount() != null ? article.getLikeCount() : 0)
                .sum();
    }

    /**
     * 获取指定日期新增评论数
     */
    private Long getNewCommentCountByDate(LocalDate date) {
        Date startTime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("created_time", startTime)
                   .lt("created_time", endTime)
                   .eq("status", 1); // 只统计已审核的评论
        return commentMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定日期页面浏览量（PV）
     * 注意：这里简化处理，实际应该从blog_article_view表统计
     */
    private Long getPageViewsByDate(LocalDate date) {
        // 由于没有详细的浏览记录表查询，这里返回0
        // 在实际项目中，应该查询blog_article_view表
        return 0L;
    }

    /**
     * 获取指定日期独立访客数（UV）
     * 注意：这里简化处理，实际应该从blog_article_view表统计
     */
    private Long getUniqueVisitorsByDate(LocalDate date) {
        // 由于没有详细的浏览记录表查询，这里返回0
        // 在实际项目中，应该查询blog_article_view表按user_id去重统计
        return 0L;
    }

    /**
     * 获取指定日期独立IP数
     * 注意：这里简化处理，实际应该从blog_article_view表统计
     */
    private Long getUniqueIpsByDate(LocalDate date) {
        // 由于没有详细的浏览记录表查询，这里返回0
        // 在实际项目中，应该查询blog_article_view表按ip_address去重统计
        return 0L;
    }

    /**
     * 获取截止指定日期的累计浏览量
     */
    private Long getTotalViewsByDate(LocalDate date) {
        return getTotalViewCountByDate(date);
    }
}
