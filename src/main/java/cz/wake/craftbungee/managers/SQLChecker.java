package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLChecker implements Runnable {

    @Override
    public void run() {
        System.out.println("[CraftBungee] Kontrola pripojeni do databaze.");
        try (Connection c = Main.getInstance().getSQLManager().getPool().getConnection()) {
            try {
                if (c == null || c.isClosed()) {
                    System.out.println("[CraftBans] Pripojeni do SQL je NULL!");
                    if (Main.getInstance().getSQLManager().getPool().getDataSource() != null) {
                        Main.getInstance().getSQLManager().getPool().getDataSource().close();
                    }
                    Main.getInstance().getSQLManager().getPool().setupPool();
                } else {
                    System.out.println("[CraftBans] Pripojeni do SQL je OK!");
                }
            } finally {
                c.close();
            }
        } catch (SQLException ex) {
            System.out.println("[CraftBans] Pripojeni do SQL je NULL! Chyba:");
            ex.printStackTrace();
        }
    }
}
