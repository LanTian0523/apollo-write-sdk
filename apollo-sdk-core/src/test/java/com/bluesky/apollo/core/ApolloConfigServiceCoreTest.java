package com.bluesky.apollo.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * ApolloConfigServiceCore 单元测试类
 *
 * <p>该测试类使用 Mockito 框架对 ApolloConfigServiceCore 的核心功能进行单元测试，
 * 包括配置项的发布、获取等操作。</p>
 *
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
public class ApolloConfigServiceCoreTest {

    /**
     * 模拟的 Apollo 客户端
     */
    private ApolloClient mockClient;

    /**
     * 被测试的服务实例
     */
    private ApolloConfigServiceCore service;

    /**
     * 测试前的初始化设置
     *
     * <p>在每个测试方法执行前创建模拟对象和被测试实例</p>
     */
    @BeforeEach
    public void setUp() {
        mockClient = Mockito.mock(ApolloClient.class);
        service = new ApolloConfigServiceCore(mockClient);
    }

    /**
     * 测试一键发布单个配置项功能
     *
     * <p>验证 publishSingle 方法能够正确调用创建配置项和发布命名空间的操作</p>
     *
     * @throws Exception 测试异常
     */
    @Test
    public void testPublishSingle() throws Exception {
        // Given: 模拟 HTTP 客户端的响应
        when(mockClient.post(contains("/items"), anyString())).thenReturn("{}");
        when(mockClient.post(contains("/releases"), anyString())).thenReturn("{}");

        // When: 执行一键发布操作
        service.publishSingle("SampleApp", "DEV", "default", "application",
                "test.key", "test.value", "unit test", "tester");

        // Then: 验证调用次数和参数
        verify(mockClient, times(1)).post(contains("/items"), anyString());
        verify(mockClient, times(1)).post(contains("/releases"), anyString());
    }

    /**
     * 测试获取单个配置项功能
     *
     * <p>验证 getItem 方法能够正确解析 API 响应并返回配置项的值</p>
     *
     * @throws Exception 测试异常
     */
    @Test
    public void testGetItem() throws Exception {
        // Given: 准备模拟的 JSON 响应数据
        String mockResponseJson = "{\"key\": \"test.key\", \"value\": \"hello\", \"comment\": \"unit test\", \"dataChangeCreatedBy\": \"tester\"}";
        when(mockClient.get(contains("/items/test.key"))).thenReturn(mockResponseJson);

        // When: 执行获取配置项操作
        String actualValue = service.getItem("SampleApp", "DEV", "default", "application", "test.key");

        // Then: 验证返回值和调用次数
        assertEquals("hello", actualValue, "配置项的值应该与预期一致");
        verify(mockClient, times(1)).get(anyString());
    }
}