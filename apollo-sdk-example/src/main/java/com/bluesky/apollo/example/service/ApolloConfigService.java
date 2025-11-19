package com.bluesky.apollo.example.service;

import com.bluesky.apollo.core.ApolloConfigServiceCore;
import com.bluesky.apollo.springboot.ApolloSdkProperties;
import com.bluesky.apollo.model.ItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Apollo 配置服务
 * 
 * <p>该服务封装了 Apollo SDK 的操作，提供了更加便捷的配置管理接口。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>一键发布配置项</li>
 *   <li>获取配置项值</li>
 *   <li>删除配置项</li>
 *   <li>获取所有配置项</li>
 *   <li>发布命名空间</li>
 * </ul>
 * 
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApolloConfigService {

    private final ApolloConfigServiceCore apolloConfigServiceCore;
    private final ApolloSdkProperties apolloSdkProperties;

    /**
     * 一键发布配置项
     * 
     * @param key 配置项键
     * @param value 配置项值
     * @param comment 配置项注释
     * @return 是否成功
     */
    public boolean publishSingle(String key, String value, String comment) {
        try {
            log.info("开始发布配置项: key={}, value={}, comment={}", key, value, comment);
            
            apolloConfigServiceCore.publishSingle(
                apolloSdkProperties.getAppId(),
                apolloSdkProperties.getEnv(),
                apolloSdkProperties.getCluster(),
                apolloSdkProperties.getNamespace(),
                key,
                value,
                comment,
                apolloSdkProperties.getOperator()
            );
            
            log.info("配置项发布成功: key={}", key);
            return true;
        } catch (Exception e) {
            log.error("配置项发布失败: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取配置项值
     * 
     * @param key 配置项键
     * @return 配置项值，如果不存在则返回 null
     */
    public String getItem(String key) {
        try {
            log.info("开始获取配置项: key={}", key);
            
            String value = apolloConfigServiceCore.getItem(
                apolloSdkProperties.getAppId(),
                apolloSdkProperties.getEnv(),
                apolloSdkProperties.getCluster(),
                apolloSdkProperties.getNamespace(),
                key
            );
            
            log.info("配置项获取成功: key={}, value={}", key, value);
            return value;
        } catch (Exception e) {
            log.error("配置项获取失败: key={}, error={}", key, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除配置项
     * 
     * @param key 配置项键
     * @return 是否成功
     */
    public boolean deleteItem(String key) {
        try {
            log.info("开始删除配置项: key={}", key);
            
            apolloConfigServiceCore.deleteItem(
                apolloSdkProperties.getAppId(),
                apolloSdkProperties.getEnv(),
                apolloSdkProperties.getCluster(),
                apolloSdkProperties.getNamespace(),
                key,
                apolloSdkProperties.getOperator()
            );
            
            log.info("配置项删除成功: key={}", key);
            return true;
        } catch (Exception e) {
            log.error("配置项删除失败: key={}, error={}", key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取所有配置项
     * 
     * @return 配置项列表
     */
    public List<ItemResponse> getAllItems() {
        try {
            log.info("开始获取所有配置项");
            
            List<ItemResponse> items = apolloConfigServiceCore.listNamespaceItems(
                apolloSdkProperties.getAppId(),
                apolloSdkProperties.getEnv(),
                apolloSdkProperties.getCluster(),
                apolloSdkProperties.getNamespace()
            );
            
            log.info("获取所有配置项成功，共 {} 个", items.size());
            return items;
        } catch (Exception e) {
            log.error("获取所有配置项失败: error={}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * 发布命名空间
     * 
     * @param releaseTitle 发布标题
     * @param releaseComment 发布注释
     * @return 是否成功
     */
    public boolean release(String releaseTitle, String releaseComment) {
        try {
            log.info("开始发布命名空间: title={}, comment={}", releaseTitle, releaseComment);
            
            apolloConfigServiceCore.publishNamespace(
                apolloSdkProperties.getAppId(),
                apolloSdkProperties.getEnv(),
                apolloSdkProperties.getCluster(),
                apolloSdkProperties.getNamespace(),
                releaseTitle,
                releaseComment,
                apolloSdkProperties.getOperator()
            );
            
            log.info("命名空间发布成功");
            return true;
        } catch (Exception e) {
            log.error("命名空间发布失败: error={}", e.getMessage(), e);
            return false;
        }
    }
}
