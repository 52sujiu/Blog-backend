package com.sujiu.blog.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求 - 严格按照数据模型文档设计
 *
 * @author sujiu
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 用户名，3-20字符，字母数字下划线
     */
    private String username;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 密码，6-20字符
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    private static final long serialVersionUID = 1L;
}
