package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import net.md_5.bungee.event.EventHandler;

public class HelpCommandListener implements Listener {

    private Main plugin;

    public HelpCommandListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void declareCommands(ProxyDefineCommandsEvent event) {
        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            plugin.getLogger().fine(event.getReceiver() + " is not a player");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        if (player.hasPermission("craftbungee.completions.whitelist")) {
            return;
        }

        //TODO: Ccominuty profile nastavení.
        if (player.hasPermission("craftbungee.completions.blacklist")) {
            event.getCommands().clear();
            return;
        }

        event.getCommands().entrySet().removeIf(commandEntry -> !plugin.checkCommand(player, commandEntry.getKey(), commandEntry.getValue()));
    }
}
