package com.sujiu.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户实体 - 严格按照数据模型文档设计
 *
 * @author sujiu
 */
@TableName(value = "sys_user")
@Data
public class User implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名，3-20字符，字母数字下划线
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 昵称，1-50字符
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 手机号，11位数字
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日，格式：yyyy-MM-dd
     */
    private Date birthday;

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

    /**
     * 用户角色：user, admin, ban
     */
    private String role;

    /**
     * 用户状态：1-正常，0-禁用，-1-删除
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private Date lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Date updatedTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}