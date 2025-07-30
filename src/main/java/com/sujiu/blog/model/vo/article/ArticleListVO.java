package com.sujiu.blog.model.vo.article;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 文章列表项
 *
 * @author sujiu
 */
@Data
public class ArticleListVO implements Serializable {

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
     * 预计阅读时间
     */
    private Integer readingTime;

    /**
     * 发布时间
     */
    private Date publishedTime;

    @Data
    public static class AuthorInfo implements Serializable {
        private Long id;
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
        private String color;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
