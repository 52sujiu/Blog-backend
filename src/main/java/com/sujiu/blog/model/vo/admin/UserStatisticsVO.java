package com.sujiu.blog.model.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户统计数据
 *
 * @author sujiu
 */
@Data
@Schema(description = "用户统计数据")
public class UserStatisticsVO implements Serializable {

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
         * 新增用户数
         */
        @Schema(description = "新增用户数")
        private Long newUserCount;

        /**
         * 活跃用户数
         */
        @Schema(description = "活跃用户数")
        private Long activeUserCount;

        /**
         * 累计用户数
         */
        @Schema(description = "累计用户数")
        private Long totalUserCount;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
