package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class CraftQueue {

    private ServerInfo serverInfo;
    private Queue<ProxiedPlayer> queue;
    private int maxPerWave;

    public CraftQueue(ServerInfo serverInfo) {
        Main.queues.add(this);
        this.serverInfo = serverInfo;
        this.queue = new LinkedList<>();
        this.maxPerWave = Main.getConfig().getInt("queue-system.max-players");
        Main.getInstance().getLogger().info("Registered queue for server: " + serverInfo.getName());
    }

    public void addToQueue(ProxiedPlayer player) {
        if (this.queue.contains(player)) return;
        this.queue.add(player);
        Main.getInstance().getLogger().info("" + queue.size());
        if (player.hasPermission("craftbungee.queue.ignore")) {
            this.connectPlayer(player, true);
            return;
        } else if (queue.size() <= Main.getConfig().getInt("queue-system.max-players")) {
            this.connectPlayer(player, false);
            return;
        } else {
            player.sendMessage(QueueManager.PREFIX + "Na tento server se snaží připojit mnoho lidí najednou, proto jsme tě dali do fronty (počet čekajících hráčů: " + this.queue.size() + ").");
        }
    }

    public boolean enoughPlayers() {
        return this.queue.size() >= Main.getConfig().getInt("queue-system.max-players");
    }

    public void connectPlayer(ProxiedPlayer player, boolean force) {
        if (force) {
            player.connect(this.serverInfo, ServerConnectEvent.Reason.PLUGIN_MESSAGE);
            queue.remove(player);
            return;
        }
        player.sendMessage(QueueManager.PREFIX + "Budeš přesměrován na server za 3 vteřiny...");
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> player.connect(this.serverInfo, ServerConnectEvent.Reason.PLUGIN_MESSAGE), 3L, 0L, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> queue.remove(player), 3L, 0L, TimeUnit.SECONDS);
        return;
    }

    public void connectPlayers(List<ProxiedPlayer> players, boolean force) {
        for (ProxiedPlayer player : players) {
            this.connectPlayer(player, force);
        }
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getServerName() {
        return this.serverInfo.getName();
    }

    public int getMaxPerWave() {
        return maxPerWave;
    }

    public Queue<ProxiedPlayer> getQueue() {
        return queue;
    }
}
