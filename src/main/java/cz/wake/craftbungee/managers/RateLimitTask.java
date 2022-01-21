package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.listeners.RateLimitJoinListener;

public class RateLimitTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Aktuální stav připojení: " + RateLimitJoinListener.actualLimit);
        RateLimitJoinListener.actualLimit = RateLimitJoinListener.actualLimit - getDynamicCount(RateLimitJoinListener.actualLimit);
        if (RateLimitJoinListener.actualLimit < 0) {
            RateLimitJoinListener.actualLimit = 0;
        }
    }

    private int getDynamicCount(int limit) {
        if (limit >= 1000) {
            return 100;
        } else if (limit >= 200) {
            return 15;
        } else if (limit >= 100) {
            return 8;
        } else {
            return 5;
        }
    }
}
