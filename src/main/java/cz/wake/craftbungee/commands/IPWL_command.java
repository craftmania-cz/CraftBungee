package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class IPWL_command extends Command {

    Main plugin;

    public IPWL_command(Main pl) {
        super("craftbungee", "craftbungee.ipwl");
        this.plugin = pl;
    }
    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }

}
