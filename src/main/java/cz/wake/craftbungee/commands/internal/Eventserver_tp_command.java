package cz.wake.craftbungee.commands.internal;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Eventserver_tp_command extends Command {

    private Main plugin;

    public Eventserver_tp_command(Main plugin) {
        super("eventserver-tp");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            ServerInfo server = ProxyServer.getInstance().getServerInfo("event-server");
            player.connect(server);
        }
    }
}
