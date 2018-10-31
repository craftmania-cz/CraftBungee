package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import cz.wake.craftbungee.utils.WhitelistedIP;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {
        List<WhitelistedIP> ips = Main.getInstance().getSQLManager().getWhitelistedIPs();

        // Smazani pred updatem
        VPNListener.getAllowedIps().clear();

        //Build patterns
        for (WhitelistedIP ip : ips) {
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "IP " + ip.getAddress() + " byla pridana na ip whitelist. Duvod pridani: " + ip.getDescription());
        }

        VPNListener.setAllowedIps(ips);
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Update Whitelistu dokoncen.");

    }
}
