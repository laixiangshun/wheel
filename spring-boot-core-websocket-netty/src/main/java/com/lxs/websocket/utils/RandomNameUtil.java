package com.lxs.websocket.utils;

import java.util.Random;

/**
 * 获取随机用么名称
 *
 * @author lxs
 */
public class RandomNameUtil {
    
    private static Random random = new Random();
    
    private final static int delta = 0x9fa5 - 0x4e00 + 1;
    
    public static char getName() {
        return (char) (0x4e00 + random.nextInt(delta));
    }
}
