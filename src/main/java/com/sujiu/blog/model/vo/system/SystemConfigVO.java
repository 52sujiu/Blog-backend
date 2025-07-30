package com.sujiu.blog.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置
 *
 * @author sujiu
 */
@Data
@Schema(description = "系统配置")
public class SystemConfigVO implements Serializable {

    /**
     * 网站名称
     */
    @Schema(description = "网站名称", example = "个人博客系统")
    private String siteName;

    /**
     * 网站描述
     */
    @Schema(description = "网站描述", example = "一个基于Spring Boot的个人博客系统")
    private String siteDescription;

    /**
     * 网站关键词
     */
    @Schema(description = "网站关键词", example = "博客,技术分享,个人网站")
    private String siteKeywords;

    /**
     * 评论是否需要审核
     */
    @Schema(description = "评论是否需要审核", example = "true")
    private Boolean commentNeedAudit;

    /**
     * 文章是否需要审核
     */
    @Schema(description = "文章是否需要审核", example = "false")
    private Boolean articleNeedAudit;

    /**
     * 文件上传最大大小
     */
    @Schema(description = "文件上传最大大小（字节）", example = "10485760")
    private Long uploadMaxSize;

    /**
     * 用户默认头像
     */
    @Schema(description = "用户默认头像URL", example = "/static/images/default-avatar.png")
    private String userDefaultAvatar;

    private static final long serialVersionUID = 1L;
}
