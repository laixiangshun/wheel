package com.lxs.bigdata.redission.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 利用分布式锁更改用户积分
     */
    public void updateUserScore() {
        RLock rLock = redissonClient.getLock("REDLOCK_KEY");
        boolean lock;
        try {
            // 500ms拿不到锁, 就认为获取锁失败。10000ms即10s是锁失效时间。
            lock = rLock.tryLock(500, 10000, TimeUnit.MILLISECONDS);
            if (lock) {
                //do something
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            rLock.unlock();
        }
    }
}
