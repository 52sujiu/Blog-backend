# 博客系统数据库设计
# 基于需求：用户管理、博客发布、评论系统、权限控制、统计功能
# @author Blog System Designer
# @date 2025-07-30

-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_system;

-- ==================== 用户相关表 ====================

-- 1. 用户基础信息表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
    `password` VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别(0:未知,1:男,2:女)',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `bio` TEXT COMMENT '个人简介',
    `website` VARCHAR(255) DEFAULT NULL COMMENT '个人网站',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '所在地',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用,-1:删除)',
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色(user:普通用户,admin:管理员,ban:封禁)',
    `register_ip` VARCHAR(50) DEFAULT NULL COMMENT '注册IP',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `login_count` INT DEFAULT 0 COMMENT '登录次数',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(1:已删除,0:未删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_last_login` (`last_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 用户关注关系表
DROP TABLE IF EXISTS `user_follow`;
CREATE TABLE `user_follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
    `following_id` BIGINT NOT NULL COMMENT '被关注者ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follow_relation` (`follower_id`, `following_id`),
    KEY `fk_follower` (`follower_id`),
    KEY `fk_following` (`following_id`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_follow_following` FOREIGN KEY (`following_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注关系表';

-- ==================== 博客内容相关表 ====================

-- 3. 博客分类表
DROP TABLE IF EXISTS `blog_category`;
CREATE TABLE `blog_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(100) NOT NULL COMMENT '分类别名(URL友好)',
    `description` TEXT COMMENT '分类描述',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '分类封面图',
    `color` VARCHAR(7) DEFAULT '#1890ff' COMMENT '分类颜色(十六进制)',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID(0为顶级分类)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `article_count` INT DEFAULT 0 COMMENT '文章数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(1:已删除,0:未删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客分类表';

-- 4. 博客标签表
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(100) NOT NULL COMMENT '标签别名(URL友好)',
    `description` TEXT COMMENT '标签描述',
    `color` VARCHAR(7) DEFAULT '#87d068' COMMENT '标签颜色(十六进制)',
    `article_count` INT DEFAULT 0 COMMENT '文章数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,0:禁用)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(1:已删除,0:未删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `idx_status` (`status`),
    KEY `idx_article_count` (`article_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客标签表';

-- 5. 博客文章表
DROP TABLE IF EXISTS `blog_article`;
CREATE TABLE `blog_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `user_id` BIGINT NOT NULL COMMENT '作者ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `slug` VARCHAR(200) NOT NULL COMMENT '文章别名(URL友好)',
    `summary` TEXT COMMENT '文章摘要',
    `content` LONGTEXT NOT NULL COMMENT '文章内容(Markdown)',
    `content_html` LONGTEXT COMMENT '文章内容(HTML)',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶(1:置顶,0:不置顶)',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐(1:推荐,0:不推荐)',
    `is_original` TINYINT DEFAULT 1 COMMENT '是否原创(1:原创,0:转载)',
    `source_url` VARCHAR(255) DEFAULT NULL COMMENT '转载来源URL',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `comment_count` INT DEFAULT 0 COMMENT '评论次数',
    `word_count` INT DEFAULT 0 COMMENT '字数统计',
    `reading_time` INT DEFAULT 0 COMMENT '预计阅读时间(分钟)',
    `password` VARCHAR(100) DEFAULT NULL COMMENT '文章密码(BCrypt加密)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0:草稿,1:审核中,2:已发布,3:已拒绝,4:已下架)',
    `audit_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核原因',
    `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(1:已删除,0:未删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    KEY `fk_article_user_id` (`user_id`),
    KEY `fk_article_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_published_time` (`published_time`),
    KEY `idx_view_count` (`view_count`),
    KEY `idx_like_count` (`like_count`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_article_list` (`status`, `published_time` DESC),
    FULLTEXT KEY `ft_content` (`title`, `summary`, `content`) WITH PARSER ngram,
    CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `blog_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- 6. 文章标签关联表
DROP TABLE IF EXISTS `blog_article_tag`;
CREATE TABLE `blog_article_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `fk_article_tag_article` (`article_id`),
    KEY `fk_article_tag_tag` (`tag_id`),
    CONSTRAINT `fk_article_tag_article_id` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_article_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `blog_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ==================== 互动相关表 ====================

-- 7. 点赞表
DROP TABLE IF EXISTS `blog_like`;
CREATE TABLE `blog_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标ID',
    `target_type` TINYINT NOT NULL COMMENT '目标类型(1:文章,2:评论)',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
    KEY `fk_like_user_id` (`user_id`),
    KEY `idx_target` (`target_id`, `target_type`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 8. 评论表
DROP TABLE IF EXISTS `blog_comment`;
CREATE TABLE `blog_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID(注册用户)',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级评论)',
    `reply_to_id` BIGINT DEFAULT NULL COMMENT '回复目标评论ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `author_name` VARCHAR(50) DEFAULT NULL COMMENT '游客姓名',
    `author_email` VARCHAR(100) DEFAULT NULL COMMENT '游客邮箱',
    `author_website` VARCHAR(255) DEFAULT NULL COMMENT '游客网站',
    `author_ip` VARCHAR(50) DEFAULT NULL COMMENT '评论IP',
    `user_agent` TEXT COMMENT '用户代理',
    `like_count` INT DEFAULT 0 COMMENT '点赞次数',
    `is_admin_reply` TINYINT DEFAULT 0 COMMENT '是否管理员回复(1:是,0:否)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态(0:待审核,1:已审核,2:已删除)',
    `audit_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核原因',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(1:已删除,0:未删除)',
    PRIMARY KEY (`id`),
    KEY `fk_comment_article_id` (`article_id`),
    KEY `fk_comment_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`),
    CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ==================== 统计相关表 ====================

-- 9. 文章浏览记录表
DROP TABLE IF EXISTS `blog_article_view`;
CREATE TABLE `blog_article_view` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID(可为空)',
    `ip_address` VARCHAR(50) NOT NULL COMMENT 'IP地址',
    `user_agent` TEXT COMMENT '用户代理',
    `referer` VARCHAR(500) DEFAULT NULL COMMENT '来源页面',
    `view_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `fk_view_article_id` (`article_id`),
    KEY `fk_view_user_id` (`user_id`),
    KEY `idx_ip_address` (`ip_address`),
    KEY `idx_view_time` (`view_time`),
    KEY `idx_article_view_stat` (`article_id`, `view_time`),
    CONSTRAINT `fk_view_article` FOREIGN KEY (`article_id`) REFERENCES `blog_article` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_view_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章浏览记录表';

-- ==================== 系统配置相关表 ====================

-- 10. 系统配置表
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `config_type` VARCHAR(20) DEFAULT 'string' COMMENT '配置类型(string,number,boolean,json)',
    `description` VARCHAR(500) COMMENT '配置描述',
    `is_system` TINYINT DEFAULT 0 COMMENT '是否系统配置(1:是,0:否)',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ==================== 初始化数据 ====================

-- 插入默认管理员用户
INSERT INTO `sys_user` (`id`, `username`, `email`, `password`, `nickname`, `role`, `status`, `register_ip`) VALUES
(1, 'admin', 'admin@blog.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXYLFSaZWOvPqHJZtTwqKvzKPP6', '系统管理员', 'admin', 1, '127.0.0.1');

-- 插入默认分类
INSERT INTO `blog_category` (`id`, `name`, `slug`, `description`, `color`, `sort_order`) VALUES
(1, '技术分享', 'tech', '技术相关的文章分享', '#1890ff', 1),
(2, '生活随笔', 'life', '生活感悟和随笔', '#52c41a', 2),
(3, '学习笔记', 'study', '学习过程中的笔记和总结', '#faad14', 3);

-- 插入默认标签
INSERT INTO `blog_tag` (`id`, `name`, `slug`, `description`, `color`) VALUES
(1, 'Java', 'java', 'Java编程语言相关', '#f50'),
(2, 'Spring Boot', 'spring-boot', 'Spring Boot框架相关', '#52c41a'),
(3, '数据库', 'database', '数据库相关技术', '#1890ff'),
(4, '前端', 'frontend', '前端开发技术', '#722ed1'),
(5, '算法', 'algorithm', '算法和数据结构', '#fa8c16');

-- 插入系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_type`, `description`, `is_system`) VALUES
('site.name', '个人博客系统', 'string', '网站名称', 1),
('site.description', '一个基于Spring Boot的个人博客系统', 'string', '网站描述', 1),
('site.keywords', '博客,技术分享,个人网站', 'string', '网站关键词', 1),
('comment.need_audit', 'true', 'boolean', '评论是否需要审核', 1),
('article.need_audit', 'false', 'boolean', '文章是否需要审核', 1),
('upload.max_size', '10485760', 'number', '文件上传最大大小(字节)', 1),
('user.default_avatar', '/static/images/default-avatar.png', 'string', '用户默认头像', 1);

-- ==================== 创建视图 ====================

-- 文章统计视图
CREATE OR REPLACE VIEW `v_article_stats` AS
SELECT
    a.id,
    a.title,
    a.user_id,
    u.nickname as author_name,
    a.category_id,
    c.name as category_name,
    a.view_count,
    a.like_count,
    a.comment_count,
    a.status,
    a.published_time,
    a.created_time
FROM `blog_article` a
LEFT JOIN `sys_user` u ON a.user_id = u.id
LEFT JOIN `blog_category` c ON a.category_id = c.id
WHERE a.deleted = 0;

-- 用户统计视图
CREATE OR REPLACE VIEW `v_user_stats` AS
SELECT
    u.id,
    u.username,
    u.nickname,
    u.email,
    u.status,
    u.role,
    u.created_time,
    u.last_login_time,
    u.last_login_ip,
    u.login_count,
    COUNT(DISTINCT a.id) as article_count,
    COUNT(DISTINCT c.id) as comment_count,
    COUNT(DISTINCT f1.id) as following_count,
    COUNT(DISTINCT f2.id) as follower_count
FROM `sys_user` u
LEFT JOIN `blog_article` a ON u.id = a.user_id AND a.deleted = 0 AND a.status = 2
LEFT JOIN `blog_comment` c ON u.id = c.user_id AND c.deleted = 0 AND c.status = 1
LEFT JOIN `user_follow` f1 ON u.id = f1.follower_id
LEFT JOIN `user_follow` f2 ON u.id = f2.following_id
WHERE u.deleted = 0
GROUP BY u.id;

-- ==================== 创建存储过程 ====================

-- 更新文章统计数据的存储过程
DELIMITER $$
CREATE PROCEDURE `UpdateArticleStats`(IN article_id BIGINT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 更新点赞数
    UPDATE `blog_article`
    SET `like_count` = (
        SELECT COUNT(*) FROM `blog_like`
        WHERE `target_id` = article_id AND `target_type` = 1
    )
    WHERE `id` = article_id;

    -- 更新评论数
    UPDATE `blog_article`
    SET `comment_count` = (
        SELECT COUNT(*) FROM `blog_comment`
        WHERE `article_id` = article_id AND `deleted` = 0 AND `status` = 1
    )
    WHERE `id` = article_id;

    COMMIT;
END$$
DELIMITER ;

-- 更新分类文章数量的存储过程
DELIMITER $$
CREATE PROCEDURE `UpdateCategoryArticleCount`(IN category_id BIGINT)
BEGIN
    UPDATE `blog_category`
    SET `article_count` = (
        SELECT COUNT(*) FROM `blog_article`
        WHERE `category_id` = category_id AND `deleted` = 0 AND `status` = 2
    )
    WHERE `id` = category_id;
END$$
DELIMITER ;

-- 更新标签文章数量的存储过程
DELIMITER $$
CREATE PROCEDURE `UpdateTagArticleCount`(IN tag_id BIGINT)
BEGIN
    UPDATE `blog_tag`
    SET `article_count` = (
        SELECT COUNT(*) FROM `blog_article_tag` at
        JOIN `blog_article` a ON at.article_id = a.id
        WHERE at.tag_id = tag_id AND a.deleted = 0 AND a.status = 2
    )
    WHERE `id` = tag_id;
END$$
DELIMITER ;

-- ==================== 创建触发器 ====================

-- 文章点赞后更新统计
DELIMITER $$
CREATE TRIGGER `tr_article_like_after_insert`
AFTER INSERT ON `blog_like`
FOR EACH ROW
BEGIN
    IF NEW.target_type = 1 THEN
        UPDATE `blog_article`
        SET `like_count` = `like_count` + 1
        WHERE `id` = NEW.target_id;
    END IF;
END$$
DELIMITER ;

-- 文章取消点赞后更新统计
DELIMITER $$
CREATE TRIGGER `tr_article_like_after_delete`
AFTER DELETE ON `blog_like`
FOR EACH ROW
BEGIN
    IF OLD.target_type = 1 THEN
        UPDATE `blog_article`
        SET `like_count` = `like_count` - 1
        WHERE `id` = OLD.target_id;
    END IF;
END$$
DELIMITER ;

-- 评论审核通过后更新文章评论数
DELIMITER $$
CREATE TRIGGER `tr_comment_after_update`
AFTER UPDATE ON `blog_comment`
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status AND NEW.status = 1 THEN
        UPDATE `blog_article`
        SET `comment_count` = `comment_count` + 1
        WHERE `id` = NEW.article_id;
    ELSEIF OLD.status != NEW.status AND OLD.status = 1 THEN
        UPDATE `blog_article`
        SET `comment_count` = `comment_count` - 1
        WHERE `id` = NEW.article_id;
    END IF;
END$$
DELIMITER ;

-- ==================== 创建索引优化 ====================

-- 为高频查询创建复合索引
CREATE INDEX `idx_article_user_status_time` ON `blog_article` (`user_id`, `status`, `published_time` DESC);
CREATE INDEX `idx_article_category_status_time` ON `blog_article` (`category_id`, `status`, `published_time` DESC);
CREATE INDEX `idx_comment_article_status_time` ON `blog_comment` (`article_id`, `status`, `created_time` DESC);
CREATE INDEX `idx_like_target_user` ON `blog_like` (`target_id`, `target_type`, `user_id`);
CREATE INDEX `idx_view_article_time` ON `blog_article_view` (`article_id`, `view_time`);

-- ==================== 数据库设计说明 ====================

/*
数据库设计特点：

1. 用户系统：
   - 支持用户注册、登录、个人信息管理
   - 记录用户注册IP、最后登录时间和IP
   - 支持用户关注功能
   - 角色权限控制（user/admin/ban）

2. 博客系统：
   - 支持分类和标签管理
   - 文章状态管理（草稿/审核中/已发布/已拒绝/已下架）
   - 文章密码保护功能
   - 浏览量和点赞量统计
   - 支持原创和转载标识

3. 评论系统：
   - 支持注册用户和游客评论
   - 评论审核机制
   - 支持评论回复（多级评论）
   - 评论点赞功能

4. 统计功能：
   - 文章浏览记录详细统计
   - 用户行为统计
   - 实时数据统计视图

5. 性能优化：
   - 合理的索引设计
   - 触发器自动更新统计数据
   - 存储过程批量处理
   - 视图简化复杂查询

6. 数据安全：
   - 逻辑删除机制
   - 外键约束保证数据一致性
   - 密码BCrypt加密
   - IP地址记录便于安全审计
*/
