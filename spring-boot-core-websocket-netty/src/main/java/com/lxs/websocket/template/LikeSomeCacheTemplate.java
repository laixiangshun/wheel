package com.lxs.websocket.template;

import com.lxs.websocket.model.UserMsg;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 通过缓存保存用户发送的消息
 *
 * @author lxs
 */
@Component
public class LikeSomeCacheTemplate {
    
    private Set<UserMsg> someCahe = new LinkedHashSet<>();
    
    public void save(String userId, String userName, String msg) {
        UserMsg userMsg = new UserMsg();
        userMsg.setId(userId);
        userMsg.setCreateTime(new Date());
        userMsg.setMsg(msg);
        userMsg.setName(userName);
        someCahe.add(userMsg);
    }
    
    public Set<UserMsg> cloneCacheMap() {
        return someCahe;
    }
    
    public void clearCacheMap() {
        someCahe.clear();
    }
}
