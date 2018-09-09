package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collections;

public class GA_command extends Command {

    Main plugin;

    public GA_command(Main pl) {
        super("ga", "craftbungee.at-chat");
        this.plugin = pl;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (p.hasPermission("craftbungee.at-chat")) {
            for (ProxiedPlayer pl : plugin.getProxy().getPlayers()) {
                if (pl != null) {
                    if (pl.hasPermission("craftbungee.at-chat")) {
                        ArrayList<String> test = new ArrayList<>();
                        Collections.addAll(test, strings);
                        pl.sendMessage("§4§lGATCHAT §a" + p.getName() + "§7: §e" + test.toString().replace("[", "").replace("]", "").replace(",", ""));
                    }
                }
            }
        } else {
            p.sendMessage("§c(!) Na tuto akci nemas prava");
        }
        else {
            p.sendMessage("§c(!) Na tuto akci nemas prava")
        }
    }
}
