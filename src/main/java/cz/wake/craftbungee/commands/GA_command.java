package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collections;

public class GA_command extends Command {

    private Main plugin;
    private ArrayList<ProxiedPlayer> disabled_players = new ArrayList<>();

    public GA_command(Main pl) {
        super("ga", "craftbungee.at-chat");
        this.plugin = pl;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (strings.length == 0) {
            p.sendMessage("§c§l[!] §cNespravny format! Pouzij /ga <text>");
            return;
        }

        if (strings[0].equalsIgnoreCase("toggle")) {
            if (disabled_players.contains(p)) {
                disabled_players.remove(p);
                p.sendMessage("§cNyni prijimas zpravy z globalniho admin team chatu.");
                return;
            } else {
                disabled_players.add(p);
                p.sendMessage("§cNyni neprijimas zpravy z globalniho admin team chatu.");
                return;
            }
        }

        if (p.hasPermission("craftbungee.at-chat")) {
            for (ProxiedPlayer pl : plugin.getProxy().getPlayers()) {
                if (pl != null) {
                    if (pl.hasPermission("craftbungee.at-chat")) {
                        StringBuilder builder = new StringBuilder();
                        for (String s : strings) {
                            builder.append(s + " §e");
                        }
                        TextComponent component = new TextComponent("§4§lGATCHAT §a" + p.getName() + "§7: " + ChatColor.YELLOW + builder);
                        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Server: §f" + p.getServer().getInfo().getName()).create()));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + p.getServer().getInfo().getName()));

                        if (!disabled_players.contains(pl)) {
                            pl.sendMessage(component);
                        }
                    }
                }
            }
        } else {
            p.sendMessage("§c(!) Na tuto akci nemas prava");
        }
    }
}
