package com.bluesky.apollo.core;

import com.bluesky.apollo.exception.ApolloException;
import com.bluesky.apollo.model.ItemResponse;
import com.bluesky.apollo.model.PulishItemRequest;
import com.bluesky.apollo.model.ReleaseRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Apollo 配置服务核心类，提供对外的易用 API
 *
 * <p>该类封装了 Apollo Portal OpenAPI 的常用操作，提供了更加友好的接口：</p>
 * <ul>
 *   <li>配置项的创建、更新、删除和查询</li>
 *   <li>命名空间的发布操作</li>
 *   <li>一键发布单个配置项（类似 Diamond 的 publishSingle）</li>
 *   <li>批量获取命名空间下的所有配置项</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * ApolloClient client = new ApolloClient("http://apollo-portal.example.com", "your-token");
 * ApolloConfigServiceCore service = new ApolloConfigServiceCore(client);
 *
 * // 一键发布配置
 * service.publishSingle("myApp", "DEV", "default", "application",
 *                      "timeout", "5000", "设置超时时间", "admin");
 *
 * // 获取配置值
 * String value = service.getItem("myApp", "DEV", "default", "application", "timeout");
 * }</pre>
 *
 * @author lantian
 * @date 2025/11/17
 * @version 1.0
 */
public class ApolloConfigServiceCore {

    /**
     * Apollo HTTP 客户端，用于与 Apollo Portal API 通信
     */
    private final ApolloClient client;

    /**
     * JSON 序列化/反序列化工具
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 构造函数，创建 Apollo 配置服务实例
     *
     * @param client Apollo HTTP 客户端实例，不能为 null
     */
    public ApolloConfigServiceCore(ApolloClient client) {
        this.client = client;
    }

