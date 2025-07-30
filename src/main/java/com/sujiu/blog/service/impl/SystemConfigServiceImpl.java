package com.sujiu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sujiu.blog.common.ErrorCode;
import com.sujiu.blog.exception.BusinessException;
import com.sujiu.blog.mapper.SystemConfigMapper;
import com.sujiu.blog.model.dto.system.SystemConfigUpdateRequest;
import com.sujiu.blog.model.entity.SystemConfig;
import com.sujiu.blog.model.entity.User;
import com.sujiu.blog.model.vo.system.SystemConfigVO;
import com.sujiu.blog.service.SystemConfigService;
import com.sujiu.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现
 *
 * @author sujiu
 */
@Service
@Slf4j
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> 
        implements SystemConfigService {

    @Resource
    private UserService userService;

    /**
     * 配置缓存
     */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /**
     * 系统配置键常量
     */
    private static final String SITE_NAME = "site.name";
    private static final String SITE_DESCRIPTION = "site.description";
    private static final String SITE_KEYWORDS = "site.keywords";
    private static final String COMMENT_NEED_AUDIT = "comment.need_audit";
    private static final String ARTICLE_NEED_AUDIT = "article.need_audit";
    private static final String UPLOAD_MAX_SIZE = "upload.max_size";
    private static final String USER_DEFAULT_AVATAR = "user.default_avatar";

    /**
     * 初始化配置缓存
     */
    @PostConstruct
    public void initConfigCache() {
        refreshConfigCache();
        log.info("系统配置缓存初始化完成，共加载 {} 个配置项", configCache.size());
    }

    @Override
    public SystemConfigVO getSystemConfig() {
        SystemConfigVO configVO = new SystemConfigVO();
        
        configVO.setSiteName(getStringConfig(SITE_NAME, "个人博客系统"));
        configVO.setSiteDescription(getStringConfig(SITE_DESCRIPTION, "一个基于Spring Boot的个人博客系统"));
        configVO.setSiteKeywords(getStringConfig(SITE_KEYWORDS, "博客,技术分享,个人网站"));
        configVO.setCommentNeedAudit(getBooleanConfig(COMMENT_NEED_AUDIT, true));
        configVO.setArticleNeedAudit(getBooleanConfig(ARTICLE_NEED_AUDIT, false));
        configVO.setUploadMaxSize(getLongConfig(UPLOAD_MAX_SIZE, 10485760L));
        configVO.setUserDefaultAvatar(getStringConfig(USER_DEFAULT_AVATAR, "/static/images/default-avatar.png"));

        return configVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemConfigVO updateSystemConfig(SystemConfigUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新请求不能为空");
        }

        // 获取当前登录用户（权限已通过@RequireAdmin验证）
        User currentUser = userService.getCurrentLoginUser(request);

        log.info("管理员 {} 开始更新系统配置", currentUser.getId());

        // 更新各个配置项
        if (StringUtils.isNotBlank(updateRequest.getSiteName())) {
            setConfigValue(SITE_NAME, updateRequest.getSiteName());
        }
        
        if (StringUtils.isNotBlank(updateRequest.getSiteDescription())) {
            setConfigValue(SITE_DESCRIPTION, updateRequest.getSiteDescription());
        }
        
        if (StringUtils.isNotBlank(updateRequest.getSiteKeywords())) {
            setConfigValue(SITE_KEYWORDS, updateRequest.getSiteKeywords());
        }
        
        if (updateRequest.getCommentNeedAudit() != null) {
            setConfigValue(COMMENT_NEED_AUDIT, updateRequest.getCommentNeedAudit().toString());
        }
        
        if (updateRequest.getArticleNeedAudit() != null) {
            setConfigValue(ARTICLE_NEED_AUDIT, updateRequest.getArticleNeedAudit().toString());
        }
        
        if (updateRequest.getUploadMaxSize() != null && updateRequest.getUploadMaxSize() > 0) {
            setConfigValue(UPLOAD_MAX_SIZE, updateRequest.getUploadMaxSize().toString());
        }
        
        if (StringUtils.isNotBlank(updateRequest.getUserDefaultAvatar())) {
            setConfigValue(USER_DEFAULT_AVATAR, updateRequest.getUserDefaultAvatar());
        }

        // 刷新缓存
        refreshConfigCache();

        log.info("管理员 {} 更新系统配置成功", currentUser.getId());

        // 返回更新后的配置
        return getSystemConfig();
    }

    @Override
    public String getConfigValue(String configKey) {
        if (StringUtils.isBlank(configKey)) {
            return null;
        }
        return configCache.get(configKey);
    }

    @Override
    public String getStringConfig(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    @Override
    public Boolean getBooleanConfig(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public Long getLongConfig(String configKey, Long defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("配置值转换失败，configKey: {}, value: {}", configKey, value);
            return defaultValue;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setConfigValue(String configKey, String configValue) {
        if (StringUtils.isBlank(configKey)) {
            return false;
        }

        UpdateWrapper<SystemConfig> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("config_key", configKey)
                    .set("config_value", configValue);

        boolean result = this.update(updateWrapper);
        if (result) {
            // 更新缓存
            configCache.put(configKey, configValue);
        }
        return result;
    }

    @Override
    public void refreshConfigCache() {
        try {
            List<SystemConfig> configs = baseMapper.getAllSystemConfigs();
            configCache.clear();
            
            for (SystemConfig config : configs) {
                if (StringUtils.isNotBlank(config.getConfigKey())) {
                    configCache.put(config.getConfigKey(), config.getConfigValue());
                }
            }
            
            log.info("配置缓存刷新成功，共加载 {} 个配置项", configCache.size());
        } catch (Exception e) {
            log.error("配置缓存刷新失败", e);
        }
    }
}
