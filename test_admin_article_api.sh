#!/bin/bash

# 博客系统管理员文章管理API测试脚本

BASE_URL="http://localhost:8101/api"

echo "=== 博客系统管理员文章管理API测试 ==="
echo

# 1. 注册一个管理员用户
echo "1. 注册管理员用户..."
ADMIN_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "articleadmin123",
    "email": "articleadmin123@example.com",
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
    "username": "articleadmin123",
    "password": "123456"
  }')

echo "管理员登录响应: $ADMIN_LOGIN_RESPONSE"
echo

# 3. 注册一个作者用户
echo "3. 注册作者用户..."
AUTHOR_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testauthor123",
    "email": "testauthor123@example.com",
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
    "username": "testauthor123",
    "password": "123456"
  }')

echo "作者登录响应: $AUTHOR_LOGIN_RESPONSE"
echo

# 5. 发布一篇待审核的文章
echo "5. 发布待审核文章..."
ARTICLE_PUBLISH_RESPONSE=$(curl -s -X POST "${BASE_URL}/articles" \
  -H "Content-Type: application/json" \
  -b author_cookies.txt \
  -d '{
    "title": "测试文章标题",
    "summary": "这是一篇测试文章的摘要",
    "content": "# 测试文章\n\n这是测试文章的内容。",
    "status": 1
  }')

echo "文章发布响应: $ARTICLE_PUBLISH_RESPONSE"
echo

# 6. 提取文章ID（简化处理，实际应该解析JSON）
ARTICLE_ID=$(echo $ARTICLE_PUBLISH_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "文章ID: $ARTICLE_ID"
echo

# 7. 手动设置管理员权限
echo "7. 需要手动将 articleadmin123 设置为管理员角色..."
echo "请在数据库中执行: UPDATE sys_user SET role='admin' WHERE username='articleadmin123';"
echo "按回车键继续..."
read

# 8. 重新登录管理员用户
echo "8. 重新登录管理员用户..."
ADMIN_LOGIN_RESPONSE2=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "articleadmin123",
    "password": "123456"
  }')

echo "管理员重新登录响应: $ADMIN_LOGIN_RESPONSE2"
echo

# 9. 获取文章列表（管理员权限）
echo "9. 获取文章列表（管理员权限）..."
ARTICLE_LIST_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/articles?current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "文章列表响应: $ARTICLE_LIST_RESPONSE"
echo

# 10. 按状态筛选文章（审核中）
echo "10. 筛选审核中的文章..."
ARTICLE_REVIEW_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/articles?status=1&current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "审核中文章响应: $ARTICLE_REVIEW_RESPONSE"
echo

# 11. 搜索文章
echo "11. 搜索文章（关键词：测试）..."
ARTICLE_SEARCH_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/articles?keyword=测试&current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "文章搜索响应: $ARTICLE_SEARCH_RESPONSE"
echo

# 12. 审核文章（通过）
if [ ! -z "$ARTICLE_ID" ]; then
    echo "12. 审核文章（通过）..."
    AUDIT_APPROVE_RESPONSE=$(curl -s -X PUT "${BASE_URL}/admin/articles/${ARTICLE_ID}/audit" \
      -H "Content-Type: application/json" \
      -b cookies.txt \
      -d '{
        "status": 2,
        "auditReason": "内容质量良好，审核通过"
      }')

    echo "审核通过响应: $AUDIT_APPROVE_RESPONSE"
    echo

    # 13. 下架文章
    echo "13. 下架文章..."
    OFFLINE_RESPONSE=$(curl -s -X PUT "${BASE_URL}/admin/articles/${ARTICLE_ID}/offline" \
      -H "Content-Type: application/json" \
      -b cookies.txt \
      -d '{
        "reason": "违规内容，需要下架"
      }')

    echo "下架文章响应: $OFFLINE_RESPONSE"
    echo

    # 14. 删除文章
    echo "14. 删除文章..."
    DELETE_RESPONSE=$(curl -s -X DELETE "${BASE_URL}/admin/articles/${ARTICLE_ID}" \
      -H "Content-Type: application/json" \
      -b cookies.txt)

    echo "删除文章响应: $DELETE_RESPONSE"
    echo
else
    echo "无法获取文章ID，跳过审核、下架和删除操作"
fi

echo "=== 测试完成 ==="
echo "注意：某些操作需要手动设置管理员权限"

# 清理
rm -f cookies.txt author_cookies.txt
