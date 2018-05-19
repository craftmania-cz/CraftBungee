package cz.wake.craftbungee.listeners;

import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {

    @EventHandler
    public void onProxyPing(final ProxyPingEvent e) {

        // Oznacovani serveru jako Vanilla type
        e.getResponse().getModinfo().setType("VANILLA");

    }
}
