package com.bluesky.apollo.example.controller;

import com.bluesky.apollo.example.model.PublishRequest;
import com.bluesky.apollo.example.service.ApolloConfigService;
import com.bluesky.apollo.model.ItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Apollo SDK 示例控制器
 * 
 * <p>该控制器提供了 REST API 来演示 Apollo SDK 的各种功能。</p>
 * 
 * <p>主要接口：</p>
 * <ul>
 *   <li>POST /api/config/publish - 一键发布配置项</li>
 *   <li>GET /api/config/{key} - 获取配置项值</li>
 *   <li>DELETE /api/config/{key} - 删除配置项</li>
 *   <li>GET /api/config - 获取所有配置项</li>
 *   <li>GET /api/config/map - 获取所有配置项（Map格式）</li>
 *   <li>POST /api/config/release - 发布命名空间</li>
 * </ul>
 * 
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ExampleController {

    private final ApolloConfigService apolloConfigService;

    /**
     * 一键发布配置项
     * 
     * @param request 发布请求
     * @return 操作结果
     */
    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publishSingle(@RequestBody PublishRequest request) {
        log.info("接收到发布配置请求: {}", request);
        
        boolean success = apolloConfigService.publishSingle(
            request.getKey(), 
            request.getValue(), 
            request.getComment()
        );
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置项发布成功",
                "data", Map.of(
                    "key", request.getKey(),
                    "value", request.getValue(),
                    "comment", request.getComment()
                )
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "配置项发布失败"
            ));
        }
    }

    /**
     * 获取配置项值
     * 
     * @param key 配置项键
     * @return 配置项值
     */
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable String key) {
        log.info("接收到获取配置请求: key={}", key);
        
        String value = apolloConfigService.getItem(key);
        
        if (value != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置项获取成功",
                "data", Map.of(
                    "key", key,
                    "value", value
                )
            ));
        } else {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", "配置项不存在或获取失败"
            ));
        }
    }

    /**
     * 删除配置项
     * 
     * @param key 配置项键
     * @return 操作结果
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable String key) {
        log.info("接收到删除配置请求: key={}", key);
        
        boolean success = apolloConfigService.deleteItem(key);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "配置项删除成功",
                "data", Map.of("key", key)
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "配置项删除失败"
            ));
        }
    }

    /**
     * 获取所有配置项
     * 
     * @return 配置项列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllItems() {
        log.info("接收到获取所有配置请求");
        
        List<ItemResponse> items = apolloConfigService.getAllItems();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "获取所有配置项成功",
            "data", Map.of(
                "items", items,
                "count", items.size()
            )
        ));
    }

    /**
     * 发布命名空间
     * 
     * @param releaseRequest 发布请求
     * @return 操作结果
     */
    @PostMapping("/release")
    public ResponseEntity<Map<String, Object>> release(@RequestBody Map<String, String> releaseRequest) {
        String releaseTitle = releaseRequest.getOrDefault("releaseTitle", "Release by API");
        String releaseComment = releaseRequest.getOrDefault("releaseComment", "Released via Apollo SDK Example API");
        
        log.info("接收到发布命名空间请求: title={}, comment={}", releaseTitle, releaseComment);
        
        boolean success = apolloConfigService.release(releaseTitle, releaseComment);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "命名空间发布成功",
                "data", Map.of(
                    "releaseTitle", releaseTitle,
                    "releaseComment", releaseComment
                )
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "命名空间发布失败"
            ));
        }
    }

    /**
     * 健康检查接口
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Apollo SDK Example API is running",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
