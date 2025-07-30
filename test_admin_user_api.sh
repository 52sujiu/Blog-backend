#!/bin/bash

# 博客系统管理员用户管理API测试脚本

BASE_URL="http://localhost:8101/api"

echo "=== 博客系统管理员用户管理API测试 ==="
echo

# 1. 注册一个管理员用户
echo "1. 注册管理员用户..."
ADMIN_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testadmin123",
    "email": "testadmin123@example.com",
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
    "username": "testadmin123",
    "password": "123456"
  }')

echo "管理员登录响应: $ADMIN_LOGIN_RESPONSE"
echo

# 3. 注册一个普通用户用于测试
echo "3. 注册普通用户..."
USER_REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser123",
    "email": "testuser123@example.com",
    "password": "123456",
    "confirmPassword": "123456"
  }')

echo "普通用户注册响应: $USER_REGISTER_RESPONSE"
echo

# 4. 尝试获取用户列表（应该失败，因为不是管理员）
echo "4. 尝试获取用户列表（普通用户权限）..."
USER_LIST_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/users" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "用户列表响应（普通用户）: $USER_LIST_RESPONSE"
echo

# 5. 手动设置管理员权限（在实际应用中，这应该通过数据库操作完成）
echo "5. 需要手动将 testadmin123 设置为管理员角色..."
echo "请在数据库中执行: UPDATE sys_user SET role='admin' WHERE username='testadmin123';"
echo "按回车键继续..."
read

# 6. 重新登录管理员用户
echo "6. 重新登录管理员用户..."
ADMIN_LOGIN_RESPONSE2=$(curl -s -X POST "${BASE_URL}/users/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "testadmin123",
    "password": "123456"
  }')

echo "管理员重新登录响应: $ADMIN_LOGIN_RESPONSE2"
echo

# 7. 获取用户列表（管理员权限）
echo "7. 获取用户列表（管理员权限）..."
USER_LIST_RESPONSE2=$(curl -s -X GET "${BASE_URL}/admin/users?current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "用户列表响应（管理员）: $USER_LIST_RESPONSE2"
echo

# 8. 搜索用户
echo "8. 搜索用户（关键词：testuser）..."
USER_SEARCH_RESPONSE=$(curl -s -X GET "${BASE_URL}/admin/users?keyword=testuser&current=1&size=10" \
  -H "Content-Type: application/json" \
  -b cookies.txt)

echo "用户搜索响应: $USER_SEARCH_RESPONSE"
echo

# 9. 更新用户状态（禁用用户）
echo "9. 禁用用户 testuser123..."
# 首先需要获取用户ID，这里假设从搜索结果中获取
echo "请从上面的搜索结果中找到 testuser123 的用户ID，然后手动测试状态更新..."
echo "示例命令："
echo "curl -X PUT \"${BASE_URL}/admin/users/{userId}/status\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -b cookies.txt \\"
echo "  -d '{\"status\": 0, \"reason\": \"测试禁用\"}'"
echo

# 10. 更新用户角色
echo "10. 更新用户角色示例..."
echo "curl -X PUT \"${BASE_URL}/admin/users/{userId}/role\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -b cookies.txt \\"
echo "  -d '{\"role\": \"ban\"}'"
echo

# 11. 删除用户
echo "11. 删除用户示例..."
echo "curl -X DELETE \"${BASE_URL}/admin/users/{userId}\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -b cookies.txt"
echo

echo "=== 测试完成 ==="
echo "注意：某些操作需要手动获取用户ID并替换到命令中"

# 清理
rm -f cookies.txt
