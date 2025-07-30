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
 * 标签实体
 *
 * @author sujiu
 */
@TableName(value = "blog_tag")
@Data
public class Tag implements Serializable {

    /**
     * 标签ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名
     */
    private String slug;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 文章数量
     */
    @TableField("article_count")
    private Integer articleCount;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

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
