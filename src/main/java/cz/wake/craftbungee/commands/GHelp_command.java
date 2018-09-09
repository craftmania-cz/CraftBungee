package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class GHelp_command extends Command {

    Main plugin;

    public GHelp_command(Main pl) {
        super("ga", "craftbungee.at-chat");
        this.plugin = pl;
    }
    @SuppressWarnings("deprecation")
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());
        ArrayList<String> strings1 = new ArrayList<>();
        for (String s : strings) {
            strings1.add(s);
        }

        jsonMessage(p, strings1.toString().replace("[", "").replace("]", "").replace(",", ""));
    }
    public void jsonMessage(ProxiedPlayer player, String message) {
        TextComponent component = new TextComponent("§6§lGHELP §7⎟ §e" + player.getName() + "§7: §f" + message);
        component.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Po kliknuti budes teleportovan na server §f" + player.getServer().getInfo().getName()).create() ) );
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + player.getServer().getInfo().getName()));

        for (ProxiedPlayer p : plugin.getOnlinePlayers()) {
            p.sendMessage(component);
        }
    }
}
