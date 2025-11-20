# Apollo Write SDK

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-11+-green.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-green.svg)](https://spring.io/projects/spring-boot)

> 让 Apollo 拥有客户端写入配置的能力，像 Diamond 一样使用！

## 📖 项目简介

**Apollo Write SDK** 是一个基于 Apollo Portal REST API 封装的 Java 工具包，提供了完整的配置管理能力，让您可以在客户端直接向 Apollo 配置中心**写入、修改、删除、发布**配置项。

### 🎯 适用场景

- 🔄 运行时动态修改配置
- 📦 批量配置管理和迁移
- 🤖 自动化运维和配置同步
- 🛠️ 配置管理工具开发
- 💎 希望 Apollo 拥有类似 Diamond 的写入能力

### ✨ 核心特性

- **🚀 开箱即用** - Spring Boot Starter 自动配置
- **🔧 完整功能** - 支持配置的增删改查和发布
- **📝 详细文档** - 完整的 JavaDoc 和使用示例
- **🧪 单元测试** - 完善的测试覆盖
- **🎨 优雅设计** - 链式调用，异常处理完善

## 🏗️ 项目结构

```
apollo-write-sdk/
├── apollo-sdk-core/              # 核心 SDK 模块
│   ├── src/main/java/
│   │   ├── core/                 # 核心类
│   │   │   ├── ApolloClient.java           # HTTP 客户端
│   │   │   └── ApolloConfigServiceCore.java # 配置服务核心
│   │   ├── model/                # 数据模型
│   │   │   ├── ItemResponse.java          # 配置项响应
│   │   │   ├── PublishItemRequest.java    # 发布请求
│   │   │   └── ReleaseRequest.java        # 发布请求
│   │   └── exception/            # 异常类
│   │       ├── ApolloException.java       # 基础异常
│   │       └── ApolloHttpException.java   # HTTP 异常
│   └── src/test/java/            # 单元测试
├── apollo-sdk-spring-boot/       # Spring Boot Starter
│   ├── src/main/java/
│   │   └── springboot/
│   │       ├── ApolloAutoConfiguration.java    # 自动配置
│   │       └── ApolloSdkProperties.java        # 配置属性
│   └── src/main/resources/META-INF/
│       ├── spring.factories                    # 自动配置注册
│       └── spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
└── apollo-sdk-example/           # 示例应用
    ├── src/main/java/
    │   └── example/
    │       ├── ApolloSdkExampleApplication.java # 启动类
    │       ├── controller/
    │       │   └── ExampleController.java       # REST 控制器
    │       ├── service/
    │       │   └── ApolloConfigService.java     # 服务层
    │       └── model/
    │           └── PublishRequest.java          # 请求模型
    └── src/main/resources/
        └── application.yml                      # 配置文件
```

## 🚀 快速开始

### 1. 添加依赖

#### Maven
```xml
<dependency>
    <groupId>com.bluesky</groupId>
    <artifactId>apollo-sdk-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle
```gradle
implementation 'com.bluesky:apollo-sdk-spring-boot-starter:1.0.0'
```

### 2. 配置属性

在 `application.yml` 中添加配置：

```yaml
apollo:
  sdk:
    portal-url: http://your-apollo-portal.com  # Apollo Portal 地址
    token: your-apollo-token                   # Apollo Portal API Token
    app-id: your-app-id                       # 应用 ID
    env: DEV                                  # 环境（DEV/TEST/PROD）
    cluster: default                          # 集群名称
    namespace: application                    # 命名空间
    operator: apollo-sdk                      # 操作者标识
```

### 3. 使用 SDK

#### 方式一：注入服务使用（推荐）

```java
@Service
public class ConfigService {
    
    @Autowired
    private ApolloConfigServiceCore apolloConfigServiceCore;
    
    @Autowired
    private ApolloSdkProperties properties;
    
    // 一键发布配置
    public void publishConfig(String key, String value, String comment) {
        apolloConfigServiceCore.publishSingle(
            properties.getAppId(),
            properties.getEnv(),
            properties.getCluster(),
            properties.getNamespace(),
            key, value, comment,
            properties.getOperator()
        );
    }
    
    // 获取配置
    public String getConfig(String key) {
        return apolloConfigServiceCore.getItem(
            properties.getAppId(),
            properties.getEnv(),
            properties.getCluster(),
            properties.getNamespace(),
            key
        );
    }
}
```

#### 方式二：直接使用核心类

```java
// 创建客户端
ApolloClient client = new ApolloClient("http://apollo-portal.com", "your-token");
ApolloConfigServiceCore service = new ApolloConfigServiceCore(client);

// 一键发布配置
service.publishSingle("myApp", "DEV", "default", "application", 
                     "timeout", "5000", "设置超时时间", "admin");

// 获取配置
String value = service.getItem("myApp", "DEV", "default", "application", "timeout");

// 删除配置
service.deleteItem("myApp", "DEV", "default", "application", "timeout", "admin");

// 发布命名空间
service.release("myApp", "DEV", "default", "application", 
               "Release v1.0", "发布新版本", "admin");
```

## 📚 API 文档

### 核心接口

| 方法 | 描述 | 参数 |
|------|------|------|
| `publishSingle` | 一键发布配置项（创建/更新并发布） | appId, env, cluster, namespace, key, value, comment, operator |
| `getItem` | 获取指定配置项的值 | appId, env, cluster, namespace, key |
| `getItems` | 获取命名空间下所有配置项 | appId, env, cluster, namespace |
| `getItemsAsMap` | 获取配置项（Map格式） | appId, env, cluster, namespace |
| `deleteItem` | 删除配置项 | appId, env, cluster, namespace, key, operator |
| `release` | 发布命名空间 | appId, env, cluster, namespace, releaseTitle, releaseComment, operator |

### REST API（示例应用）

| 方法 | 路径 | 描述 |
|------|------|------|
| `POST` | `/api/config/publish` | 一键发布配置项 |
| `GET` | `/api/config/{key}` | 获取配置项值 |
| `DELETE` | `/api/config/{key}` | 删除配置项 |
| `GET` | `/api/config` | 获取所有配置项 |
| `GET` | `/api/config/map` | 获取配置项（Map格式） |
| `POST` | `/api/config/release` | 发布命名空间 |
| `GET` | `/api/config/health` | 健康检查 |

## 🧪 运行示例

### 1. 启动示例应用

```bash
cd apollo-sdk-example
mvn spring-boot:run
```

### 2. 测试 API

#### 发布配置
```bash
curl -X POST http://localhost:8080/api/config/publish \
  -H "Content-Type: application/json" \
  -d '{
    "key": "timeout",
    "value": "5000",
    "comment": "设置超时时间"
  }'
