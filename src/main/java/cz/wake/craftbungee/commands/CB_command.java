package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CB_command extends Command {

    Main plugin;

    public CB_command(Main pl) {
        super("craftbungee", "craftbungee.reload");
        this.plugin = pl;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            return;
        }

        if (strings[0].contains("reload")) {
            Main.getInstance().reloadConfig();
            commandSender.sendMessage("§cConfig reloadnut!");
        }
    }
}