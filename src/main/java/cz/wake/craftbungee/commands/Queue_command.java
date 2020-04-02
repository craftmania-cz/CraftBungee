package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.managers.queue.CraftQueue;
import cz.wake.craftbungee.managers.queue.QueueManager;
import cz.wake.craftbungee.utils.TextComponentBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Queue_command extends Command implements TabExecutor {

    public Queue_command() {
        super("queue", null, "fronta");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 0) {
            if (!Main.getQueueManager().isInAnyQueue(player)) {
                player.sendMessage(QueueManager.PREFIX + "Nenacházíš se v žádně frontě.");
                return;
            }
            player.sendMessage(QueueManager.PREFIX + "Fronty ve kterých se nacházíš:");
            for (CraftQueue playerQueue : Main.getQueueManager().getPlayerQueues(player)) {
                TextComponent base = new TextComponentBuilder("§8- §d#" + playerQueue.getPosition(player) + "§f na server §7" + playerQueue.getServerName()).getComponent();
                TextComponent leave = new TextComponentBuilder(" §c[Opustit]").setTooltip("Kliknutím opustíš tuto frontu").setPerformedCommand("queue leave " + playerQueue.getServerName()).getComponent();
                BaseComponent msg = base.duplicate();
                msg.addExtra(leave);
                player.sendMessage(msg);
            }
        }
        switch (args[0].toLowerCase()) {
            case "remove":
            case "leave":
                if (args.length >= 2) {
                    String server = args[1];
                    if (!Main.getQueueManager().isInAnyQueue(player)) {
                        player.sendMessage(QueueManager.PREFIX + "Nenacházíš se v žádně frontě.");
                        return;
                    }
                    if (!Main.getQueueManager().isInQueue(player, server)) {
                        player.sendMessage(QueueManager.PREFIX + "Ve frontě na tento server se nenacházíš.");
                        return;
                    }
                    CraftQueue craftQueue = Main.getQueueManager().getQueue(server);
                    craftQueue.removePlayer(player);
                    player.sendMessage(QueueManager.PREFIX + "Opustil jsi frontu pro server " + craftQueue.getServerName() + ".");
                } else {
                    player.sendMessage(QueueManager.PREFIX + "Použití: /queue leave <server>");
                }
                break;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof ProxiedPlayer)) return out;

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            out.add("leave");
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "remove":
                case "leave":
                    out.addAll(Main.getQueueManager().getPlayerQueues(player).stream().map(CraftQueue::getServerName).collect(Collectors.toList()));
                    break;
            }
        }

        Collections.sort(out);
        int lastArg = args.length - 1;
        if (args[lastArg].length() > 0) {
            return out.stream().filter(string -> string.startsWith(args[lastArg])).collect(Collectors.toList());
        }

        return out;
    }
}
