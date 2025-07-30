# 博客系统 RESTful API 接口设计文档

## 1. 概述

### 1.1 设计原则
- 遵循 RESTful 架构风格
- 符合阿里巴巴 Java 开发手册规范
- 统一的响应格式和错误处理
- 支持国际化和多语言
- 完善的权限控制和安全机制

### 1.2 基础信息
- **Base URL**: `http://localhost:8101/api`
- **API Version**: `v1`
- **Content-Type**: `application/json`
- **Character Encoding**: `UTF-8`

### 1.3 通用响应格式

```json
{
  "code": 0,
  "data": {},
  "message": "success"
}
```


**响应码说明：**
- `0`: 成功
- `40000`: 请求参数错误
- `40001`: 请求数据为空
- `40100`: 未登录
- `40101`: 无权限
- `40400`: 请求数据不存在
- `50000`: 系统内部异常
- `50001`: 操作失败

### 1.4 分页响应格式

```json
{
  "code": 0,
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "message": "success"
}
```

## 2. 认证授权

### 2.1 认证方式
- 基于 Session 的认证机制
- 支持记住登录状态
- 自动登录过期处理

### 2.2 权限级别
- **游客**: 只能查看公开内容
- **普通用户**: 可发布文章、评论、点赞等
- **管理员**: 拥有所有权限，可管理用户、内容等

## 3. 用户管理模块

### 3.1 用户注册

**接口地址**: `POST /user/register`

**请求参数**:
```json
{
  "username": "testuser",
  "email": "test@example.com", 
  "password": "password123",
  "confirmPassword": "password123"
}
```

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "userId": 1001
  },
  "message": "注册成功"
}
```

### 3.2 用户登录

**接口地址**: `POST /user/login`

**请求参数**:
```json
{
  "account": "testuser",
  "password": "password123",
  "rememberMe": true
}
```

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "user": {
      "id": 1001,
      "username": "testuser",
      "nickname": "测试用户",
      "avatar": "http://example.com/avatar.jpg",
      "email": "test@example.com",
      "role": "user"
    }
  },
  "message": "登录成功"
}
```

### 3.3 用户注销

**接口地址**: `POST /user/logout`

**权限要求**: 需要登录

**响应示例**:
```json
{
  "code": 0,
  "data": true,
  "message": "注销成功"
}
```

### 3.4 获取当前用户信息

**接口地址**: `GET /user/current`

**权限要求**: 需要登录

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": "http://example.com/avatar.jpg",
    "email": "test@example.com",
    "phone": "13800138000",
    "gender": 1,
    "birthday": "1990-01-01",
    "bio": "这是个人简介",
    "website": "https://example.com",
    "location": "北京",
    "role": "user",
    "createdTime": "2025-01-01T00:00:00",
    "lastLoginTime": "2025-01-15T10:30:00"
  },
  "message": "success"
}
```

### 3.5 更新个人信息

**接口地址**: `PUT /user/profile`

**权限要求**: 需要登录

**请求参数**:
```json
{
  "nickname": "新昵称",
  "avatar": "http://example.com/new-avatar.jpg",
  "phone": "13900139000",
  "gender": 1,
  "birthday": "1990-01-01",
  "bio": "更新后的个人简介",
  "website": "https://newsite.com",
  "location": "上海"
}
```

### 3.6 修改密码

**接口地址**: `PUT /user/password`

**权限要求**: 需要登录

**请求参数**:
```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

### 3.7 用户关注

**接口地址**: `POST /user/{userId}/follow`

**权限要求**: 需要登录

**路径参数**:
- `userId`: 被关注用户ID

### 3.8 取消关注

**接口地址**: `DELETE /user/{userId}/follow`

**权限要求**: 需要登录

### 3.9 获取关注列表

**接口地址**: `GET /user/{userId}/following`

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10

### 3.10 获取粉丝列表

**接口地址**: `GET /user/{userId}/followers`

**查询参数**:
- `current`: 当前页码，默认1  
- `size`: 每页大小，默认10

### 3.11 获取用户公开信息

