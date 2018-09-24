package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import cz.wake.craftbungee.utils.GHelp;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GHelp_command extends Command {

    Main plugin;
    public static HashMap<ProxiedPlayer, Integer> cooldowns = new HashMap<>();


    public GHelp_command(Main pl) {
        super("ghelp");
        this.plugin = pl;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (0 < strings.length && strings[0].contains("list")) {
            if (!BungeeUtils.getGroupBool(p)) {
                p.sendMessage("§6§lGHELP §7⎟ §cNemas prava na zobrazeni seznamu odeslanych zprav v GHELP.");
                return;
            }
            if (GHelp.helps.size() == 0) {
                p.sendMessage("§6§lGHELP §7⎟ §7§lAktualne zde nejsou zadne zpravy.");
                return;
            }

            p.sendMessage("§r");
            p.sendMessage("§7§l§m--------§r§7[ §e§lSeznam poslednich GHelp zprav §7]§m--------");
            p.sendMessage("§r");
            p.sendMessage("    §7§oNajetim na nick uvidis, odkud byla zprava poslana.");
            p.sendMessage("    §7§oPo najeti na zpravu uvidis cas odeslani.");
            p.sendMessage("§r");

            for (GHelp help : GHelp.helps) {
                TextComponent component1 = new TextComponent("§e" + help.getPlayer().getName() + "§7:");
                component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Odeslano ze serveru: §f" + help.getPlayer().getServer().getInfo().getName()).create()));
                component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + help.getPlayer().getServer().getInfo().getName()));

                TextComponent component2 = new TextComponent(" §f" + help.getMessage());
                component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Odeslano: §f" + BungeeUtils.getDate(help.getLong()) + "\n§7Po kliknuti budes presunut na server: §f" + help.getPlayer().getServer().getInfo().getName()).create()));
                component2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + help.getPlayer().getServer().getInfo().getName()));

                TextComponent component = new TextComponent();
                component.addExtra(component1);
                component.addExtra(component2);

                p.sendMessage(component);
            }
            p.sendMessage("§r");
            p.sendMessage("§7§l§m---------------§r§7[ §e§lKonec seznamu §7]§m----------------");
            p.sendMessage("§r");
            return;
        }

        if (BungeeUtils.getGroupBool(p)) {
            p.sendMessage("§6§lGHELP §7⎟ §cCleni AT pouzivaji /ga nebo /a, nikoliv /ghelp.");
            return;
        }

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

        StringBuilder zprava = new StringBuilder();
        for (String s : strings) {
            zprava.append(s + " §f");
        }

        for (ProxiedPlayer pl : plugin.getOnlinePlayers()) {
            if (pl.hasPermission("craftbungee.at-ghelp")) {
                TextComponent component = new TextComponent("§6§lGHELP §7⎟ §e" + commandSender.getName() + "§7: §f" + zprava);
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Po kliknuti budes teleportnut na server §f" + p.getServer().getInfo().getName()).create()));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + p.getServer().getInfo().getName()));
                pl.sendMessage(component);
            }
        }
        p.sendMessage("§6§lGHELP §7⎟ §eZprava byla odeslana vsem pripojenym clenum AT.");
        GHelp.saveGhelp(p, zprava.toString());
        cooldowns.put(p, 60);
    }
}