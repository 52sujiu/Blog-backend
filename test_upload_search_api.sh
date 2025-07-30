#!/bin/bash

# 文件上传和搜索API测试脚本
# 使用方法：./test_upload_search_api.sh

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

print_info "开始文件上传和搜索API测试..."

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
    print_info "管理员登录成功，开始测试文件上传和搜索接口..."
else
    print_error "管理员登录失败，请检查用户名和密码"
    exit 1
fi

# 2. 创建测试图片文件
print_info "2. 创建测试图片文件..."
echo "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==" | base64 -d > test_image.png

# 3. 测试图片上传
print_info "3. 测试图片上传..."
UPLOAD_IMAGE_RESPONSE=$(curl -s -X POST "$BASE_URL/upload/image" \
    -H "Content-Type: multipart/form-data" \
    -F "file=@test_image.png" \
    -F "type=avatar" \
    -b cookies.txt)

check_response "$UPLOAD_IMAGE_RESPONSE" "图片上传"

# 4. 创建测试文档文件
print_info "4. 创建测试文档文件..."
echo "这是一个测试PDF文档的内容" > test_document.txt

# 5. 测试文档上传
print_info "5. 测试文档上传..."
UPLOAD_FILE_RESPONSE=$(curl -s -X POST "$BASE_URL/upload/file" \
    -H "Content-Type: multipart/form-data" \
    -F "file=@test_document.txt" \
    -F "type=document" \
    -b cookies.txt)

check_response "$UPLOAD_FILE_RESPONSE" "文档上传"

# 6. 测试无效文件类型上传
print_info "6. 测试无效文件类型上传..."
INVALID_UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/upload/image" \
    -H "Content-Type: multipart/form-data" \
    -F "file=@test_document.txt" \
    -F "type=avatar" \
    -b cookies.txt)

if echo "$INVALID_UPLOAD_RESPONSE" | grep -q '"code":40000'; then
    print_success "无效文件类型上传 - 正确拒绝"
else
    print_error "无效文件类型上传 - 应该被拒绝"
    echo "响应内容: $INVALID_UPLOAD_RESPONSE"
fi

# 7. 测试搜索功能（全部类型）
print_info "7. 测试搜索功能（全部类型）..."
SEARCH_ALL_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=Spring&type=all&current=1&size=10" \
    -H "Content-Type: application/json")

check_response "$SEARCH_ALL_RESPONSE" "搜索全部类型"

# 8. 测试搜索文章
print_info "8. 测试搜索文章..."
SEARCH_ARTICLE_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=Java&type=article&current=1&size=5" \
    -H "Content-Type: application/json")

check_response "$SEARCH_ARTICLE_RESPONSE" "搜索文章"

# 9. 测试搜索用户
print_info "9. 测试搜索用户..."
SEARCH_USER_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=admin&type=user&current=1&size=5" \
    -H "Content-Type: application/json")

check_response "$SEARCH_USER_RESPONSE" "搜索用户"

# 10. 测试搜索标签
print_info "10. 测试搜索标签..."
SEARCH_TAG_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=tech&type=tag&current=1&size=5" \
    -H "Content-Type: application/json")

check_response "$SEARCH_TAG_RESPONSE" "搜索标签"

# 11. 测试搜索分类
print_info "11. 测试搜索分类..."
SEARCH_CATEGORY_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=programming&type=category&current=1&size=5" \
    -H "Content-Type: application/json")

check_response "$SEARCH_CATEGORY_RESPONSE" "搜索分类"

# 12. 测试高级搜索（POST方式）
print_info "12. 测试高级搜索（POST方式）..."
ADVANCED_SEARCH_RESPONSE=$(curl -s -X POST "$BASE_URL/search" \
    -H "Content-Type: application/json" \
    -d "{
        \"keyword\": \"Spring Boot\",
        \"type\": \"article\",
        \"current\": 1,
        \"size\": 10,
        \"sortField\": \"time\",
        \"sortOrder\": \"desc\"
    }")

check_response "$ADVANCED_SEARCH_RESPONSE" "高级搜索"

# 13. 测试搜索建议
print_info "13. 测试搜索建议..."
SEARCH_SUGGEST_RESPONSE=$(curl -s -X GET "$BASE_URL/search/suggest?keyword=Sp&limit=5" \
    -H "Content-Type: application/json")

check_response "$SEARCH_SUGGEST_RESPONSE" "搜索建议"

# 14. 测试空关键词搜索
print_info "14. 测试空关键词搜索..."
EMPTY_SEARCH_RESPONSE=$(curl -s -X GET "$BASE_URL/search?keyword=" \
    -H "Content-Type: application/json")

if echo "$EMPTY_SEARCH_RESPONSE" | grep -q '"code":40000'; then
    print_success "空关键词搜索 - 正确拒绝"
else
    print_error "空关键词搜索 - 应该被拒绝"
    echo "响应内容: $EMPTY_SEARCH_RESPONSE"
fi

# 15. 测试未登录文件上传
print_info "15. 测试未登录文件上传..."
NO_LOGIN_UPLOAD_RESPONSE=$(curl -s -X POST "$BASE_URL/upload/image" \
    -H "Content-Type: multipart/form-data" \
    -F "file=@test_image.png" \
    -F "type=avatar")

if echo "$NO_LOGIN_UPLOAD_RESPONSE" | grep -q '"code":40100'; then
    print_success "未登录文件上传 - 正确拒绝"
else
    print_error "未登录文件上传 - 应该被拒绝"
    echo "响应内容: $NO_LOGIN_UPLOAD_RESPONSE"
fi

# 清理临时文件
rm -f cookies.txt test_image.png test_document.txt

print_info "文件上传和搜索API测试完成！"

# 显示详细的搜索结果数据
print_info "搜索结果数据详情："
echo "$SEARCH_ALL_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$SEARCH_ALL_RESPONSE"
