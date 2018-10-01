package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.awt.*;
import java.util.ArrayList;

public class GBP_command extends Command {

    Main plugin;

    public GBP_command(Main pl) {
        super("gbp", "craftbungee.broadcast.preview");
        this.plugin = pl;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        Configuration config = Main.getConfig();

        if (strings.length == 1 && config.getSection("automessages").getSection("list").getKeys().contains(strings[0])) {
            BungeeUtils.sendBroadcast(commandSender, new ArrayList<>(config.getSection("automessages").getSection("list").getStringList(strings[0])), false);
            return;
        }

        commandSender.sendMessage("==========[ §c§lSeznam dostupnych oznameni §f]==========");
        for (String s : config.getSection("automessages").getSection("list").getKeys()) {
            TextComponent component = new TextComponent("§7- §f" + s);
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gbp " + s));
            commandSender.sendMessage(component);
        }
        commandSender.sendMessage("=================[ §c§lKonec seznamu §f]=================");
    }
}