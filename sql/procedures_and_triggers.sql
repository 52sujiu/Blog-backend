# 博客系统存储过程和触发器
# 如果主脚本中的存储过程和触发器创建失败，请单独执行此脚本
# @author Blog System Designer
# @date 2025-07-30

USE blog_system;

-- ==================== 创建存储过程 ====================

-- 更新文章统计数据的存储过程
DROP PROCEDURE IF EXISTS `UpdateArticleStats`;

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
DROP PROCEDURE IF EXISTS `UpdateCategoryArticleCount`;

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
DROP PROCEDURE IF EXISTS `UpdateTagArticleCount`;

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
DROP TRIGGER IF EXISTS `tr_article_like_after_insert`;

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
DROP TRIGGER IF EXISTS `tr_article_like_after_delete`;

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
DROP TRIGGER IF EXISTS `tr_comment_after_update`;

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
