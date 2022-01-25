package cz.wake.craftbungee.listeners;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Random;
import java.util.logging.Level;

public class VoteListener implements Listener {

    @EventHandler
    public void onVote(final VotifierEvent e) {

        ProxiedPlayer player;

        try {
            player = ProxyServer.getInstance().getPlayer(e.getVote().getUsername());
        } catch (Exception ex) {
            player = null;
        }

        int coins = getChanceCoins(randRange(1, 100));
        int votetokens = Main.getConfig().getInt("votetokens-per-vote");

        if (player != null) {

            System.out.println("Hrac je na serveru...");
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "Zpracování hlasu pro: " + player.getName());

            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(player.getName()))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "Hrac " + player.getName() + " hlasoval driv nez za 2h.");
                return;
            }

            // Server na kterem je hrac
            String server = player.getServer().getInfo().getName();
            for (String configServer : Main.getVoteServers()) {
                if (configServer.equalsIgnoreCase(server)) {
                    BungeeUtils.sendMessageToBukkit("vote", player.getName(), String.valueOf(coins), String.valueOf(votetokens), player.getServer().getInfo());
                } else { // Hráč je na serveru, kde není votifier
                    this.addOfflineVotes(e.getVote().getUsername(), votetokens, coins);
                }
            }
        } else {
            if (!(System.currentTimeMillis() > Main.getInstance().getSQLManager().getLastVote(e.getVote().getUsername()))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.AQUA + "Hrac " + e.getVote().getUsername() + " hlasoval driv nez za 2h.");
                return;
            }

            // Kdyz je offline force to DB (obejit CraftEconomy)
            this.addOfflineVotes(e.getVote().getUsername(), votetokens, coins);
        }
    }

    private int getChanceCoins(int chance) {
        if (chance == 1) { //1% sance
            return 100;
        } else if (chance <= 5 && chance >= 2) { //5% sance
            return 50;
        } else if (chance <= 25 && chance >= 6) { //25% sance
            return 25;
        } else {
            return 10;
        }
    }

    private static int randRange(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    private void addOfflineVotes(String nick, int voteTokens, int coins) {
        Main.getInstance().getSQLManager().addPlayerVote(nick);
        Main.getInstance().getSQLManager().addVoteToken(nick, voteTokens);
        Main.getInstance().getSQLManager().addVoteToken2(nick, voteTokens);
        Main.getInstance().getSQLManager().addCraftCoins(nick, coins);
    }

}
