package com.bluesky.apollo.example.model;

import lombok.Data;

/**
 * 发布配置请求模型
 * 
 * <p>用于接收前端发送的配置发布请求参数。</p>
 * 
 * @author lantian
 * @date 2025/11/19
 * @version 1.0
 */
@Data
public class PublishRequest {
    
    /**
     * 配置项的键
     */
    private String key;
    
    /**
     * 配置项的值
     */
    private String value;
    
    /**
     * 配置项的注释说明
     */
    private String comment;
}
