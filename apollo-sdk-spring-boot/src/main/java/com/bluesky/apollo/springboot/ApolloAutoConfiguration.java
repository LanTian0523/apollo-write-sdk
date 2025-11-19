package com.bluesky.apollo.springboot;

import com.bluesky.apollo.core.ApolloClient;
import com.bluesky.apollo.core.ApolloConfigServiceCore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Apollo SDK Spring Boot 自动配置类
 *
 * <p>该配置类提供了 Apollo SDK 在 Spring Boot 环境下的自动装配功能，
 * 会根据配置属性自动创建和注册相关的 Bean。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>自动创建 {@link ApolloClient} Bean</li>
 *   <li>自动创建 {@link ApolloConfigServiceCore} Bean</li>
 *   <li>支持通过 {@code apollo.sdk.*} 配置属性进行定制</li>
 * </ul>
 *
 * <p>配置示例：</p>
 * <pre>{@code
 * apollo.sdk.portal-url=http://apollo-portal.example.com
 * apollo.sdk.token=your-apollo-token
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
@Configuration
@EnableConfigurationProperties(ApolloSdkProperties.class)
public class ApolloAutoConfiguration {

    /**
     * 创建 Apollo HTTP 客户端 Bean
     *
     * <p>该 Bean 负责与 Apollo Portal API 进行 HTTP 通信。</p>
     *
     * @param properties Apollo SDK 配置属性
     * @return Apollo HTTP 客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ApolloClient apolloClient(ApolloSdkProperties properties) {
        return new ApolloClient(properties.getPortalUrl(), properties.getToken());
    }

    /**
     * 创建 Apollo 配置服务核心 Bean
     *
     * <p>该 Bean 提供了对 Apollo 配置的高级操作接口，
     * 包括配置项的创建、更新、删除、查询和发布等功能。</p>
     *
     * @param client Apollo HTTP 客户端
     * @return Apollo 配置服务核心实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ApolloConfigServiceCore apolloConfigServiceCore(ApolloClient client) {
        return new ApolloConfigServiceCore(client);
    }
}
