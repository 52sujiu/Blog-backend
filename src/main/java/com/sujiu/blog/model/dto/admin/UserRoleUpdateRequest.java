package com.sujiu.blog.model.dto.admin;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户角色更新请求
 *
 * @author sujiu
 */
@Data
public class UserRoleUpdateRequest implements Serializable {

    /**
     * 用户角色：user, admin, ban
     */
    private String role;

    private static final long serialVersionUID = 1L;
}
