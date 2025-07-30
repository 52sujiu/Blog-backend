#!/bin/bash

# 管理员仪表板API测试脚本
# 使用方法：./test_admin_dashboard_api.sh

# 配置
BASE_URL="http://localhost:8101/api"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="1234567"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印函数
print_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查响应状态
check_response() {
    local response="$1"
    local test_name="$2"
    
    if echo "$response" | grep -q '"code":0'; then
        print_success "$test_name - 测试通过"
        return 0
    else
        print_error "$test_name - 测试失败"
        echo "响应内容: $response"
        return 1
    fi
}

print_info "开始管理员仪表板API测试..."

# 1. 管理员登录
print_info "1. 管理员登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"account\": \"$ADMIN_USERNAME\",
        \"password\": \"$ADMIN_PASSWORD\"
    }" \
    -c cookies.txt)

if check_response "$LOGIN_RESPONSE" "管理员登录"; then
    print_info "管理员登录成功，开始测试仪表板接口..."
else
    print_error "管理员登录失败，请检查用户名和密码"
    exit 1
fi

# 2. 获取系统概览
print_info "2. 测试获取系统概览..."
OVERVIEW_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/dashboard/overview" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$OVERVIEW_RESPONSE" "获取系统概览"

# 3. 获取用户统计（默认参数）
print_info "3. 测试获取用户统计（默认参数）..."
USER_STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/users" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$USER_STATS_RESPONSE" "获取用户统计（默认参数）"

# 4. 获取用户统计（自定义参数）
print_info "4. 测试获取用户统计（自定义参数）..."
USER_STATS_CUSTOM_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/users?type=daily&days=7" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$USER_STATS_CUSTOM_RESPONSE" "获取用户统计（自定义参数）"

# 5. 获取文章统计（默认参数）
print_info "5. 测试获取文章统计（默认参数）..."
ARTICLE_STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/articles" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$ARTICLE_STATS_RESPONSE" "获取文章统计（默认参数）"

# 6. 获取文章统计（自定义参数）
print_info "6. 测试获取文章统计（自定义参数）..."
ARTICLE_STATS_CUSTOM_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/articles?type=weekly&days=14" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$ARTICLE_STATS_CUSTOM_RESPONSE" "获取文章统计（自定义参数）"

# 7. 获取访问统计（默认参数）
print_info "7. 测试获取访问统计（默认参数）..."
VIEW_STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/views" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$VIEW_STATS_RESPONSE" "获取访问统计（默认参数）"

# 8. 获取访问统计（自定义参数）
print_info "8. 测试获取访问统计（自定义参数）..."
VIEW_STATS_CUSTOM_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/statistics/views?type=monthly&days=60" \
    -H "Content-Type: application/json" \
    -b cookies.txt)

check_response "$VIEW_STATS_CUSTOM_RESPONSE" "获取访问统计（自定义参数）"

# 9. 测试权限控制 - 普通用户登录
print_info "9. 测试权限控制 - 普通用户访问..."

# 先注册一个普通用户（如果不存在）
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/user/register" \
    -H "Content-Type: application/json" \
    -d "{
        \"username\": \"testuser\",
        \"password\": \"123456\",
        \"confirmPassword\": \"123456\",
        \"email\": \"testuser@example.com\"
    }")

# 普通用户登录
USER_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"account\": \"testuser\",
        \"password\": \"123456\"
    }" \
    -c user_cookies.txt)

# 普通用户尝试访问管理员接口
USER_ACCESS_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/dashboard/overview" \
    -H "Content-Type: application/json" \
    -b user_cookies.txt)

if echo "$USER_ACCESS_RESPONSE" | grep -q '"code":40101'; then
    print_success "权限控制 - 普通用户被正确拒绝访问"
else
    print_error "权限控制 - 普通用户访问控制失败"
    echo "响应内容: $USER_ACCESS_RESPONSE"
fi

# 10. 测试未登录访问
print_info "10. 测试未登录访问..."
NO_LOGIN_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/dashboard/overview" \
    -H "Content-Type: application/json")

if echo "$NO_LOGIN_RESPONSE" | grep -q '"code":40100'; then
    print_success "权限控制 - 未登录用户被正确拒绝访问"
else
    print_error "权限控制 - 未登录用户访问控制失败"
    echo "响应内容: $NO_LOGIN_RESPONSE"
fi

# 清理临时文件
rm -f cookies.txt user_cookies.txt

print_info "管理员仪表板API测试完成！"

# 显示详细的系统概览数据
print_info "系统概览数据详情："
echo "$OVERVIEW_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$OVERVIEW_RESPONSE"
