package com.sujiu.blog.model.dto.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "搜索请求")
public class SearchRequest implements Serializable {

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词", example = "Spring Boot")
    private String keyword;

    /**
     * 搜索类型
     */
    @Schema(description = "搜索类型：article-文章，user-用户，tag-标签，category-分类，all-全部",
            example = "article")
    private String type;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码，默认1", example = "1")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小，默认10", example = "10")
    private Integer size = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段：relevance-相关性，time-时间，views-浏览量",
            example = "relevance")
    private String sortField = "relevance";

    /**
     * 排序方式
     */
    @Schema(description = "排序方式：asc-升序，desc-降序", example = "desc")
    private String sortOrder = "desc";

    private static final long serialVersionUID = 1L;
}
