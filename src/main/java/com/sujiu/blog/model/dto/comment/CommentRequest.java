package com.sujiu.blog.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * 评论创建请求
 *
 * @author sujiu
 */
@Data
@Schema(description = "评论创建请求")
public class CommentRequest implements Serializable {

    /**
     * 评论内容，1-1000字符
     */
    @Schema(description = "评论内容，1-1000字符", example = "这是一条评论", required = true)
    private String content;

    /**
     * 文章ID
     */
    @Schema(description = "文章ID", example = "1", required = true)
    private Long articleId;

    /**
     * 父评论ID，0为顶级评论
     */
    @Schema(description = "父评论ID，0为顶级评论", example = "0")
    private Long parentId;

    /**
     * 回复目标评论ID
     */
    @Schema(description = "回复目标评论ID", example = "null")
    private Long replyToId;

    private static final long serialVersionUID = 1L;
}
