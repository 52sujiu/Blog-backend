# 博客系统数据库脚本执行指南

## 问题说明

在执行原始的 `blog_database_design.sql` 脚本时，你遇到了以下问题：

1. **DELIMITER语法错误**：某些MySQL客户端不支持`DELIMITER`语法
2. **外键约束错误**：无法删除被外键引用的表

## 解决方案

我已经为你创建了多个版本的脚本来解决这些问题：

### 1. 修复后的完整脚本
- **文件**: `blog_database_design.sql`
- **说明**: 已修复外键约束问题和DELIMITER语法问题
- **推荐**: 优先使用此脚本

### 2. 仅表结构和数据脚本
- **文件**: `blog_tables_only.sql`
- **说明**: 只包含表结构、索引和初始数据，不包含存储过程和触发器
- **适用**: 如果你的MySQL客户端仍然不支持存储过程创建

### 3. 存储过程和触发器脚本
- **文件**: `procedures_and_triggers.sql`
- **说明**: 单独的存储过程和触发器脚本，使用标准DELIMITER语法
- **适用**: 在表结构创建完成后单独执行

## 执行步骤

### 方案一：使用修复后的完整脚本（推荐）

```sql
-- 直接执行修复后的完整脚本
source /path/to/sql/blog_database_design.sql;
```

### 方案二：分步执行

如果完整脚本仍有问题，请按以下步骤执行：

1. **创建表结构和基础数据**
```sql
source /path/to/sql/blog_tables_only.sql;
```

2. **创建存储过程和触发器**（可选）
```sql
source /path/to/sql/procedures_and_triggers.sql;
```

## 修复内容说明

### 1. 外键约束问题修复
- 在脚本开始时添加 `SET FOREIGN_KEY_CHECKS = 0;`
- 在脚本结束时添加 `SET FOREIGN_KEY_CHECKS = 1;`
- 这样可以安全地删除和重建有外键关系的表

### 2. DELIMITER语法修复
- 在主脚本中移除了 `DELIMITER $$` 语法
- 存储过程和触发器使用标准语法
- 单独的存储过程脚本保留了 `DELIMITER` 语法供支持的客户端使用

### 3. 数据库结构
- 10个核心表：用户、关注、分类、标签、文章、文章标签关联、点赞、评论、浏览记录、系统配置
- 完整的外键约束和索引优化
- 默认管理员账户：admin/admin@blog.com（密码：admin123）

## 验证安装

执行完脚本后，可以运行以下查询验证安装：

```sql
-- 检查表是否创建成功
SHOW TABLES;

-- 检查默认数据是否插入
SELECT * FROM sys_user WHERE username = 'admin';
SELECT * FROM blog_category;
SELECT * FROM blog_tag;
SELECT * FROM sys_config;

-- 检查存储过程是否创建成功（如果执行了存储过程脚本）
SHOW PROCEDURE STATUS WHERE Db = 'blog_system';

-- 检查触发器是否创建成功（如果执行了触发器脚本）
SHOW TRIGGERS;
```

## 注意事项

1. **备份数据**：如果数据库中已有数据，请先备份
2. **权限要求**：确保MySQL用户有创建数据库、表、存储过程和触发器的权限
3. **字符集**：数据库使用utf8mb4字符集，支持emoji和特殊字符
4. **默认密码**：管理员默认密码是BCrypt加密的"admin123"

## 故障排除

如果仍然遇到问题：

1. **检查MySQL版本**：确保使用MySQL 5.7+或MariaDB 10.2+
2. **检查客户端**：某些GUI工具可能不支持复杂脚本，建议使用命令行
3. **逐步执行**：可以将脚本分解为更小的部分逐步执行
4. **查看错误日志**：检查MySQL错误日志获取详细错误信息
