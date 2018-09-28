package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;

public class GBC_command extends Command {

    Main plugin;

    public GBC_command(Main pl) {
        super("gbc", "craftbungee.broadcast.send");
        this.plugin = pl;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("§c§l(!) §cNespravny format! Pouzij /gbc <text>");
            return;
        }

        for (ProxiedPlayer pl : Main.getInstance().getOnlinePlayers()) {
            ArrayList<String> s = new ArrayList<>();
            Collections.addAll(s, strings);
            BungeeUtils.sendBroadcast(pl, s, true);
        }
    }
}