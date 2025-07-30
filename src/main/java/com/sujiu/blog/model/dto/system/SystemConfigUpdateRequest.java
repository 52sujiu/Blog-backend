package com.sujiu.blog.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统配置更新请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "系统配置更新请求")
public class SystemConfigUpdateRequest implements Serializable {

    /**
     * 网站名称
     */
    @Schema(description = "网站名称", example = "新的网站名称")
    @Size(max = 100, message = "网站名称不能超过100个字符")
    private String siteName;

    /**
     * 网站描述
     */
    @Schema(description = "网站描述", example = "新的网站描述")
    @Size(max = 500, message = "网站描述不能超过500个字符")
    private String siteDescription;

    /**
     * 网站关键词
     */
    @Schema(description = "网站关键词", example = "博客,技术,分享")
    @Size(max = 200, message = "网站关键词不能超过200个字符")
    private String siteKeywords;

    /**
     * 评论是否需要审核
     */
    @Schema(description = "评论是否需要审核", example = "false")
    private Boolean commentNeedAudit;

    /**
     * 文章是否需要审核
     */
    @Schema(description = "文章是否需要审核", example = "true")
    private Boolean articleNeedAudit;

    /**
     * 文件上传最大大小
     */
    @Schema(description = "文件上传最大大小（字节）", example = "20971520")
    @Min(value = 1024, message = "文件上传最大大小不能小于1KB")
    private Long uploadMaxSize;

    /**
     * 用户默认头像
     */
    @Schema(description = "用户默认头像URL", example = "/static/images/new-default-avatar.png")
    @Size(max = 255, message = "用户默认头像URL不能超过255个字符")
    private String userDefaultAvatar;

    private static final long serialVersionUID = 1L;
}
