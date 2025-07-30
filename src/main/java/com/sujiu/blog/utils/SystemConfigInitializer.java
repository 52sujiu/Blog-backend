package com.sujiu.blog.utils;

import com.sujiu.blog.mapper.SystemConfigMapper;
import com.sujiu.blog.model.entity.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 系统配置初始化器
 * 在应用启动时检查并初始化系统配置
 *
 * @author sujiu
 */
@Component
@Slf4j
public class SystemConfigInitializer implements CommandLineRunner {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始检查系统配置初始化状态...");
        
        // 检查是否已有配置数据
        Long configCount = systemConfigMapper.selectCount(null);
        if (configCount > 0) {
            log.info("系统配置已存在，跳过初始化");
            return;
        }

        log.info("系统配置不存在，开始初始化...");
        
        // 初始化系统配置
        initSystemConfigs();
        
        log.info("系统配置初始化完成");
    }

    /**
     * 初始化系统配置
     */
    private void initSystemConfigs() {
        Date now = new Date();
        
        // 网站名称
        SystemConfig siteName = new SystemConfig();
        siteName.setConfigKey("site.name");
        siteName.setConfigValue("个人博客系统");
        siteName.setConfigType("string");
        siteName.setDescription("网站名称");
        siteName.setIsSystem(1);
        siteName.setCreatedTime(now);
        siteName.setUpdatedTime(now);
        systemConfigMapper.insert(siteName);

        // 网站描述
        SystemConfig siteDescription = new SystemConfig();
        siteDescription.setConfigKey("site.description");
        siteDescription.setConfigValue("一个基于Spring Boot的个人博客系统");
        siteDescription.setConfigType("string");
        siteDescription.setDescription("网站描述");
        siteDescription.setIsSystem(1);
        siteDescription.setCreatedTime(now);
        siteDescription.setUpdatedTime(now);
        systemConfigMapper.insert(siteDescription);

        // 网站关键词
        SystemConfig siteKeywords = new SystemConfig();
        siteKeywords.setConfigKey("site.keywords");
        siteKeywords.setConfigValue("博客,技术分享,个人网站");
        siteKeywords.setConfigType("string");
        siteKeywords.setDescription("网站关键词");
        siteKeywords.setIsSystem(1);
        siteKeywords.setCreatedTime(now);
        siteKeywords.setUpdatedTime(now);
        systemConfigMapper.insert(siteKeywords);

        // 评论审核
        SystemConfig commentNeedAudit = new SystemConfig();
        commentNeedAudit.setConfigKey("comment.need_audit");
        commentNeedAudit.setConfigValue("true");
        commentNeedAudit.setConfigType("boolean");
        commentNeedAudit.setDescription("评论是否需要审核");
        commentNeedAudit.setIsSystem(1);
        commentNeedAudit.setCreatedTime(now);
        commentNeedAudit.setUpdatedTime(now);
        systemConfigMapper.insert(commentNeedAudit);

        // 文章审核
        SystemConfig articleNeedAudit = new SystemConfig();
        articleNeedAudit.setConfigKey("article.need_audit");
        articleNeedAudit.setConfigValue("false");
        articleNeedAudit.setConfigType("boolean");
        articleNeedAudit.setDescription("文章是否需要审核");
        articleNeedAudit.setIsSystem(1);
        articleNeedAudit.setCreatedTime(now);
        articleNeedAudit.setUpdatedTime(now);
        systemConfigMapper.insert(articleNeedAudit);

        // 上传文件大小限制
        SystemConfig uploadMaxSize = new SystemConfig();
        uploadMaxSize.setConfigKey("upload.max_size");
        uploadMaxSize.setConfigValue("10485760");
        uploadMaxSize.setConfigType("number");
        uploadMaxSize.setDescription("文件上传最大大小(字节)");
        uploadMaxSize.setIsSystem(1);
        uploadMaxSize.setCreatedTime(now);
        uploadMaxSize.setUpdatedTime(now);
        systemConfigMapper.insert(uploadMaxSize);

        // 用户默认头像
        SystemConfig userDefaultAvatar = new SystemConfig();
        userDefaultAvatar.setConfigKey("user.default_avatar");
        userDefaultAvatar.setConfigValue("/static/images/default-avatar.png");
        userDefaultAvatar.setConfigType("string");
        userDefaultAvatar.setDescription("用户默认头像");
        userDefaultAvatar.setIsSystem(1);
        userDefaultAvatar.setCreatedTime(now);
        userDefaultAvatar.setUpdatedTime(now);
        systemConfigMapper.insert(userDefaultAvatar);

        log.info("已插入 7 个系统配置项");
    }
}
