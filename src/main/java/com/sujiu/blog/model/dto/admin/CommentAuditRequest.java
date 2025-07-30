package com.sujiu.blog.model.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 评论审核请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "评论审核请求")
public class CommentAuditRequest implements Serializable {

    /**
     * 审核状态：1-通过，2-拒绝
     */
    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态：1-通过，2-拒绝", required = true)
    private Integer status;

    /**
     * 审核原因
     */
    @Schema(description = "审核原因")
    private String auditReason;

    private static final long serialVersionUID = 1L;
}
