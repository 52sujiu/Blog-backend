package com.sujiu.blog.model.dto.article;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文章查询请求")
public class ArticleQueryRequest extends PageRequest implements Serializable {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 标签ID
     */
    @Schema(description = "标签ID")
    private Long tagId;

    /**
     * 作者ID
     */
    @Schema(description = "作者ID")
    private Long authorId;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 文章状态
     */
    @Schema(description = "文章状态：0-草稿，1-审核中，2-已发布")
    private Integer status;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Boolean isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Boolean isRecommend;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段：publishedTime-发布时间，viewCount-浏览量，likeCount-点赞数")
    private String sortField;

    /**
     * 排序方式
     */
    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}
