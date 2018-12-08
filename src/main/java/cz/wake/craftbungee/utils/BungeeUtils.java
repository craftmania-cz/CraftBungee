package cz.wake.craftbungee.utils;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class BungeeUtils {

    private static int latest = 0;

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
        if (p.hasPermission("craftmania.at.majitel")) {
            return "Majitel";
        }
        if (p.hasPermission("craftmania.at.vedeni")) {
            return "Vedeni";
        }
        if (p.hasPermission("craftmania.at.developer")) {
            return "Developer";
        }
        if (p.hasPermission("craftmania.at.eventer")) {
            return "Eventer";
        }
        if (p.hasPermission("craftmania.at.admin")) {
            return "Admin";
        }
        if (p.hasPermission("craftmania.at.builder")) {
            return "Builder";
        }
        if (p.hasPermission("craftmania.at.helper")) {
            return "Helper";
        }
        return "Hrac";
    }

    public static boolean getGroupBool(ProxiedPlayer p) {
        if (p.hasPermission("craftmania.at.majitel")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.vedeni")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.developer")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.eventer")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.admin")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.builder")) {
            return true;
        }
        if (p.hasPermission("craftmania.at.helper")) {
            return true;
        }
        return false;
    }

    public static Integer getATOnlinePlayers() {
        ArrayList<ProxiedPlayer> players = new ArrayList<>();
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            if (getGroupBool(p)) {

                String group = getGroup(p);

                if (group.contains("Majitel")) {
                    players.add(p);
                }
                if (group.contains("Vedeni")) {
                    players.add(p);
                }
                if (group.contains("Developer")) {
                    players.add(p);
                }
                if (group.contains("Admin")) {
                    players.add(p);
                }
                if (group.contains("Eventer")) {
                    players.add(p);
                }
                if (group.contains("Helper")) {
                    players.add(p);
                }
                if (group.contains("Builder")) {
                    players.add(p);
                }

            }
        }
        return players.size();
    }

    public static String getServer(ProxiedPlayer p) {
        String server = p.getServer().getInfo().getName();
        if (Main.getConfig().getStringList("survival-servery").contains(server)) {
            return "survival serveru";
        } else if (Main.getConfig().getStringList("minigames-servery").contains(server)) {
            return "minigames serveru";
        } else if (Main.getConfig().getStringList("lobby").contains(server)) {
            return "lobby";
        } else if (Main.getConfig().getStringList("event-servery").contains(server)) {
            return "eventu";
        }
        return "tajnem serveru";
    }

    public static String getDate(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
    }

    public static ArrayList<String> nextBroadcastFromConfig() {
        Configuration config = Main.getConfig();
        ArrayList<String> strings = new ArrayList<String>(config.getSection("automessages").getSection("list").getKeys());
        if (strings.size() == latest) {
            latest = 0;
        }
        String s = strings.get(latest);
        latest++;

        return new ArrayList<String>(config.getSection("automessages").getSection("list").getStringList(s));
    }

    private static ArrayList<String> getBroadcastFormat() {
        return new ArrayList<String>(Main.getConfig().getStringList("broadcast-format"));
    }

    public static void sendBroadcast(CommandSender commandSender, ArrayList<String> brc, boolean format) {
        if (commandSender instanceof ProxiedPlayer) {
            sendBroadcast((ProxiedPlayer) commandSender, brc, format);
        } else {
            for (String broadc : brc) {
                commandSender.sendMessage(broadc.replaceAll("&", "§").replace("{text}", ""));
            }
        }
    }
    
    public static void sendBroadcast(ProxiedPlayer p, ArrayList<String> brc, boolean format) {
        if (!Main.getConfig().getSection("automessages").getStringList("server-blacklist").contains(p.getServer().getInfo().getName())) {
            if (format && p.getServer().getInfo().getPlayers().size() >= Main.getConfig().getSection("automessages").getInt("minimum-players")) {
                for (String s : getBroadcastFormat()) {
                    if (s.contains("{text}")){
                        StringBuilder builder = new StringBuilder();
                        for (String broadc : brc) {
                            if (broadc.contains("/n")) {
                                builder.append(s.replace("{text}", "").replaceAll("&", "§") + broadc.replaceAll("&", "§"));
                            }
                            else {
                                builder.append(s.replace("{text}", "").replaceAll("&", "§") + broadc.replaceAll("&", "§") + " ");
                            }
                        }
                        for (String s1 : builder.toString().split("/n")) {
                            p.sendMessage(s1);
                        }
                    }
                    else {
                        p.sendMessage(s.replaceAll("&", "§"));
                    }
                }
            }
            else {
                for (String broadc : brc) {
                    p.sendMessage(broadc.replaceAll("&", "§").replace("{text}", ""));
                }
            }
        }
    }

}
