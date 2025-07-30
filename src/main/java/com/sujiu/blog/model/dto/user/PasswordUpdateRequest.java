package com.sujiu.blog.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 密码修改请求
 *
 * @author sujiu
 */
@Data
public class PasswordUpdateRequest implements Serializable {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}
