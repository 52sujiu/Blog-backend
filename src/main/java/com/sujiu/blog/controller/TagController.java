package com.sujiu.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.tag.TagQueryRequest;
import com.sujiu.blog.model.dto.tag.TagRequest;
import com.sujiu.blog.model.vo.tag.TagVO;
import com.sujiu.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 标签管理控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/tags")
@Slf4j
@Tag(name = "标签管理", description = "标签管理相关接口")
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 创建标签
     *
     * @param tagRequest 标签创建请求
     * @param request HTTP请求对象
     * @return 标签详情
     */
    @Operation(summary = "创建标签", description = "创建新标签，需要管理员权限")
    @RequireAdmin
    @PostMapping
    public BaseResponse<TagVO> createTag(@RequestBody TagRequest tagRequest,
                                         HttpServletRequest request) {
        if (tagRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        TagVO tagVO = tagService.createTag(tagRequest, request);
        return ResultUtils.success(tagVO, "标签创建成功");
    }

    /**
     * 更新标签
     *
     * @param tagId 标签ID
     * @param tagRequest 标签更新请求
     * @param request HTTP请求对象
     * @return 标签详情
     */
    @Operation(summary = "更新标签", description = "更新指定标签，需要管理员权限")
    @RequireAdmin
    @PutMapping("/{tagId}")
    public BaseResponse<TagVO> updateTag(@Parameter(description = "标签ID") @PathVariable Long tagId,
                                         @RequestBody TagRequest tagRequest,
                                         HttpServletRequest request) {
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }
        if (tagRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        TagVO tagVO = tagService.updateTag(tagId, tagRequest, request);
        return ResultUtils.success(tagVO, "标签更新成功");
    }

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除标签", description = "删除指定标签，需要管理员权限")
    @RequireAdmin
    @DeleteMapping("/{tagId}")
    public BaseResponse<Boolean> deleteTag(@Parameter(description = "标签ID") @PathVariable Long tagId,
                                           HttpServletRequest request) {
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }

        Boolean result = tagService.deleteTag(tagId, request);
        return ResultUtils.success(result, "标签删除成功");
    }

    /**
     * 获取标签详情
     *
     * @param tagId 标签ID
     * @return 标签详情
     */
    @Operation(summary = "获取标签详情", description = "获取指定标签的详细信息")
    @GetMapping("/{tagId}")
    public BaseResponse<TagVO> getTagDetail(@Parameter(description = "标签ID") @PathVariable Long tagId) {
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }

        TagVO tagVO = tagService.getTagDetail(tagId);
        return ResultUtils.success(tagVO, "获取成功");
    }

    /**
     * 获取标签列表（分页）
     *
     * @param tagQueryRequest 查询请求
     * @return 标签分页列表
     */
    @Operation(summary = "获取标签列表（分页）", description = "分页获取标签列表，支持关键词搜索和排序")
    @PostMapping("/page")
    public BaseResponse<Page<TagVO>> getTagPage(@RequestBody TagQueryRequest tagQueryRequest) {
        if (tagQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数为空");
        }

        Page<TagVO> tagPage = tagService.getTagPage(tagQueryRequest);
        return ResultUtils.success(tagPage, "获取成功");
    }

    /**
     * 获取标签列表
     *
     * @param keyword 搜索关键词
     * @param status 状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 标签列表
     */
    @Operation(summary = "获取标签列表", description = "获取标签列表，支持关键词搜索、状态筛选和排序")
    @GetMapping
    public BaseResponse<List<TagVO>> getTagList(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态：1-正常，0-禁用") @RequestParam(required = false) Integer status,
            @Parameter(description = "排序字段：name-名称，articleCount-文章数量，createTime-创建时间") @RequestParam(required = false) String sortField,
            @Parameter(description = "排序方式：asc-升序，desc-降序") @RequestParam(required = false) String sortOrder) {
        
        List<TagVO> tagList = tagService.getTagList(keyword, status, sortField, sortOrder);
        return ResultUtils.success(tagList, "获取成功");
    }

    /**
     * 获取热门标签
     *
     * @param limit 返回数量，默认20
     * @return 热门标签列表
     */
    @Operation(summary = "获取热门标签", description = "获取热门标签列表，按文章数量降序排列")
    @GetMapping("/hot")
    public BaseResponse<List<TagVO>> getHotTags(
            @Parameter(description = "返回数量，默认20，最大100") @RequestParam(required = false, defaultValue = "20") Integer limit) {
        
        List<TagVO> hotTags = tagService.getHotTags(limit);
        return ResultUtils.success(hotTags, "获取成功");
    }
}
