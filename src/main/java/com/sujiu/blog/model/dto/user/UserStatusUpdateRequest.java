package com.sujiu.blog.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户状态更新请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "用户状态更新请求")
public class UserStatusUpdateRequest implements Serializable {

    /**
     * 用户状态：1-正常，0-禁用
     */
    @NotNull(message = "用户状态不能为空")
    @Schema(description = "用户状态：1-正常，0-禁用", required = true)
    private Integer status;

    /**
     * 操作原因
     */
    @Schema(description = "操作原因")
    private String reason;

    private static final long serialVersionUID = 1L;
}
