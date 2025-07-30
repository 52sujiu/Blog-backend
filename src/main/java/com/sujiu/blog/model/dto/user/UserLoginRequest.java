package com.sujiu.blog.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户登录请求 - 严格按照数据模型文档设计
 *
 * @author sujiu
 */
@Data
public class UserLoginRequest implements Serializable {

    /**
     * 账号（用户名或邮箱）
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否记住登录，默认false
     */
    private Boolean rememberMe;

    private static final long serialVersionUID = 1L;
}
