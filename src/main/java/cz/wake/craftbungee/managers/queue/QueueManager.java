package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class QueueManager {

    public static final String PREFIX = "§d§lQueue §8| §7";

    public QueueManager() {
        for (String server : Main.getConfig().getStringList("queue-system.servers")) {
            Main.queues.add(new CraftQueue(Main.getInstance().getProxy().getServerInfo(server)));
        }

        if (!Main.queues.isEmpty()) {
            ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new QueueTask(), 0L, Main.getConfig().getInt("queue-system.interval"), TimeUnit.SECONDS);
        }
    }

    public void unregisterPlayer(ProxiedPlayer player) {
        Main.queues.forEach(craftQueue -> craftQueue.getQueue().remove(player));
    }

    public boolean isInAnyQueue(ProxiedPlayer player) {
        return Main.queues.stream().anyMatch(craftQueue -> craftQueue.getQueue().contains(player));
    }

    public List<CraftQueue> getPlayerQueues(ProxiedPlayer player) {
        return Main.queues.stream().filter(craftQueue -> craftQueue.getQueue().contains(player)).collect(Collectors.toList());
    }

    public boolean isInQueue(ProxiedPlayer player, String serverName) {
        return getPlayerQueues(player).stream().anyMatch(craftQueue -> craftQueue.getServerName().equalsIgnoreCase(serverName));
    }

    public CraftQueue getQueue(String serverName) {
        if (Main.queues.stream().noneMatch(craftQueue -> craftQueue.getServerName().equalsIgnoreCase(serverName))) return null;
        return Main.queues.stream().filter(craftQueue -> craftQueue.getServerName().equalsIgnoreCase(serverName)).findFirst().get();
    }
}
