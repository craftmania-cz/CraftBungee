package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.listeners.VPNListener;
import cz.wake.craftbungee.utils.WhitelistedIP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.logging.Level;

public class IPWL_command extends Command {

    Main plugin;

    public IPWL_command(Main pl) {
        super("ipwl", "craftbungee.ipwl");
        this.plugin = pl;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ProxiedPlayer p = plugin.getProxy().getPlayer(commandSender.getName());

        if (strings.length < 1) {
            p.sendMessage("§eSeznam vsech IP Adres na whitelistu");
            for (WhitelistedIP ip : VPNListener.getAllowedIps()) {
                p.sendMessage("§eIP: §f" + ip.getAddress() + " §8| §eDuvod: §f" + ip.getDescription());
            }
            return;
        }

        if (strings[0].equals("add")) {
            if (strings.length < 3) {
                p.sendMessage("§c§l(!) §cNespravne napsane argumenty! Pr. /ipwl add 1.1.1.1 Testovaci Zprava");
                return;
            }

            for (WhitelistedIP ip : Main.getInstance().getSQLManager().getWhitelistedIPs()) {
                if (strings[1].equals(ip.getAddress())) {
                    p.sendMessage("§c§l(!) §cIP Adresa je davno pridana!");
                    return;
                }
            }
            String description = strings[2];

            for (int i = 3 ; i != strings.length ; i++){
                description += " " + strings[i];
            }
            Main.getInstance().getSQLManager().addWhitelistedIP(strings[1], description);
            List<WhitelistedIP> ips = VPNListener.getAllowedIps();
            ips.add(new WhitelistedIP(strings[1], description));
            VPNListener.setAllowedIps(ips);
            p.sendMessage("§aAdresa " + strings[1] + " byla uspesne pridana!");
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "IP adresa " + strings[1] + " byla pridana na ip whitelist");
        }

        if (strings[0].equals("remove")) {
            if (strings.length < 2) {
                p.sendMessage("§c§l(!) §cNespravne napsane argumenty! Pr. /ipwl remove 1.1.1.1");
                return;
            }

            for (WhitelistedIP ip : Main.getInstance().getSQLManager().getWhitelistedIPs()) {
                if (strings[1].equals(ip.getAddress())) {
                    Main.getInstance().getSQLManager().removeWhitelistedIP(strings[1]);
                    List<WhitelistedIP> ips = VPNListener.getAllowedIps();
                    WhitelistedIP whitelistedIP;
                    for(WhitelistedIP w : VPNListener.getAllowedIps()){
                        if(w.getAddress().equals(strings[1])){
                            whitelistedIP = w;
                            ips.remove(whitelistedIP);
                            VPNListener.setAllowedIps(ips);
                            p.sendMessage("§aAdresa " + strings[1] + " byla uspesne odebrana!");
                            Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "IP adresa " + strings[1] + " byla odebrana z ip whitelistu");
                            return;
                        }
                    }
                }
            }
            p.sendMessage("§c§l(!) §cIP Adresa nebyla nalezena!");
        }
    }
}