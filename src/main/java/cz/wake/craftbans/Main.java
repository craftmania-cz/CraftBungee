package cz.wake.craftbans;

import cz.wake.craftbans.sql.SQLManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin {

    private static Main instance;
    private static Configuration config;
    private SQLManager sql;

    @Override
    public void onEnable(){

        instance = this;
        initDatabase();
    }

    @Override
    public void onDisable(){

        instance = null;
        sql.onDisable();
    }

    public static Main getInstance(){
        return instance;
    }

    public Configuration getConfig(){
        return config;
    }

    private void initDatabase() {
        sql = new SQLManager(this);
    }

    public SQLManager getSQLManager() {
        return sql;
    }


}
