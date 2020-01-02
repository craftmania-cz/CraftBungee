package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.NameBlacklistListener;
import cz.wake.craftbungee.utils.Logger;

import java.util.List;

public class NameBlacklistTask implements Runnable {

    @Override
    public void run() {
        // Blacklisted name words
        List<String> blacklisted = Main.getInstance().getSQLManager().getBlacklistedNameWords();
        NameBlacklistListener.setBlacklistedWords(blacklisted);
        Logger.info("Update blokovanych slov ve jmene, celkem (" + blacklisted.size() + ").");

        // Allowed names with blacklisted word
        List<String> whitelisted = Main.getInstance().getSQLManager().getAllowedBlacklistedNames();
        NameBlacklistListener.setWhitelistedNames(whitelisted);
        Logger.info("Update povolenych jmen s blokovanym slovem, celkem (" + whitelisted.size() + ").");

        Logger.success("Update Name blacklistu dokoncen!");
    }
}
