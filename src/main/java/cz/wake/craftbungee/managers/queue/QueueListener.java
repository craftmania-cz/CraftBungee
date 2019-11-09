package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QueueListener implements Listener {

    @EventHandler
    public void onServerSwitch(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
        ServerInfo server = e.getTarget();
        if (p.getServer() != null) {
            if (p.getServer().getInfo().equals(server)) return;
        }
        if (server.getName().equalsIgnoreCase("event-server")) {
            if (Main.getInstance().getSQLManager().getConfigValue("allow_queue").equalsIgnoreCase("false")) return;
            CraftQueue q = Main.queues.stream().filter(queue -> queue.getServerInfo() == server).findFirst().get();
            if (e.getReason().equals(ServerConnectEvent.Reason.PLUGIN_MESSAGE)) return;
            q.addToQueue(p);
            e.setCancelled(true);
        }
    }
}
