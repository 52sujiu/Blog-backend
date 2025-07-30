package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.CategoryMapper;
import com.sujiu.blog.mapper.TagMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.search.SearchRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.Category;
import com.sujiu.blog.model.entity.Tag;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.search.SearchResultVO;
import com.sujiu.blog.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public SearchResultVO search(SearchRequest searchRequest) {
        if (searchRequest == null || StringUtils.isBlank(searchRequest.getKeyword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "搜索关键词不能为空");
        }

        log.info("执行搜索，关键词：{}，类型：{}", searchRequest.getKeyword(), searchRequest.getType());

        SearchResultVO result = new SearchResultVO();
        String type = searchRequest.getType();
        String keyword = searchRequest.getKeyword().trim();

        // 根据搜索类型执行不同的搜索
        if (StringUtils.isBlank(type) || "all".equals(type)) {
            // 搜索所有类型
            result.setArticles(searchArticles(keyword, searchRequest));
            result.setUsers(searchUsers(keyword, searchRequest));
            result.setTags(searchTags(keyword, searchRequest));
            result.setCategories(searchCategories(keyword, searchRequest));
        } else {
            switch (type) {
                case "article":
                    result.setArticles(searchArticles(keyword, searchRequest));
                    break;
                case "user":
                    result.setUsers(searchUsers(keyword, searchRequest));
                    break;
                case "tag":
                    result.setTags(searchTags(keyword, searchRequest));
                    break;
                case "category":
                    result.setCategories(searchCategories(keyword, searchRequest));
                    break;
                default:
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的搜索类型");
            }
        }

        return result;
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, Integer limit) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        log.info("获取搜索建议，关键词：{}，限制：{}", keyword, limit);

        List<String> suggestions = new ArrayList<>();

        // 从文章标题中获取建议
        QueryWrapper<Article> articleQuery = new QueryWrapper<>();
        articleQuery.select("title")
                   .like("title", keyword)
                   .eq("status", 2) // 已发布
                   .orderByDesc("view_count")
                   .last("LIMIT " + limit);

        List<Article> articles = articleMapper.selectList(articleQuery);
        suggestions.addAll(articles.stream()
                .map(Article::getTitle)
                .collect(Collectors.toList()));

        // 从标签名称中获取建议
        if (suggestions.size() < limit) {
            QueryWrapper<Tag> tagQuery = new QueryWrapper<>();
            tagQuery.select("name")
                   .like("name", keyword)
                   .eq("status", 1) // 正常状态
                   .orderByDesc("article_count")
                   .last("LIMIT " + (limit - suggestions.size()));

            List<Tag> tags = tagMapper.selectList(tagQuery);
            suggestions.addAll(tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        }

        return suggestions.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 搜索文章
     */
    private SearchResultVO.ArticleSearchResult searchArticles(String keyword, SearchRequest request) {
        Page<Article> page = new Page<>(request.getCurrent(), request.getSize());
        
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2) // 只搜索已发布的文章
                   .and(wrapper -> wrapper
                           .like("title", keyword)
                           .or()
                           .like("summary", keyword)
                           .or()
                           .like("content", keyword));

        // 排序
        applySorting(queryWrapper, request.getSortField(), request.getSortOrder());

        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        SearchResultVO.ArticleSearchResult result = new SearchResultVO.ArticleSearchResult();
        result.setTotal(articlePage.getTotal());
        
        List<SearchResultVO.ArticleSearchItem> items = articlePage.getRecords().stream()
                .map(article -> convertToArticleSearchItem(article, keyword))
                .collect(Collectors.toList());
        result.setRecords(items);

        return result;
    }

    /**
     * 搜索用户
     */
    private SearchResultVO.UserSearchResult searchUsers(String keyword, SearchRequest request) {
        Page<User> page = new Page<>(request.getCurrent(), request.getSize());
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1) // 只搜索正常状态的用户
                   .and(wrapper -> wrapper
                           .like("username", keyword)
                           .or()
                           .like("nickname", keyword));

        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        SearchResultVO.UserSearchResult result = new SearchResultVO.UserSearchResult();
        result.setTotal(userPage.getTotal());
        
        List<SearchResultVO.UserSearchItem> items = userPage.getRecords().stream()
                .map(this::convertToUserSearchItem)
                .collect(Collectors.toList());
        result.setRecords(items);

        return result;
    }

    /**
     * 搜索标签
     */
    private SearchResultVO.TagSearchResult searchTags(String keyword, SearchRequest request) {
        Page<Tag> page = new Page<>(request.getCurrent(), request.getSize());
        
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1) // 只搜索正常状态的标签
                   .like("name", keyword)
                   .orderByDesc("article_count");

        Page<Tag> tagPage = tagMapper.selectPage(page, queryWrapper);

        SearchResultVO.TagSearchResult result = new SearchResultVO.TagSearchResult();
        result.setTotal(tagPage.getTotal());
        
        List<SearchResultVO.TagSearchItem> items = tagPage.getRecords().stream()
                .map(this::convertToTagSearchItem)
                .collect(Collectors.toList());
        result.setRecords(items);

        return result;
    }

    /**
     * 搜索分类
     */
    private SearchResultVO.CategorySearchResult searchCategories(String keyword, SearchRequest request) {
        Page<Category> page = new Page<>(request.getCurrent(), request.getSize());
        
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1) // 只搜索正常状态的分类
                   .and(wrapper -> wrapper
                           .like("name", keyword)
                           .or()
                           .like("description", keyword))
                   .orderByDesc("article_count");

        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);

        SearchResultVO.CategorySearchResult result = new SearchResultVO.CategorySearchResult();
        result.setTotal(categoryPage.getTotal());
        
        List<SearchResultVO.CategorySearchItem> items = categoryPage.getRecords().stream()
                .map(this::convertToCategorySearchItem)
                .collect(Collectors.toList());
        result.setRecords(items);

        return result;
    }

    /**
     * 应用排序
     */
    private void applySorting(QueryWrapper<?> queryWrapper, String sortField, String sortOrder) {
        if (StringUtils.isBlank(sortField)) {
            sortField = "relevance";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }

        boolean isAsc = "asc".equals(sortOrder);

        switch (sortField) {
            case "time":
                if (isAsc) {
                    queryWrapper.orderByAsc("created_time");
                } else {
                    queryWrapper.orderByDesc("created_time");
                }
                break;
            case "views":
                if (isAsc) {
                    queryWrapper.orderByAsc("view_count");
                } else {
                    queryWrapper.orderByDesc("view_count");
                }
                break;
            case "relevance":
            default:
                // 默认按创建时间倒序（相关性排序需要全文搜索引擎支持）
                queryWrapper.orderByDesc("created_time");
                break;
        }
    }

    /**
     * 转换为文章搜索项
     */
    private SearchResultVO.ArticleSearchItem convertToArticleSearchItem(Article article, String keyword) {
        SearchResultVO.ArticleSearchItem item = new SearchResultVO.ArticleSearchItem();
        item.setId(article.getId());
        item.setTitle(article.getTitle());
        item.setSummary(article.getSummary());
        item.setPublishedTime(article.getPublishedTime());

        // 设置作者信息
        SearchResultVO.AuthorInfo author = new SearchResultVO.AuthorInfo();
        // 这里需要查询用户信息，简化处理
        User user = userMapper.selectById(article.getAuthorId());
        if (user != null) {
            author.setNickname(user.getNickname());
            author.setAvatar(user.getAvatar());
        }
        item.setAuthor(author);

        // 设置高亮信息
        SearchResultVO.HighlightInfo highlight = new SearchResultVO.HighlightInfo();
        highlight.setTitle(highlightKeyword(article.getTitle(), keyword));
        highlight.setContent(highlightKeyword(article.getContent(), keyword));
        highlight.setSummary(highlightKeyword(article.getSummary(), keyword));
        item.setHighlight(highlight);

        // 简单的相关性得分计算
        item.setScore(calculateRelevanceScore(article, keyword));

        return item;
    }

    /**
     * 转换为用户搜索项
     */
    private SearchResultVO.UserSearchItem convertToUserSearchItem(User user) {
        SearchResultVO.UserSearchItem item = new SearchResultVO.UserSearchItem();
        item.setId(user.getId());
        item.setUsername(user.getUsername());
        item.setNickname(user.getNickname());
        item.setAvatar(user.getAvatar());
        item.setScore(1.0); // 简单设置为1.0
        return item;
    }

    /**
     * 转换为标签搜索项
     */
    private SearchResultVO.TagSearchItem convertToTagSearchItem(Tag tag) {
        SearchResultVO.TagSearchItem item = new SearchResultVO.TagSearchItem();
        item.setId(tag.getId());
        item.setName(tag.getName());
        item.setColor(tag.getColor());
        item.setArticleCount(tag.getArticleCount());
        item.setScore(1.0); // 简单设置为1.0
        return item;
    }

    /**
     * 转换为分类搜索项
     */
    private SearchResultVO.CategorySearchItem convertToCategorySearchItem(Category category) {
        SearchResultVO.CategorySearchItem item = new SearchResultVO.CategorySearchItem();
        item.setId(category.getId());
        item.setName(category.getName());
        item.setDescription(category.getDescription());
        item.setArticleCount(category.getArticleCount());
        item.setScore(1.0); // 简单设置为1.0
        return item;
    }

    /**
     * 高亮关键词
     */
    private String highlightKeyword(String text, String keyword) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(keyword)) {
            return text;
        }
        
        // 简单的高亮处理，实际项目中可以使用更复杂的高亮算法
        String highlighted = text.replaceAll("(?i)" + keyword, "<em>" + keyword + "</em>");
        
        // 如果文本太长，截取包含关键词的片段
        if (highlighted.length() > 200) {
            int keywordIndex = highlighted.toLowerCase().indexOf(keyword.toLowerCase());
            if (keywordIndex != -1) {
                int start = Math.max(0, keywordIndex - 50);
                int end = Math.min(highlighted.length(), keywordIndex + keyword.length() + 50);
                highlighted = "..." + highlighted.substring(start, end) + "...";
            } else {
                highlighted = highlighted.substring(0, 200) + "...";
            }
        }
        
        return highlighted;
    }

    /**
     * 计算相关性得分
     */
    private Double calculateRelevanceScore(Article article, String keyword) {
        double score = 0.0;
        
        // 标题匹配得分更高
        if (article.getTitle() != null && article.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
            score += 3.0;
        }
        
        // 摘要匹配
        if (article.getSummary() != null && article.getSummary().toLowerCase().contains(keyword.toLowerCase())) {
            score += 2.0;
        }
        
        // 内容匹配
        if (article.getContent() != null && article.getContent().toLowerCase().contains(keyword.toLowerCase())) {
            score += 1.0;
        }
        
        // 浏览量加权
        if (article.getViewCount() != null && article.getViewCount() > 0) {
            score += Math.log(article.getViewCount()) * 0.1;
        }
        
        return score;
    }
}
