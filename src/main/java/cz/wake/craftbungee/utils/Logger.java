package cz.wake.craftbungee.utils;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ChatColor;

import java.util.logging.Level;

public class Logger {

    public static void info(final String text) {
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "[CraftBungee] " + ChatColor.WHITE + text);
    }

    public static void success(final String text) {
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "[CraftBungee] " + ChatColor.GREEN + text);
    }

    public static void warning(final String text) {
        Main.getInstance().getLogger().log(Level.WARNING, ChatColor.YELLOW + "[CraftBungee] " + ChatColor.YELLOW + text);
    }

    public static void danger(final String text) {
        Main.getInstance().getLogger().log(Level.SEVERE, ChatColor.YELLOW + "[CraftBungee] " + ChatColor.RED + text);
    }
}
