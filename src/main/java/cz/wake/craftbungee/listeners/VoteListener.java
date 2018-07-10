package cz.wake.craftbungee.listeners;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.nifheim.beelzebu.coins.CoinsAPI;

import java.util.Random;
import java.util.logging.Level;

public class VoteListener implements Listener {

    @EventHandler
    public void onVote(final VotifierEvent e){

        ProxiedPlayer player;

        System.out.println("Spusten votifier event!");

        try {
            player = ProxyServer.getInstance().getPlayer(e.getVote().getUsername());
        } catch (Exception ex) {
            player = null;
        }

        if (player != null) {

            System.out.println("Hrac je na serveru...");

            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(player.getName()))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "Hrac " + player.getName() + " hlasoval driv nez za 2h.");
                return;
            }

            // Server na kterem je hrac
            String server = player.getServer().getInfo().getName();
            int coins = getChanceCoins(randRange(1, 100));
            for (String configServer : Main.getVoteServers()) {
                if (configServer.equalsIgnoreCase(server)) {
                    BungeeUtils.sendMessageToBukkit("vote",  player.getName(), String.valueOf(coins), player.getServer().getInfo());
                }
            }

            Main.getInstance().getSQLManager().addPlayerVote(player.getName());
            Main.getInstance().getSQLManager().addVoteToken(player.getName());

            if (CoinsAPI.isindb(player.getName())) {
                CoinsAPI.addCoins(player.getName(), (double) coins, false);
            }
        } else {
            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(e.getVote().getUsername()))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "Hrac " + e.getVote().getUsername() + " hlasoval driv nez za 2h.");
                return;
            }

            if (CoinsAPI.isindb(e.getVote().getUsername())) {
                CoinsAPI.addCoins(e.getVote().getUsername(), (double) 10, false);
            }

            Main.getInstance().getSQLManager().addPlayerVote(e.getVote().getUsername());
            Main.getInstance().getSQLManager().addVoteToken(e.getVote().getUsername());
            //todo: zapis zmen
        }


    }

    private int getChanceCoins(int chance) {
        if (chance == 1) { //1% sance
            return 200;
        } else if (chance <= 5 && chance >= 2) { //5% sance
            return 100;
        } else if (chance <= 25 && chance >= 6) { //25% sance
            return 50;
        } else {
            return 10;
        }
    }

    private static int randRange(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

}
