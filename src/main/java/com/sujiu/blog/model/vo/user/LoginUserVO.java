package com.sujiu.blog.model.vo.user;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 登录用户信息
 *
 * @author sujiu
 */
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 所在地
     */
    private String location;

    /**
     * 角色：user, admin
     */
    private String role;

    /**
     * 注册时间
     */
    private Date createdTime;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    private static final long serialVersionUID = 1L;
}