**接口地址**: `GET /user/{userId}`

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "username": "testuser",
    "nickname": "测试用户",
    "avatar": "http://example.com/avatar.jpg",
    "bio": "这是个人简介",
    "website": "https://example.com",
    "location": "北京",
    "articleCount": 25,
    "followingCount": 10,
    "followerCount": 15,
    "createdTime": "2025-01-01T00:00:00"
  },
  "message": "success"
}
```

## 4. 文章管理模块

### 4.1 发布文章

**接口地址**: `POST /articles`

**权限要求**: 需要登录

**请求参数**:
```json
{
  "title": "文章标题",
  "slug": "article-slug",
  "summary": "文章摘要",
  "content": "文章内容(Markdown格式)",
  "coverImage": "http://example.com/cover.jpg",
  "categoryId": 1,
  "tagIds": [1, 2, 3],
  "isTop": false,
  "isRecommend": false,
  "isOriginal": true,
  "sourceUrl": null,
  "password": null,
  "status": 2
}
```

**状态说明**:
- `0`: 草稿
- `1`: 审核中  
- `2`: 已发布

### 4.2 更新文章

**接口地址**: `PUT /articles/{articleId}`

**权限要求**: 需要登录且为文章作者或管理员

### 4.3 删除文章

**接口地址**: `DELETE /articles/{articleId}`

**权限要求**: 需要登录且为文章作者或管理员

### 4.4 获取文章详情

**接口地址**: `GET /articles/{articleId}`

**查询参数**:
- `password`: 文章密码（如果文章加密）

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "id": 1001,
    "title": "文章标题",
    "slug": "article-slug", 
    "summary": "文章摘要",
    "content": "文章内容",
    "contentHtml": "<p>文章HTML内容</p>",
    "coverImage": "http://example.com/cover.jpg",
    "author": {
      "id": 1,
      "username": "author",
      "nickname": "作者昵称",
      "avatar": "http://example.com/avatar.jpg"
    },
    "category": {
      "id": 1,
      "name": "技术分享",
      "slug": "tech"
    },
    "tags": [
      {
        "id": 1,
        "name": "Java",
        "slug": "java",
        "color": "#f50"
      }
    ],
    "isTop": false,
    "isRecommend": false,
    "isOriginal": true,
    "sourceUrl": null,
    "viewCount": 100,
    "likeCount": 10,
    "commentCount": 5,
    "wordCount": 1500,
    "readingTime": 6,
    "status": 2,
    "publishedTime": "2025-01-15T10:00:00",
    "createdTime": "2025-01-15T09:00:00",
    "updatedTime": "2025-01-15T10:00:00"
  },
  "message": "success"
}
```

### 4.5 获取文章列表

**接口地址**: `GET /articles`

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10
- `categoryId`: 分类ID
- `tagId`: 标签ID
- `authorId`: 作者ID
- `keyword`: 搜索关键词
- `status`: 文章状态
- `isTop`: 是否置顶
- `isRecommend`: 是否推荐
- `sortField`: 排序字段（publishedTime, viewCount, likeCount）
- `sortOrder`: 排序方式（asc, desc）

### 4.6 获取热门文章

**接口地址**: `GET /articles/hot`

**查询参数**:
- `limit`: 返回数量，默认10
- `days`: 统计天数，默认7

### 4.7 获取推荐文章

**接口地址**: `GET /articles/recommend`

**查询参数**:
- `limit`: 返回数量，默认10

### 4.8 获取置顶文章

**接口地址**: `GET /articles/top`

### 4.9 文章点赞

**接口地址**: `POST /articles/{articleId}/like`

**权限要求**: 需要登录

### 4.10 取消点赞

**接口地址**: `DELETE /articles/{articleId}/like`

**权限要求**: 需要登录

### 4.11 检查点赞状态

**接口地址**: `GET /articles/{articleId}/like/status`

**权限要求**: 需要登录

### 4.12 增加浏览量

**接口地址**: `POST /articles/{articleId}/view`

**请求参数**:
```json
{
  "referer": "http://example.com/previous-page"
}
```

## 5. 分类管理模块

### 5.1 创建分类

**接口地址**: `POST /categories`

**权限要求**: 管理员

**请求参数**:
```json
{
  "name": "分类名称",
  "slug": "category-slug",
  "description": "分类描述",
  "coverImage": "http://example.com/cover.jpg",
  "color": "#1890ff",
  "parentId": 0,
  "sortOrder": 1
}
```

### 5.2 更新分类

**接口地址**: `PUT /categories/{categoryId}`

**权限要求**: 管理员

