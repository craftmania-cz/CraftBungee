package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;

import java.util.List;
import java.util.regex.Pattern;

public class WhitelistTask implements Runnable {

    @Override
    public void run() {

        // Ziskani z SQL
        List<String> list = Main.getInstance().getSQLManager().getWhitelistedIPs();

        // Smazani pred updatem
        Main.getInstance().allowedIps.clear();

        //Build patterns
        list.stream().map(Pattern::compile).forEach(p -> Main.getInstance().allowedIps.add(p));
    }
}
