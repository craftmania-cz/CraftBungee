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
        if (queue.size() <= Main.getConfig().getInt("queue-system.max-players")) {
            this.connectPlayer(player);
            return;
        } else {
            player.sendMessage(QueueManager.PREFIX + "Na tento server se snazi pripojit mnoho lidi najednou, proto sme te dali do fronty (pocet cekajicich hracu: " + this.queue.size() + ").");
        }
    }

    public boolean enoughPlayers() {
        return this.queue.size() >= Main.getConfig().getInt("queue-system.max-players");
    }

    public void connectPlayer(ProxiedPlayer player) {
        player.sendMessage(QueueManager.PREFIX + "Budes presmerovan na server za 3 vteriny...");
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> player.connect(this.serverInfo, ServerConnectEvent.Reason.PLUGIN_MESSAGE), 3L, 0L, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), () -> queue.remove(player), 3L, 0L, TimeUnit.SECONDS);
        return;
    }

    public void connectPlayers(List<ProxiedPlayer> players) {
        for (ProxiedPlayer player : players) {
            this.connectPlayer(player);
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
