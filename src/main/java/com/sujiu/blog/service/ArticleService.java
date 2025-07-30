package com.sujiu.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.article.AdminArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleAuditRequest;
import com.sujiu.blog.model.dto.article.ArticleOfflineRequest;
import com.sujiu.blog.model.dto.article.ArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.entity.Article;
import com.sujiu.blog.model.vo.article.AdminArticleVO;
import com.sujiu.blog.model.vo.article.ArticleVO;
import com.sujiu.blog.model.vo.common.PageVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章服务
 *
 * @author sujiu
 */
public interface ArticleService extends IService<Article> {

    /**
     * 发布文章
     *
     * @param articleRequest 文章发布请求
     * @param request HTTP请求对象
     * @return 文章详情
     */
    ArticleVO publishArticle(ArticleRequest articleRequest, HttpServletRequest request);

    /**
     * 计算文章字数
     *
     * @param content Markdown内容
     * @return 字数
     */
    Integer calculateWordCount(String content);

    /**
     * 计算预计阅读时间
     *
     * @param wordCount 字数
     * @return 预计阅读时间（分钟）
     */
    Integer calculateReadingTime(Integer wordCount);

    /**
     * 将Markdown转换为HTML
     *
     * @param markdown Markdown内容
     * @return HTML内容
     */
    String markdownToHtml(String markdown);

    /**
     * 生成文章别名
     *
     * @param title 文章标题
     * @return 文章别名
     */
    String generateSlug(String title);

    /**
     * 更新文章
     *
     * @param articleId 文章ID
     * @param articleRequest 文章更新请求
     * @param request HTTP请求对象
     * @return 文章详情
     */
    ArticleVO updateArticle(Long articleId, ArticleRequest articleRequest, HttpServletRequest request);

    /**
     * 删除文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteArticle(Long articleId, HttpServletRequest request);

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @param password 文章密码（如果文章加密）
     * @param request HTTP请求对象
     * @return 文章详情
     */
    ArticleVO getArticleDetail(Long articleId, String password, HttpServletRequest request);

    /**
     * 检查用户是否有权限操作文章
     *
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    Boolean hasPermission(Long articleId, Long userId);

    /**
     * 获取文章列表
     *
     * @param articleQueryRequest 查询请求
     * @return 文章列表
     */
    PageVO<ArticleVO> getArticleList(ArticleQueryRequest articleQueryRequest);

    /**
     * 获取热门文章
     *
     * @param limit 返回数量
     * @param days 统计天数
     * @return 热门文章列表
     */
    List<ArticleVO> getHotArticles(Integer limit, Integer days);

    /**
     * 获取推荐文章
     *
     * @param limit 返回数量
     * @return 推荐文章列表
     */
    List<ArticleVO> getRecommendArticles(Integer limit);

    /**
     * 获取置顶文章
     *
     * @return 置顶文章列表
     */
    List<ArticleVO> getTopArticles();

    /**
     * 增加文章浏览量
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    Boolean addArticleView(Long articleId, HttpServletRequest request);

    // ==================== 管理员文章管理接口 ====================

    /**
     * 管理员获取文章列表
     *
     * @param adminArticleQueryRequest 管理员文章查询请求
     * @return 文章列表
     */
    PageVO<AdminArticleVO> listArticlesByAdmin(AdminArticleQueryRequest adminArticleQueryRequest);

    /**
     * 管理员审核文章
     *
     * @param articleId 文章ID
     * @param articleAuditRequest 文章审核请求
     * @param request HTTP请求对象
     * @return 审核结果
     */
    Boolean auditArticle(Long articleId, ArticleAuditRequest articleAuditRequest, HttpServletRequest request);

    /**
     * 管理员下架文章
     *
     * @param articleId 文章ID
     * @param articleOfflineRequest 文章下架请求
     * @param request HTTP请求对象
     * @return 下架结果
     */
    Boolean offlineArticle(Long articleId, ArticleOfflineRequest articleOfflineRequest, HttpServletRequest request);

    /**
     * 管理员删除文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteArticleByAdmin(Long articleId, HttpServletRequest request);
}
