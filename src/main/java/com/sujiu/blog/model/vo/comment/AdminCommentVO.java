package com.sujiu.blog.model.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员评论视图对象
 *
 * @author sujiu
 */
@Data
@Schema(description = "管理员评论视图对象")
public class AdminCommentVO implements Serializable {

    /**
     * 评论ID
     */
    @Schema(description = "评论ID")
    private Long id;

    /**
     * 评论内容
     */
    @Schema(description = "评论内容")
    private String content;

    /**
     * 文章信息
     */
    @Schema(description = "文章信息")
    private ArticleInfo article;

    /**
     * 评论者信息
     */
    @Schema(description = "评论者信息")
    private AuthorInfo author;

    /**
     * 父评论ID
     */
    @Schema(description = "父评论ID")
    private Long parentId;

    /**
     * 回复目标评论ID
     */
    @Schema(description = "回复目标评论ID")
    private Long replyToId;

    /**
     * 回复目标用户信息
     */
    @Schema(description = "回复目标用户信息")
    private ReplyToInfo replyTo;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Integer likeCount;

    /**
     * 状态：0-待审核，1-已审核，2-已删除
     */
    @Schema(description = "状态：0-待审核，1-已审核，2-已删除")
    private Integer status;

    /**
     * 审核原因
     */
    @Schema(description = "审核原因")
    private String auditReason;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    @Data
    @Schema(description = "文章信息")
    public static class ArticleInfo implements Serializable {
        @Schema(description = "文章ID")
        private Long id;
        @Schema(description = "文章标题")
        private String title;
        @Schema(description = "文章状态")
        private Integer status;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "评论者信息")
    public static class AuthorInfo implements Serializable {
        @Schema(description = "用户ID")
        private Long id;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "头像")
        private String avatar;
        @Schema(description = "邮箱")
        private String email;
        @Schema(description = "用户状态")
        private Integer status;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "回复目标信息")
    public static class ReplyToInfo implements Serializable {
        @Schema(description = "用户ID")
        private Long id;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "昵称")
        private String nickname;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
