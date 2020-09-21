package com.lxs.gateway.shutdown;

public class TestMain {
    
    private ShutdownHook shutdownHook;
    
    public static void main(String[] args) {
        TestMain app = new TestMain();
        System.out.println("Hello World!");
        app.execute();
        System.out.println("End of main()");
        
    }
    
    public TestMain() {
        this.shutdownHook = new ShutdownHook(Thread.currentThread());
    }
    
    public void execute() {
        int i = 0;
        while (!shutdownHook.shouldShutDown()) {
            System.out.println("I am sleep");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("execute() interrupted");
            }
            System.out.println("I am not sleep");
            i++;
            if (i == 20) {
                System.exit(-1);
            }
        }
        System.out.println("end of execute()");
    }
    
}
