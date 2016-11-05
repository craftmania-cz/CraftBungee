package cz.wake.craftbans.listeners;

import cz.wake.craftbans.Main;
import cz.wake.craftbans.sql.SQLManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener{

    private Main plugin;
    private SQLManager sqlManager = new SQLManager(plugin);

    public PlayerListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final PostLoginEvent e){
        ProxiedPlayer p = e.getPlayer();
        if(!checkPlayerRecord(p)){
            sqlManager.createRecord(p);
        } else {
            //TODO: Update loginu + zkontrolovani banu
        }
        
    }

    private boolean checkPlayerRecord(final ProxiedPlayer p){
        return sqlManager.hasData(p);
    }
}
