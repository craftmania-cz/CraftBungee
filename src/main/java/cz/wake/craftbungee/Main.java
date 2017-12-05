package cz.wake.craftbungee;

import cz.wake.craftbungee.listeners.PlayerListener;
import cz.wake.craftbungee.managers.PlayerUpdateTask;
import cz.wake.craftbungee.managers.SQLChecker;
import cz.wake.craftbungee.sql.SQLManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    private static Main instance;
    private static Configuration config;
    private SQLManager sql;
    public static HashSet<ProxiedPlayer> online_players = new HashSet<>();

    @Override
    public void onEnable(){

        instance = this;
        initDatabase();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));

        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new SQLChecker(), 1L, 1L, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new PlayerUpdateTask(), 1L, 1L,  TimeUnit.MINUTES);
    }

    @Override
    public void onDisable(){

        sql.onDisable();
        instance = null;
    }

    public static Main getInstance(){
        return instance;
    }

    public Configuration getConfig(){
        return config;
    }

    public void initDatabase() {
        sql = new SQLManager(this);
    }

    public SQLManager getSQLManager() {
        return sql;
    }

    public HashSet<ProxiedPlayer> getOnlinePlayers() {
        return online_players;
    }
}
