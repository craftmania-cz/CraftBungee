package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener{

    private Main plugin;

    public PlayerListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final PostLoginEvent e){
        ProxiedPlayer p = e.getPlayer();

        Main.getInstance().getOnlinePlayers().add(p);
        Main.getInstance().getSQLManager().updateStats(p, true);
        
    }

    @EventHandler
    public void onDisconect(final PlayerDisconnectEvent e){
        ProxiedPlayer p = e.getPlayer();

        Main.getInstance().getSQLManager().updateStats(p, false);
        Main.getInstance().getOnlinePlayers().remove(p);
    }
}
