package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.constant.UserConstant;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.TagMapper;
import com.sujiu.blog.mapper.UserMapper;
import com.sujiu.blog.model.dto.tag.TagQueryRequest;
import com.sujiu.blog.model.dto.tag.TagRequest;
import com.sujiu.blog.model.entity.Tag;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.tag.TagVO;
import com.sujiu.blog.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private UserMapper userMapper;

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
    public TagVO createTag(TagRequest tagRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (tagRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 详细参数校验
        validateTagRequest(tagRequest, false);

        // 4. 检查标签名称和别名是否重复
        if (checkTagNameExists(tagRequest.getName(), null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名称已存在");
        }

        if (StringUtils.isNotBlank(tagRequest.getSlug()) && 
            checkTagSlugExists(tagRequest.getSlug(), null)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签别名已存在");
        }

        // 5. 创建标签实体
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagRequest, tag);
        
        // 设置默认值
        tag.setArticleCount(0);
        tag.setStatus(1);
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());

        // 6. 保存到数据库
        boolean result = this.save(tag);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签创建失败");
        }

        // 7. 记录日志
        log.info("管理员 {} 创建了标签：{}", user.getId(), tag.getName());

        // 8. 返回标签详情
        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagVO updateTag(Long tagId, TagRequest tagRequest, HttpServletRequest request) {
        // 1. 参数校验
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }
        if (tagRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 检查标签是否存在
        Tag existingTag = this.getById(tagId);
        if (existingTag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        }

        // 4. 详细参数校验
        validateTagRequest(tagRequest, true);

        // 5. 检查标签名称和别名是否重复（排除当前标签）
        if (checkTagNameExists(tagRequest.getName(), tagId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名称已存在");
        }

        if (StringUtils.isNotBlank(tagRequest.getSlug()) && 
            checkTagSlugExists(tagRequest.getSlug(), tagId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签别名已存在");
        }

        // 6. 更新标签信息
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagRequest, tag);
        tag.setId(tagId);
        tag.setUpdateTime(new Date());
        
        // 保持原有的文章数量和状态
        tag.setArticleCount(existingTag.getArticleCount());
        tag.setStatus(existingTag.getStatus());
        tag.setCreateTime(existingTag.getCreateTime());

        // 7. 保存到数据库
        boolean result = this.updateById(tag);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签更新失败");
        }

        // 8. 记录日志
        log.info("管理员 {} 更新了标签：{}", user.getId(), tag.getName());

        // 9. 返回标签详情
        return convertToVO(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTag(Long tagId, HttpServletRequest request) {
        // 1. 参数校验
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }

        // 2. 获取当前登录用户（权限已通过@RequireAdmin验证）
        User user = getCurrentLoginUser(request);

        // 3. 检查标签是否存在
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        }

        // 4. 检查是否有文章使用该标签
        if (tag.getArticleCount() > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该标签下还有文章，无法删除");
        }

        // 5. 执行逻辑删除
        boolean result = this.removeById(tagId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "标签删除失败");
        }

        // 6. 记录日志
        log.info("管理员 {} 删除了标签：{}", user.getId(), tag.getName());

        return true;
    }

    @Override
    public TagVO getTagDetail(Long tagId) {
        // 1. 参数校验
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不能为空");
        }

        // 2. 查询标签信息
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        }

        // 3. 转换为VO并返回
        return convertToVO(tag);
    }

    @Override
    public Page<TagVO> getTagPage(TagQueryRequest tagQueryRequest) {
        // 1. 参数校验
        if (tagQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询参数为空");
        }

        // 2. 构建查询条件
        QueryWrapper<Tag> queryWrapper = getQueryWrapper(tagQueryRequest);

        // 3. 分页查询
        Page<Tag> tagPage = this.page(new Page<>(tagQueryRequest.getCurrent(), tagQueryRequest.getSize()), queryWrapper);

        // 4. 转换为VO分页对象
        Page<TagVO> tagVOPage = new Page<>(tagPage.getCurrent(), tagPage.getSize(), tagPage.getTotal());
        List<TagVO> tagVOList = tagPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        tagVOPage.setRecords(tagVOList);

        return tagVOPage;
    }

    @Override
    public List<TagVO> getTagList(String keyword, Integer status, String sortField, String sortOrder) {
        // 1. 构建查询条件
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or()
                    .like("description", keyword));
        }
        
        // 状态筛选
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            switch (sortField) {
                case "name":
                    queryWrapper.orderBy(true, isAsc, "name");
                    break;
                case "articleCount":
                    queryWrapper.orderBy(true, isAsc, "article_count");
                    break;
                case "createTime":
                    queryWrapper.orderBy(true, isAsc, "created_time");
                    break;
                default:
                    queryWrapper.orderByDesc("created_time");
                    break;
            }
        } else {
            queryWrapper.orderByDesc("created_time");
        }

        // 2. 查询标签列表
        List<Tag> tags = this.list(queryWrapper);

        // 3. 转换为VO列表
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getHotTags(Integer limit) {
        // 1. 参数校验和默认值设置
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (limit > 100) {
            limit = 100; // 限制最大返回数量
        }

        // 2. 构建查询条件：按文章数量降序排列
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询正常状态的标签
        queryWrapper.gt("article_count", 0); // 只查询有文章的标签
        queryWrapper.orderByDesc("article_count", "created_time");
        queryWrapper.last("LIMIT " + limit);

        // 3. 查询热门标签
        List<Tag> hotTags = this.list(queryWrapper);

        // 4. 转换为VO列表
        return hotTags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void validateTagRequest(TagRequest tagRequest, boolean isUpdate) {
        String name = tagRequest.getName();
        String slug = tagRequest.getSlug();
        String color = tagRequest.getColor();

        // 标签名称校验
        if (StringUtils.isBlank(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名称不能为空");
        }
        if (name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签名称不能超过50个字符");
        }

        // 标签别名校验
        if (StringUtils.isNotBlank(slug)) {
            if (slug.length() > 100) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签别名不能超过100个字符");
            }
            // 检查别名格式（只允许字母、数字、连字符）
            if (!slug.matches("^[a-zA-Z0-9-]+$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签别名只能包含字母、数字和连字符");
            }
        }

        // 标签描述校验
        if (StringUtils.isNotBlank(tagRequest.getDescription()) &&
            tagRequest.getDescription().length() > 500) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签描述不能超过500个字符");
        }

        // 标签颜色校验
        if (StringUtils.isNotBlank(color)) {
            if (!color.matches("^#[0-9a-fA-F]{6}$")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签颜色格式不正确，请使用十六进制格式（如：#87d068）");
            }
        }
    }

    @Override
    public Boolean checkTagNameExists(String name, Long excludeId) {
        if (StringUtils.isBlank(name)) {
            return false;
        }

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }

        return this.count(queryWrapper) > 0;
    }

    @Override
    public Boolean checkTagSlugExists(String slug, Long excludeId) {
        if (StringUtils.isBlank(slug)) {
            return false;
        }

        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("slug", slug);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }

        return this.count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTagArticleCount(Long tagId, Integer increment) {
        if (tagId == null || increment == null || increment == 0) {
            return;
        }

        Tag tag = this.getById(tagId);
        if (tag == null) {
            return;
        }

        int newCount = Math.max(0, tag.getArticleCount() + increment);
        tag.setArticleCount(newCount);
        tag.setUpdateTime(new Date());

        this.updateById(tag);

        log.debug("更新标签 {} 的文章数量：{} -> {}", tagId, tag.getArticleCount() - increment, newCount);
    }

    @Override
    public QueryWrapper<Tag> getQueryWrapper(TagQueryRequest tagQueryRequest) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();

        if (tagQueryRequest == null) {
            return queryWrapper;
        }

        String keyword = tagQueryRequest.getKeyword();
        Integer status = tagQueryRequest.getStatus();
        String sortField = tagQueryRequest.getSortField();
        String sortOrder = tagQueryRequest.getSortOrder();

        // 关键词搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", keyword)
                    .or()
                    .like("description", keyword));
        }

        // 状态筛选
        if (status != null) {
            queryWrapper.eq("status", status);
        }

        // 排序
        if (StringUtils.isNotBlank(sortField)) {
            boolean isAsc = !"desc".equalsIgnoreCase(sortOrder);
            switch (sortField) {
                case "name":
                    queryWrapper.orderBy(true, isAsc, "name");
                    break;
                case "articleCount":
                    queryWrapper.orderBy(true, isAsc, "article_count");
                    break;
                case "createTime":
                    queryWrapper.orderBy(true, isAsc, "created_time");
                    break;
                default:
                    queryWrapper.orderByDesc("created_time");
                    break;
            }
        } else {
            queryWrapper.orderByDesc("created_time");
        }

        return queryWrapper;
    }

    @Override
    public TagVO convertToVO(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tag, tagVO);
        return tagVO;
    }
}
