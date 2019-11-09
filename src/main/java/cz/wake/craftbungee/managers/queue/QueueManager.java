package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.TimeUnit;

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
}
