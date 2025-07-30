package com.sujiu.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.ArticleTagMapper;
import com.sujiu.blog.mapper.CategoryMapper;
import com.sujiu.blog.mapper.TagMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.article.AdminArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleAuditRequest;
import com.sujiu.blog.model.dto.article.ArticleOfflineRequest;
import com.sujiu.blog.model.dto.article.ArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.entity.ArticleTag;
import com.sujiu.blog.model.entity.Category;
import com.sujiu.blog.model.entity.Tag;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.article.AdminArticleVO;
import com.sujiu.blog.model.vo.article.ArticleVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文章服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private com.sujiu.blog.service.UserService userService;

    @Resource
    private com.sujiu.blog.service.CategoryService categoryService;

    @Resource
    private com.sujiu.blog.service.TagService tagService;



    /**
     * 中文字符正则
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    /**
     * 英文单词正则
     */
    private static final Pattern WORD_PATTERN = Pattern.compile("\\b\\w+\\b");

    /**
     * 平均阅读速度（字/分钟）
     */
    private static final int READING_SPEED = 200;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO publishArticle(ArticleRequest articleRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User user = getCurrentLoginUser(request);
        Long userId = user.getId();

        // 5. 详细参数校验
        String title = articleRequest.getTitle();
        String slug = articleRequest.getSlug();
        String content = articleRequest.getContent();
        Long categoryId = articleRequest.getCategoryId();
        List<Long> tagIds = articleRequest.getTagIds();
        Integer status = articleRequest.getStatus();

        // 标题校验
        if (StringUtils.isBlank(title)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题不能为空");
        }
        if (title.length() < 1 || title.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题长度应为1-200字符");
        }

        // 内容校验
        if (StringUtils.isBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章内容不能为空");
        }

        // 状态校验
        if (status == null || (status < 0 || status > 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章状态不正确");
        }

        // 分类校验
        if (categoryId != null) {
            Category category = categoryMapper.selectById(categoryId);
            if (category == null || category.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类不存在或已禁用");
            }
        }

        // 标签校验
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "部分标签不存在");
            }
            for (Tag tag : tags) {
                if (tag.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签【" + tag.getName() + "】已被禁用");
                }
            }
        }

        // 6. 生成或校验别名
        if (StringUtils.isBlank(slug)) {
            slug = generateSlug(title);
        } else {
            // 校验别名格式
            if (!slug.matches("^[a-z0-9-]+$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章别名只能包含小写字母、数字和连字符");
            }
        }

        // 检查别名是否重复
        QueryWrapper<Article> slugQuery = new QueryWrapper<>();
        slugQuery.eq("slug", slug);
        Article existingArticle = this.getOne(slugQuery);
        if (existingArticle != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章别名已存在");
        }

        // 7. 计算文章统计信息
        Integer wordCount = calculateWordCount(content);
        Integer readingTime = calculateReadingTime(wordCount);
        String contentHtml = markdownToHtml(content);

        // 8. 创建文章
        Article article = new Article();
        BeanUtils.copyProperties(articleRequest, article);
        article.setSlug(slug);
        article.setAuthorId(userId);
        article.setWordCount(wordCount);
        article.setReadingTime(readingTime);
        article.setContentHtml(contentHtml);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        // 设置发布时间
        if (status == 2) { // 已发布状态
            article.setPublishedTime(new Date());
        }

        // 设置默认值
        if (article.getIsTop() == null) {
            article.setIsTop(false);
        }
        if (article.getIsRecommend() == null) {
            article.setIsRecommend(false);
        }
        if (article.getIsOriginal() == null) {
            article.setIsOriginal(true);
        }

        // 9. 保存文章
        boolean saveResult = this.save(article);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章保存失败");
        }

        // 10. 保存文章标签关联
        if (tagIds != null && !tagIds.isEmpty()) {
            List<ArticleTag> articleTags = new ArrayList<>();
            for (Long tagId : tagIds) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tagId);
                articleTag.setCreateTime(new Date());
                articleTags.add(articleTag);
            }
            
            // 批量保存
            for (ArticleTag articleTag : articleTags) {
                articleTagMapper.insert(articleTag);
            }
        }

        log.info("用户发布文章成功，用户ID：{}，文章ID：{}，标题：{}", userId, article.getId(), title);

        // 11. 构建返回结果
        return buildArticleVO(article);
    }

    @Override
    public Integer calculateWordCount(String content) {
        if (StringUtils.isBlank(content)) {
            return 0;
        }

        // 移除Markdown标记
        String plainText = content
                .replaceAll("```[\\s\\S]*?```", "") // 代码块
                .replaceAll("`[^`]*`", "") // 行内代码
                .replaceAll("!\\[.*?\\]\\(.*?\\)", "") // 图片
                .replaceAll("\\[.*?\\]\\(.*?\\)", "") // 链接
                .replaceAll("[#*_~`>-]", "") // 其他Markdown标记
                .replaceAll("\\s+", " ") // 多个空白字符替换为单个空格
                .trim();

        // 统计中文字符
        int chineseCount = 0;
        java.util.regex.Matcher chineseMatcher = CHINESE_PATTERN.matcher(plainText);
        while (chineseMatcher.find()) {
            chineseCount++;
        }

        // 统计英文单词
        int englishWordCount = 0;
        java.util.regex.Matcher wordMatcher = WORD_PATTERN.matcher(plainText);
        while (wordMatcher.find()) {
            englishWordCount++;
        }

        return chineseCount + englishWordCount;
    }

    @Override
    public Integer calculateReadingTime(Integer wordCount) {
        if (wordCount == null || wordCount <= 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil((double) wordCount / READING_SPEED));
    }

    @Override
    public String markdownToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        
        // 简单的Markdown转HTML实现
        // 在实际项目中，建议使用专业的Markdown解析库如flexmark-java
        String html = markdown
                .replaceAll("### (.*)", "<h3>$1</h3>")
                .replaceAll("## (.*)", "<h2>$1</h2>")
                .replaceAll("# (.*)", "<h1>$1</h1>")
                .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
                .replaceAll("`(.*?)`", "<code>$1</code>")
                .replaceAll("\\n", "<br>");
        
        return html;
    }

    @Override
    public String generateSlug(String title) {
        if (StringUtils.isBlank(title)) {
            return "untitled-" + System.currentTimeMillis();
        }

        // 转换为小写并规范化
        String slug = title.toLowerCase();
        
        // Unicode规范化
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        
        // 移除特殊字符，保留字母、数字、空格和连字符
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");
        
        // 将空格替换为连字符
        slug = slug.replaceAll("\\s+", "-");
        
        // 移除多余的连字符
        slug = slug.replaceAll("-+", "-");
        
        // 移除首尾连字符
        slug = slug.replaceAll("^-|-$", "");
        
        // 如果为空，使用时间戳
        if (StringUtils.isBlank(slug)) {
            slug = "article-" + System.currentTimeMillis();
        }
        
        // 限制长度
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
            // 确保不以连字符结尾
            slug = slug.replaceAll("-$", "");
        }
        
        return slug;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO updateArticle(Long articleId, ArticleRequest articleRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User currentUser = getCurrentLoginUser(request);
        Long userId = currentUser.getId();

        // 3. 检查文章是否存在
        Article existingArticle = this.getById(articleId);
        if (existingArticle == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 4. 检查权限
        if (!hasPermission(articleId, userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作此文章");
        }

        // 5. 详细参数校验
        String title = articleRequest.getTitle();
        String slug = articleRequest.getSlug();
        String content = articleRequest.getContent();
        Long categoryId = articleRequest.getCategoryId();
        List<Long> tagIds = articleRequest.getTagIds();
        Integer status = articleRequest.getStatus();

        // 标题校验
        if (StringUtils.isNotBlank(title)) {
            if (title.length() < 1 || title.length() > 200) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章标题长度应为1-200字符");
            }
        }

        // 内容校验
        if (StringUtils.isNotBlank(content)) {
            // 内容不为空时进行校验
        }

        // 状态校验
        if (status != null && (status < 0 || status > 2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章状态不正确");
        }

        // 分类校验
        if (categoryId != null) {
            Category category = categoryMapper.selectById(categoryId);
            if (category == null || category.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类不存在或已禁用");
            }
        }

        // 标签校验
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "部分标签不存在");
            }
            for (Tag tag : tags) {
                if (tag.getStatus() != 1) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签【" + tag.getName() + "】已被禁用");
                }
            }
        }

        // 6. 处理别名
        if (StringUtils.isNotBlank(slug)) {
            // 校验别名格式
            if (!slug.matches("^[a-z0-9-]+$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章别名只能包含小写字母、数字和连字符");
            }

            // 检查别名是否重复（排除当前文章）
            QueryWrapper<Article> slugQuery = new QueryWrapper<>();
            slugQuery.eq("slug", slug).ne("id", articleId);
            Article duplicateArticle = this.getOne(slugQuery);
            if (duplicateArticle != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章别名已存在");
            }
        }

        // 7. 更新文章信息
        Article updateArticle = new Article();
        updateArticle.setId(articleId);

        // 只更新非空字段
        if (StringUtils.isNotBlank(title)) {
            updateArticle.setTitle(title);
        }
        if (StringUtils.isNotBlank(slug)) {
            updateArticle.setSlug(slug);
        }
        if (StringUtils.isNotBlank(articleRequest.getSummary())) {
            updateArticle.setSummary(articleRequest.getSummary());
        }
        if (StringUtils.isNotBlank(content)) {
            updateArticle.setContent(content);
            // 重新计算统计信息
            Integer wordCount = calculateWordCount(content);
            Integer readingTime = calculateReadingTime(wordCount);
            String contentHtml = markdownToHtml(content);
            updateArticle.setWordCount(wordCount);
            updateArticle.setReadingTime(readingTime);
            updateArticle.setContentHtml(contentHtml);
        }
        if (StringUtils.isNotBlank(articleRequest.getCoverImage())) {
            updateArticle.setCoverImage(articleRequest.getCoverImage());
        }
        if (categoryId != null) {
            updateArticle.setCategoryId(categoryId);
        }
        if (articleRequest.getIsTop() != null) {
            updateArticle.setIsTop(articleRequest.getIsTop());
        }
        if (articleRequest.getIsRecommend() != null) {
            updateArticle.setIsRecommend(articleRequest.getIsRecommend());
        }
        if (articleRequest.getIsOriginal() != null) {
            updateArticle.setIsOriginal(articleRequest.getIsOriginal());
        }
        if (StringUtils.isNotBlank(articleRequest.getSourceUrl())) {
            updateArticle.setSourceUrl(articleRequest.getSourceUrl());
        }
        if (StringUtils.isNotBlank(articleRequest.getPassword())) {
            updateArticle.setPassword(articleRequest.getPassword());
        }
        if (status != null) {
            updateArticle.setStatus(status);
            // 如果状态改为已发布且之前没有发布时间，设置发布时间
            if (status == 2 && existingArticle.getPublishedTime() == null) {
                updateArticle.setPublishedTime(new Date());
            }
        }

        updateArticle.setUpdateTime(new Date());

        // 8. 保存文章更新
        boolean updateResult = this.updateById(updateArticle);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章更新失败");
        }

        // 9. 更新标签关联
        if (tagIds != null) {
            // 删除原有标签关联
            QueryWrapper<ArticleTag> deleteQuery = new QueryWrapper<>();
            deleteQuery.eq("article_id", articleId);
            articleTagMapper.delete(deleteQuery);

            // 添加新的标签关联
            if (!tagIds.isEmpty()) {
                List<ArticleTag> articleTags = new ArrayList<>();
                for (Long tagId : tagIds) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagId);
                    articleTag.setCreateTime(new Date());
                    articleTags.add(articleTag);
                }

                for (ArticleTag articleTag : articleTags) {
                    articleTagMapper.insert(articleTag);
                }
            }
        }

        log.info("用户更新文章成功，用户ID：{}，文章ID：{}", userId, articleId);

        // 10. 获取更新后的文章信息
        Article updatedArticle = this.getById(articleId);
        return buildArticleVO(updatedArticle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteArticle(Long articleId, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 2. 获取当前登录用户（已验证权限）
        User currentUser = getCurrentLoginUser(request);
        Long userId = currentUser.getId();

        // 3. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 4. 检查权限
        if (!hasPermission(articleId, userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限删除此文章");
        }

        // 5. 删除文章（逻辑删除）
        boolean deleteResult = this.removeById(articleId);
        if (!deleteResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章删除失败");
        }

        // 6. 删除文章标签关联
        QueryWrapper<ArticleTag> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("article_id", articleId);
        articleTagMapper.delete(deleteQuery);

        log.info("用户删除文章成功，用户ID：{}，文章ID：{}", userId, articleId);
        return true;
    }

    /**
     * 构建文章VO
     */
    private ArticleVO buildArticleVO(Article article) {
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        
        // 设置作者信息
        User author = userMapper.selectById(article.getAuthorId());
        if (author != null) {
            ArticleVO.AuthorInfo authorInfo = new ArticleVO.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            articleVO.setAuthor(authorInfo);
        }
        
        // 设置分类信息
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                ArticleVO.CategoryInfo categoryInfo = new ArticleVO.CategoryInfo();
                categoryInfo.setId(category.getId());
                categoryInfo.setName(category.getName());
                categoryInfo.setSlug(category.getSlug());
                articleVO.setCategory(categoryInfo);
            }
        }
        
        // 设置标签信息
        QueryWrapper<ArticleTag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("article_id", article.getId());
        List<ArticleTag> articleTags = articleTagMapper.selectList(tagQuery);
        
        if (!articleTags.isEmpty()) {
            List<Long> tagIds = articleTags.stream()
                    .map(ArticleTag::getTagId)
                    .collect(Collectors.toList());
            
            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            List<ArticleVO.TagInfo> tagInfos = tags.stream().map(tag -> {
                ArticleVO.TagInfo tagInfo = new ArticleVO.TagInfo();
                tagInfo.setId(tag.getId());
                tagInfo.setName(tag.getName());
                tagInfo.setSlug(tag.getSlug());
                tagInfo.setColor(tag.getColor());
                return tagInfo;
            }).collect(Collectors.toList());
            
            articleVO.setTags(tagInfos);
        }

        return articleVO;
    }

    @Override
    public ArticleVO getArticleDetail(Long articleId, String password, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 2. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 3. 检查文章状态
        if (article.getStatus() == 0) {
            // 草稿状态，需要检查权限
            try {
                User currentUser = getCurrentLoginUser(request);
                if (!hasPermission(articleId, currentUser.getId())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文章不存在或无权限访问");
                }
            } catch (BusinessException e) {
                if (e.getCode() == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文章不存在或无权限访问");
                }
                throw e;
            }
        } else if (article.getStatus() == 1) {
            // 审核中状态，需要检查权限
            try {
                User currentUser = getCurrentLoginUser(request);
                if (!hasPermission(articleId, currentUser.getId())) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文章正在审核中");
                }
            } catch (BusinessException e) {
                if (e.getCode() == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
                    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文章正在审核中");
                }
                throw e;
            }
        }

        // 4. 检查文章密码
        if (StringUtils.isNotBlank(article.getPassword())) {
            if (StringUtils.isBlank(password) || !article.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "文章密码错误");
            }
        }

        // 5. 构建返回结果（不再自动增加浏览量）
        return buildArticleVO(article);
    }

    @Override
    public Boolean hasPermission(Long articleId, Long userId) {
        if (articleId == null || userId == null) {
            return false;
        }

        // 1. 获取文章信息
        Article article = this.getById(articleId);
        if (article == null) {
            return false;
        }

        // 2. 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        // 3. 检查权限
        // 文章作者有权限
        if (article.getAuthorId().equals(userId)) {
            return true;
        }

        // 管理员有权限
        if ("admin".equals(user.getRole())) {
            return true;
        }

        return false;
    }

    /**
     * 获取当前登录用户（已验证权限）
     *
     * @param request HTTP请求对象
     * @return 当前登录用户
     */
    private User getCurrentLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取当前登录用户
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        }

        // 从数据库查询最新的用户信息
        Long userId = currentUser.getId();
        User user = userMapper.selectById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用");
        }

        return user;
    }

    @Override
    public PageVO<ArticleVO> getArticleList(ArticleQueryRequest articleQueryRequest) {
        if (articleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 构建查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 只查询已发布的文章（对外接口）
        queryWrapper.eq("status", 2);

        // 分类筛选
        if (articleQueryRequest.getCategoryId() != null) {
            queryWrapper.eq("category_id", articleQueryRequest.getCategoryId());
        }

        // 作者筛选
        if (articleQueryRequest.getAuthorId() != null) {
            queryWrapper.eq("user_id", articleQueryRequest.getAuthorId());
        }

        // 状态筛选（管理员可能需要）
        if (articleQueryRequest.getStatus() != null) {
            queryWrapper.eq("status", articleQueryRequest.getStatus());
        }

        // 置顶筛选
        if (articleQueryRequest.getIsTop() != null) {
            queryWrapper.eq("is_top", articleQueryRequest.getIsTop());
        }

        // 推荐筛选
        if (articleQueryRequest.getIsRecommend() != null) {
            queryWrapper.eq("is_recommend", articleQueryRequest.getIsRecommend());
        }

        // 关键词搜索
        if (StringUtils.isNotBlank(articleQueryRequest.getKeyword())) {
            String keyword = articleQueryRequest.getKeyword().trim();
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("summary", keyword)
                .or()
                .like("content", keyword)
            );
        }

        // 标签筛选（需要关联查询）
        if (articleQueryRequest.getTagId() != null) {
            QueryWrapper<ArticleTag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("tag_id", articleQueryRequest.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(tagQuery);

            if (!articleTags.isEmpty()) {
                List<Long> articleIds = articleTags.stream()
                    .map(ArticleTag::getArticleId)
                    .collect(Collectors.toList());
                queryWrapper.in("id", articleIds);
            } else {
                // 如果没有找到相关文章，返回空结果
                return new PageVO<>(new ArrayList<>(), 0L, articleQueryRequest.getCurrent(), articleQueryRequest.getSize());
            }
        }

        // 排序
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();

        if (StringUtils.isNotBlank(sortField)) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrder);

            switch (sortField) {
                case "publishedTime":
                    if (isAsc) {
                        queryWrapper.orderByAsc("published_time");
                    } else {
                        queryWrapper.orderByDesc("published_time");
                    }
                    break;
                case "viewCount":
                    if (isAsc) {
                        queryWrapper.orderByAsc("view_count");
                    } else {
                        queryWrapper.orderByDesc("view_count");
                    }
                    break;
                case "likeCount":
                    if (isAsc) {
                        queryWrapper.orderByAsc("like_count");
                    } else {
                        queryWrapper.orderByDesc("like_count");
                    }
                    break;
                default:
                    // 默认按发布时间倒序
                    queryWrapper.orderByDesc("published_time");
                    break;
            }
        } else {
            // 默认排序：置顶文章在前，然后按发布时间倒序
            queryWrapper.orderByDesc("is_top", "published_time");
        }

        // 分页查询
        Page<Article> page = new Page<>(articleQueryRequest.getCurrent(), articleQueryRequest.getSize());
        Page<Article> articlePage = this.page(page, queryWrapper);

        // 转换为VO
        List<ArticleVO> articleVOs = articlePage.getRecords().stream()
            .map(this::buildArticleVO)
            .collect(Collectors.toList());

        return new PageVO<>(articleVOs, articlePage.getTotal(),
            articleQueryRequest.getCurrent(), articleQueryRequest.getSize());
    }

    @Override
    public List<ArticleVO> getHotArticles(Integer limit, Integer days) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (days == null || days <= 0) {
            days = 7;
        }

        // 计算时间范围
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2) // 只查询已发布的文章
                   .ge("published_time", startDate) // 在指定天数内发布的
                   .orderByDesc("view_count", "like_count", "published_time") // 按浏览量、点赞数、发布时间排序
                   .last("LIMIT " + limit);

        List<Article> articles = this.list(queryWrapper);
        return articles.stream()
            .map(this::buildArticleVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getRecommendArticles(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2) // 只查询已发布的文章
                   .eq("is_recommend", true) // 推荐文章
                   .orderByDesc("published_time") // 按发布时间倒序
                   .last("LIMIT " + limit);

        List<Article> articles = this.list(queryWrapper);
        return articles.stream()
            .map(this::buildArticleVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getTopArticles() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 2) // 只查询已发布的文章
                   .eq("is_top", true) // 置顶文章
                   .orderByDesc("published_time"); // 按发布时间倒序

        List<Article> articles = this.list(queryWrapper);
        return articles.stream()
            .map(this::buildArticleVO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addArticleView(Long articleId, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 2. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 3. 检查文章状态（只有已发布的文章才能增加浏览量）
        if (article.getStatus() != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章未发布");
        }

        // 4. 直接增加浏览量（简化实现）
        Article updateArticle = new Article();
        updateArticle.setId(articleId);
        updateArticle.setViewCount(article.getViewCount() + 1);
        updateArticle.setUpdateTime(new Date());

        boolean updateResult = this.updateById(updateArticle);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "浏览量更新失败");
        }

        // 5. 记录日志
        String ipAddress = getClientIpAddress(request);
        log.info("文章浏览量增加成功，文章ID：{}，IP：{}，新浏览量：{}", articleId, ipAddress, article.getViewCount() + 1);

        return true;
    }



    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求对象
     * @return IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    // ==================== 管理员文章管理接口实现 ====================

    @Override
    public PageVO<AdminArticleVO> listArticlesByAdmin(AdminArticleQueryRequest adminArticleQueryRequest) {
        // 1. 参数校验
        if (adminArticleQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 构建查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        // 关键词搜索（标题、摘要）
        String keyword = adminArticleQueryRequest.getKeyword();
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("title", keyword)
                .or()
                .like("summary", keyword)
            );
        }

        // 状态筛选
        Integer status = adminArticleQueryRequest.getStatus();
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 作者筛选
        Long authorId = adminArticleQueryRequest.getAuthorId();
        if (authorId != null) {
            queryWrapper.eq("user_id", authorId);
        }

        // 分类筛选
        Long categoryId = adminArticleQueryRequest.getCategoryId();
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }

        // 时间范围筛选
        if (adminArticleQueryRequest.getStartTime() != null) {
            queryWrapper.ge("created_time", adminArticleQueryRequest.getStartTime());
        }
        if (adminArticleQueryRequest.getEndTime() != null) {
            queryWrapper.le("created_time", adminArticleQueryRequest.getEndTime());
        }

        // 置顶筛选
        Boolean isTop = adminArticleQueryRequest.getIsTop();
        if (isTop != null) {
            queryWrapper.eq("is_top", isTop);
        }

        // 推荐筛选
        Boolean isRecommend = adminArticleQueryRequest.getIsRecommend();
        if (isRecommend != null) {
            queryWrapper.eq("is_recommend", isRecommend);
        }

        // 原创筛选
        Boolean isOriginal = adminArticleQueryRequest.getIsOriginal();
        if (isOriginal != null) {
            queryWrapper.eq("is_original", isOriginal);
        }

        // 排序
        String sortField = adminArticleQueryRequest.getSortField();
        String sortOrder = adminArticleQueryRequest.getSortOrder();
        if (StrUtil.isNotBlank(sortField)) {
            if ("asc".equals(sortOrder)) {
                queryWrapper.orderByAsc(sortField);
            } else {
                queryWrapper.orderByDesc(sortField);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc("created_time");
        }

        // 3. 分页查询
        Page<Article> page = new Page<>(adminArticleQueryRequest.getCurrent(), adminArticleQueryRequest.getSize());
        Page<Article> articlePage = this.page(page, queryWrapper);

        // 4. 转换为VO
        List<AdminArticleVO> adminArticleVOList = articlePage.getRecords().stream()
            .map(this::convertToAdminArticleVO)
            .collect(Collectors.toList());

        // 5. 构建分页结果
        return new PageVO<>(adminArticleVOList, articlePage.getTotal(),
            articlePage.getCurrent(), articlePage.getSize());
    }

    @Override
    public Boolean auditArticle(Long articleId, ArticleAuditRequest articleAuditRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Integer status = articleAuditRequest.getStatus();
        if (status == null || (status != 2 && status != 3)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态参数错误");
        }

        // 2. 获取当前操作用户
        User currentUser = userService.getCurrentLoginUser(request);
        log.info("管理员 {} 审核文章 {}，状态：{}，原因：{}",
            currentUser.getId(), articleId, status, articleAuditRequest.getAuditReason());

        // 3. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 4. 检查文章状态（只有审核中的文章才能审核）
        if (article.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不在审核状态");
        }

        // 5. 更新文章状态
        Article updateArticle = new Article();
        updateArticle.setId(articleId);
        updateArticle.setStatus(status);
        updateArticle.setAuditReason(articleAuditRequest.getAuditReason());
        updateArticle.setUpdateTime(new Date());

        // 如果审核通过，设置发布时间
        if (status == 2) {
            updateArticle.setPublishedTime(new Date());
        }

        boolean result = this.updateById(updateArticle);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章审核失败");
        }

        return true;
    }

    @Override
    public Boolean offlineArticle(Long articleId, ArticleOfflineRequest articleOfflineRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleOfflineRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        String reason = articleOfflineRequest.getReason();
        if (StrUtil.isBlank(reason)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "下架原因不能为空");
        }

        // 2. 获取当前操作用户
        User currentUser = userService.getCurrentLoginUser(request);
        log.info("管理员 {} 下架文章 {}，原因：{}", currentUser.getId(), articleId, reason);

        // 3. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 4. 检查文章状态（只有已发布的文章才能下架）
        if (article.getStatus() != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只有已发布的文章才能下架");
        }

        // 5. 更新文章状态为下架
        Article updateArticle = new Article();
        updateArticle.setId(articleId);
        updateArticle.setStatus(4); // 4-已下架
        updateArticle.setAuditReason(reason);
        updateArticle.setUpdateTime(new Date());

        boolean result = this.updateById(updateArticle);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章下架失败");
        }

        return true;
    }

    @Override
    public Boolean deleteArticleByAdmin(Long articleId, HttpServletRequest request) {
        // 1. 参数校验
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        // 2. 获取当前操作用户
        User currentUser = userService.getCurrentLoginUser(request);
        log.info("管理员 {} 删除文章 {}", currentUser.getId(), articleId);

        // 3. 检查文章是否存在
        Article article = this.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章不存在");
        }

        // 4. 逻辑删除文章
        boolean result = this.removeById(articleId);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文章删除失败");
        }

        // 5. 删除文章标签关联
        QueryWrapper<ArticleTag> deleteQuery = new QueryWrapper<>();
        deleteQuery.eq("article_id", articleId);
        articleTagMapper.delete(deleteQuery);

        return true;
    }

    /**
     * 转换Article实体为AdminArticleVO
     *
     * @param article 文章实体
     * @return AdminArticleVO
     */
    private AdminArticleVO convertToAdminArticleVO(Article article) {
        if (article == null) {
            return null;
        }

        AdminArticleVO adminArticleVO = new AdminArticleVO();
        BeanUtils.copyProperties(article, adminArticleVO);
        adminArticleVO.setCreateTime(article.getCreateTime());
        adminArticleVO.setUpdateTime(article.getUpdateTime());

        // 设置作者信息
        User author = userService.getById(article.getAuthorId());
        if (author != null) {
            AdminArticleVO.AuthorInfo authorInfo = new AdminArticleVO.AuthorInfo();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setNickname(author.getNickname());
            authorInfo.setAvatar(author.getAvatar());
            authorInfo.setEmail(author.getEmail());
            adminArticleVO.setAuthor(authorInfo);
        }

        // 设置分类信息
        if (article.getCategoryId() != null) {
            Category category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                AdminArticleVO.CategoryInfo categoryInfo = new AdminArticleVO.CategoryInfo();
                categoryInfo.setId(category.getId());
                categoryInfo.setName(category.getName());
                categoryInfo.setSlug(category.getSlug());
                adminArticleVO.setCategory(categoryInfo);
            }
        }

        // 设置标签信息
        QueryWrapper<ArticleTag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("article_id", article.getId());
        List<ArticleTag> articleTags = articleTagMapper.selectList(tagQuery);
        if (!articleTags.isEmpty()) {
            List<Long> tagIds = articleTags.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());

            List<Tag> tags = tagMapper.selectBatchIds(tagIds);
            List<AdminArticleVO.TagInfo> tagInfos = tags.stream()
                .map(tag -> {
                    AdminArticleVO.TagInfo tagInfo = new AdminArticleVO.TagInfo();
                    tagInfo.setId(tag.getId());
                    tagInfo.setName(tag.getName());
                    tagInfo.setSlug(tag.getSlug());
                    tagInfo.setColor(tag.getColor());
                    return tagInfo;
                })
                .collect(Collectors.toList());
            adminArticleVO.setTags(tagInfos);
        }

        return adminArticleVO;
    }
}
