package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class NameBlacklistListener implements Listener {

    private static List<String> blacklistedWords = new ArrayList<>();
    private static List<String> whitelistedNames = new ArrayList<>();


    @EventHandler
    public void onLogin(PreLoginEvent e) {
        if(e.isCancelled()) {
            return;
        }

        String name = e.getConnection().getName();
        for(String blacklisted : blacklistedWords) {
            if(name.toLowerCase().contains(blacklisted.toLowerCase())) {
                if(!whitelistedNames.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(name.toLowerCase())) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Jmeno hrace " + name + " obsahuje nepovolene slovo (" + blacklisted +"), proto nebyl pusten na server.");
                    e.setCancelReason("&c&lTvé jmeno obsahuje nepovolená slova\n§fPokud si myslis, ze to tak neni, napis\n§fna webu nebo na Discordu uzivateli §e§lMrWakeCZ\n&fDetekovane slovo: §e§l" + blacklisted);
                    e.setCancelled(true);
                    return;
                } else {
                    Logger.danger("Jmeno hrace " + name + " obsahuje nepovolene slovo (" + blacklisted +"), ale nachazi se na whitelistu a proto byl pusten na server.");
                    return;
                }
            }
        }
    }

    public static void setBlacklistedWords(List<String> blacklistedWords) {
        NameBlacklistListener.blacklistedWords = blacklistedWords;
    }

    public static void setWhitelistedNames(List<String> whitelistedNames) {
        NameBlacklistListener.whitelistedNames = whitelistedNames;
    }
}
