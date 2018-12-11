package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import cz.wake.craftbungee.utils.Logger;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedUUID;

import java.util.List;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {

        // IP Whitelist
        List<WhitelistedIP> ips = Main.getInstance().getSQLManager().getWhitelistedIPs();
        VPNListener.getAllowedIps().clear();
        VPNListener.setAllowedIps(ips);
        Logger.info("Update IP adres na whitelistu, celkem (" + ips.size() + ").");

        // UUID Whitelist
        List<WhitelistedUUID> uuids = Main.getInstance().getSQLManager().getWhitelistedUUIDs();
        VPNListener.getAllowedUUIDs().clear();
        VPNListener.setAllowedUUIDs(uuids);
        Logger.info("Update UUID adres na whitelistu, celkem (" + uuids.size() + ").");

        Logger.success("Update IP & UUID Whitelistu dokoncen!");
    }
}
