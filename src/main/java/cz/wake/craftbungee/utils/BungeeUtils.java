package cz.wake.craftbungee.utils;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    public static void sendMessageToBukkit(String channel, String nick, String token, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(channel);
            out.writeUTF(nick);
            out.writeUTF(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("craftbungee", stream.toByteArray());
    }

    public static String getGroup(ProxiedPlayer p) {
        if (p.hasPermission("craftmania.at.majitel")) { return "Majitel"; }
        if (p.hasPermission("craftmania.at.vedeni")) { return "Vedeni"; }
        if (p.hasPermission("craftmania.at.developer")) { return "Developer"; }
        if (p.hasPermission("craftmania.at.admin")) { return "Admin"; }
        if (p.hasPermission("craftmania.at.helper")) { return "Helper"; }
        if (p.hasPermission("craftmania.at.eventer")) { return "Eventer"; }
        if (p.hasPermission("craftmania.at.builder")) { return "Builder"; }
        return "Hrac";
    }

    public static boolean getGroupBool(ProxiedPlayer p) {
        if (p.hasPermission("craftmania.at.majitel")) { return true; }
        if (p.hasPermission("craftmania.at.vedeni")) { return true; }
        if (p.hasPermission("craftmania.at.developer")) { return true; }
        if (p.hasPermission("craftmania.at.admin")) { return true; }
        if (p.hasPermission("craftmania.at.helper")) { return true; }
        if (p.hasPermission("craftmania.at.eventer")) { return true; }
        if (p.hasPermission("craftmania.at.builder")) { return true; }
        return false;
    }

    public static String getPlayers(ArrayList<ProxiedPlayer> players) {
        if (players.size() == 1) {
            return "clen";
        } else if (players.size() >= 2 && players.size() <= 4) {
            return "cleni";
        } else {
            return "clenu";
        }
    }

    public static Integer getATOnlinePlayers() {
        ArrayList<ProxiedPlayer> players = new ArrayList<>();
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            if (getGroupBool(p)) {

                String group = getGroup(p);

                if (group.contains("Majitel")) { players.add(p); }
                if (group.contains("Vedeni")) { players.add(p); }
                if (group.contains("Developer")) { players.add(p); }
                if (group.contains("Admin")) { players.add(p); }
                if (group.contains("Helper")) { players.add(p); }
                if (group.contains("Eventer")) { players.add(p); }
                if (group.contains("Builder")) { players.add(p); }

            }
        }
        return players.size();
    }
}
