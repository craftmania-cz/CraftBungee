package cz.wake.craftbungee.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbungee.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private Main plugin;

    private HikariDataSource dataSource;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    public ConnectionPoolManager(Main plugin){
        this.plugin = plugin;
        init();
        setupPool();
    }

    private void init() {
        host = Main.getConfig().getString("sql.hostname");
        port = Main.getConfig().getString("sql.port");
        database = Main.getConfig().getString("sql.database");
        username = Main.getConfig().getString("sql.username");
        password = Main.getConfig().getString("sql.password");
        minimumConnections = Main.getConfig().getInt("settings.minimumConnections");
        maximumConnections = Main.getConfig().getInt("settings.maximumConnections");
        connectionTimeout = Main.getConfig().getLong("settings.timeout");
    }

    public void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("encoding", "UTF-8");
        config.addDataSourceProperty("useUnicode", "true");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setMaxLifetime(60000);
        config.setIdleTimeout(30000);
        config.setLeakDetectionThreshold(30000);
        config.validate();
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
