package com.master.RetryPolicy.utils;

public class SetTimeout {
    public static void setTimeout(Runnable runnable, int delay){
        if (delay == -1) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
