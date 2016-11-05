package cz.wake.craftbans.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbans.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public final boolean hasData(final ProxiedPlayer p) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM craftbans_players WHERE player = '" + p.toString() + "';");
            ps.executeQuery();
            return ps.getResultSet().next();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public final void createRecord(final ProxiedPlayer p) {
        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                PreparedStatement ps = null;
                try {
                    conn = pool.getConnection();
                    ps = conn.prepareStatement("INSERT INTO craftbans_players (player, uuid, ip, firstlogin, lastlogin, lastserver) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE ip = ?, lastlogin = ?;");
                    ps.setString(1, p.getName());
                    ps.setString(2, p.getUUID());
                    ps.setString(3, p.getAddress().getAddress().getHostAddress());
                    ps.setLong(4, System.currentTimeMillis());
                    ps.setString(7, p.getAddress().getAddress().getHostAddress());
                    ps.setLong(8, System.currentTimeMillis());
                    ps.executeUpdate();
                } catch (SQLException e){
                    e.printStackTrace();
                } finally {
                    pool.close(conn, ps, null);
                }
            }
        });
    }
}
