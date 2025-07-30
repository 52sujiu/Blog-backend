package com.sujiu.blog.model.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 标签创建/更新请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "标签创建/更新请求")
public class TagRequest implements Serializable {

    /**
     * 标签名称，1-50字符
     */
    @Schema(description = "标签名称，1-50字符", example = "Java", required = true)
    private String name;

    /**
     * 标签别名，URL友好
     */
    @Schema(description = "标签别名，URL友好", example = "java")
    private String slug;

    /**
     * 标签描述
     */
    @Schema(description = "标签描述", example = "Java编程语言相关内容")
    private String description;

    /**
     * 标签颜色，十六进制
     */
    @Schema(description = "标签颜色，十六进制格式", example = "#87d068")
    private String color;

    private static final long serialVersionUID = 1L;
}
