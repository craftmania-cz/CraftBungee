package cz.wake.craftbans.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbans.Main;

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
        host = plugin.getConfig().getString("sql.hostname");
        port = plugin.getConfig().getString("sql.port");
        database = plugin.getConfig().getString("sql.database");
        username = plugin.getConfig().getString("sql.username");
        password = plugin.getConfig().getString("sql.password");
        minimumConnections = plugin.getConfig().getInt("settings.minimumConnections");
        maximumConnections = plugin.getConfig().getInt("settings.maximumConnections");
        connectionTimeout = plugin.getConfig().getInt("settings.timeout");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException
    {
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
}
