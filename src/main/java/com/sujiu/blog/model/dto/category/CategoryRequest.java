package com.sujiu.blog.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 分类创建/更新请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "分类创建/更新请求")
public class CategoryRequest implements Serializable {

    /**
     * 分类名称，1-50字符
     */
    @Schema(description = "分类名称，1-50字符", example = "技术分享", required = true)
    private String name;

    /**
     * 分类别名，URL友好
     */
    @Schema(description = "分类别名，URL友好", example = "tech")
    private String slug;

    /**
     * 分类描述
     */
    @Schema(description = "分类描述", example = "技术相关的文章分享")
    private String description;

    /**
     * 分类封面图
     */
    @Schema(description = "分类封面图URL", example = "http://example.com/cover.jpg")
    private String coverImage;

    /**
     * 分类颜色，十六进制
     */
    @Schema(description = "分类颜色，十六进制格式", example = "#1890ff")
    private String color;

    /**
     * 父分类ID，0为顶级分类
     */
    @Schema(description = "父分类ID，0为顶级分类", example = "0")
    private Long parentId;

    /**
     * 排序值
     */
    @Schema(description = "排序值，数值越小越靠前", example = "1")
    private Integer sortOrder;

    private static final long serialVersionUID = 1L;
}