    /**
     * 创建或更新单个配置项
     *
     * <p>该方法会调用 Apollo Portal 的 item 接口，该接口本身会处理创建/更新逻辑：</p>
     * <ul>
     *   <li>如果配置项不存在，则创建新的配置项</li>
     *   <li>如果配置项已存在，则更新现有配置项</li>
     * </ul>
     *
     * <p><strong>注意：</strong>此操作只是修改配置项，不会自动发布。
     * 需要调用 {@link #publishNamespace} 或 {@link #publishSingle} 来发布配置。</p>
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @param key 配置项的键
     * @param value 配置项的值
     * @param comment 配置项的注释说明
     * @param operator 操作人员标识
     * @throws ApolloException 当 API 调用失败时抛出
     */
    public void createOrUpdateItem(String appId, String env, String cluster, String namespace,
                                      String key, String value, String comment, String operator) {
        try {
            String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/items/",
                    urlEncode(appId), urlEncode(env), urlEncode(cluster), urlEncode(namespace));
            PulishItemRequest request = new PulishItemRequest(key, value, comment, operator);
            String jsonBody = mapper.writeValueAsString(request);
            client.post(path, jsonBody);
        } catch (Exception e) {
            throw new ApolloException("Failed to create or update item: " + key, e);
        }
    }

    /**
     * 发布命名空间（创建 release）
     *
     * <p>发布操作会将命名空间中的所有配置项变更生效，使客户端能够获取到最新的配置。</p>
     *
     * <p><strong>重要：</strong>只有发布后，配置的变更才会对客户端生效。</p>
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @param releaseTitle 发布标题，用于标识本次发布
     * @param releaseComment 发布说明，描述本次发布的内容
     * @param releasedBy 发布人员标识
     * @throws ApolloException 当 API 调用失败时抛出
     */
    public void publishNamespace(String appId, String env, String cluster, String namespace,
                                    String releaseTitle, String releaseComment, String releasedBy) {
        try {
            String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/releases",
                    urlEncode(appId), urlEncode(env), urlEncode(cluster), urlEncode(namespace));
            ReleaseRequest request = new ReleaseRequest(releaseTitle, releasedBy, releaseComment);
            String jsonBody = mapper.writeValueAsString(request);
            client.post(path, jsonBody);
        } catch (Exception e) {
            throw new ApolloException("Failed to publish namespace: " + namespace, e);
        }
    }

    /**
     * 一键创建/更新并发布配置项
     *
     * <p>这是一个便捷方法，相当于依次调用：</p>
     * <ol>
     *   <li>{@link #createOrUpdateItem} - 创建或更新配置项</li>
     *   <li>{@link #publishNamespace} - 发布命名空间</li>
     * </ol>
     *
     * <p>类似于 Diamond 的 publishSingle 方法，适用于需要立即生效的单个配置项修改。</p>
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @param key 配置项的键
     * @param value 配置项的值
     * @param comment 配置项的注释说明
     * @param operator 操作人员标识
     * @throws ApolloException 当任一步骤失败时抛出
     */
    public void publishSingle(String appId, String env, String cluster, String namespace,
                                  String key, String value, String comment, String operator) {
        // 先创建或更新配置项
        createOrUpdateItem(appId, env, cluster, namespace, key, value, comment, operator);

        // 然后发布命名空间，使配置生效
        publishNamespace(appId, env, cluster, namespace,
                        "Auto release - " + key, comment, operator);
    }

    /**
     * 获取单个配置项的值
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @param key 配置项的键
     * @return 配置项的值，如果不存在则抛出异常
     * @throws ApolloException 当配置项不存在或 API 调用失败时抛出
     */
    public String getItem(String appId, String env, String cluster, String namespace, String key) {
        try {
            String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/items/%s",
                    urlEncode(appId), urlEncode(env), urlEncode(cluster), urlEncode(namespace), urlEncode(key));
            String responseJson = client.get(path);
            ItemResponse item = mapper.readValue(responseJson, ItemResponse.class);
            return item.getValue();
        } catch (Exception e) {
            throw new ApolloException("Failed to get item: " + key, e);
        }
    }

    /**
     * 删除单个配置项
     *
     * <p><strong>注意：</strong>删除操作只是标记配置项为删除状态，
     * 需要调用 {@link #publishNamespace} 来发布变更，删除才会生效。</p>
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @param key 要删除的配置项的键
     * @param operator 操作人员标识
     * @throws ApolloException 当 API 调用失败时抛出
     */
    public void deleteItem(String appId, String env, String cluster, String namespace, String key, String operator) {
        try {
            String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/items/%s",
                    urlEncode(appId), urlEncode(env), urlEncode(cluster), urlEncode(namespace), urlEncode(key));

            // 通过查询参数传递操作人员信息
            String deleteUrl = path + "?operator=" + urlEncode(operator);
            client.delete(deleteUrl);
        } catch (Exception e) {
            throw new ApolloException("Failed to delete item: " + key, e);
        }
    }

    /**
     * 获取命名空间下的所有配置项
     *
     * @param appId 应用 ID
     * @param env 环境名称（如：DEV、TEST、PROD）
     * @param cluster 集群名称（通常为 default）
     * @param namespace 命名空间名称（如：application）
     * @return 配置项列表，包含键值对和元数据信息
     * @throws ApolloException 当 API 调用失败时抛出
     */
    public List<ItemResponse> listNamespaceItems(String appId, String env, String cluster, String namespace) {
        try {
            String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/items",
                    urlEncode(appId), urlEncode(env), urlEncode(cluster), urlEncode(namespace));
            String responseJson = client.get(path);
            return mapper.readValue(responseJson, new TypeReference<List<ItemResponse>>() {});
        } catch (Exception e) {
            throw new ApolloException("Failed to list namespace items for: " + namespace, e);
        }
    }

    /**
     * URL 编码工具方法
     *
     * <p>对字符串进行 UTF-8 编码，确保 URL 参数的安全性。</p>
     *
     * @param input 需要编码的字符串
     * @return 编码后的字符串，如果编码失败则返回原字符串
     */
    private String urlEncode(String input) {
        try {
            return java.net.URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            // 编码失败时返回原字符串，避免程序中断
            return input;
        }
    }
}