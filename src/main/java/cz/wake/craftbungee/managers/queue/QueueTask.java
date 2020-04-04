package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class QueueTask implements Runnable {

    @Override
    public void run() {
        for (CraftQueue craftQueue : Main.queues) {
            if (!craftQueue.enoughPlayers()) return;
            java.util.Queue<ProxiedPlayer> queue = craftQueue.getQueue();
            for (int i = 0; i <= craftQueue.getMaxPerWave(); i++) {
                ProxiedPlayer chosenPlayer = queue.poll();
                if (chosenPlayer == null) continue;
                if (!chosenPlayer.isConnected()) continue;
                craftQueue.connectPlayer(chosenPlayer, false);
                Logger.info("Connecting queued player '" + chosenPlayer.getDisplayName() + "' to '" + craftQueue.getServerName() + "'.");
            }
        }
    }
}
