# 博客系统数据模型文档

## 1. 概述

本文档定义了博客系统中所有数据传输对象（DTO）、值对象（VO）和请求对象（Request）的结构。

## 2. 用户相关模型

### 2.1 用户注册请求 (UserRegisterRequest)

```json
{
  "username": "string",      // 用户名，3-20字符，字母数字下划线
  "email": "string",         // 邮箱地址
  "password": "string",      // 密码，6-20字符
  "confirmPassword": "string" // 确认密码
}
```

### 2.2 用户登录请求 (UserLoginRequest)

```json
{
  "account": "string",       // 账号（用户名或邮箱）
  "password": "string",      // 密码
  "rememberMe": "boolean"    // 是否记住登录，默认false
}
```

### 2.3 用户信息更新请求 (UserUpdateRequest)

```json
{
  "nickname": "string",      // 昵称，1-50字符
  "avatar": "string",        // 头像URL
  "phone": "string",         // 手机号
  "gender": "integer",       // 性别：0-未知，1-男，2-女
  "birthday": "string",      // 生日，格式：yyyy-MM-dd
  "bio": "string",           // 个人简介，最多500字符
  "website": "string",       // 个人网站
  "location": "string"       // 所在地
}
```

### 2.4 密码修改请求 (PasswordUpdateRequest)

```json
{
  "oldPassword": "string",   // 原密码
  "newPassword": "string",   // 新密码
  "confirmPassword": "string" // 确认新密码
}
```

### 2.5 登录用户信息 (LoginUserVO)

```json
{
  "id": "long",              // 用户ID
  "username": "string",      // 用户名
  "nickname": "string",      // 昵称
  "avatar": "string",        // 头像URL
  "email": "string",         // 邮箱
  "phone": "string",         // 手机号
  "gender": "integer",       // 性别
  "birthday": "string",      // 生日
  "bio": "string",           // 个人简介
  "website": "string",       // 个人网站
  "location": "string",      // 所在地
  "role": "string",          // 角色：user, admin
  "createdTime": "string",   // 注册时间
  "lastLoginTime": "string"  // 最后登录时间
}
```

### 2.6 用户公开信息 (UserVO)

```json
{
  "id": "long",              // 用户ID
  "username": "string",      // 用户名
  "nickname": "string",      // 昵称
  "avatar": "string",        // 头像URL
  "bio": "string",           // 个人简介
  "website": "string",       // 个人网站
  "location": "string",      // 所在地
  "articleCount": "integer", // 文章数量
  "followingCount": "integer", // 关注数
  "followerCount": "integer",  // 粉丝数
  "createdTime": "string"    // 注册时间
}
```

## 3. 文章相关模型

### 3.1 文章创建/更新请求 (ArticleRequest)

```json
{
  "title": "string",         // 文章标题，1-200字符
  "slug": "string",          // 文章别名，URL友好
  "summary": "string",       // 文章摘要，最多500字符
  "content": "string",       // 文章内容（Markdown格式）
  "coverImage": "string",    // 封面图片URL
  "categoryId": "long",      // 分类ID
  "tagIds": ["long"],        // 标签ID数组
  "isTop": "boolean",        // 是否置顶，默认false
  "isRecommend": "boolean",  // 是否推荐，默认false
  "isOriginal": "boolean",   // 是否原创，默认true
  "sourceUrl": "string",     // 转载来源URL（非原创时）
  "password": "string",      // 文章密码（可选）
  "status": "integer"        // 状态：0-草稿，1-审核中，2-已发布
}
```

### 3.2 文章详情 (ArticleVO)

