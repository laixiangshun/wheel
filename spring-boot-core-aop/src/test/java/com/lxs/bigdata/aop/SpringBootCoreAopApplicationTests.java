package com.lxs.bigdata.aop;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringBootCoreAopApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void run() {
        String url = "http://localhost:23412/api/user";
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            String userId = "userId" + i;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.set("token", "token");
            Map<String, Object> body = new HashMap<>();
            body.put("userId", userId);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            executorService.execute(() -> {
                try {
                    countDownLatch.await();
                    log.info("开始Thread:" + Thread.currentThread().getName() + ",Time:" + System.currentTimeMillis());
                    ResponseEntity<String> result = restTemplate.postForEntity(url, entity, String.class);
                    log.info("结束Thread:" + Thread.currentThread().getName() + ",Body:" + result.getBody());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            countDownLatch.countDown();
        }
    }

}
