package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUpdateTask implements Runnable {

    @Override
    public void run() {
        for(ProxiedPlayer p : Main.getInstance().getOnlinePlayers()){
            if(p.isConnected()){
                Main.getInstance().getSQLManager().updateTime(p);
            }
        }
    }
}
