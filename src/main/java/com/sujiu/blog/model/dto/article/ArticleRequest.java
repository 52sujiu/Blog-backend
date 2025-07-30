package com.sujiu.blog.model.dto.article;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 文章创建/更新请求
 *
 * @author sujiu
 */
@Data
public class ArticleRequest implements Serializable {

    /**
     * 文章标题，1-200字符
     */
    private String title;

    /**
     * 文章别名，URL友好
     */
    private String slug;

    /**
     * 文章摘要，最多500字符
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID数组
     */
    private List<Long> tagIds;

    /**
     * 是否置顶，默认false
     */
    private Boolean isTop;

    /**
     * 是否推荐，默认false
     */
    private Boolean isRecommend;

    /**
     * 是否原创，默认true
     */
    private Boolean isOriginal;

    /**
     * 转载来源URL（非原创时）
     */
    private String sourceUrl;

    /**
     * 文章密码（可选）
     */
    private String password;

    /**
     * 状态：0-草稿，1-审核中，2-已发布
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
