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
 * 分类实体
 *
 * @author sujiu
 */
@TableName(value = "blog_category")
@Data
public class Category implements Serializable {

    /**
     * 分类ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类别名
     */
    private String slug;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类封面图
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 分类颜色
     */
    private String color;

    /**
     * 父分类ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 排序值
     */
    @TableField("sort_order")
    private Integer sortOrder;

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