### 5.3 删除分类

**接口地址**: `DELETE /categories/{categoryId}`

**权限要求**: 管理员

### 5.4 获取分类详情

**接口地址**: `GET /categories/{categoryId}`

### 5.5 获取分类列表

**接口地址**: `GET /categories`

**查询参数**:
- `parentId`: 父分类ID
- `status`: 状态
- `includeCount`: 是否包含文章数量统计

**响应示例**:
```json
{
  "code": 0,
  "data": [
    {
      "id": 1,
      "name": "技术分享",
      "slug": "tech",
      "description": "技术相关的文章分享",
      "coverImage": "http://example.com/tech-cover.jpg",
      "color": "#1890ff",
      "parentId": 0,
      "sortOrder": 1,
      "articleCount": 25,
      "status": 1,
      "children": [
        {
          "id": 2,
          "name": "Java",
          "slug": "java",
          "parentId": 1,
          "articleCount": 10
        }
      ]
    }
  ],
  "message": "success"
}
```

### 5.6 获取分类树

**接口地址**: `GET /categories/tree`

## 6. 标签管理模块

### 6.1 创建标签

**接口地址**: `POST /tags`

**权限要求**: 管理员

**请求参数**:
```json
{
  "name": "标签名称",
  "slug": "tag-slug",
  "description": "标签描述",
  "color": "#87d068"
}
```

### 6.2 更新标签

**接口地址**: `PUT /tags/{tagId}`

**权限要求**: 管理员

### 6.3 删除标签

**接口地址**: `DELETE /tags/{tagId}`

**权限要求**: 管理员

### 6.4 获取标签详情

**接口地址**: `GET /tags/{tagId}`

### 6.5 获取标签列表

**接口地址**: `GET /tags`

**查询参数**:
- `keyword`: 搜索关键词
- `status`: 状态
- `sortField`: 排序字段
- `sortOrder`: 排序方式

### 6.6 获取热门标签

**接口地址**: `GET /tags/hot`

**查询参数**:
- `limit`: 返回数量，默认20

## 7. 评论管理模块

### 7.1 发表评论

**接口地址**: `POST /articles/{articleId}/comments`

**权限要求**: 需要登录

**请求参数**:
```json
{
  "content": "评论内容",
  "parentId": 0,
  "replyToId": null
}
```

### 7.2 删除评论

**接口地址**: `DELETE /comments/{commentId}`

**权限要求**: 需要登录且为评论作者或管理员

### 7.3 获取评论列表

