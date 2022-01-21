package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class RateLimitJoinListener implements Listener {

    public static int actualLimit = 0;
    public static int joinLimit = 10;
    public static int limitInSeconds = 1;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFirstJoin(PreLoginEvent event) {

        if (actualLimit > joinLimit) {
            event.setCancelReason("§c§lDočasně se nelze připojit.\n§7Na server se připojuje moc hráčů, počkej chvilku.");
            event.setCancelled(true);
            return;
        }
        actualLimit++;
    }
}
