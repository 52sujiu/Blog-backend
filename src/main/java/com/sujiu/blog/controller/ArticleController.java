package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireLogin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.article.ArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleRequest;
import com.sujiu.blog.model.vo.article.ArticleVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.ArticleService;
import com.sujiu.blog.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章接口
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/articles")
@Slf4j
@Tag(name = "文章管理", description = "文章相关接口")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private LikeService likeService;

    /**
     * 发布文章
     *
     * @param articleRequest 文章发布请求
     * @param request HTTP请求对象
     * @return 文章详情
     */
    @Operation(summary = "发布文章", description = "发布新文章")
    @RequireLogin
    @PostMapping
    public BaseResponse<ArticleVO> publishArticle(@RequestBody ArticleRequest articleRequest,
                                                  HttpServletRequest request) {
        if (articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        ArticleVO articleVO = articleService.publishArticle(articleRequest, request);
        return ResultUtils.success(articleVO, "文章发布成功");
    }

    /**
     * 更新文章
     *
     * @param articleId 文章ID
     * @param articleRequest 文章更新请求
     * @param request HTTP请求对象
     * @return 文章详情
     */
    @Operation(summary = "更新文章", description = "更新指定文章，需要登录且为文章作者或管理员")
    @RequireLogin
    @PutMapping("/{articleId}")
    public BaseResponse<ArticleVO> updateArticle(@PathVariable Long articleId,
                                                 @RequestBody ArticleRequest articleRequest,
                                                 HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        ArticleVO articleVO = articleService.updateArticle(articleId, articleRequest, request);
        return ResultUtils.success(articleVO, "文章更新成功");
    }

    /**
     * 删除文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除文章", description = "删除指定文章，需要登录且为文章作者或管理员")
    @RequireLogin
    @DeleteMapping("/{articleId}")
    public BaseResponse<Boolean> deleteArticle(@PathVariable Long articleId,
                                               HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = articleService.deleteArticle(articleId, request);
        return ResultUtils.success(result, "文章删除成功");
    }

    /**
     * 获取文章详情
     *
     * @param articleId 文章ID
     * @param password 文章密码（如果文章加密）
     * @param request HTTP请求对象
     * @return 文章详情
     */
    @Operation(summary = "获取文章详情", description = "获取指定文章的详细信息")
    @GetMapping("/{articleId}")
    public BaseResponse<ArticleVO> getArticleDetail(@PathVariable Long articleId,
                                                    @RequestParam(required = false) String password,
                                                    HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        ArticleVO articleVO = articleService.getArticleDetail(articleId, password, request);
        return ResultUtils.success(articleVO, "获取成功");
    }

    /**
     * 获取文章列表
     *
     * @param articleQueryRequest 查询请求
     * @return 文章列表
     */
    @Operation(summary = "获取文章列表", description = "分页获取文章列表，支持多种筛选和排序")
    @GetMapping
    public BaseResponse<PageVO<ArticleVO>> getArticleList(ArticleQueryRequest articleQueryRequest) {
        if (articleQueryRequest == null) {
            articleQueryRequest = new ArticleQueryRequest();
        }

        PageVO<ArticleVO> result = articleService.getArticleList(articleQueryRequest);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 获取热门文章
     *
     * @param limit 返回数量，默认10
     * @param days 统计天数，默认7
     * @return 热门文章列表
     */
    @Operation(summary = "获取热门文章", description = "获取指定天数内的热门文章")
    @GetMapping("/hot")
    public BaseResponse<List<ArticleVO>> getHotArticles(@RequestParam(defaultValue = "10") Integer limit,
                                                        @RequestParam(defaultValue = "7") Integer days) {
        List<ArticleVO> result = articleService.getHotArticles(limit, days);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 获取推荐文章
     *
     * @param limit 返回数量，默认10
     * @return 推荐文章列表
     */
    @Operation(summary = "获取推荐文章", description = "获取推荐文章列表")
    @GetMapping("/recommend")
    public BaseResponse<List<ArticleVO>> getRecommendArticles(@RequestParam(defaultValue = "10") Integer limit) {
        List<ArticleVO> result = articleService.getRecommendArticles(limit);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 获取置顶文章
     *
     * @return 置顶文章列表
     */
    @Operation(summary = "获取置顶文章", description = "获取所有置顶文章")
    @GetMapping("/top")
    public BaseResponse<List<ArticleVO>> getTopArticles() {
        List<ArticleVO> result = articleService.getTopArticles();
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 点赞结果
     */
    @Operation(summary = "点赞文章", description = "对指定文章进行点赞")
    @RequireLogin
    @PostMapping("/{articleId}/like")
    public BaseResponse<Boolean> likeArticle(@PathVariable Long articleId,
                                             HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = likeService.likeArticle(articleId, request);
        return ResultUtils.success(result, "点赞成功");
    }

    /**
     * 取消点赞
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 取消点赞结果
     */
    @Operation(summary = "取消点赞", description = "取消对指定文章的点赞")
    @RequireLogin
    @DeleteMapping("/{articleId}/like")
    public BaseResponse<Boolean> unlikeArticle(@PathVariable Long articleId,
                                               HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = likeService.unlikeArticle(articleId, request);
        return ResultUtils.success(result, "取消点赞成功");
    }

    /**
     * 检查点赞状态
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 点赞状态
     */
    @Operation(summary = "检查点赞状态", description = "检查当前用户是否已点赞指定文章")
    @RequireLogin
    @GetMapping("/{articleId}/like/status")
    public BaseResponse<Boolean> checkLikeStatus(@PathVariable Long articleId,
                                                 HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = likeService.checkArticleLikeStatus(articleId, request);
        return ResultUtils.success(result, "获取成功");
    }

    /**
     * 增加文章浏览量
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 操作结果
     */
    @Operation(summary = "增加文章浏览量", description = "增加文章浏览量")
    @PostMapping("/{articleId}/view")
    public BaseResponse<Boolean> addArticleView(@PathVariable Long articleId,
                                                HttpServletRequest request) {
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = articleService.addArticleView(articleId, request);
        return ResultUtils.success(result, "浏览量增加成功");
    }
}