```json
{
  "id": "long",              // 文章ID
  "title": "string",         // 文章标题
  "slug": "string",          // 文章别名
  "summary": "string",       // 文章摘要
  "content": "string",       // 文章内容（Markdown）
  "contentHtml": "string",   // 文章内容（HTML）
  "coverImage": "string",    // 封面图片URL
  "author": {                // 作者信息
    "id": "long",
    "username": "string",
    "nickname": "string",
    "avatar": "string"
  },
  "category": {              // 分类信息
    "id": "long",
    "name": "string",
    "slug": "string"
  },
  "tags": [{                 // 标签列表
    "id": "long",
    "name": "string",
    "slug": "string",
    "color": "string"
  }],
  "isTop": "boolean",        // 是否置顶
  "isRecommend": "boolean",  // 是否推荐
  "isOriginal": "boolean",   // 是否原创
  "sourceUrl": "string",     // 转载来源URL
  "viewCount": "integer",    // 浏览次数
  "likeCount": "integer",    // 点赞次数
  "commentCount": "integer", // 评论次数
  "wordCount": "integer",    // 字数统计
  "readingTime": "integer",  // 预计阅读时间（分钟）
  "status": "integer",       // 状态
  "publishedTime": "string", // 发布时间
  "createdTime": "string",   // 创建时间
  "updatedTime": "string"    // 更新时间
}
```

### 3.3 文章列表项 (ArticleListVO)

```json
{
  "id": "long",              // 文章ID
  "title": "string",         // 文章标题
  "slug": "string",          // 文章别名
  "summary": "string",       // 文章摘要
  "coverImage": "string",    // 封面图片URL
  "author": {                // 作者信息
    "id": "long",
    "nickname": "string",
    "avatar": "string"
  },
  "category": {              // 分类信息
    "id": "long",
    "name": "string",
    "slug": "string"
  },
  "tags": [{                 // 标签列表
    "id": "long",
    "name": "string",
    "color": "string"
  }],
  "isTop": "boolean",        // 是否置顶
  "isRecommend": "boolean",  // 是否推荐
  "viewCount": "integer",    // 浏览次数
  "likeCount": "integer",    // 点赞次数
  "commentCount": "integer", // 评论次数
  "readingTime": "integer",  // 预计阅读时间
  "publishedTime": "string"  // 发布时间
}
```

### 3.4 文章查询请求 (ArticleQueryRequest)

```json
{
  "current": "integer",      // 当前页码，默认1
  "size": "integer",         // 每页大小，默认10
  "categoryId": "long",      // 分类ID
  "tagId": "long",           // 标签ID
  "authorId": "long",        // 作者ID
  "keyword": "string",       // 搜索关键词
  "status": "integer",       // 文章状态
  "isTop": "boolean",        // 是否置顶
  "isRecommend": "boolean",  // 是否推荐
  "sortField": "string",     // 排序字段
  "sortOrder": "string"      // 排序方式：asc, desc
}
```

## 4. 分类相关模型

### 4.1 分类创建/更新请求 (CategoryRequest)

```json
{
  "name": "string",          // 分类名称，1-50字符
  "slug": "string",          // 分类别名，URL友好
  "description": "string",   // 分类描述
  "coverImage": "string",    // 分类封面图
  "color": "string",         // 分类颜色，十六进制
  "parentId": "long",        // 父分类ID，0为顶级分类
  "sortOrder": "integer"     // 排序值
}
```

### 4.2 分类信息 (CategoryVO)

```json
{
  "id": "long",              // 分类ID
  "name": "string",          // 分类名称
  "slug": "string",          // 分类别名
  "description": "string",   // 分类描述
  "coverImage": "string",    // 分类封面图
  "color": "string",         // 分类颜色
  "parentId": "long",        // 父分类ID
  "sortOrder": "integer",    // 排序值
  "articleCount": "integer", // 文章数量
  "status": "integer",       // 状态：1-正常，0-禁用
  "children": [              // 子分类列表
    {
      "id": "long",
      "name": "string",
      "slug": "string",
      "articleCount": "integer"
    }
  ]
}
```

## 5. 标签相关模型

### 5.1 标签创建/更新请求 (TagRequest)

