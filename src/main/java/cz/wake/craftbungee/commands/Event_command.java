package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.managers.events.Event;
import cz.wake.craftbungee.managers.events.EventManager;
import cz.wake.craftbungee.utils.CenteredMessage;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class Event_command extends Command {

    public Event_command() {
        super("event", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ArrayList<String> messages = new ArrayList<>();

        ProxiedPlayer player = (ProxiedPlayer) sender;
        Event event = EventManager.event;

        Logger.info(event.toString());

        messages.add("§a=========================================");
        messages.add(" ");
        if (!event.isSelected()) {
            messages.add("§cPrávě neprobíha žádny event.");
        } else {
            switch (event.getEventState()) {
                case LOBBY:
                case STARTING:
                    messages.add("§7Zachvíli začína event §a" + event.getName());
                    messages.add("§7(" + event.getCategory() + ")");
                    messages.add("§7Odměna: §e" + event.getReward() + " EP");
                    break;
                case INGAME:
                    messages.add("§7Právě probíha event §a" + event.getName());
                    messages.add("§7(" + event.getCategory() + ")");
                    messages.add("§7Odměna: §e" + event.getReward() + " EP");
                    break;
                case ENDING:
                    messages.add("§7Končí event §a" + event.getName());
                    messages.add("§7(" + event.getCategory() + ")");
                    messages.add("§7Odměna: §e" + event.getReward() + " EP");
                    break;
            }
        }
        messages.add(" ");
        messages.add("§a=========================================");

        CenteredMessage.sendMessage(player, messages.toArray(new String[messages.size()]));
    }
}
