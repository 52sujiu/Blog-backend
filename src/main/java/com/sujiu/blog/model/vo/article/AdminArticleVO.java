package com.sujiu.blog.model.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 管理员文章视图对象
 *
 * @author sujiu
 */
@Data
@Schema(description = "管理员文章视图对象")
public class AdminArticleVO implements Serializable {

    /**
     * 文章ID
     */
    @Schema(description = "文章ID")
    private Long id;

    /**
     * 文章标题
     */
    @Schema(description = "文章标题")
    private String title;

    /**
     * 文章别名
     */
    @Schema(description = "文章别名")
    private String slug;

    /**
     * 文章摘要
     */
    @Schema(description = "文章摘要")
    private String summary;

    /**
     * 封面图片URL
     */
    @Schema(description = "封面图片URL")
    private String coverImage;

    /**
     * 作者信息
     */
    @Schema(description = "作者信息")
    private AuthorInfo author;

    /**
     * 分类信息
     */
    @Schema(description = "分类信息")
    private CategoryInfo category;

    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    private List<TagInfo> tags;

    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶")
    private Boolean isTop;

    /**
     * 是否推荐
     */
    @Schema(description = "是否推荐")
    private Boolean isRecommend;

    /**
     * 是否原创
     */
    @Schema(description = "是否原创")
    private Boolean isOriginal;

    /**
     * 转载来源URL
     */
    @Schema(description = "转载来源URL")
    private String sourceUrl;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Integer likeCount;

    /**
     * 评论次数
     */
    @Schema(description = "评论次数")
    private Integer commentCount;

    /**
     * 字数统计
     */
    @Schema(description = "字数统计")
    private Integer wordCount;

    /**
     * 预计阅读时间（分钟）
     */
    @Schema(description = "预计阅读时间（分钟）")
    private Integer readingTime;

    /**
     * 状态：0-草稿，1-审核中，2-已发布，3-已拒绝，4-已下架
     */
    @Schema(description = "状态：0-草稿，1-审核中，2-已发布，3-已拒绝，4-已下架")
    private Integer status;

    /**
     * 审核原因
     */
    @Schema(description = "审核原因")
    private String auditReason;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishedTime;

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
    @Schema(description = "作者信息")
    public static class AuthorInfo implements Serializable {
        @Schema(description = "作者ID")
        private Long id;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "头像")
        private String avatar;
        @Schema(description = "邮箱")
        private String email;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "分类信息")
    public static class CategoryInfo implements Serializable {
        @Schema(description = "分类ID")
        private Long id;
        @Schema(description = "分类名称")
        private String name;
        @Schema(description = "分类别名")
        private String slug;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "标签信息")
    public static class TagInfo implements Serializable {
        @Schema(description = "标签ID")
        private Long id;
        @Schema(description = "标签名称")
        private String name;
        @Schema(description = "标签别名")
        private String slug;
        @Schema(description = "标签颜色")
        private String color;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
