# 博客系统数据库设计需求对应说明

## 需求分析与数据库设计对应关系

### 1. 用户登录注册，个人信息修改，头像修改

**对应表结构：**
- `sys_user` - 用户基础信息表

**关键字段：**
```sql
- id: 用户唯一标识
- username: 用户名（唯一）
- email: 邮箱（唯一）
- password: 密码（BCrypt加密）
- nickname: 昵称
- avatar: 头像URL
- phone: 手机号
- gender: 性别
- birthday: 生日
- bio: 个人简介
- website: 个人网站
- location: 所在地
- register_ip: 注册IP
- last_login_time: 最后登录时间
- last_login_ip: 最后登录IP
- login_count: 登录次数
```

**功能实现：**
- ✅ 用户注册：插入用户基础信息，记录注册IP
- ✅ 用户登录：验证用户名/邮箱和密码，更新最后登录信息
- ✅ 个人信息修改：更新nickname、bio、website等字段
- ✅ 头像修改：更新avatar字段

### 2. 未登录用户可以查看文章，但不能评论；用户关注功能；博客点赞功能

**对应表结构：**
- `user_follow` - 用户关注关系表
- `blog_like` - 点赞表
- `blog_comment` - 评论表

**用户关注功能：**
```sql
-- user_follow表
- follower_id: 关注者ID
- following_id: 被关注者ID
- created_time: 关注时间
```

**博客点赞功能：**
```sql
-- blog_like表
- user_id: 点赞用户ID
- target_id: 目标ID（文章或评论）
- target_type: 目标类型（1:文章,2:评论）
- ip_address: IP地址
```

**评论权限控制：**
```sql
-- blog_comment表
- user_id: 用户ID（NULL表示游客，但游客不能评论）
- article_id: 文章ID
- content: 评论内容
```

### 3. 博客浏览量和点赞量记录，博客加密功能

**对应表结构：**
- `blog_article` - 博客文章表
- `blog_article_view` - 文章浏览记录表

**浏览量统计：**
```sql
-- blog_article表
- view_count: 浏览次数（实时统计）

-- blog_article_view表（详细记录）
- article_id: 文章ID
- user_id: 用户ID（可为空）
- ip_address: IP地址
- user_agent: 用户代理
- referer: 来源页面
- view_time: 浏览时间
```

**点赞量统计：**
```sql
-- blog_article表
- like_count: 点赞次数（通过触发器自动更新）
```

**博客加密功能：**
```sql
-- blog_article表
- password: 文章密码（BCrypt加密，NULL表示无密码）
```

### 4. 管理员功能：标签管理、分类管理、评论审核删除、博客下架、用户管理

**对应表结构：**
- `blog_tag` - 博客标签表
- `blog_category` - 博客分类表
- `blog_comment` - 评论表
- `blog_article` - 文章表
- `sys_user` - 用户表

**标签管理：**
```sql
-- blog_tag表
- name: 标签名称
- slug: 标签别名
- description: 标签描述
- color: 标签颜色
- article_count: 文章数量
- status: 状态（1:正常,0:禁用）
```

**分类管理：**
```sql
-- blog_category表
- name: 分类名称
- slug: 分类别名
- description: 分类描述
- parent_id: 父分类ID（支持多级分类）
- sort_order: 排序
- article_count: 文章数量
- status: 状态（1:正常,0:禁用）
```

**评论审核删除：**
```sql
-- blog_comment表
- status: 状态（0:待审核,1:已审核,2:已删除）
- audit_reason: 审核原因
- deleted: 逻辑删除标记
```

**博客下架：**
```sql
-- blog_article表
- status: 状态（0:草稿,1:审核中,2:已发布,3:已拒绝,4:已下架）
```

**用户管理：**
```sql
-- sys_user表
- status: 用户状态（1:正常,0:禁用,-1:删除）
- role: 用户角色（user:普通用户,admin:管理员,ban:封禁）
- created_time: 注册时间
- last_login_time: 最后上线时间
- last_login_ip: 最后登录IP
- register_ip: 注册IP
```

### 5. 博客发布状态管理：草稿、审核中、已拒绝、已发布

**对应表结构：**
- `blog_article` - 博客文章表

**状态字段设计：**
```sql
-- blog_article表
- status: 文章状态
  * 0: 草稿
  * 1: 审核中
  * 2: 已发布
  * 3: 已拒绝
  * 4: 已下架
- audit_reason: 审核原因（拒绝时填写）
- published_time: 发布时间（状态变为已发布时设置）
```

## 数据库设计亮点

### 1. 性能优化
- **索引设计**：为高频查询字段创建合适的索引
- **复合索引**：针对多条件查询创建复合索引
- **分区表**：浏览记录表可按时间分区
- **触发器**：自动更新统计数据，减少应用层计算

### 2. 数据一致性
- **外键约束**：保证数据引用完整性
- **唯一约束**：防止重复数据
- **逻辑删除**：保留历史数据，支持数据恢复
- **事务处理**：关键操作使用事务保证一致性

### 3. 扩展性设计
- **分类层级**：支持多级分类结构
- **标签系统**：多对多关系，灵活标记
- **配置表**：系统参数可配置化
- **状态枚举**：预留扩展状态值

### 4. 安全性考虑
- **密码加密**：使用BCrypt加密存储
- **IP记录**：记录用户行为IP，便于安全审计
- **权限控制**：基于角色的访问控制
- **数据脱敏**：敏感信息适当保护

### 5. 统计分析
- **详细日志**：浏览记录、操作日志
- **实时统计**：文章浏览量、点赞数等
- **数据视图**：简化复杂统计查询
- **存储过程**：批量数据处理

## 使用建议

### 1. 部署顺序
1. 执行 `sql/blog_database_design.sql` 创建表结构
2. 检查外键约束是否正确创建
3. 验证触发器和存储过程是否正常工作
4. 插入初始化数据

### 2. 性能监控
- 定期检查慢查询日志
- 监控表大小和索引使用情况
- 适时进行数据归档和清理

### 3. 数据备份
- 定期备份数据库
- 重要操作前进行数据备份
- 测试数据恢复流程

### 4. 扩展建议
- 考虑引入Redis缓存热点数据
- 大量数据时考虑读写分离
- 文章内容可考虑存储到文档数据库
