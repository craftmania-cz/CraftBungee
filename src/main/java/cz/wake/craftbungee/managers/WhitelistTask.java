package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import cz.wake.craftbungee.utils.Logger;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedNames;

import java.util.List;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {

        // IP Whitelist
        List<WhitelistedIP> ips = Main.getInstance().getSQLManager().getWhitelistedIPs();
        VPNListener.getAllowedIps().clear();
        VPNListener.setAllowedIps(ips);
        Logger.info("Update IP adres na whitelistu, celkem (" + ips.size() + ").");

        // Nick Whitelist
        List<WhitelistedNames> names = Main.getInstance().getSQLManager().getWhitelistedNames();
        VPNListener.getAllowedNames().clear();
        VPNListener.setAllowedNames(names);
        Logger.info("Update Nicku na whitelistu, celkem (" + names.size() + ").");

        Logger.success("Update IP & Nick Whitelistu dokoncen!");
    }
}
