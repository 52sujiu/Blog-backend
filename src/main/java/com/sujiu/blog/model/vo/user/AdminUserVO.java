package com.sujiu.blog.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员用户视图对象
 *
 * @author sujiu
 */
@Data
@Schema(description = "管理员用户视图对象")
public class AdminUserVO implements Serializable {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;

    /**
     * 生日
     */
    @Schema(description = "生日")
    private Date birthday;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    private String bio;

    /**
     * 个人网站
     */
    @Schema(description = "个人网站")
    private String website;

    /**
     * 所在地
     */
    @Schema(description = "所在地")
    private String location;

    /**
     * 用户角色：user-普通用户，admin-管理员，ban-封禁用户
     */
    @Schema(description = "用户角色：user-普通用户，admin-管理员，ban-封禁用户")
    private String role;

    /**
     * 用户状态：1-正常，0-禁用，-1-删除
     */
    @Schema(description = "用户状态：1-正常，0-禁用，-1-删除")
    private Integer status;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updatedTime;

    /**
     * 文章数量
     */
    @Schema(description = "文章数量")
    private Integer articleCount;

    /**
     * 关注数
     */
    @Schema(description = "关注数")
    private Integer followingCount;

    /**
     * 粉丝数
     */
    @Schema(description = "粉丝数")
    private Integer followerCount;

    private static final long serialVersionUID = 1L;
}
