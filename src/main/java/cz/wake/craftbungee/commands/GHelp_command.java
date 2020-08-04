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

import java.util.Arrays;
import java.util.HashMap;

public class GHelp_command extends Command {

    private final Main plugin;
    public static HashMap<ProxiedPlayer, Integer> cooldowns = new HashMap<>();


    public GHelp_command(Main pl) {
        super("ghelp");
        this.plugin = pl;
    }

    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (strings.length > 0) {
            if (strings[0].contains("list")) {
                if (!BungeeUtils.getGroupBool(p)) {
                    p.sendMessage("§6§lGHELP §7⎟ §cNemáš práva na zobrazení seznamu odeslaných zprav v GHELP.");
                    return;
                }
                if (GHelp.helps.size() == 0) {
                    p.sendMessage("§6§lGHELP §7⎟ §7§lAktuálně zde nejsou žádne zprávy.");
                    return;
                }

                p.sendMessage("§r");
                p.sendMessage("§7§l§m--------§r§7[ §e§lSeznam posledních GHelp zprav §7]§m--------");
                p.sendMessage("§r");
                p.sendMessage("    §7§oNajetím na nick uvidíš, odkud byla zpráva poslána.");
                p.sendMessage("    §7§oPo najetí na zprávu uvidíš čas odeslání.");
                p.sendMessage("§r");

                for (GHelp help : GHelp.helps) {
                    TextComponent component1 = new TextComponent("§e" + help.getPlayer().getName() + "§7:");
                    component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Odesláno ze serveru: §f" + help.getPlayer().getServer().getInfo().getName()).create()));
                    component1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + help.getPlayer().getServer().getInfo().getName()));

                    TextComponent component2 = new TextComponent(" §f" + help.getMessage());
                    component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Odesláno: §f" + BungeeUtils.getDate(help.getLong()) + "\n§7Po kliknutí budeš přesunut na server: §f" + help.getPlayer().getServer().getInfo().getName()).create()));
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
            } else if (strings[0].contains("respond") && strings.length >= 3) {
                try {
                    if (!BungeeUtils.getGroupBool(p)) {
                        p.sendMessage("§6§lGHELP §7⎟ §cNemáš práva na odepisování v GHELP.");
                        return;
                    }
                    Integer ID = Integer.parseInt(strings[1]);
                    StringBuilder message = new StringBuilder();
                    for (String s : Arrays.asList(strings).subList(3, strings.length)) {
                        message.append(s).append(" §f");
                    }

                    GHelp gHelp = GHelp.getGHelpById(ID);
                    if (gHelp == null) {
                        p.sendMessage("§cGHELP zpráva s ID " + ID + " nebyla nalezena.");
                        return;
                    }
                    if (!gHelp.getPlayer().isConnected()) {
                        p.sendMessage("Hráč, který napsal tuto zprávu již není online.");
                        return;
                    }
                    gHelp.getPlayer().sendMessage("§6§lGHELP §7⎟ §eNa tvoji zprávu v GHELP jsi dostal odpověd.");
                    gHelp.getPlayer().sendMessage("§7Tvoje zpráva: " + gHelp.getMessage());
                    gHelp.getPlayer().sendMessage("§e" + p.getName() + ": §7" + message);
                    return;
                } catch (Exception e) {
                    p.sendMessage("§c§lNesprávný formát! Použij /ghelp respond <ID> <zpráva>");
                    return;
                }
            }
        }

        if (BungeeUtils.getGroupBool(p)) {
            p.sendMessage("§6§lGHELP §7⎟ §cČleni AT používají /ga nebo /a, nikoliv /ghelp.");
            return;
        }

        if (cooldowns.containsKey(p)) {
            p.sendMessage("§c§l[!] §cMusíš počkat ještě " + cooldowns.get(p) + " sekund!");
            return;
        }

        if (strings.length == 0) {
            p.sendMessage("§c§l[!] §cNesprávný formát! Použij /ghelp <text>");
            return;
        }

        /*if (BungeeUtils.getATOnlinePlayers() == 0) {
            p.sendMessage("§6§lGHELP §7⎟ §eAktualne neni na serveru online nikdo z AT, kontaktuj cleny AT na Discordu nebo na webu.");
            return;
        }*/

        StringBuilder zprava = new StringBuilder();
        for (String s : strings) {
            zprava.append(s + " §f");
        }

        for (ProxiedPlayer pl : plugin.getOnlinePlayers()) {
            if (pl.hasPermission("craftbungee.at-ghelp")) {
                TextComponent component = new TextComponent("§6§lGHELP §7⎟ §e" + commandSender.getName() + "§7: §f" + zprava);
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Po kliknutí budeš teleportnut na server §f" + p.getServer().getInfo().getName()).create()));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + p.getServer().getInfo().getName()));
                pl.sendMessage(component);
            }
        }
        p.sendMessage("§6§lGHELP §7⎟ §eZpráva byla odeslána všem připojeným členům AT.");
        GHelp.saveGhelp(p, zprava.toString());
        cooldowns.put(p, 60);
    }
}