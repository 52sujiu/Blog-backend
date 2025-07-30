package com.sujiu.blog.model.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 统计查询请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "统计查询请求")
public class StatisticsQueryRequest implements Serializable {

    /**
     * 统计类型：daily-按日，weekly-按周，monthly-按月
     */
    @Schema(description = "统计类型：daily-按日，weekly-按周，monthly-按月")
    private String type;

    /**
     * 统计天数，默认30天
     */
    @Schema(description = "统计天数，默认30天")
    private Integer days;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Date endTime;

    private static final long serialVersionUID = 1L;
}
