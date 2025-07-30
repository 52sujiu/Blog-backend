package com.sujiu.blog.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户信息更新请求 - 严格按照数据模型文档设计
 *
 * @author sujiu
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * 昵称，1-50字符
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日，格式：yyyy-MM-dd
     */
    private String birthday;

    /**
     * 个人简介，最多500字符
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

    private static final long serialVersionUID = 1L;
}