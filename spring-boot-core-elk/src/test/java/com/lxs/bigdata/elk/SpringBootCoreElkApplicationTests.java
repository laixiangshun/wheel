package com.lxs.bigdata.elk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootCoreElkApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Value("${logstash_host}")
    private String ip;

    private final static Logger log = LoggerFactory.getLogger(SpringBootCoreElkApplicationTests.class);

    @Test
    public void test() {
        log.info("ip:{}的filebeat  测试 info 成功了！！！", ip);
        log.warn("ip:{}的filebeat   测试 warn 成功了！！！", ip);
        log.error("ip:{}的filebeat   测试 error 成功了！！", ip);
        log.error("{\"msg\":\"出现一个异常错误：请求连接失败\",\"level\":\"ERROR\",\"createTime\":\"2018-5-21 20:22:22\",\"provider\":\"xishuai\",\"ip\":\"192.168.1.11\",\"stackTrace\":\"java.lang.Exception\\n\\tat com.example.log_demo.LogDemoTests.logCustomField(LogDemoTests.java:33)\\n\\tat org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:191)\\n\\tat org.junit.runner.JUnitCore.run(JUnitCore.java:137)\\n\\tat com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)\\n\\tat com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)\\n\\tat com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)\\n\\tat com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)\\n\",\"tag\":\"\",\"url\":\"\"}");
//        throw new RuntimeException("抛出异常");
    }
}
