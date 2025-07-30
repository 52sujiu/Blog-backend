package com.sujiu.blog.model.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文章下架请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "文章下架请求")
public class ArticleOfflineRequest implements Serializable {

    /**
     * 下架原因
     */
    @NotBlank(message = "下架原因不能为空")
    @Schema(description = "下架原因", required = true)
    private String reason;

    private static final long serialVersionUID = 1L;
}
