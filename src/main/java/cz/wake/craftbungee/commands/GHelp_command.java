package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GHelp_command extends Command {

    private Main plugin;
    public static HashMap<ProxiedPlayer, Integer> cooldowns = new HashMap<>();


    public GHelp_command(Main pl) {
        super("ghelp");
        this.plugin = pl;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (cooldowns.containsKey(p)) {
            p.sendMessage("§c§l(!) §cMusis pockat jeste " + cooldowns.get(p) + " sekund!");
            return;
        }

        if (strings.length == 0) {
            p.sendMessage("§c§l(!) §cNespravny format! Pouzij /ghelp <text>");
            return;
        }

        if (BungeeUtils.getATOnlinePlayers() == 0) {
            p.sendMessage("§6§lGHELP §7⎟ §eAktualne neni na serveru online nikdo z AT, kontaktuj cleny AT na Discordu nebo na webu.");
            return;
        }

        ArrayList<String> message = new ArrayList<>();
        Collections.addAll(message, strings);
        for (ProxiedPlayer pl : plugin.getOnlinePlayers()) {
            if (pl.hasPermission("craftbungee.at-ghelp")) {
                TextComponent component = new TextComponent("§6§lGHELP §7⎟ §e" + commandSender.getName() + "§7: §f" + message.toString().replace("[", "").replace("]", "").replace(",", ""));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Po kliknuti budes teleportnut na server §f" + p.getServer().getInfo().getName()).create()));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + p.getServer().getInfo().getName()));
                pl.sendMessage(component);
            }
        }
        p.sendMessage("§6§lGHELP §7⎟ §eZprava byla odeslana vsem pripojenym clenum AT.");
        cooldowns.put(p, 60);
    }
}