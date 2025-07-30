package com.sujiu.blog.model.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章统计数据
 *
 * @author sujiu
 */
@Data
@Schema(description = "文章统计数据")
public class ArticleStatisticsVO implements Serializable {

    /**
     * 统计项目列表
     */
    @Schema(description = "统计项目列表")
    private List<StatisticsItem> items;

    /**
     * 统计项目
     */
    @Data
    @Schema(description = "统计项目")
    public static class StatisticsItem implements Serializable {

        /**
         * 日期（格式：yyyy-MM-dd）
         */
        @Schema(description = "日期")
        private String date;

        /**
         * 新增文章数
         */
        @Schema(description = "新增文章数")
        private Long newArticleCount;

        /**
         * 发布文章数
         */
        @Schema(description = "发布文章数")
        private Long publishedArticleCount;

        /**
         * 累计文章数
         */
        @Schema(description = "累计文章数")
        private Long totalArticleCount;

        /**
         * 总浏览量
         */
        @Schema(description = "总浏览量")
        private Long totalViewCount;

        /**
         * 总点赞数
         */
        @Schema(description = "总点赞数")
        private Long totalLikeCount;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
