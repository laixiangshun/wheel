package com.lxs.bigdata.eureka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootEurekaApplicationTests {
    
    @Test
    public void contextLoads() {
    }
    
    public static void main(String[] args) {
        Persion persion = new Persion();
        System.out.println(ClassLayout.parseInstance(persion).toPrintable());
    }
}
