package cz.wake.craftbungee.managers;


import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BroadcastTask implements Runnable {

    @Override
    public void run() {
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            BungeeUtils.sendBroadcast(p, BungeeUtils.nextBroadcastFromConfig(), false);
        }
    }
}