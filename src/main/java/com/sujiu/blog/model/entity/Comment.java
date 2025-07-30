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
 * 评论实体
 *
 * @author sujiu
 */
@TableName(value = "blog_comment")
@Data
public class Comment implements Serializable {

    /**
     * 评论ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 评论者ID
     */
    @TableField("user_id")
    private Long authorId;

    /**
     * 父评论ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 回复目标评论ID
     */
    @TableField("reply_to_id")
    private Long replyToId;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 状态：0-待审核，1-已审核，2-已删除
     */
    private Integer status;

    /**
     * 审核原因
     */
    @TableField("audit_reason")
    private String auditReason;

    /**
     * IP地址
     */
    @TableField("author_ip")
    private String ipAddress;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
