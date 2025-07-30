package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.article.AdminArticleQueryRequest;
import com.sujiu.blog.model.dto.article.ArticleAuditRequest;
import com.sujiu.blog.model.dto.article.ArticleOfflineRequest;
import com.sujiu.blog.model.vo.article.AdminArticleVO;
import com.sujiu.blog.model.vo.common.PageVO;
import com.sujiu.blog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员文章管理控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/admin/articles")
@Slf4j
@Tag(name = "管理员文章管理", description = "管理员文章管理相关接口")
public class AdminArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取所有文章列表
     *
     * @param adminArticleQueryRequest 文章查询请求
     * @return 文章列表
     */
    @Operation(summary = "获取所有文章列表", description = "管理员获取所有文章列表，支持分页、搜索、筛选")
    @RequireAdmin
    @GetMapping
    public BaseResponse<PageVO<AdminArticleVO>> listArticles(AdminArticleQueryRequest adminArticleQueryRequest) {
        if (adminArticleQueryRequest == null) {
            adminArticleQueryRequest = new AdminArticleQueryRequest();
        }

        PageVO<AdminArticleVO> result = articleService.listArticlesByAdmin(adminArticleQueryRequest);
        return ResultUtils.success(result, "获取文章列表成功");
    }

    /**
     * 审核文章
     *
     * @param articleId 文章ID
     * @param articleAuditRequest 文章审核请求
     * @param request HTTP请求对象
     * @return 审核结果
     */
    @Operation(summary = "审核文章", description = "管理员审核文章（通过/拒绝）")
    @RequireAdmin
    @PutMapping("/{articleId}/audit")
    public BaseResponse<Boolean> auditArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId,
            @RequestBody ArticleAuditRequest articleAuditRequest,
            HttpServletRequest request) {
        
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleAuditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = articleService.auditArticle(articleId, articleAuditRequest, request);
        return ResultUtils.success(result, "文章审核成功");
    }

    /**
     * 下架文章
     *
     * @param articleId 文章ID
     * @param articleOfflineRequest 文章下架请求
     * @param request HTTP请求对象
     * @return 下架结果
     */
    @Operation(summary = "下架文章", description = "管理员下架已发布的文章")
    @RequireAdmin
    @PutMapping("/{articleId}/offline")
    public BaseResponse<Boolean> offlineArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId,
            @RequestBody ArticleOfflineRequest articleOfflineRequest,
            HttpServletRequest request) {
        
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }
        if (articleOfflineRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        Boolean result = articleService.offlineArticle(articleId, articleOfflineRequest, request);
        return ResultUtils.success(result, "文章下架成功");
    }

    /**
     * 删除文章
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除文章", description = "管理员删除文章（逻辑删除）")
    @RequireAdmin
    @DeleteMapping("/{articleId}")
    public BaseResponse<Boolean> deleteArticle(
            @Parameter(description = "文章ID", required = true) @PathVariable Long articleId,
            HttpServletRequest request) {
        
        if (articleId == null || articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文章ID不能为空");
        }

        Boolean result = articleService.deleteArticleByAdmin(articleId, request);
        return ResultUtils.success(result, "文章删除成功");
    }
}
