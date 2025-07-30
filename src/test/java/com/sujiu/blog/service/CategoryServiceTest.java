package com.sujiu.blog.service;

import com.sujiu.blog.model.dto.category.CategoryRequest;
import com.sujiu.blog.model.entity.Category;
import com.sujiu.blog.model.vo.category.CategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类服务测试
 *
 * @author sujiu
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryServiceTest {

    @Resource
    private CategoryService categoryService;

    @Test
    public void testValidateCategoryRequest() {
        // 测试正常情况
        CategoryRequest validRequest = new CategoryRequest();
        validRequest.setName("测试分类");
        validRequest.setSlug("test-category");
        validRequest.setDescription("这是一个测试分类");
        validRequest.setColor("#1890ff");
        validRequest.setSortOrder(1);

        assertDoesNotThrow(() -> categoryService.validateCategoryRequest(validRequest, false));

        // 测试名称为空
        CategoryRequest emptyNameRequest = new CategoryRequest();
        emptyNameRequest.setName("");
        
        assertThrows(Exception.class, () -> categoryService.validateCategoryRequest(emptyNameRequest, false));

        // 测试名称过长
        CategoryRequest longNameRequest = new CategoryRequest();
        longNameRequest.setName("a".repeat(51));
        
        assertThrows(Exception.class, () -> categoryService.validateCategoryRequest(longNameRequest, false));

        // 测试别名格式错误
        CategoryRequest invalidSlugRequest = new CategoryRequest();
        invalidSlugRequest.setName("测试分类");
        invalidSlugRequest.setSlug("invalid slug!");
        
        assertThrows(Exception.class, () -> categoryService.validateCategoryRequest(invalidSlugRequest, false));

        // 测试颜色格式错误
        CategoryRequest invalidColorRequest = new CategoryRequest();
        invalidColorRequest.setName("测试分类");
        invalidColorRequest.setColor("invalid-color");
        
        assertThrows(Exception.class, () -> categoryService.validateCategoryRequest(invalidColorRequest, false));
    }

    @Test
    public void testCheckCategoryNameExists() {
        // 测试不存在的名称
        assertFalse(categoryService.checkCategoryNameExists("不存在的分类", null));

        // 测试空名称
        assertFalse(categoryService.checkCategoryNameExists("", null));
        assertFalse(categoryService.checkCategoryNameExists(null, null));
    }

    @Test
    public void testCheckCategorySlugExists() {
        // 测试不存在的别名
        assertFalse(categoryService.checkCategorySlugExists("non-existent-slug", null));

        // 测试空别名
        assertFalse(categoryService.checkCategorySlugExists("", null));
        assertFalse(categoryService.checkCategorySlugExists(null, null));
    }

    @Test
    public void testConvertToVO() {
        // 测试空对象
        assertNull(categoryService.convertToVO(null, true));

        // 测试正常转换
        Category category = new Category();
        category.setId(1L);
        category.setName("测试分类");
        category.setSlug("test-category");
        category.setDescription("测试描述");
        category.setArticleCount(5);
        category.setStatus(1);

        CategoryVO vo = categoryService.convertToVO(category, true);
        assertNotNull(vo);
        assertEquals(category.getId(), vo.getId());
        assertEquals(category.getName(), vo.getName());
        assertEquals(category.getSlug(), vo.getSlug());
        assertEquals(category.getDescription(), vo.getDescription());
        assertEquals(category.getArticleCount(), vo.getArticleCount());
        assertEquals(category.getStatus(), vo.getStatus());

        // 测试不包含文章数量
        CategoryVO voWithoutCount = categoryService.convertToVO(category, false);
        assertNotNull(voWithoutCount);
        assertNull(voWithoutCount.getArticleCount());
    }

    @Test
    public void testBuildCategoryTree() {
        // 测试空列表
        List<CategoryVO> emptyTree = categoryService.buildCategoryTree(List.of(), 0L);
        assertTrue(emptyTree.isEmpty());

        // 测试单个分类
        Category category = new Category();
        category.setId(1L);
        category.setName("根分类");
        category.setParentId(0L);
        category.setStatus(1);

        List<CategoryVO> singleTree = categoryService.buildCategoryTree(List.of(category), 0L);
        assertEquals(1, singleTree.size());
        assertEquals("根分类", singleTree.get(0).getName());
    }

    @Test
    public void testGetCategoryList() {
        // 测试获取所有分类
        List<CategoryVO> allCategories = categoryService.getCategoryList(null, null, true);
        assertNotNull(allCategories);

        // 测试按状态筛选
        List<CategoryVO> activeCategories = categoryService.getCategoryList(null, 1, true);
        assertNotNull(activeCategories);

        // 测试按父分类筛选
        List<CategoryVO> topCategories = categoryService.getCategoryList(0L, null, true);
        assertNotNull(topCategories);
    }

    @Test
    public void testGetCategoryTree() {
        // 测试获取分类树
        List<CategoryVO> categoryTree = categoryService.getCategoryTree();
        assertNotNull(categoryTree);
    }
}
