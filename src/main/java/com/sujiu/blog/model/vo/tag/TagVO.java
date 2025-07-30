package com.sujiu.blog.model.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 标签信息
 *
 * @author sujiu
 */
@Data
@Schema(description = "标签信息")
public class TagVO implements Serializable {

    /**
     * 标签ID
     */
    @Schema(description = "标签ID")
    private Long id;

    /**
     * 标签名称
     */
    @Schema(description = "标签名称")
    private String name;

    /**
     * 标签别名
     */
    @Schema(description = "标签别名")
    private String slug;

    /**
     * 标签描述
     */
    @Schema(description = "标签描述")
    private String description;

    /**
     * 标签颜色
     */
    @Schema(description = "标签颜色")
    private String color;

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

    private static final long serialVersionUID = 1L;
}
