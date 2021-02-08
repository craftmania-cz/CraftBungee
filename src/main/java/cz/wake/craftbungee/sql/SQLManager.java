package cz.wake.craftbungee.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.objects.BlacklistedASN;
import cz.wake.craftbungee.utils.BungeeUtils;
import cz.wake.craftbungee.objects.WhitelistedIP;
import cz.wake.craftbungee.objects.WhitelistedNames;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SQLManager {

    private final Main plugin;
    private final ConnectionPoolManager pool;
    private HikariDataSource dataSource;

    public SQLManager(Main plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
    }

    public void onDisable() {
        pool.closePool();
    }

    public ConnectionPoolManager getPool() {
        return pool;
    }

    public final void updateStats(final ProxiedPlayer p, final boolean online) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET last_server = ?, last_online = ?, is_online = ? WHERE nick = '" + p.getName() + "';");
                ps.setString(1, BungeeUtils.getPlayerServer(p));
                ps.setLong(2, System.currentTimeMillis());
                if (online) {
                    ps.setString(3, "1");
                } else {
                    ps.setString(3, "0");
                }
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void updateMCVersion(final ProxiedPlayer p, int protocolVersion) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET mc_version = ? WHERE nick = ?;");
                ps.setString(1, protocolVersion + "");
                ps.setString(2, p.getName());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void updateTime(final ProxiedPlayer p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET played_time = played_time + 1, last_server = ? WHERE nick = ?;");
                ps.setString(1, BungeeUtils.getPlayerServer(p));
                ps.setString(2, p.getName());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void addWhitelistedIP(final String address, final String description) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("INSERT INTO minigames.ip_whitelist (address, description) VALUES (?, ?);");
                ps.setString(1, address);
                ps.setString(2, description);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void removeWhitelistedIP(final String address) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("DELETE FROM minigames.ip_whitelist WHERE address = ?;");
                ps.setString(1, address);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final List<WhitelistedIP> getWhitelistedIPs() {
        List<WhitelistedIP> whitelistedIPS = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM minigames.ip_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedIPS.add(new WhitelistedIP(Pattern.compile(ps.getResultSet().getString("address")), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedIPS;
    }

    public final List<WhitelistedNames> getWhitelistedNames() {
        List<WhitelistedNames> whitelistedNames = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM minigames.name_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedNames.add(new WhitelistedNames(ps.getResultSet().getString("nick"), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedNames;
    }

    public final List<String> getAllowedBlacklistedNames() {
        List<String> allowedNames = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM minigames.allowed_blacklisted_names;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                allowedNames.add(ps.getResultSet().getString("nick"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return allowedNames;
    }

    public final List<BlacklistedASN> getBlacklistedASNs() {
        List<BlacklistedASN> allowedNames = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM minigames.blacklisted_asns;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                allowedNames.add(new BlacklistedASN(ps.getResultSet().getString("asn")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return allowedNames;
    }

    public final List<String> getBlacklistedNameWords() {
        List<String> blacklistedWords = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM minigames.blacklisted_name_words;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                blacklistedWords.add(ps.getResultSet().getString("word"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return blacklistedWords;
    }

    public final long getLastVote(final String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT last_vote FROM minigames.player_profile WHERE nick = ?;");
            ps.setString(1, p);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getLong("last_vote");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return 0L;
    }


    public final void addPlayerVote(final String p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                    conn = pool.getConnection();
                    ps = conn.prepareStatement("UPDATE minigames.player_profile SET total_votes = total_votes + 1, week_votes = week_votes + 1, month_votes = month_votes + 1, last_vote = '" + System.currentTimeMillis() + "' WHERE nick = '" + p + "';");
                    ps.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pool.close(conn, ps, null);
                }
        });
    }

    public final void addVoteToken(final String p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET votetokens = votetokens + 1 WHERE nick = ?;");
                ps.setString(1, p);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void addVoteToken2(final String p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET votetokens_2 = votetokens_2 + 1 WHERE nick = ?;");
                ps.setString(1, p);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final void addCraftCoins(final String p, final int coins) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE minigames.player_profile SET craftcoins = craftcoins + " + coins + " WHERE nick = ?;");
                ps.setString(1, p);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }

    public final String getConfigValue(final String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT value FROM minigames.craftbungee_config WHERE name = ?;");
            ps.setString(1, name);
            ps.executeQuery();
            if (ps.getResultSet().next()) {
                return ps.getResultSet().getString("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return "";
    }

    public final void createNotesTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("create table if not exists notes_data\n" +
                    "(\n" +
                    "    id     int auto_increment\n" +
                    "        primary key,\n" +
                    "    player varchar(32)                          not null,\n" +
                    "    note   longtext                             not null,\n" +
                    "    admin  varchar(32)                          null,\n" +
                    "    date   datetime default current_timestamp() null\n" +
                    ");\n" +
                    "\n");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public boolean hasNotes(String player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM bungeecord.notes_data WHERE player = ?;");
            ps.setString(1, player);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return false;
    }

    public boolean isIPBanned(String ipAddress) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM bungeecord.`litebans_bans` WHERE ip = ? AND `active` = 1;");
            ps.setString(1, ipAddress);
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return false;
    }
}
