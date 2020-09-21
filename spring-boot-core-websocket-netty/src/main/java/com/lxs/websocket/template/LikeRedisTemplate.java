package com.lxs.websocket.template;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存连接的用户连接信息
 *
 * @author lxs
 */
@Component
public class LikeRedisTemplate {
    
    private Map<String, Object> redisMap = new ConcurrentHashMap<>();
    
    public void save(String id, Object value) {
        redisMap.put(id, value);
    }
    
    public void delete(String id) {
        redisMap.remove(id);
    }
    
    public Object get(String id) {
        return redisMap.get(id);
    }
}
