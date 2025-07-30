package com.sujiu.blog.model.dto.article;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员文章查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员文章查询请求")
public class AdminArticleQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词（标题、内容）
     */
    @Schema(description = "搜索关键词（标题、内容）")
    private String keyword;

    /**
     * 文章状态：0-草稿，1-审核中，2-已发布，3-已拒绝，4-已下架
     */
    @Schema(description = "文章状态：0-草稿，1-审核中，2-已发布，3-已拒绝，4-已下架")
    private Integer status;

    /**
     * 作者ID
     */
    @Schema(description = "作者ID")
    private Long authorId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 创建开始时间
     */
    @Schema(description = "创建开始时间")
    private Date startTime;

    /**
     * 创建结束时间
     */
    @Schema(description = "创建结束时间")
    private Date endTime;

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
     * 是否原创
     */
    @Schema(description = "是否原创")
    private Boolean isOriginal;

    private static final long serialVersionUID = 1L;
}
