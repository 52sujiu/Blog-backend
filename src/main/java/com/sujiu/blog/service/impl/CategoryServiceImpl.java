package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.ArticleMapper;
import com.sujiu.blog.mapper.CategoryMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.category.CategoryRequest;
import com.sujiu.blog.model.entity.Category;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.category.CategoryVO;
import com.sujiu.blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 获取当前登录用户
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
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO createCategory(CategoryRequest categoryRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (categoryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 详细参数校验
        validateCategoryRequest(categoryRequest, false);

        // 4. 检查分类名称和别名是否重复
        if (checkCategoryNameExists(categoryRequest.getName(), null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称已存在");
        }

        if (StringUtils.isNotBlank(categoryRequest.getSlug()) && 
            checkCategorySlugExists(categoryRequest.getSlug(), null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类别名已存在");
        }

        // 5. 检查父分类是否存在
        if (categoryRequest.getParentId() != null && categoryRequest.getParentId() > 0) {
            Category parentCategory = this.getById(categoryRequest.getParentId());
            if (parentCategory == null || parentCategory.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父分类不存在或已禁用");
            }
        }

        // 6. 创建分类实体
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        
        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        category.setArticleCount(0);
        category.setStatus(1);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());

        // 7. 保存到数据库
        boolean result = this.save(category);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分类创建失败");
        }

        // 8. 记录日志
        log.info("管理员 {} 创建了分类：{}", user.getId(), category.getName());

        // 9. 返回分类详情
        return convertToVO(category, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO updateCategory(Long categoryId, CategoryRequest categoryRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }
        if (categoryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 检查分类是否存在
        Category existingCategory = this.getById(categoryId);
        if (existingCategory == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 4. 详细参数校验
        validateCategoryRequest(categoryRequest, true);

        // 5. 检查分类名称和别名是否重复（排除当前分类）
        if (checkCategoryNameExists(categoryRequest.getName(), categoryId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称已存在");
        }

        if (StringUtils.isNotBlank(categoryRequest.getSlug()) && 
            checkCategorySlugExists(categoryRequest.getSlug(), categoryId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类别名已存在");
        }

        // 6. 检查父分类是否存在（不能设置自己为父分类）
        if (categoryRequest.getParentId() != null && categoryRequest.getParentId() > 0) {
            if (categoryRequest.getParentId().equals(categoryId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能设置自己为父分类");
            }
            Category parentCategory = this.getById(categoryRequest.getParentId());
            if (parentCategory == null || parentCategory.getStatus() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "父分类不存在或已禁用");
            }
        }

        // 7. 更新分类信息
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setId(categoryId);
        category.setUpdateTime(new Date());
        
        // 保持原有的文章数量和状态
        category.setArticleCount(existingCategory.getArticleCount());
        category.setStatus(existingCategory.getStatus());
        category.setCreateTime(existingCategory.getCreateTime());

        // 8. 保存到数据库
        boolean result = this.updateById(category);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分类更新失败");
        }

        // 9. 记录日志
        log.info("管理员 {} 更新了分类：{}", user.getId(), category.getName());

        // 10. 返回分类详情
        return convertToVO(category, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long categoryId, HttpServletRequest request) {
        // 1. 参数校验
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 检查分类是否存在
        Category category = this.getById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 4. 检查是否有子分类
        QueryWrapper<Category> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parent_id", categoryId);
        long childCount = this.count(childWrapper);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下还有子分类，无法删除");
        }

        // 5. 检查是否有文章使用该分类
        if (category.getArticleCount() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该分类下还有文章，无法删除");
        }

        // 6. 执行逻辑删除
        boolean result = this.removeById(categoryId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分类删除失败");
        }

        // 7. 记录日志
        log.info("管理员 {} 删除了分类：{}", user.getId(), category.getName());

        return true;
    }

    @Override
    public CategoryVO getCategoryDetail(Long categoryId) {
        // 1. 参数校验
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类ID不能为空");
        }

        // 2. 查询分类信息
        Category category = this.getById(categoryId);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        // 3. 转换为VO并返回
        return convertToVO(category, true);
    }

    @Override
    public List<CategoryVO> getCategoryList(Long parentId, Integer status, Boolean includeCount) {
        // 1. 构建查询条件
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        
        // 父分类筛选
        if (parentId != null) {
            queryWrapper.eq("parent_id", parentId);
        }
        
        // 状态筛选
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 排序
        queryWrapper.orderByAsc("sort_order", "created_time");

        // 2. 查询分类列表
        List<Category> categories = this.list(queryWrapper);

        // 3. 转换为VO列表
        return categories.stream()
                .map(category -> convertToVO(category, includeCount != null ? includeCount : false))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 1. 查询所有正常状态的分类
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByAsc("sort_order", "created_time");

        List<Category> allCategories = this.list(queryWrapper);

        // 2. 构建分类树
        return buildCategoryTree(allCategories, 0L);
    }

    @Override
    public void validateCategoryRequest(CategoryRequest categoryRequest, boolean isUpdate) {
        String name = categoryRequest.getName();
        String slug = categoryRequest.getSlug();
        String color = categoryRequest.getColor();

        // 分类名称校验
        if (StringUtils.isBlank(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称不能为空");
        }
        if (name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类名称不能超过50个字符");
        }

        // 分类别名校验
        if (StringUtils.isNotBlank(slug)) {
            if (slug.length() > 100) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类别名不能超过100个字符");
            }
            // 检查别名格式（只允许字母、数字、连字符）
            if (!slug.matches("^[a-zA-Z0-9-]+$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类别名只能包含字母、数字和连字符");
            }
        }

        // 分类描述校验
        if (StringUtils.isNotBlank(categoryRequest.getDescription()) &&
            categoryRequest.getDescription().length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类描述不能超过500个字符");
        }

        // 分类颜色校验
        if (StringUtils.isNotBlank(color)) {
            if (!color.matches("^#[0-9a-fA-F]{6}$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类颜色格式不正确，请使用十六进制格式（如：#1890ff）");
            }
        }

        // 排序值校验
        if (categoryRequest.getSortOrder() != null && categoryRequest.getSortOrder() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序值不能为负数");
        }
    }

    @Override
    public Boolean checkCategoryNameExists(String name, Long excludeId) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }

        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean checkCategorySlugExists(String slug, Long excludeId) {
        if (StringUtils.isBlank(slug)) {
            return false;
        }

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("slug", slug);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }

        return this.count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryArticleCount(Long categoryId, Integer increment) {
        if (categoryId == null || increment == null || increment == 0) {
            return;
        }

        Category category = this.getById(categoryId);
        if (category == null) {
            return;
        }

        int newCount = Math.max(0, category.getArticleCount() + increment);
        category.setArticleCount(newCount);
        category.setUpdateTime(new Date());

        this.updateById(category);

        log.debug("更新分类 {} 的文章数量：{} -> {}", categoryId, category.getArticleCount() - increment, newCount);
    }

    @Override
    public List<CategoryVO> buildCategoryTree(List<Category> categories, Long parentId) {
        List<CategoryVO> result = new ArrayList<>();

        for (Category category : categories) {
            if (category.getParentId().equals(parentId)) {
                CategoryVO categoryVO = convertToVO(category, true);

                // 递归构建子分类
                List<CategoryVO> children = buildCategoryTree(categories, category.getId());
                if (!children.isEmpty()) {
                    categoryVO.setChildren(children.stream()
                            .map(child -> {
                                CategoryVO.ChildCategory childCategory = new CategoryVO.ChildCategory();
                                childCategory.setId(child.getId());
                                childCategory.setName(child.getName());
                                childCategory.setSlug(child.getSlug());
                                childCategory.setArticleCount(child.getArticleCount());
                                return childCategory;
                            })
                            .collect(Collectors.toList()));
                }

                result.add(categoryVO);
            }
        }

        return result;
    }

    @Override
    public CategoryVO convertToVO(Category category, Boolean includeCount) {
        if (category == null) {
            return null;
        }

        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);

        // 如果不需要包含文章数量，则设置为null
        if (includeCount == null || !includeCount) {
            categoryVO.setArticleCount(null);
        }

        return categoryVO;
    }
}