```json
{
  "name": "string",          // 标签名称，1-50字符
  "slug": "string",          // 标签别名，URL友好
  "description": "string",   // 标签描述
  "color": "string"          // 标签颜色，十六进制
}
```

### 5.2 标签信息 (TagVO)

```json
{
  "id": "long",              // 标签ID
  "name": "string",          // 标签名称
  "slug": "string",          // 标签别名
  "description": "string",   // 标签描述
  "color": "string",         // 标签颜色
  "articleCount": "integer", // 文章数量
  "status": "integer"        // 状态：1-正常，0-禁用
}
```

## 6. 评论相关模型

### 6.1 评论创建请求 (CommentRequest)

```json
{
  "content": "string",       // 评论内容，1-1000字符
  "parentId": "long",        // 父评论ID，0为顶级评论
  "replyToId": "long"        // 回复目标评论ID
}
```

### 6.2 评论信息 (CommentVO)

```json
{
  "id": "long",              // 评论ID
  "content": "string",       // 评论内容
  "author": {                // 评论者信息
    "id": "long",
    "username": "string",
    "nickname": "string",
    "avatar": "string"
  },
  "parentId": "long",        // 父评论ID
  "replyTo": {               // 回复目标（如果是回复）
    "id": "long",
    "nickname": "string"
  },
  "likeCount": "integer",    // 点赞次数
  "isLiked": "boolean",      // 当前用户是否已点赞
  "status": "integer",       // 状态：0-待审核，1-已审核
  "createdTime": "string",   // 创建时间
  "replies": [               // 回复列表
    {
      "id": "long",
      "content": "string",
      "author": {
        "id": "long",
        "nickname": "string",
        "avatar": "string"
      },
      "replyTo": {
        "id": "long",
        "nickname": "string"
      },
      "likeCount": "integer",
      "isLiked": "boolean",
      "createdTime": "string"
    }
  ]
}
```

## 7. 文件上传模型

### 7.1 文件上传响应 (FileUploadVO)

```json
{
  "url": "string",           // 文件访问URL
  "filename": "string",      // 文件名
  "originalName": "string",  // 原始文件名
  "size": "long",            // 文件大小（字节）
  "type": "string"           // 文件MIME类型
}
```

## 8. 搜索相关模型

### 8.1 搜索请求 (SearchRequest)

```json
{
  "keyword": "string",       // 搜索关键词
  "type": "string",          // 搜索类型：article, user, tag, category
  "current": "integer",      // 当前页码
  "size": "integer"          // 每页大小
}
```

### 8.2 搜索结果 (SearchResultVO)

```json
{
  "articles": {              // 文章搜索结果
    "records": [{
      "id": "long",
      "title": "string",
      "summary": "string",
      "author": {
        "nickname": "string"
      },
      "publishedTime": "string",
      "highlight": {           // 高亮信息
        "title": "string",
        "content": "string"
      }
    }],
    "total": "long"
  },
  "users": {                 // 用户搜索结果
    "records": [{
      "id": "long",
      "username": "string",
      "nickname": "string",
      "avatar": "string"
    }],
    "total": "long"
  },
  "tags": {                  // 标签搜索结果
    "records": [{
      "id": "long",
      "name": "string",
      "color": "string"
    }],
    "total": "long"
  }
}
```

## 9. 管理员相关模型

### 9.1 用户状态更新请求 (UserStatusUpdateRequest)

```json
{
  "status": "integer",       // 用户状态：1-正常，0-禁用，-1-删除
  "reason": "string"         // 操作原因
}
```

### 9.2 用户角色更新请求 (UserRoleUpdateRequest)

```json
{
  "role": "string"           // 用户角色：user, admin, ban
}
```

### 9.3 文章审核请求 (ArticleAuditRequest)

```json
{
  "status": "integer",       // 审核状态：2-通过，3-拒绝
  "auditReason": "string"    // 审核原因
}
```

