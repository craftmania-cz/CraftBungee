package cz.wake.craftbungee.listeners;

import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {

    @EventHandler
    public void onProxyPing(final ProxyPingEvent e) {

        // Oznacovani serveru jako Vanilla type
        e.getResponse().getModinfo().setType("VANILLA");
/*
        if (System.currentTimeMillis() < 1609455600000L) { // 1.1.2021 0:00
            e.getResponse().setPlayers(new ServerPing.Players(2021, 2020, null));
        } else if (System.currentTimeMillis() > 1609455600000L && System.currentTimeMillis() < 1609473600000L) { //1.1.2021 5:00
            e.getResponse().setPlayers(new ServerPing.Players(2021, 2021, null));
        }*/
    }
}
