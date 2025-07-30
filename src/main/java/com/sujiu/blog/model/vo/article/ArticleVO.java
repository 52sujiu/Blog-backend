package com.sujiu.blog.model.vo.article;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 文章详情
 *
 * @author sujiu
 */
@Data
public class ArticleVO implements Serializable {

    /**
     * 文章ID
     */
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
     * 文章内容（Markdown）
     */
    private String content;

    /**
     * 文章内容（HTML）
     */
    private String contentHtml;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 作者信息
     */
    private AuthorInfo author;

    /**
     * 分类信息
     */
    private CategoryInfo category;

    /**
     * 标签列表
     */
    private List<TagInfo> tags;

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
     * 状态
     */
    private Integer status;

    /**
     * 发布时间
     */
    private Date publishedTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Data
    public static class AuthorInfo implements Serializable {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private static final long serialVersionUID = 1L;
    }

    @Data
    public static class CategoryInfo implements Serializable {
        private Long id;
        private String name;
        private String slug;
        private static final long serialVersionUID = 1L;
    }

    @Data
    public static class TagInfo implements Serializable {
        private Long id;
        private String name;
        private String slug;
        private String color;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
