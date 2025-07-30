package com.sujiu.blog.model.vo.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 搜索结果
 *
 * @author sujiu
 */
@Data
@Schema(description = "搜索结果")
public class SearchResultVO implements Serializable {

    /**
     * 文章搜索结果
     */
    @Schema(description = "文章搜索结果")
    private ArticleSearchResult articles;

    /**
     * 用户搜索结果
     */
    @Schema(description = "用户搜索结果")
    private UserSearchResult users;

    /**
     * 标签搜索结果
     */
    @Schema(description = "标签搜索结果")
    private TagSearchResult tags;

    /**
     * 分类搜索结果
     */
    @Schema(description = "分类搜索结果")
    private CategorySearchResult categories;

    @Data
    @Schema(description = "文章搜索结果")
    public static class ArticleSearchResult implements Serializable {
        @Schema(description = "文章列表")
        private List<ArticleSearchItem> records;
        @Schema(description = "总数")
        private Long total;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "用户搜索结果")
    public static class UserSearchResult implements Serializable {
        @Schema(description = "用户列表")
        private List<UserSearchItem> records;
        @Schema(description = "总数")
        private Long total;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "标签搜索结果")
    public static class TagSearchResult implements Serializable {
        @Schema(description = "标签列表")
        private List<TagSearchItem> records;
        @Schema(description = "总数")
        private Long total;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "分类搜索结果")
    public static class CategorySearchResult implements Serializable {
        @Schema(description = "分类列表")
        private List<CategorySearchItem> records;
        @Schema(description = "总数")
        private Long total;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "文章搜索项")
    public static class ArticleSearchItem implements Serializable {
        @Schema(description = "文章ID")
        private Long id;
        @Schema(description = "文章标题")
        private String title;
        @Schema(description = "文章摘要")
        private String summary;
        @Schema(description = "作者信息")
        private AuthorInfo author;
        @Schema(description = "发布时间")
        private Date publishedTime;
        @Schema(description = "高亮信息")
        private HighlightInfo highlight;
        @Schema(description = "相关性得分")
        private Double score;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "用户搜索项")
    public static class UserSearchItem implements Serializable {
        @Schema(description = "用户ID")
        private Long id;
        @Schema(description = "用户名")
        private String username;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "头像")
        private String avatar;
        @Schema(description = "相关性得分")
        private Double score;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "标签搜索项")
    public static class TagSearchItem implements Serializable {
        @Schema(description = "标签ID")
        private Long id;
        @Schema(description = "标签名称")
        private String name;
        @Schema(description = "标签颜色")
        private String color;
        @Schema(description = "文章数量")
        private Integer articleCount;
        @Schema(description = "相关性得分")
        private Double score;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "分类搜索项")
    public static class CategorySearchItem implements Serializable {
        @Schema(description = "分类ID")
        private Long id;
        @Schema(description = "分类名称")
        private String name;
        @Schema(description = "分类描述")
        private String description;
        @Schema(description = "文章数量")
        private Integer articleCount;
        @Schema(description = "相关性得分")
        private Double score;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "作者信息")
    public static class AuthorInfo implements Serializable {
        @Schema(description = "作者昵称")
        private String nickname;
        @Schema(description = "作者头像")
        private String avatar;
        private static final long serialVersionUID = 1L;
    }

    @Data
    @Schema(description = "高亮信息")
    public static class HighlightInfo implements Serializable {
        @Schema(description = "高亮标题")
        private String title;
        @Schema(description = "高亮内容片段")
        private String content;
        @Schema(description = "高亮摘要")
        private String summary;
        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
