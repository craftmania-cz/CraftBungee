package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class GA_command extends Command {
    Main plugin;

    public GA_command(Main pl) {
        super("ga", "craftbungee.at-chat");
        this.plugin = pl;
    }
    @SuppressWarnings("deprecation")
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (p.hasPermission("craftbungee.at-chat")) {
            for (ProxiedPlayer pl : plugin.getProxy().getPlayers()) {
                if (pl instanceof ProxiedPlayer) {
                    if (pl.hasPermission("craftbungee.at-chat")) {
                        ArrayList<String> test = new ArrayList<>();
                        for (String s : strings) {
                            test.add(s);
                        }
                        pl.sendMessage("§8§lG§c§lATCHAT §a" + p.getName() + ": §e" + test.toString().replace("[", "").replace("]", "").replace(",", ""));
                    }
                }
            }
        }
    }
}
