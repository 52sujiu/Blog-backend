package com.sujiu.blog.model.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 评论信息
 *
 * @author sujiu
 */
@Data
@Schema(description = "评论信息")
public class CommentVO implements Serializable {

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
     * 回复目标（如果是回复）
     */
    @Schema(description = "回复目标信息")
    private ReplyToInfo replyTo;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Integer likeCount;

    /**
     * 当前用户是否已点赞
     */
    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    /**
     * 状态：0-待审核，1-已审核
     */
    @Schema(description = "状态：0-待审核，1-已审核")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createdTime;

    /**
     * 回复列表
     */
    @Schema(description = "回复列表")
    private List<ReplyComment> replies;

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
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "回复目标信息")
    public static class ReplyToInfo implements Serializable {
        @Schema(description = "用户ID")
        private Long id;
        @Schema(description = "昵称")
        private String nickname;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "回复评论信息")
    public static class ReplyComment implements Serializable {
        @Schema(description = "评论ID")
        private Long id;
        @Schema(description = "评论内容")
        private String content;
        @Schema(description = "评论者信息")
        private AuthorInfo author;
        @Schema(description = "回复目标信息")
        private ReplyToInfo replyTo;
        @Schema(description = "点赞次数")
        private Integer likeCount;
        @Schema(description = "当前用户是否已点赞")
        private Boolean isLiked;
        @Schema(description = "创建时间")
        private Date createdTime;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
