package cz.wake.craftbungee.managers.queue;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class QueueTask implements Runnable {

    private ProxiedPlayer previousPlayer;

    @Override
    public void run() {
        for (CraftQueue craftQueue : Main.queues) {
            if (!craftQueue.enoughPlayers()) return;
            java.util.Queue<ProxiedPlayer> queue = craftQueue.getQueue();
            for (int i = 0; i <= craftQueue.getMaxPerWave(); i++) {
                ProxiedPlayer chosenPlayer = queue.peek();
                if (previousPlayer == chosenPlayer) continue;
                previousPlayer = chosenPlayer;
                if (chosenPlayer == null) continue;
                craftQueue.connectPlayer(chosenPlayer);
            }
        }
    }
}
