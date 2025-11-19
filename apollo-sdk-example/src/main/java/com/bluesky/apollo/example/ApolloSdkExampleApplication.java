package com.bluesky.apollo.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Apollo SDK 示例应用启动类
 * 
 * <p>该应用演示了如何使用 Apollo SDK Spring Boot Starter 进行配置管理。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>自动配置 Apollo SDK 相关 Bean</li>
 *   <li>提供 REST API 演示 SDK 功能</li>
 *   <li>展示配置的增删改查和发布操作</li>
 * </ul>
 * 
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
@SpringBootApplication
public class ApolloSdkExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApolloSdkExampleApplication.class, args);
    }
}
