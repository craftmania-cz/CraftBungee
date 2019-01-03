package cz.wake.craftbungee.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedUUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
                ps = conn.prepareStatement("UPDATE player_profile SET last_server = ?, last_online = ?, is_online = ? WHERE nick = '" + p.getName() + "';");
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

    public final void updateTime(final ProxiedPlayer p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), () -> {
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = pool.getConnection();
                ps = conn.prepareStatement("UPDATE player_profile SET played_time = played_time + 1, last_server = ? WHERE nick = ?;");
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
                ps = conn.prepareStatement("INSERT INTO ip_whitelist (address, description) VALUES (?, ?);");
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
                ps = conn.prepareStatement("DELETE FROM ip_whitelist WHERE address = ?;");
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
            ps = conn.prepareStatement("SELECT * FROM ip_whitelist;");
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

    public final List<WhitelistedUUID> getWhitelistedUUIDs() {
        List<WhitelistedUUID> whitelistedUUIDS = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM uuid_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                whitelistedUUIDS.add(new WhitelistedUUID(Pattern.compile(ps.getResultSet().getString("uuid")), ps.getResultSet().getString("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return whitelistedUUIDS;
    }

    public final long getLastVote(final String p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT last_vote FROM player_profile WHERE nick = ?;");
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
                    ps = conn.prepareStatement("UPDATE player_profile SET total_votes = total_votes + 1, week_votes = week_votes + 1, month_votes = month_votes + 1, last_vote = '" + String.valueOf(System.currentTimeMillis() + 3600000L) + "' WHERE last_name = '" + p + "';");
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
                ps = conn.prepareStatement("UPDATE player_profile SET votetokens = votetokens + 1 WHERE nick = ?;");
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
                ps = conn.prepareStatement("UPDATE player_profile SET craftcoins = craftcoins + " + coins + " WHERE nick = ?;");
                ps.setString(1, p);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.close(conn, ps, null);
            }
        });
    }
}
