package com.sujiu.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sujiu.blog.model.dto.system.SystemConfigUpdateRequest;
import com.sujiu.blog.model.entity.SystemConfig;
import com.sujiu.blog.model.vo.system.SystemConfigVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统配置服务
 *
 * @author sujiu
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 获取系统配置
     *
     * @return 系统配置
     */
    SystemConfigVO getSystemConfig();

    /**
     * 更新系统配置
     *
     * @param updateRequest 更新请求
     * @param request HTTP请求对象
     * @return 更新后的系统配置
     */
    SystemConfigVO updateSystemConfig(SystemConfigUpdateRequest updateRequest, HttpServletRequest request);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取字符串配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getStringConfig(String configKey, String defaultValue);

    /**
     * 根据配置键获取布尔配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Boolean getBooleanConfig(String configKey, Boolean defaultValue);

    /**
     * 根据配置键获取长整型配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Long getLongConfig(String configKey, Long defaultValue);

    /**
     * 设置配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    boolean setConfigValue(String configKey, String configValue);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();
}
