package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedUUID;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {

        // IP Whitelist
        List<WhitelistedIP> ips = Main.getInstance().getSQLManager().getWhitelistedIPs();
        VPNListener.getAllowedIps().clear();
        VPNListener.setAllowedIps(ips);
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Byly pridany IP adresy na whitelist. (" + ips.size() + ")");

        // UUID Whitelist
        List<WhitelistedUUID> uuids = Main.getInstance().getSQLManager().getWhitelistedUUIDs();
        VPNListener.getAllowedUUIDs().clear();
        VPNListener.setAllowedUUIDs(uuids);
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Byly pridany UUID na whitelist. (" + uuids.size() + ")");

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Update IP & UUID Whitelistu dokoncen.");
    }
}
