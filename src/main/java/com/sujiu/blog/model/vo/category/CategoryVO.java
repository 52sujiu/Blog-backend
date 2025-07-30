package com.sujiu.blog.model.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 分类信息
 *
 * @author sujiu
 */
@Data
@Schema(description = "分类信息")
public class CategoryVO implements Serializable {

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 分类别名
     */
    @Schema(description = "分类别名")
    private String slug;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述")
    private String description;

    /**
     * 分类封面图
     */
    @Schema(description = "分类封面图URL")
    private String coverImage;

    /**
     * 分类颜色
     */
    @Schema(description = "分类颜色")
    private String color;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID")
    private Long parentId;

    /**
     * 排序值
     */
    @Schema(description = "排序值")
    private Integer sortOrder;

    /**
     * 文章数量
     */
    @Schema(description = "文章数量")
    private Integer articleCount;

    /**
     * 状态：1-正常，0-禁用
     */
    @Schema(description = "状态：1-正常，0-禁用")
    private Integer status;

    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<ChildCategory> children;

    @Data
    @Schema(description = "子分类信息")
    public static class ChildCategory implements Serializable {
        @Schema(description = "分类ID")
        private Long id;
        @Schema(description = "分类名称")
        private String name;
        @Schema(description = "分类别名")
        private String slug;
        @Schema(description = "文章数量")
        private Integer articleCount;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
