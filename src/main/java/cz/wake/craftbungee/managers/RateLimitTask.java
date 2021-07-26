package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.listeners.RateLimitJoinListener;

public class RateLimitTask implements Runnable {

    @Override
    public void run() {
        System.out.println("Aktuální stav připojení: " + RateLimitJoinListener.actualLimit);
        RateLimitJoinListener.actualLimit = RateLimitJoinListener.actualLimit - 2;
        if (RateLimitJoinListener.actualLimit < 0) {
            RateLimitJoinListener.actualLimit = 0;
        }
    }
}
