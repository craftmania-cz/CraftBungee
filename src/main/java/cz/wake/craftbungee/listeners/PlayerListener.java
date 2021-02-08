package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

public class PlayerListener implements Listener {

    private Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(final PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
        final String address = e.getPlayer().getAddress().getAddress().getHostAddress();

        Main.getInstance().getOnlinePlayers().add(p);
        Main.getInstance().getSQLManager().updateStats(p, true);

        if (Main.getInstance().getSQLManager().isIPBanned(address)) {
            TextComponent exclamationMark = new TextComponent("[!]");
            exclamationMark.setColor(ChatColor.RED);
            exclamationMark.setBold(true);
            TextComponent message = new TextComponent(" Na tvé IP adrese je zabanovaný nějaký účet. Pokud nevlastníš dynamickou IP adresu tak se může jednat o obcházení banu a můžeš mít problém.");
            message.setColor(ChatColor.RED);
            message.setBold(false);

            exclamationMark.addExtra(message);
            p.sendMessage(exclamationMark);
        }

        if (Via.getAPI() != null) {
            Main.getInstance().getSQLManager().updateMCVersion(p, Via.getAPI().getPlayerVersion(p.getUniqueId()));
        }
    }

    @EventHandler
    public void onDisconect(final PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        Main.getInstance().getSQLManager().updateStats(p, false);
        Main.getInstance().getOnlinePlayers().remove(p);
    }
}
