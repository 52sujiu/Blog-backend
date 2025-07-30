# Knife4j 升级到 OpenAPI 3.0 指南

## 问题描述

你的项目原本使用的是 `knife4j-openapi2-spring-boot-starter`，这个依赖支持的是 OpenAPI 2.0 (Swagger 2)，但你希望升级到 OpenAPI 3.0 (Swagger 3)。

## 解决方案

### 1. 更新 Maven 依赖

**修改前 (OpenAPI 2.0):**
```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

**修改后 (OpenAPI 3.0):**
```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

### 2. 更新配置文件

**application.yml 配置:**
```yaml
# 接口文档配置
knife4j:
  enable: true
  setting:
    language: zh_cn
```

### 3. 创建 OpenAPI 3.0 配置类

创建 `src/main/java/com/sujiu/blog/config/Knife4jConfig.java`:

```java
package com.sujiu.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("博客系统接口文档")
                        .version("1.0.0")
                        .description("基于Spring Boot的个人博客系统API文档")
                        .contact(new Contact()
                                .name("sujiu")
                                .email("sujiu@example.com")
                                .url("https://github.com/sujiu"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
```

### 4. 更新控制器注解

**使用 OpenAPI 3.0 注解:**
```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 实现代码
    }

    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 实现代码
    }
}
```

## 验证升级结果

### 1. 启动项目
```bash
mvn spring-boot:run
```

### 2. 访问文档页面
- **Knife4j UI**: http://localhost:8101/api/doc.html
- **OpenAPI JSON**: http://localhost:8101/api/v3/api-docs

### 3. 验证 OpenAPI 版本
访问 `/v3/api-docs` 端点，确认返回的 JSON 中包含：
```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "博客系统接口文档",
    "description": "基于Spring Boot的个人博客系统API文档",
    "version": "1.0.0"
  }
}
```

## 主要变化对比

| 特性 | OpenAPI 2.0 | OpenAPI 3.0 |
|------|-------------|-------------|
| 版本标识 | `"swagger": "2.0"` | `"openapi": "3.0.1"` |
| 依赖包 | `knife4j-openapi2-spring-boot-starter` | `knife4j-openapi3-spring-boot-starter` |
| 注解包 | `io.swagger.annotations.*` | `io.swagger.v3.oas.annotations.*` |
| 类注解 | `@Api` | `@Tag` |
| 方法注解 | `@ApiOperation` | `@Operation` |
| 参数注解 | `@ApiParam` | `@Parameter` |

## 优势

1. **更现代的规范**：OpenAPI 3.0 是更新的标准，支持更多特性
2. **更好的类型支持**：改进的数据类型定义和验证
3. **更灵活的服务器配置**：支持多个服务器环境
4. **更好的安全性定义**：改进的安全方案配置
5. **组件复用**：更好的组件定义和复用机制

## 注意事项

1. **Spring Boot 版本兼容性**：确保你的 Spring Boot 版本与 Knife4j 4.4.0 兼容
2. **注解迁移**：需要将所有控制器中的 Swagger 2 注解替换为 OpenAPI 3 注解
3. **配置格式变化**：配置文件格式有所变化，需要相应调整
4. **测试验证**：升级后需要全面测试所有接口文档的生成和显示

## 完成状态

✅ **升级成功！** 你的项目现在已经成功升级到 OpenAPI 3.0，可以享受更现代的 API 文档体验。
