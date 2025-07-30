# 博客系统 API 接口汇总表

## 接口概览

本文档提供博客系统所有API接口的快速查询表格，详细接口说明请参考 [API设计文档](./api-design.md)。

## 1. 用户管理模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 用户注册 | POST | `/user/register` | 无 | 用户注册 |
| 用户登录 | POST | `/user/login` | 无 | 用户登录 |
| 用户注销 | POST | `/user/logout` | 登录 | 用户注销 |
| 获取当前用户信息 | GET | `/user/current` | 登录 | 获取当前登录用户信息 |
| 更新个人信息 | PUT | `/user/profile` | 登录 | 更新个人资料 |
| 修改密码 | PUT | `/user/password` | 登录 | 修改登录密码 |
| 用户关注 | POST | `/user/{userId}/follow` | 登录 | 关注指定用户 |
| 取消关注 | DELETE | `/user/{userId}/follow` | 登录 | 取消关注指定用户 |
| 获取关注列表 | GET | `/user/{userId}/following` | 无 | 获取用户关注的人列表 |
| 获取粉丝列表 | GET | `/user/{userId}/followers` | 无 | 获取用户粉丝列表 |
| 获取用户公开信息 | GET | `/user/{userId}` | 无 | 获取用户公开资料 |

## 2. 文章管理模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 发布文章 | POST | `/articles` | 登录 | 发布新文章 |
| 更新文章 | PUT | `/articles/{articleId}` | 作者/管理员 | 更新文章内容 |
| 删除文章 | DELETE | `/articles/{articleId}` | 作者/管理员 | 删除文章 |
| 获取文章详情 | GET | `/articles/{articleId}` | 无 | 获取文章详细信息 |
| 获取文章列表 | GET | `/articles` | 无 | 获取文章列表（支持筛选） |
| 获取热门文章 | GET | `/articles/hot` | 无 | 获取热门文章列表 |
| 获取推荐文章 | GET | `/articles/recommend` | 无 | 获取推荐文章列表 |
| 获取置顶文章 | GET | `/articles/top` | 无 | 获取置顶文章列表 |
| 文章点赞 | POST | `/articles/{articleId}/like` | 登录 | 给文章点赞 |
| 取消点赞 | DELETE | `/articles/{articleId}/like` | 登录 | 取消文章点赞 |
| 检查点赞状态 | GET | `/articles/{articleId}/like/status` | 登录 | 检查是否已点赞 |
| 增加浏览量 | POST | `/articles/{articleId}/view` | 无 | 记录文章浏览 |

## 3. 分类管理模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 创建分类 | POST | `/categories` | 管理员 | 创建新分类 |
| 更新分类 | PUT | `/categories/{categoryId}` | 管理员 | 更新分类信息 |
| 删除分类 | DELETE | `/categories/{categoryId}` | 管理员 | 删除分类 |
| 获取分类详情 | GET | `/categories/{categoryId}` | 无 | 获取分类详细信息 |
| 获取分类列表 | GET | `/categories` | 无 | 获取分类列表 |
| 获取分类树 | GET | `/categories/tree` | 无 | 获取分类树形结构 |

## 4. 标签管理模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 创建标签 | POST | `/tags` | 管理员 | 创建新标签 |
| 更新标签 | PUT | `/tags/{tagId}` | 管理员 | 更新标签信息 |
| 删除标签 | DELETE | `/tags/{tagId}` | 管理员 | 删除标签 |
| 获取标签详情 | GET | `/tags/{tagId}` | 无 | 获取标签详细信息 |
| 获取标签列表 | GET | `/tags` | 无 | 获取标签列表 |
| 获取热门标签 | GET | `/tags/hot` | 无 | 获取热门标签列表 |

## 5. 评论管理模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 发表评论 | POST | `/articles/{articleId}/comments` | 登录 | 发表文章评论 |
| 删除评论 | DELETE | `/comments/{commentId}` | 作者/管理员 | 删除评论 |
| 获取评论列表 | GET | `/articles/{articleId}/comments` | 无 | 获取文章评论列表 |
| 评论点赞 | POST | `/comments/{commentId}/like` | 登录 | 给评论点赞 |
| 取消评论点赞 | DELETE | `/comments/{commentId}/like` | 登录 | 取消评论点赞 |

## 6. 管理员模块

### 6.1 用户管理

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 获取用户列表 | GET | `/admin/users` | 管理员 | 获取所有用户列表 |
| 更新用户状态 | PUT | `/admin/users/{userId}/status` | 管理员 | 禁用/启用用户 |
| 更新用户角色 | PUT | `/admin/users/{userId}/role` | 管理员 | 修改用户角色 |
| 删除用户 | DELETE | `/admin/users/{userId}` | 管理员 | 删除用户账号 |

### 6.2 文章管理

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 获取所有文章列表 | GET | `/admin/articles` | 管理员 | 获取所有文章（包括草稿） |
| 审核文章 | PUT | `/admin/articles/{articleId}/audit` | 管理员 | 审核文章发布 |
| 下架文章 | PUT | `/admin/articles/{articleId}/offline` | 管理员 | 下架已发布文章 |
| 删除文章 | DELETE | `/admin/articles/{articleId}` | 管理员 | 彻底删除文章 |

### 6.3 评论管理

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 获取所有评论列表 | GET | `/admin/comments` | 管理员 | 获取所有评论 |
| 审核评论 | PUT | `/admin/comments/{commentId}/audit` | 管理员 | 审核评论 |
| 删除评论 | DELETE | `/admin/comments/{commentId}` | 管理员 | 删除评论 |

### 6.4 系统统计

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 获取系统概览 | GET | `/admin/dashboard/overview` | 管理员 | 获取系统统计概览 |
| 获取用户统计 | GET | `/admin/statistics/users` | 管理员 | 获取用户增长统计 |
| 获取文章统计 | GET | `/admin/statistics/articles` | 管理员 | 获取文章发布统计 |
| 获取访问统计 | GET | `/admin/statistics/views` | 管理员 | 获取访问量统计 |

## 7. 文件上传模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 上传图片 | POST | `/upload/image` | 登录 | 上传图片文件 |
| 上传文件 | POST | `/upload/file` | 登录 | 上传其他类型文件 |

## 8. 搜索模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 全文搜索 | GET | `/search` | 无 | 全文搜索文章、用户等 |
| 搜索建议 | GET | `/search/suggest` | 无 | 获取搜索关键词建议 |

## 9. 系统配置模块

| 接口名称 | HTTP方法 | 接口地址 | 权限要求 | 描述 |
|---------|----------|----------|----------|------|
| 获取系统配置 | GET | `/system/config` | 无 | 获取系统公开配置 |
| 更新系统配置 | PUT | `/admin/system/config` | 管理员 | 更新系统配置 |

## 接口统计

- **总接口数**: 60+
- **公开接口**: 25个（无需登录）
- **用户接口**: 20个（需要登录）
- **管理员接口**: 15个（需要管理员权限）

## 权限说明

- **无**: 任何人都可以访问
- **登录**: 需要用户登录
- **作者**: 需要是内容的创建者
- **管理员**: 需要管理员权限
- **作者/管理员**: 内容创建者或管理员都可以访问

## 注意事项

1. 所有接口都使用统一的响应格式
2. 分页接口统一使用 `current` 和 `size` 参数
3. 时间格式统一使用 ISO 8601 标准
4. 所有需要权限的接口都会进行权限验证
5. 敏感信息（如密码）不会在响应中返回
