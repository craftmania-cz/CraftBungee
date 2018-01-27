package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ChatColor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLChecker implements Runnable {

    @Override
    public void run() {
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola pripojeni do databaze.");
        try (Connection c = Main.getInstance().getSQLManager().getPool().getConnection()) {
            try {
                if (c == null || c.isClosed()) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Pripojeni do SQL je NULL!");
                    if (Main.getInstance().getSQLManager().getPool().getDataSource() != null) {
                        Main.getInstance().getSQLManager().getPool().getDataSource().close();
                    }
                    Main.getInstance().getSQLManager().getPool().setupPool();
                } else {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "Pripojeni do SQL je OK!");
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Pripojeni do SQL je NULL! Chyba:");
            ex.printStackTrace();
        }
    }
}
