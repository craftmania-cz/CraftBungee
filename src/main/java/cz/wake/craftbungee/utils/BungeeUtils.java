package cz.wake.craftbungee.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.regex.Pattern;

public class BungeeUtils {

    private static Pattern ipPattern =
            Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    public static String getPlayerIP(final ProxiedPlayer p) {
        return p.getAddress().getAddress().getHostAddress();
    }

    public static boolean validIP(final String ip) {
        return ipPattern.matcher(ip).matches();
    }

    public static String getPlayerServer(final ProxiedPlayer p) {
        if (p.getServer() == null) {
            return "hub";
        }
        return p.getServer().getInfo().getName();
    }

    public static boolean isServer(final String serverName) {
        return ProxyServer.getInstance().getServers().containsKey(serverName);
    }
}
