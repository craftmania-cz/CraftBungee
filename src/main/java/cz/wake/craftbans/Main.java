package cz.wake.craftbans;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public class Main extends Plugin {

    private static Main instance;
    private static Configuration config;

    @Override
    public void onEnable(){
        instance = this;
    }

    @Override
    public void onDisable(){
        instance = null;
    }

    public static Main getInstance(){
        return instance;
    }

    public static Configuration getConfig(){
        return config;
    }


}
