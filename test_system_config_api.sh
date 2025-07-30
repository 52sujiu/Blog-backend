#!/bin/bash

# 系统配置API测试脚本
# 使用方法：./test_system_config_api.sh

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

print_info "开始系统配置API测试..."

# 1. 测试获取系统配置（无需登录）
print_info "1. 测试获取系统配置（无需登录）..."
GET_CONFIG_RESPONSE=$(curl -s -X GET "$BASE_URL/system/config" \
    -H "Content-Type: application/json")

if check_response "$GET_CONFIG_RESPONSE" "获取系统配置"; then
    print_info "系统配置详情："
    echo "$GET_CONFIG_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$GET_CONFIG_RESPONSE"
fi

# 2. 管理员登录
print_info "2. 管理员登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"account\": \"$ADMIN_USERNAME\",
        \"password\": \"$ADMIN_PASSWORD\"
    }" \
    -c admin_cookies.txt)

if check_response "$LOGIN_RESPONSE" "管理员登录"; then
    print_info "管理员登录成功，开始测试管理员系统配置接口..."
else
    print_error "管理员登录失败，请检查用户名和密码"
    exit 1
fi

# 3. 测试管理员获取系统配置
print_info "3. 测试管理员获取系统配置..."
ADMIN_GET_CONFIG_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt)

check_response "$ADMIN_GET_CONFIG_RESPONSE" "管理员获取系统配置"

# 4. 测试更新系统配置
print_info "4. 测试更新系统配置..."
UPDATE_CONFIG_RESPONSE=$(curl -s -X PUT "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt \
    -d "{
        \"siteName\": \"测试更新的博客系统\",
        \"siteDescription\": \"这是一个测试更新的描述\",
        \"siteKeywords\": \"测试,更新,博客\",
        \"commentNeedAudit\": false,
        \"articleNeedAudit\": true,
        \"uploadMaxSize\": 20971520,
        \"userDefaultAvatar\": \"/static/images/test-avatar.png\"
    }")

if check_response "$UPDATE_CONFIG_RESPONSE" "更新系统配置"; then
    print_info "更新后的系统配置："
    echo "$UPDATE_CONFIG_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$UPDATE_CONFIG_RESPONSE"
fi

# 5. 验证配置是否更新成功
print_info "5. 验证配置是否更新成功..."
VERIFY_CONFIG_RESPONSE=$(curl -s -X GET "$BASE_URL/system/config" \
    -H "Content-Type: application/json")

if check_response "$VERIFY_CONFIG_RESPONSE" "验证配置更新"; then
    # 检查特定字段是否更新
    if echo "$VERIFY_CONFIG_RESPONSE" | grep -q "测试更新的博客系统"; then
        print_success "网站名称更新验证成功"
    else
        print_error "网站名称更新验证失败"
    fi
    
    if echo "$VERIFY_CONFIG_RESPONSE" | grep -q "这是一个测试更新的描述"; then
        print_success "网站描述更新验证成功"
    else
        print_error "网站描述更新验证失败"
    fi
fi

# 6. 测试部分更新系统配置
print_info "6. 测试部分更新系统配置..."
PARTIAL_UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt \
    -d "{
        \"siteName\": \"部分更新测试\",
        \"commentNeedAudit\": true
    }")

check_response "$PARTIAL_UPDATE_RESPONSE" "部分更新系统配置"

# 7. 测试刷新配置缓存
print_info "7. 测试刷新配置缓存..."
REFRESH_CACHE_RESPONSE=$(curl -s -X POST "$BASE_URL/admin/system/config/refresh" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt)

check_response "$REFRESH_CACHE_RESPONSE" "刷新配置缓存"

# 8. 测试无效参数更新
print_info "8. 测试无效参数更新..."
INVALID_UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt \
    -d "{
        \"uploadMaxSize\": -1
    }")

if echo "$INVALID_UPDATE_RESPONSE" | grep -q '"code":40000'; then
    print_success "无效参数更新 - 正确拒绝"
else
    print_error "无效参数更新 - 应该被拒绝"
    echo "响应内容: $INVALID_UPDATE_RESPONSE"
fi

# 9. 测试空请求更新
print_info "9. 测试空请求更新..."
EMPTY_UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt \
    -d "{}")

if echo "$EMPTY_UPDATE_RESPONSE" | grep -q '"code":40000'; then
    print_success "空请求更新 - 正确拒绝"
else
    print_error "空请求更新 - 应该被拒绝"
    echo "响应内容: $EMPTY_UPDATE_RESPONSE"
fi

# 10. 测试普通用户访问管理员接口
print_info "10. 测试普通用户访问管理员接口..."

# 先登录普通用户
USER_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/user/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"account\": \"testuser\",
        \"password\": \"12345678\"
    }" \
    -c user_cookies.txt)

if echo "$USER_LOGIN_RESPONSE" | grep -q '"code":0'; then
    # 普通用户尝试访问管理员接口
    USER_ACCESS_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/system/config" \
        -H "Content-Type: application/json" \
        -b user_cookies.txt)

    if echo "$USER_ACCESS_RESPONSE" | grep -q '"code":40101'; then
        print_success "普通用户访问管理员接口 - 正确拒绝"
    else
        print_error "普通用户访问管理员接口 - 应该被拒绝"
        echo "响应内容: $USER_ACCESS_RESPONSE"
    fi
else
    print_info "普通用户不存在，跳过权限测试"
fi

# 11. 测试未登录访问管理员接口
print_info "11. 测试未登录访问管理员接口..."
NO_LOGIN_RESPONSE=$(curl -s -X GET "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json")

if echo "$NO_LOGIN_RESPONSE" | grep -q '"code":40100'; then
    print_success "未登录访问管理员接口 - 正确拒绝"
else
    print_error "未登录访问管理员接口 - 应该被拒绝"
    echo "响应内容: $NO_LOGIN_RESPONSE"
fi

# 12. 恢复原始配置
print_info "12. 恢复原始配置..."
RESTORE_CONFIG_RESPONSE=$(curl -s -X PUT "$BASE_URL/admin/system/config" \
    -H "Content-Type: application/json" \
    -b admin_cookies.txt \
    -d "{
        \"siteName\": \"个人博客系统\",
        \"siteDescription\": \"一个基于Spring Boot的个人博客系统\",
        \"siteKeywords\": \"博客,技术分享,个人网站\",
        \"commentNeedAudit\": true,
        \"articleNeedAudit\": false,
        \"uploadMaxSize\": 10485760,
        \"userDefaultAvatar\": \"/static/images/default-avatar.png\"
    }")

check_response "$RESTORE_CONFIG_RESPONSE" "恢复原始配置"

# 清理临时文件
rm -f admin_cookies.txt user_cookies.txt

print_info "系统配置API测试完成！"

# 显示最终配置状态
print_info "最终系统配置状态："
FINAL_CONFIG_RESPONSE=$(curl -s -X GET "$BASE_URL/system/config" \
    -H "Content-Type: application/json")
echo "$FINAL_CONFIG_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$FINAL_CONFIG_RESPONSE"
