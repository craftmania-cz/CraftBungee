package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.commands.GHelp_command;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CooldownUpdateTask implements Runnable {

    @Override
    public void run() {
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            if (GHelp_command.cooldowns.containsKey(p)) {
                int cooldown = GHelp_command.cooldowns.get(p);
                GHelp_command.cooldowns.remove(p);
                GHelp_command.cooldowns.put(p, cooldown-1);

                if (cooldown == 0) {
                    GHelp_command.cooldowns.remove(p);
                }
            }
        }
    }
}