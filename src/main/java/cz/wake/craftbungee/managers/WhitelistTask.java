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

    private boolean started = false;
    @Override
    public void run() {
        List<WhitelistedIP> ips = Main.getInstance().getSQLManager().getWhitelistedIPs();

        // Smazani pred updatem
        VPNListener.getAllowedIps().clear();

        VPNListener.setAllowedIps(ips);
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Byly pridany IP adresy na whitelist. (" + ips.size() + ")");
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Update IP Whitelistu dokoncen.");
    }
}
