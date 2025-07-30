package com.sujiu.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.category.CategoryRequest;
import com.sujiu.blog.model.entity.Category;
import com.sujiu.blog.model.vo.category.CategoryVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类服务
 *
 * @author sujiu
 */
public interface CategoryService extends IService<Category> {

    /**
     * 创建分类
     *
     * @param categoryRequest 分类创建请求
     * @param request HTTP请求对象
     * @return 分类详情
     */
    CategoryVO createCategory(CategoryRequest categoryRequest, HttpServletRequest request);

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param categoryRequest 分类更新请求
     * @param request HTTP请求对象
     * @return 分类详情
     */
    CategoryVO updateCategory(Long categoryId, CategoryRequest categoryRequest, HttpServletRequest request);

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteCategory(Long categoryId, HttpServletRequest request);

    /**
     * 获取分类详情
     *
     * @param categoryId 分类ID
     * @return 分类详情
     */
    CategoryVO getCategoryDetail(Long categoryId);

    /**
     * 获取分类列表
     *
     * @param parentId 父分类ID
     * @param status 状态
     * @param includeCount 是否包含文章数量统计
     * @return 分类列表
     */
    List<CategoryVO> getCategoryList(Long parentId, Integer status, Boolean includeCount);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 校验分类参数
     *
     * @param categoryRequest 分类请求
     * @param isUpdate 是否为更新操作
     */
    void validateCategoryRequest(CategoryRequest categoryRequest, boolean isUpdate);

    /**
     * 检查分类名称是否重复
     *
     * @param name 分类名称
     * @param excludeId 排除的分类ID（更新时使用）
     * @return 是否重复
     */
    Boolean checkCategoryNameExists(String name, Long excludeId);

    /**
     * 检查分类别名是否重复
     *
     * @param slug 分类别名
     * @param excludeId 排除的分类ID（更新时使用）
     * @return 是否重复
     */
    Boolean checkCategorySlugExists(String slug, Long excludeId);

    /**
     * 更新分类文章数量
     *
     * @param categoryId 分类ID
     * @param increment 增量（可为负数）
     */
    void updateCategoryArticleCount(Long categoryId, Integer increment);

    /**
     * 构建分类树结构
     *
     * @param categories 分类列表
     * @param parentId 父分类ID
     * @return 分类树
     */
    List<CategoryVO> buildCategoryTree(List<Category> categories, Long parentId);

    /**
     * 转换实体为VO
     *
     * @param category 分类实体
     * @param includeCount 是否包含文章数量
     * @return 分类VO
     */
    CategoryVO convertToVO(Category category, Boolean includeCount);
}
