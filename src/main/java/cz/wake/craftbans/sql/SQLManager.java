package cz.wake.craftbans.sql;

import com.zaxxer.hikari.HikariDataSource;
import cz.wake.craftbans.Main;

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
}
