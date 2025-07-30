package com.sujiu.blog.model.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文章审核请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "文章审核请求")
public class ArticleAuditRequest implements Serializable {

    /**
     * 审核状态：2-审核通过（发布），3-审核拒绝
     */
    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态：2-审核通过（发布），3-审核拒绝", required = true)
    private Integer status;

    /**
     * 审核原因
     */
    @Schema(description = "审核原因")
    private String auditReason;

    private static final long serialVersionUID = 1L;
}
