package com.sujiu.blog.model.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 访问统计数据
 *
 * @author sujiu
 */
@Data
@Schema(description = "访问统计数据")
public class ViewStatisticsVO implements Serializable {

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
         * 页面浏览量（PV）
         */
        @Schema(description = "页面浏览量（PV）")
        private Long pageViews;

        /**
         * 独立访客数（UV）
         */
        @Schema(description = "独立访客数（UV）")
        private Long uniqueVisitors;

        /**
         * 独立IP数
         */
        @Schema(description = "独立IP数")
        private Long uniqueIps;

        /**
         * 累计浏览量
         */
        @Schema(description = "累计浏览量")
        private Long totalViews;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
