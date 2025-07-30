package com.sujiu.blog.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 点赞实体
 *
 * @author sujiu
 */
@TableName(value = "blog_like")
@Data
public class Like implements Serializable {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 目标ID（文章ID或评论ID）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 目标类型：1-文章，2-评论
     */
    @TableField("target_type")
    private Integer targetType;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
