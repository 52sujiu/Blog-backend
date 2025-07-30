package com.sujiu.blog.model.dto.comment;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "评论查询请求")
public class CommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 文章ID
     */
    @Schema(description = "文章ID")
    private Long articleId;

    /**
     * 评论者ID
     */
    @Schema(description = "评论者ID")
    private Long authorId;

    /**
     * 父评论ID
     */
    @Schema(description = "父评论ID")
    private Long parentId;

    /**
     * 评论状态：0-待审核，1-已审核，2-已删除
     */
    @Schema(description = "评论状态：0-待审核，1-已审核，2-已删除")
    private Integer status;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词")
    private String keyword;

    /**
     * 排序方式：asc-升序，desc-降序
     */
    @Schema(description = "排序方式：asc-升序，desc-降序")
    private String sortOrder;

    private static final long serialVersionUID = 1L;
}
