package com.bluesky.apollo.springboot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Apollo SDK 配置属性类
 *
 * <p>该类用于绑定 Spring Boot 配置文件中的 Apollo SDK 相关配置项，
 * 支持通过 {@code apollo.sdk.*} 前缀进行配置。</p>
 *
 * <p>配置示例：</p>
 * <pre>{@code
 * # Apollo Portal 相关配置
 * apollo.sdk.portal-url=http://apollo-portal.example.com
 * apollo.sdk.token=your-apollo-token
 *
 * # Apollo 应用相关配置
 * apollo.sdk.app-id=your-app-id
 * apollo.sdk.env=DEV
 * apollo.sdk.cluster=default
 * apollo.sdk.namespace=application
 * apollo.sdk.operator=admin
 * }</pre>
 *
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
@ConfigurationProperties(prefix = "apollo.sdk")
@Data
public class ApolloSdkProperties {

    /**
     * Apollo Portal 的基础 URL
     * 例如：http://apollo-portal.example.com
     */
    private String portalUrl;

    /**
     * Apollo Portal API 访问令牌
     */
    private String token;

    /**
     * Apollo 应用 ID
     */
    private String appId;

    /**
     * Apollo 环境名称
     * 例如：DEV、TEST、PROD
     */
    private String env;

    /**
     * Apollo 集群名称
     * 默认值：default
     */
    private String cluster = "default";

    /**
     * Apollo 命名空间名称
     * 默认值：application
     */
    private String namespace = "application";

    /**
     * 操作人员标识
     * 默认值：apollo
     */
    private String operator = "apollo";
}
