package com.lxs.bigdata.eureka;

public class Test {
    
    private static Test test = new Test();
    
    private static int a = 50;
    
    private static int b;
    
    public Test() {
        a -= 50;
        b += 50;
    }
    
    public static void main(String[] args) {
//        Test test = new Test();
        System.out.println(a);
        System.out.println(b);
    }
}
