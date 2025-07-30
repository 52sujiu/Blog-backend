package com.sujiu.blog.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户角色更新请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "用户角色更新请求")
public class UserRoleUpdateRequest implements Serializable {

    /**
     * 用户角色：user-普通用户，admin-管理员，ban-封禁用户
     */
    @NotBlank(message = "用户角色不能为空")
    @Schema(description = "用户角色：user-普通用户，admin-管理员，ban-封禁用户", required = true)
    private String role;

    private static final long serialVersionUID = 1L;
}