**接口地址**: `GET /articles/{articleId}/comments`

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10
- `sortOrder`: 排序方式（asc, desc）

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "records": [
      {
        "id": 1001,
        "content": "评论内容",
        "author": {
          "id": 1,
          "username": "commenter",
          "nickname": "评论者",
          "avatar": "http://example.com/avatar.jpg"
        },
        "parentId": 0,
        "replyTo": null,
        "likeCount": 5,
        "isLiked": false,
        "status": 1,
        "createdTime": "2025-01-15T10:30:00",
        "replies": [
          {
            "id": 1002,
            "content": "回复内容",
            "author": {
              "id": 2,
              "username": "replier",
              "nickname": "回复者",
              "avatar": "http://example.com/avatar2.jpg"
            },
            "parentId": 1001,
            "replyTo": {
              "id": 1,
              "nickname": "评论者"
            },
            "likeCount": 2,
            "isLiked": true,
            "createdTime": "2025-01-15T11:00:00"
          }
        ]
      }
    ],
    "total": 50,
    "current": 1,
    "size": 10,
    "pages": 5
  },
  "message": "success"
}
```

### 7.4 评论点赞

**接口地址**: `POST /comments/{commentId}/like`

**权限要求**: 需要登录

### 7.5 取消评论点赞

**接口地址**: `DELETE /comments/{commentId}/like`

**权限要求**: 需要登录

## 8. 管理员模块

### 8.1 用户管理

#### 8.1.1 获取用户列表

**接口地址**: `GET /admin/users`

**权限要求**: 管理员

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（用户名、邮箱、昵称）
- `status`: 用户状态
- `role`: 用户角色
- `startTime`: 注册开始时间
- `endTime`: 注册结束时间

#### 8.1.2 更新用户状态

**接口地址**: `PUT /admin/users/{userId}/status`

**权限要求**: 管理员

**请求参数**:
```json
{
  "status": 0,
  "reason": "违规操作"
}
```

#### 8.1.3 更新用户角色

**接口地址**: `PUT /admin/users/{userId}/role`

**权限要求**: 管理员

**请求参数**:
```json
{
  "role": "admin"
}
```

#### 8.1.4 删除用户

**接口地址**: `DELETE /admin/users/{userId}`

**权限要求**: 管理员

### 8.2 文章管理

#### 8.2.1 获取所有文章列表

**接口地址**: `GET /admin/articles`

**权限要求**: 管理员

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词
- `status`: 文章状态
- `authorId`: 作者ID
- `categoryId`: 分类ID
- `startTime`: 创建开始时间
- `endTime`: 创建结束时间

#### 8.2.2 审核文章

**接口地址**: `PUT /admin/articles/{articleId}/audit`

**权限要求**: 管理员

**请求参数**:
```json
{
  "status": 2,
  "auditReason": "审核通过"
}
```

#### 8.2.3 下架文章

**接口地址**: `PUT /admin/articles/{articleId}/offline`

**权限要求**: 管理员

**请求参数**:
```json
{
  "reason": "违规内容"
}
```

#### 8.2.4 删除文章

**接口地址**: `DELETE /admin/articles/{articleId}`

**权限要求**: 管理员

### 8.3 评论管理

#### 8.3.1 获取所有评论列表

**接口地址**: `GET /admin/comments`

**权限要求**: 管理员

**查询参数**:
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词
- `status`: 评论状态
- `articleId`: 文章ID
- `authorId`: 评论者ID
- `startTime`: 创建开始时间
- `endTime`: 创建结束时间

#### 8.3.2 审核评论

**接口地址**: `PUT /admin/comments/{commentId}/audit`

**权限要求**: 管理员

**请求参数**:
```json
{
  "status": 1,
  "auditReason": "审核通过"
}
```

#### 8.3.3 删除评论

**接口地址**: `DELETE /admin/comments/{commentId}`

**权限要求**: 管理员

### 8.4 系统统计

#### 8.4.1 获取系统概览

**接口地址**: `GET /admin/dashboard/overview`

**权限要求**: 管理员

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "userCount": 1000,
    "articleCount": 500,
    "commentCount": 2000,
    "viewCount": 50000,
    "todayUserCount": 10,
    "todayArticleCount": 5,
    "todayCommentCount": 20,
    "todayViewCount": 500
  },
  "message": "success"
}
```

#### 8.4.2 获取用户统计

**接口地址**: `GET /admin/statistics/users`

**权限要求**: 管理员

**查询参数**:
- `type`: 统计类型（daily, weekly, monthly）
- `days`: 统计天数，默认30

#### 8.4.3 获取文章统计

**接口地址**: `GET /admin/statistics/articles`

**权限要求**: 管理员

#### 8.4.4 获取访问统计

**接口地址**: `GET /admin/statistics/views`

**权限要求**: 管理员

## 9. 文件上传模块

### 9.1 上传图片

**接口地址**: `POST /upload/image`

**权限要求**: 需要登录

**请求参数**: `multipart/form-data`
- `file`: 图片文件
- `type`: 上传类型（avatar, cover, content）

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "url": "http://example.com/uploads/images/2025/01/15/abc123.jpg",
    "filename": "abc123.jpg",
    "originalName": "image.jpg",
    "size": 102400,
    "type": "image/jpeg"
  },
  "message": "上传成功"
}
```

### 9.2 上传文件

**接口地址**: `POST /upload/file`

**权限要求**: 需要登录

**请求参数**: `multipart/form-data`
- `file`: 文件
- `type`: 上传类型（document, attachment）

## 10. 搜索模块

### 10.1 全文搜索

**接口地址**: `GET /search`

**查询参数**:
- `keyword`: 搜索关键词
- `type`: 搜索类型（article, user, tag, category）
- `current`: 当前页码，默认1
- `size`: 每页大小，默认10

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "articles": {
      "records": [
        {
          "id": 1001,
          "title": "文章标题",
          "summary": "文章摘要",
          "author": {
            "nickname": "作者昵称"
          },
          "publishedTime": "2025-01-15T10:00:00",
          "highlight": {
            "title": "文章<em>标题</em>",
            "content": "包含关键词的<em>内容</em>片段"
          }
        }
      ],
      "total": 10
    },
    "users": {
      "records": [],
      "total": 0
    },
    "tags": {
      "records": [],
      "total": 0
    }
  },
  "message": "success"
}
```