```

#### 获取配置
```bash
curl http://localhost:8080/api/config/timeout
```

#### 删除配置
```bash
curl -X DELETE http://localhost:8080/api/config/timeout
```

#### 发布命名空间
```bash
curl -X POST http://localhost:8080/api/config/release \
  -H "Content-Type: application/json" \
  -d '{
    "releaseTitle": "Release v1.0",
    "releaseComment": "发布新版本配置"
  }'
```

## 🔧 高级配置

### 自定义 HTTP 客户端

```java
@Configuration
public class ApolloConfig {
    
    @Bean
    @Primary
    public ApolloClient customApolloClient(ApolloSdkProperties properties) {
        // 自定义 HTTP 客户端配置
        return new ApolloClient(properties.getPortalUrl(), properties.getToken());
    }
}
```

### 异常处理

```java
try {
    service.publishSingle(appId, env, cluster, namespace, key, value, comment, operator);
} catch (ApolloHttpException e) {
    // HTTP 异常处理
    log.error("HTTP Error: {}, Response: {}", e.getStatusCode(), e.getBody());
} catch (ApolloException e) {
    // 通用异常处理
    log.error("Apollo Error: {}", e.getMessage());
}
```

## 🧪 测试

### 运行单元测试

```bash
mvn test
```

### 测试覆盖率

```bash
mvn jacoco:report
```

## 📋 版本规划

- ✅ **v1.0.0** - 基本的增删改查和发布能力
- 🔄 **v1.1.0** - 支持批量导入导出
- 📅 **v1.2.0** - 异步发布与任务队列
- 📅 **v2.0.0** - RESTful 管理服务，可远程调用

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 开源协议

本项目采用 [Apache License 2.0](LICENSE.md) 开源协议。

## 📞 联系方式

- **作者**: Sky Blue
- **邮箱**: skybluebluede@gmail.com
- **项目地址**: [GitHub](https://github.com/SkyArcX/apollo-write-sdk)

## ⭐ Star History

如果这个项目对您有帮助，请点个 ⭐ Star 支持一下！

---

**让配置管理变得更简单！** 🚀
