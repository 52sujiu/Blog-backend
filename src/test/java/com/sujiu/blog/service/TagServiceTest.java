package com.sujiu.blog.service;

import com.sujiu.blog.model.dto.tag.TagRequest;
import com.sujiu.blog.model.entity.Tag;
import com.sujiu.blog.model.vo.tag.TagVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 标签服务测试
 *
 * @author sujiu
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TagServiceTest {

    @Resource
    private TagService tagService;

    @Test
    public void testValidateTagRequest() {
        // 测试正常情况
        TagRequest validRequest = new TagRequest();
        validRequest.setName("测试标签");
        validRequest.setSlug("test-tag");
        validRequest.setDescription("这是一个测试标签");
        validRequest.setColor("#87d068");

        assertDoesNotThrow(() -> tagService.validateTagRequest(validRequest, false));

        // 测试名称为空
        TagRequest emptyNameRequest = new TagRequest();
        emptyNameRequest.setName("");
        
        assertThrows(Exception.class, () -> tagService.validateTagRequest(emptyNameRequest, false));

        // 测试名称过长
        TagRequest longNameRequest = new TagRequest();
        longNameRequest.setName("a".repeat(51));
        
        assertThrows(Exception.class, () -> tagService.validateTagRequest(longNameRequest, false));

        // 测试别名格式错误
        TagRequest invalidSlugRequest = new TagRequest();
        invalidSlugRequest.setName("测试标签");
        invalidSlugRequest.setSlug("invalid slug!");
        
        assertThrows(Exception.class, () -> tagService.validateTagRequest(invalidSlugRequest, false));

        // 测试颜色格式错误
        TagRequest invalidColorRequest = new TagRequest();
        invalidColorRequest.setName("测试标签");
        invalidColorRequest.setColor("invalid-color");
        
        assertThrows(Exception.class, () -> tagService.validateTagRequest(invalidColorRequest, false));
    }

    @Test
    public void testCheckTagNameExists() {
        // 测试不存在的名称
        assertFalse(tagService.checkTagNameExists("不存在的标签", null));

        // 测试空名称
        assertFalse(tagService.checkTagNameExists("", null));
        assertFalse(tagService.checkTagNameExists(null, null));
    }

    @Test
    public void testCheckTagSlugExists() {
        // 测试不存在的别名
        assertFalse(tagService.checkTagSlugExists("non-existent-slug", null));

        // 测试空别名
        assertFalse(tagService.checkTagSlugExists("", null));
        assertFalse(tagService.checkTagSlugExists(null, null));
    }

    @Test
    public void testConvertToVO() {
        // 测试空对象
        assertNull(tagService.convertToVO(null));

        // 测试正常转换
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("测试标签");
        tag.setSlug("test-tag");
        tag.setDescription("测试描述");
        tag.setColor("#87d068");
        tag.setArticleCount(5);
        tag.setStatus(1);

        TagVO vo = tagService.convertToVO(tag);
        assertNotNull(vo);
        assertEquals(tag.getId(), vo.getId());
        assertEquals(tag.getName(), vo.getName());
        assertEquals(tag.getSlug(), vo.getSlug());
        assertEquals(tag.getDescription(), vo.getDescription());
        assertEquals(tag.getColor(), vo.getColor());
        assertEquals(tag.getArticleCount(), vo.getArticleCount());
        assertEquals(tag.getStatus(), vo.getStatus());
    }

    @Test
    public void testGetTagList() {
        // 测试获取所有标签
        List<TagVO> allTags = tagService.getTagList(null, null, null, null);
        assertNotNull(allTags);

        // 测试按状态筛选
        List<TagVO> activeTags = tagService.getTagList(null, 1, null, null);
        assertNotNull(activeTags);

        // 测试关键词搜索
        List<TagVO> searchTags = tagService.getTagList("Java", null, null, null);
        assertNotNull(searchTags);

        // 测试排序
        List<TagVO> sortedTags = tagService.getTagList(null, null, "name", "asc");
        assertNotNull(sortedTags);
    }

    @Test
    public void testGetHotTags() {
        // 测试获取热门标签
        List<TagVO> hotTags = tagService.getHotTags(10);
        assertNotNull(hotTags);

        // 测试默认参数
        List<TagVO> defaultHotTags = tagService.getHotTags(null);
        assertNotNull(defaultHotTags);

        // 测试超大限制
        List<TagVO> limitedHotTags = tagService.getHotTags(200);
        assertNotNull(limitedHotTags);
    }
}
