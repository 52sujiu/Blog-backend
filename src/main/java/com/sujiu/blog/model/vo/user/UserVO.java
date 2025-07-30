package com.sujiu.blog.model.vo.user;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户公开信息
 *
 * @author sujiu
 */
@Data
public class UserVO implements Serializable {

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
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 关注数
     */
    private Integer followingCount;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 注册时间
     */
    private Date createdTime;

    private static final long serialVersionUID = 1L;
}