### 9.4 评论审核请求 (CommentAuditRequest)

```json
{
  "status": "integer",       // 审核状态：1-通过，2-拒绝
  "auditReason": "string"    // 审核原因
}
```

### 9.5 系统概览 (DashboardOverviewVO)

```json
{
  "userCount": "long",       // 总用户数
  "articleCount": "long",    // 总文章数
  "commentCount": "long",    // 总评论数
  "viewCount": "long",       // 总浏览量
  "todayUserCount": "long",  // 今日新增用户
  "todayArticleCount": "long", // 今日新增文章
  "todayCommentCount": "long", // 今日新增评论
  "todayViewCount": "long"   // 今日浏览量
}
```

## 10. 系统配置模型

### 10.1 系统配置 (SystemConfigVO)

```json
{
  "siteName": "string",      // 网站名称
  "siteDescription": "string", // 网站描述
  "siteKeywords": "string",  // 网站关键词
  "commentNeedAudit": "boolean", // 评论是否需要审核
  "articleNeedAudit": "boolean", // 文章是否需要审核
  "uploadMaxSize": "long",   // 文件上传最大大小
  "userDefaultAvatar": "string" // 用户默认头像
}
```

### 10.2 系统配置更新请求 (SystemConfigUpdateRequest)

```json
{
  "siteName": "string",
  "siteDescription": "string",
  "siteKeywords": "string",
  "commentNeedAudit": "boolean",
  "articleNeedAudit": "boolean",
  "uploadMaxSize": "long",
  "userDefaultAvatar": "string"
}
```

## 11. 通用模型

### 11.1 分页请求 (PageRequest)

```json
{
  "current": "integer",      // 当前页码，默认1
  "size": "integer",         // 每页大小，默认10，最大100
  "sortField": "string",     // 排序字段
  "sortOrder": "string"      // 排序方式：asc, desc
}
```

### 11.2 分页响应 (PageVO)

```json
{
  "records": [],             // 数据列表
  "total": "long",           // 总记录数
  "size": "long",            // 每页大小
  "current": "long",         // 当前页码
  "pages": "long"            // 总页数
}
```

### 11.3 基础响应 (BaseResponse)

```json
{
  "code": "integer",         // 响应码：0-成功，其他-失败
  "data": "object",          // 响应数据
  "message": "string"        // 响应消息
}
```

## 12. 枚举定义

### 12.1 用户状态 (UserStatus)

- `1`: 正常
- `0`: 禁用
- `-1`: 删除

### 12.2 用户角色 (UserRole)

- `user`: 普通用户
- `admin`: 管理员
- `ban`: 封禁用户

### 12.3 文章状态 (ArticleStatus)

- `0`: 草稿
- `1`: 审核中
- `2`: 已发布
- `3`: 已拒绝
- `4`: 已下架

### 12.4 评论状态 (CommentStatus)

- `0`: 待审核
- `1`: 已审核
- `2`: 已删除

### 12.5 点赞目标类型 (LikeTargetType)

- `1`: 文章
- `2`: 评论

### 12.6 性别 (Gender)

- `0`: 未知
- `1`: 男
- `2`: 女

## 13. 验证规则

### 13.1 字段长度限制

- 用户名：3-20字符
- 密码：6-20字符
- 昵称：1-50字符
- 文章标题：1-200字符
- 文章摘要：最多500字符
- 评论内容：1-1000字符
- 个人简介：最多500字符

### 13.2 格式验证

- 邮箱：符合邮箱格式
- 手机号：11位数字
- 日期：yyyy-MM-dd格式
- 颜色：十六进制格式（如#1890ff）
- URL：符合URL格式

### 13.3 业务规则

- 用户名和邮箱全局唯一
- 文章别名全局唯一
- 分类和标签名称唯一
- 不能关注自己
- 不能重复点赞
- 评论层级最多3层
```
