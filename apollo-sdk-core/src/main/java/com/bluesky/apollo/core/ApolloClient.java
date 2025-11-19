package com.bluesky.apollo.core;

import com.bluesky.apollo.exception.ApolloHttpException;
import lombok.Data;
import okhttp3.*;

import java.io.IOException;

/**
 * Apollo 客户端，封装 Apollo Portal OpenAPI 的基础调用
 *
 * <p>该客户端提供了对 Apollo Portal OpenAPI 的基础 HTTP 操作封装，
 * 支持 GET、POST、PUT、DELETE 等常用 HTTP 方法。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>自动处理 Authorization 头部认证</li>
 *   <li>统一的请求/响应处理</li>
 *   <li>异常处理和错误码封装</li>
 *   <li>JSON 格式的请求体和响应体处理</li>
 * </ul>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * ApolloClient client = new ApolloClient("http://apollo-portal.example.com", "your-token");
 * String response = client.get("/openapi/v1/apps");
 * }</pre>
 *
 * @author lantian
 * @date 2025/11/17
 * @version 1.0
 */
@Data
public class ApolloClient {

    /**
     * OkHttp 客户端实例，用于执行 HTTP 请求
     */
    private final OkHttpClient client;

    /**
     * Apollo Portal 的基础 URL
     * 例如：http://apollo-portal.example.com
     */
    private final String portalUrl;

    /**
     * API 访问令牌，用于身份认证
     * 可以包含 "Bearer " 前缀，也可以不包含（会自动添加）
     */
    private final String token;

    /**
     * 构造函数，创建 Apollo 客户端实例
     *
     * @param portalUrl Apollo Portal 的基础 URL，不能为空
     * @param token API 访问令牌，可以为空（但会影响需要认证的 API 调用）
     */
    public ApolloClient(String portalUrl, String token) {
        this.client = new OkHttpClient();
        this.portalUrl = portalUrl;
        this.token = token;
    }

    /**
     * 创建基础请求构建器，包含通用的请求头设置
     *
     * <p>该方法会自动处理以下内容：</p>
     * <ul>
     *   <li>URL 拼接（处理路径分隔符）</li>
     *   <li>Authorization 头部设置</li>
     *   <li>Content-Type 和 Accept 头部设置</li>
     * </ul>
     *
     * @param path API 路径，例如 "/openapi/v1/apps"
     * @return 配置好基础信息的请求构建器
     */
    private Request.Builder baseBuilder(String path) {
        // 处理 URL 拼接，避免双斜杠问题
        String url = portalUrl.endsWith("/") && path.startsWith("/")
            ? portalUrl + path.substring(1)
            : portalUrl + path;

        Request.Builder builder = new Request.Builder().url(url);

        // 设置认证头部
        if (token != null && !token.isBlank()) {
            if (token.toLowerCase().startsWith("bearer ")) {
                builder.header("Authorization", token);
            } else {
                builder.header("Authorization", "Bearer " + token);
            }
        }

        // 设置内容类型和接受类型
        builder.header("Content-Type", "application/json");
        builder.header("Accept", "application/json");

        return builder;
    }

    /**
     * 执行 POST 请求
     *
     * @param path API 路径
     * @param jsonBody JSON 格式的请求体，可以为 null
     * @return 响应体内容
     * @throws IOException 网络请求异常
     * @throws ApolloHttpException HTTP 状态码异常（非 2xx）
     */
    public String post(String path, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(
            jsonBody == null ? "" : jsonBody,
            MediaType.get("application/json; charset=utf-8")
        );
        Request request = baseBuilder(path).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            return unwrapResponse(response);
        }
    }

    /**
     * 执行 GET 请求
     *
     * @param path API 路径
     * @return 响应体内容
     * @throws IOException 网络请求异常
     * @throws ApolloHttpException HTTP 状态码异常（非 2xx）
     */
    public String get(String path) throws IOException {
        Request request = baseBuilder(path).get().build();

        try (Response response = client.newCall(request).execute()) {
            return unwrapResponse(response);
        }
    }

    /**
     * 执行 PUT 请求
     *
     * @param path API 路径
     * @param jsonBody JSON 格式的请求体，可以为 null
     * @return 响应体内容
     * @throws IOException 网络请求异常
     * @throws ApolloHttpException HTTP 状态码异常（非 2xx）
     */
    public String put(String path, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(
            jsonBody == null ? "" : jsonBody,
            MediaType.get("application/json; charset=utf-8")
        );
        Request request = baseBuilder(path).put(body).build();

        try (Response response = client.newCall(request).execute()) {
            return unwrapResponse(response);
        }
    }

    /**
     * 执行 DELETE 请求
     *
     * @param path API 路径
     * @return 响应体内容
     * @throws IOException 网络请求异常
     * @throws ApolloHttpException HTTP 状态码异常（非 2xx）
     */
    public String delete(String path) throws IOException {
        Request request = baseBuilder(path).delete().build();

        try (Response response = client.newCall(request).execute()) {
            return unwrapResponse(response);
        }
    }

    /**
     * 解析 HTTP 响应，处理状态码和响应体
     *
     * <p>该方法会检查 HTTP 状态码：</p>
     * <ul>
     *   <li>2xx：返回响应体内容</li>
     *   <li>其他：抛出 ApolloHttpException 异常</li>
     * </ul>
     *
     * @param response HTTP 响应对象
     * @return 响应体内容字符串
     * @throws IOException 读取响应体异常
     * @throws ApolloHttpException HTTP 状态码异常（非 2xx）
     */
    private String unwrapResponse(Response response) throws IOException {
        if (response == null) {
            throw new ApolloHttpException(-1, "No response from server");
        }

        int code = response.code();
        String body = response.body() != null ? response.body().string() : "";

        if (code >= 200 && code < 300) {
            return body;
        } else {
            throw new ApolloHttpException(code, body);
        }
    }
}