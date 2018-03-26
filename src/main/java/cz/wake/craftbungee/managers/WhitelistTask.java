package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {

        // Info
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Probehne update whitelistu IP:");

        // Ziskani z SQL
        List<String> list = Main.getInstance().getSQLManager().getWhitelistedIPs();

        // Smazani pred updatem
        VPNListener.getAllowedIps().clear();

        //Build patterns
        for(String text : list){
            Pattern p = Pattern.compile(text);
            VPNListener.getAllowedIps().add(p);
            Main.getInstance().getLogger().log(Level.INFO, text + " pridano na whitelist.");
        }

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Update Whitelistu dokoncen.");

    }
}
