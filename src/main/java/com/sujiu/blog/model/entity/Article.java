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
 * 文章实体
 *
 * @author sujiu
 */
@TableName(value = "blog_article")
@Data
public class Article implements Serializable {

    /**
     * 文章ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章别名
     */
    private String slug;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 文章内容（HTML格式）
     */
    private String contentHtml;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者ID
     */
    @TableField("user_id")
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否推荐
     */
    private Boolean isRecommend;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 转载来源URL
     */
    private String sourceUrl;

    /**
     * 文章密码
     */
    private String password;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;

    /**
     * 评论次数
     */
    private Integer commentCount;

    /**
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 预计阅读时间（分钟）
     */
    private Integer readingTime;

    /**
     * 状态：0-草稿，1-审核中，2-已发布，3-已拒绝，4-已下架
     */
    private Integer status;

    /**
     * 审核原因
     */
    private String auditReason;

    /**
     * 发布时间
     */
    private Date publishedTime;

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
