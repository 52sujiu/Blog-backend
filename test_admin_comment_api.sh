#!/bin/bash

# 博客系统管理员评论管理API测试脚本

BASE_URL="http://localhost:8101/api"

echo "=== 博客系统管理员评论管理API测试 ==="
echo

# 1. 注册一个管理员用户
echo "1. 注册管理员用户..."
ADMIN_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "commentadmin456",
    "email": "commentadmin456@example.com",
    "password": "123456",
    "confirmPassword": "123456"
  }')

echo "管理员注册响应: $ADMIN_REGISTER_RESPONSE"
echo

# 2. 登录管理员用户
echo "2. 登录管理员用户..."
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "commentadmin456",
    "password": "123456"
  }')

echo "管理员登录响应: $ADMIN_LOGIN_RESPONSE"
echo

# 3. 注册一个作者用户
echo "3. 注册作者用户..."
AUTHOR_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "commentauthor456",
    "email": "commentauthor456@example.com",
    "password": "123456",
    "confirmPassword": "123456"
  }')

echo "作者注册响应: $AUTHOR_REGISTER_RESPONSE"
echo

# 4. 登录作者用户并发布文章
echo "4. 登录作者用户..."
AUTHOR_LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -c author_cookies.txt \
  -d '{
    "username": "commentauthor456",
    "password": "123456"
  }')

echo "作者登录响应: $AUTHOR_LOGIN_RESPONSE"
echo

# 5. 发布一篇文章
echo "5. 发布文章..."
ARTICLE_PUBLISH_RESPONSE=$(curl -s -X POST "${BASE_URL}/articles" \
  -H "Content-Type: application/json" \
  -b author_cookies.txt \
  -d '{
    "title": "测试文章标题",
    "summary": "这是一篇测试文章的摘要",
    "content": "# 测试文章\n\n这是测试文章的内容。",
    "status": 2
  }')

echo "文章发布响应: $ARTICLE_PUBLISH_RESPONSE"
echo

# 6. 提取文章ID（简化处理，实际应该解析JSON）
ARTICLE_ID=$(echo $ARTICLE_PUBLISH_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "文章ID: $ARTICLE_ID"
echo

# 7. 发表评论
echo "7. 发表评论..."
COMMENT_RESPONSE=$(curl -s -X POST "${BASE_URL}/comments" \
  -H "Content-Type: application/json" \
  -b author_cookies.txt \
  -d "{
    \"content\": \"这是一条测试评论\",
    \"articleId\": $ARTICLE_ID
  }")

echo "评论发表响应: $COMMENT_RESPONSE"
echo

# 8. 提取评论ID
COMMENT_ID=$(echo $COMMENT_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "评论ID: $COMMENT_ID"
echo

# 9. 手动设置管理员权限
echo "9. 需要手动将 commentadmin456 设置为管理员角色..."
echo "请在数据库中执行: UPDATE sys_user SET role='admin' WHERE username='commentadmin456';"
echo "按回车键继续..."
read

# 10. 重新登录管理员用户
echo "10. 重新登录管理员用户..."
ADMIN_LOGIN_RESPONSE2=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "commentadmin456",
    "password": "123456"
  }')

echo "管理员重新登录响应: $ADMIN_LOGIN_RESPONSE2"
echo

# 11. 获取评论列表（管理员权限）
echo "11. 获取评论列表（管理员权限）..."
COMMENT_LIST_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/comments?current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "评论列表响应: $COMMENT_LIST_RESPONSE"
echo

# 12. 按状态筛选评论（已审核）
echo "12. 筛选已审核的评论..."
COMMENT_APPROVED_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/comments?status=1&current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "已审核评论响应: $COMMENT_APPROVED_RESPONSE"
echo

# 13. 搜索评论
echo "13. 搜索评论（关键词：测试）..."
COMMENT_SEARCH_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/comments?keyword=测试&current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "评论搜索响应: $COMMENT_SEARCH_RESPONSE"
echo

# 14. 按文章ID筛选评论
if [ ! -z "$ARTICLE_ID" ]; then
    echo "14. 按文章ID筛选评论..."
    COMMENT_BY_ARTICLE_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/comments?articleId=${ARTICLE_ID}&current=1&size=10" \
      -H "Content-Type: application/json" \
      -b cookies.txt)

    echo "按文章筛选评论响应: $COMMENT_BY_ARTICLE_RESPONSE"
    echo
fi

# 15. 审核评论（通过）
if [ ! -z "$COMMENT_ID" ]; then
    echo "15. 审核评论（通过）..."
    AUDIT_APPROVE_RESPONSE=$(curl -s -X PUT "${BASE_URL}/admin/comments/${COMMENT_ID}/audit" \
      -H "Content-Type: application/json" \
      -b cookies.txt \
      -d '{
        "status": 1,
        "auditReason": "内容合规，审核通过"
      }')

    echo "审核通过响应: $AUDIT_APPROVE_RESPONSE"
    echo
fi

# 16. 删除评论
if [ ! -z "$COMMENT_ID" ]; then
    echo "16. 删除评论..."
    DELETE_COMMENT_RESPONSE=$(curl -s -X DELETE "${BASE_URL}/admin/comments/${COMMENT_ID}" \
      -H "Content-Type: application/json" \
      -b cookies.txt)

    echo "删除评论响应: $DELETE_COMMENT_RESPONSE"
    echo
fi

echo "=== 测试完成 ==="
echo "注意：某些操作需要手动设置管理员权限"

# 清理
rm -f cookies.txt author_cookies.txt
