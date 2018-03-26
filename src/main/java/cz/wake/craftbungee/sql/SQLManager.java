package cz.wake.craftbungee.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
                if(online){
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

    public final List<String> getWhitelistedIPs() {
        List<String> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM ip_whitelist;");
            ps.executeQuery();
            while (ps.getResultSet().next()) {
                list.add(ps.getResultSet().getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
        return list;
    }

}
