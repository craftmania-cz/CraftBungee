package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
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
        Main.getInstance().allowedIps.clear();

        //Build patterns
        list.stream().map(Pattern::compile).forEach(p -> {
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.WHITE + p.pattern() + " pridano na whitelist.");
            Main.getInstance().allowedIps.add(p);
        });
    }
}