### 10.2 搜索建议

**接口地址**: `GET /search/suggest`

**查询参数**:
- `keyword`: 搜索关键词
- `limit`: 返回数量，默认10

## 11. 系统配置模块

### 11.1 获取系统配置

**接口地址**: `GET /system/config`

**响应示例**:
```json
{
  "code": 0,
  "data": {
    "siteName": "个人博客系统",
    "siteDescription": "一个基于Spring Boot的个人博客系统",
    "siteKeywords": "博客,技术分享,个人网站",
    "commentNeedAudit": true,
    "articleNeedAudit": false,
    "uploadMaxSize": 10485760,
    "userDefaultAvatar": "/static/images/default-avatar.png"
  },
  "message": "success"
}
```

### 11.2 更新系统配置

**接口地址**: `PUT /admin/system/config`

**权限要求**: 管理员

**请求参数**:
```json
{
  "siteName": "新的网站名称",
  "siteDescription": "新的网站描述",
  "commentNeedAudit": false
}
```

## 12. 错误码定义

| 错误码 | 说明 | 示例场景 |
|--------|------|----------|
| 0 | 成功 | 操作成功 |
| 40000 | 请求参数错误 | 参数格式不正确、必填参数缺失 |
| 40001 | 请求数据为空 | 请求体为空 |
| 40100 | 未登录 | 需要登录的接口未提供认证信息 |
| 40101 | 无权限 | 普通用户访问管理员接口 |
| 40102 | 账号被禁用 | 用户账号被管理员禁用 |
| 40103 | 登录过期 | Session过期需要重新登录 |
| 40400 | 请求数据不存在 | 查询的资源不存在 |
| 40401 | 用户不存在 | 用户ID不存在 |
| 40402 | 文章不存在 | 文章ID不存在 |
| 40403 | 评论不存在 | 评论ID不存在 |
| 40900 | 请求过于频繁 | 触发限流机制 |
| 50000 | 系统内部异常 | 服务器内部错误 |
| 50001 | 操作失败 | 数据库操作失败 |
| 50002 | 文件上传失败 | 文件上传过程中出错 |

## 13. 接口规范说明

### 13.1 命名规范

1. **URL路径**: 使用小写字母和连字符，遵循RESTful风格
   - 正确: `/api/articles`, `/api/user/profile`
   - 错误: `/api/getArticles`, `/api/userProfile`

2. **HTTP方法使用**:
   - `GET`: 查询数据
   - `POST`: 创建数据
   - `PUT`: 更新数据（完整更新）
   - `PATCH`: 更新数据（部分更新）
   - `DELETE`: 删除数据

3. **参数命名**: 使用驼峰命名法
   - 正确: `userId`, `articleId`, `createdTime`
   - 错误: `user_id`, `article-id`, `created_time`

### 13.2 状态码使用

- `200`: 成功
- `201`: 创建成功
- `204`: 删除成功（无返回内容）
- `400`: 请求参数错误
- `401`: 未认证
- `403`: 无权限
- `404`: 资源不存在
- `409`: 资源冲突
- `429`: 请求过于频繁
- `500`: 服务器内部错误

### 13.3 分页规范

所有列表接口统一使用以下分页参数：
- `current`: 当前页码，从1开始
- `size`: 每页大小，默认10，最大100

### 13.4 时间格式

统一使用ISO 8601格式：`yyyy-MM-ddTHH:mm:ss`

### 13.5 安全规范

1. **敏感信息**: 密码等敏感信息不在响应中返回
2. **权限验证**: 所有需要权限的接口都要进行权限验证
3. **参数验证**: 对所有输入参数进行严格验证
4. **SQL注入防护**: 使用参数化查询
5. **XSS防护**: 对用户输入进行转义处理

## 14. 版本说明

- **当前版本**: v1.0.0
- **更新时间**: 2025-01-30
- **维护状态**: 活跃开发中

## 15. 联系方式

- **开发者**: sujiu
- **邮箱**: sujiu@example.com
- **项目地址**: https://github.com/sujiu/blog-backend
```
