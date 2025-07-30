package com.sujiu.blog.model.dto.comment;

import com.sujiu.blog.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员评论查询请求
 *
 * @author sujiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "管理员评论查询请求")
public class AdminCommentQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词（评论内容）
     */
    @Schema(description = "搜索关键词（评论内容）")
    private String keyword;

    /**
     * 评论状态：0-待审核，1-已审核，2-已删除
     */
    @Schema(description = "评论状态：0-待审核，1-已审核，2-已删除")
    private Integer status;

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
     * 创建开始时间
     */
    @Schema(description = "创建开始时间")
    private Date startTime;

    /**
     * 创建结束时间
     */
    @Schema(description = "创建结束时间")
    private Date endTime;

    /**
     * 父评论ID（0表示顶级评论，null表示所有评论）
     */
    @Schema(description = "父评论ID（0表示顶级评论，null表示所有评论）")
    private Long parentId;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ipAddress;

    private static final long serialVersionUID = 1L;
}
