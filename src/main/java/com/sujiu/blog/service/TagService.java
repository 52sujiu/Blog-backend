package com.sujiu.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.tag.TagQueryRequest;
import com.sujiu.blog.model.dto.tag.TagRequest;
import com.sujiu.blog.model.entity.Tag;
import com.sujiu.blog.model.vo.tag.TagVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 标签服务
 *
 * @author sujiu
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     *
     * @param tagRequest 标签创建请求
     * @param request HTTP请求对象
     * @return 标签详情
     */
    TagVO createTag(TagRequest tagRequest, HttpServletRequest request);

    /**
     * 更新标签
     *
     * @param tagId 标签ID
     * @param tagRequest 标签更新请求
     * @param request HTTP请求对象
     * @return 标签详情
     */
    TagVO updateTag(Long tagId, TagRequest tagRequest, HttpServletRequest request);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @param request HTTP请求对象
     * @return 删除结果
     */
    Boolean deleteTag(Long tagId, HttpServletRequest request);

    /**
     * 获取标签详情
     *
     * @param tagId 标签ID
     * @return 标签详情
     */
    TagVO getTagDetail(Long tagId);

    /**
     * 获取标签列表（分页）
     *
     * @param tagQueryRequest 查询请求
     * @return 标签分页列表
     */
    Page<TagVO> getTagPage(TagQueryRequest tagQueryRequest);

    /**
     * 获取标签列表
     *
     * @param keyword 搜索关键词
     * @param status 状态
     * @param sortField 排序字段
     * @param sortOrder 排序方式
     * @return 标签列表
     */
    List<TagVO> getTagList(String keyword, Integer status, String sortField, String sortOrder);

    /**
     * 获取热门标签
     *
     * @param limit 返回数量
     * @return 热门标签列表
     */
    List<TagVO> getHotTags(Integer limit);

    /**
     * 校验标签参数
     *
     * @param tagRequest 标签请求
     * @param isUpdate 是否为更新操作
     */
    void validateTagRequest(TagRequest tagRequest, boolean isUpdate);

    /**
     * 检查标签名称是否重复
     *
     * @param name 标签名称
     * @param excludeId 排除的标签ID（更新时使用）
     * @return 是否重复
     */
    Boolean checkTagNameExists(String name, Long excludeId);

    /**
     * 检查标签别名是否重复
     *
     * @param slug 标签别名
     * @param excludeId 排除的标签ID（更新时使用）
     * @return 是否重复
     */
    Boolean checkTagSlugExists(String slug, Long excludeId);

    /**
     * 更新标签文章数量
     *
     * @param tagId 标签ID
     * @param increment 增量（可为负数）
     */
    void updateTagArticleCount(Long tagId, Integer increment);

    /**
     * 获取查询包装器
     *
     * @param tagQueryRequest 查询请求
     * @return 查询包装器
     */
    QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest);

    /**
     * 转换实体为VO
     *
     * @param tag 标签实体
     * @return 标签VO
     */
    TagVO convertToVO(Tag tag);
}
