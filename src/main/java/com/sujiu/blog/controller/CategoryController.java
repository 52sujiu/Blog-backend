package com.sujiu.blog.controller;

import com.sujiu.blog.annotation.RequireAdmin;
import com.sujiu.blog.common.BaseResponse;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.common.ResultUtils;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.model.dto.category.CategoryRequest;
import com.sujiu.blog.model.vo.category.CategoryVO;
import com.sujiu.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类管理控制器
 *
 * @author sujiu
 */
@RestController
@RequestMapping("/categories")
@Slf4j
@Tag(name = "分类管理", description = "分类管理相关接口")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 创建分类
     *
     * @param categoryRequest 分类创建请求
     * @param request HTTP请求对象
     * @return 分类详情
     */
    @Operation(summary = "创建分类", description = "创建新分类，需要管理员权限")
    @RequireAdmin
    @PostMapping
    public BaseResponse<CategoryVO> createCategory(@RequestBody CategoryRequest categoryRequest,
                                                   HttpServletRequest request) {
        if (categoryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        CategoryVO categoryVO = categoryService.createCategory(categoryRequest, request);
        return ResultUtils.success(categoryVO, "分类创建成功");
    }

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param categoryRequest 分类更新请求
     * @param request HTTP请求对象
     * @return 分类详情
     */
    @Operation(summary = "更新分类", description = "更新指定分类，需要管理员权限")
    @RequireAdmin
    @PutMapping("/{categoryId}")
    public BaseResponse<CategoryVO> updateCategory(@Parameter(description = "分类ID") @PathVariable Long categoryId,
                                                   @RequestBody CategoryRequest categoryRequest,
                                                   HttpServletRequest request) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }
        if (categoryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        CategoryVO categoryVO = categoryService.updateCategory(categoryId, categoryRequest, request);
        return ResultUtils.success(categoryVO, "分类更新成功");
    }

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    @Operation(summary = "删除分类", description = "删除指定分类，需要管理员权限")
    @RequireAdmin
    @DeleteMapping("/{categoryId}")
    public BaseResponse<Boolean> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long categoryId,
                                                HttpServletRequest request) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        Boolean result = categoryService.deleteCategory(categoryId, request);
        return ResultUtils.success(result, "分类删除成功");
    }

    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    @Operation(summary = "获取分类详情", description = "获取指定分类的详细信息")
    @GetMapping("/{categoryId}")
    public BaseResponse<CategoryVO> getCategoryDetail(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        CategoryVO categoryVO = categoryService.getCategoryDetail(categoryId);
        return ResultUtils.success(categoryVO, "获取成功");
    }

    /**
     * 获取分类列表
     *
     * @param parentId 父分类ID
     * @param status 状态
     * @param includeCount 是否包含文章数量统计
     * @return 分类列表
     */
    @Operation(summary = "获取分类列表", description = "获取分类列表，支持按父分类和状态筛选")
    @GetMapping
    public BaseResponse<List<CategoryVO>> getCategoryList(
            @Parameter(description = "父分类ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "状态：1-正常，0-禁用") @RequestParam(required = false) Integer status,
            @Parameter(description = "是否包含文章数量统计") @RequestParam(required = false, defaultValue = "true") Boolean includeCount) {
        
        List<CategoryVO> categoryList = categoryService.getCategoryList(parentId, status, includeCount);
        return ResultUtils.success(categoryList, "获取成功");
    }

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    @GetMapping("/tree")
    public BaseResponse<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> categoryTree = categoryService.getCategoryTree();
        return ResultUtils.success(categoryTree, "获取成功");
    }
}
