package com.sujiu.blog.model.vo.admin;

import java.io.Serializable;
import lombok.Data;

/**
 * 系统概览
 *
 * @author sujiu
 */
@Data
public class DashboardOverviewVO implements Serializable {

    /**
     * 总用户数
     */
    private Long userCount;

    /**
     * 总文章数
     */
    private Long articleCount;

    /**
     * 总评论数
     */
    private Long commentCount;

    /**
     * 总浏览量
     */
    private Long viewCount;

    /**
     * 今日新增用户
     */
    private Long todayUserCount;

    /**
     * 今日新增文章
     */
    private Long todayArticleCount;

    /**
     * 今日新增评论
     */
    private Long todayCommentCount;

    /**
     * 今日浏览量
     */
    private Long todayViewCount;

    private static final long serialVersionUID = 1L;
}
