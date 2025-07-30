-- 初始化系统配置数据
USE blog_system;

-- 清空现有配置（如果有的话）
DELETE FROM sys_config;

-- 插入系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`, `is_system`) VALUES
('site.name', '个人博客系统', 'string', '网站名称', 1),
('site.description', '一个基于Spring Boot的个人博客系统', 'string', '网站描述', 1),
('site.keywords', '博客,技术分享,个人网站', 'string', '网站关键词', 1),
('comment.need_audit', 'true', 'boolean', '评论是否需要审核', 1),
('article.need_audit', 'false', 'boolean', '文章是否需要审核', 1),
('upload.max_size', '10485760', 'number', '文件上传最大大小(字节)', 1),
('user.default_avatar', '/static/images/default-avatar.png', 'string', '用户默认头像', 1);

-- 查看插入结果
SELECT * FROM sys_config;
