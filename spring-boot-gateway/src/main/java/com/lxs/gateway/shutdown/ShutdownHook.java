package com.lxs.gateway.shutdown;

public class ShutdownHook extends Thread {
    
    private Thread mainThread;
    
    private boolean shutDownSignalReceived;
    
    @Override
    public void run() {
        System.out.println("Shut down signal received.");
        this.shutDownSignalReceived = true;
        mainThread.interrupt();
        try {
            mainThread.join(); //当收到停止信号时，等待mainThread的执行完成
        } catch (InterruptedException e) {
        }
        System.out.println("Shut down complete.");
    }
    
    public ShutdownHook(Thread mainThread) {
        super();
        this.mainThread = mainThread;
        this.shutDownSignalReceived = false;
        Runtime.getRuntime().addShutdownHook(this);
    }
    
    public boolean shouldShutDown() {
        return shutDownSignalReceived;
    }
    
}
