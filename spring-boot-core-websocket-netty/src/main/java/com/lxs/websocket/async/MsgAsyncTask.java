package com.lxs.websocket.async;

import com.lxs.websocket.model.UserMsg;
import com.lxs.websocket.template.LikeSomeCacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * 消息处理异步任务
 *
 * @author lxs
 */
@Component
public class MsgAsyncTask {
    
    @Autowired
    private LikeSomeCacheTemplate likeSomeCacheTemplate;
    
    @Async(value = "taskExecutor")
    public Future<Boolean> saveChatMsgTask() {
        Set<UserMsg> userMsgs = likeSomeCacheTemplate.cloneCacheMap();
        for (UserMsg userMsg : userMsgs) {
            //todo 保存到数据库
        }
        likeSomeCacheTemplate.clearCacheMap();
        return new AsyncResult<>(true);
    }
}
