package cz.wake.craftbungee.commands;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.BungeeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

public class AT_command extends net.md_5.bungee.api.plugin.Command {

    Main plugin;

    public AT_command(Main plugin) {
        super("at");
        this.plugin = plugin;
    }
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        ArrayList<ProxiedPlayer> players = new ArrayList<>();
        ArrayList<ProxiedPlayer> majitel = new ArrayList<>();
        ArrayList<ProxiedPlayer> vedeni = new ArrayList<>();
        ArrayList<ProxiedPlayer> developer = new ArrayList<>();
        ArrayList<ProxiedPlayer> admin = new ArrayList<>();
        ArrayList<ProxiedPlayer> helper = new ArrayList<>();
        ArrayList<ProxiedPlayer> eventer = new ArrayList<>();
        ArrayList<ProxiedPlayer> builder = new ArrayList<>();
        ProxiedPlayer sender = (ProxiedPlayer) commandSender;

        sender.sendMessage("§r");
        sender.sendMessage("§7§l§m--------§r§7[ §e§lSeznam clenu Admin Teamu online §7]§m--------");
        sender.sendMessage("§r");
        sender.sendMessage("    §7§oStaci najet na nick clena a uvidis na jakem serveru je.");
        sender.sendMessage("§r");

        for (ProxiedPlayer p : plugin.getOnlinePlayers()) {
            if (BungeeUtils.getGroupBool(p)) {

                String group = BungeeUtils.getGroup(p);

                if (group.contains("Majitel")) { majitel.add(p); players.add(p); }
                if (group.contains("Vedeni")) { vedeni.add(p); players.add(p); }
                if (group.contains("Developer")) { developer.add(p); players.add(p); }
                if (group.contains("Admin")) { admin.add(p); players.add(p); }
                if (group.contains("Helper")) { helper.add(p); players.add(p); }
                if (group.contains("Eventer")) { eventer.add(p); players.add(p); }
                if (group.contains("Builder")) { builder.add(p); players.add(p); }

            }
        }

        jsonMessage(sender, majitel, "§3§lMajitel");
        jsonMessage(sender, vedeni, "§4§lVedeni");
        jsonMessage(sender, developer, "§e§lDeveloper");
        jsonMessage(sender, admin, "§c§lAdmin");
        jsonMessage(sender, eventer,  "§d§lEventer");
        jsonMessage(sender, helper, "§2§lHelper");
        jsonMessage(sender, builder, "§5§lBuilder");

        sender.sendMessage("§r");
        if (players.size() == 0) {
            sender.sendMessage(" §7§lAktualne jsou vsichni cleni AT offline :(");
        } else if (players.size() == 1){
            sender.sendMessage(" §a§lAktualne je pripojen " + players.size() + " clen AT.");
        }
        else if (players.size() >= 2 && players.size() <= 4) {
            sender.sendMessage(" §a§lAktualne jsou pripojeni " + players.size() + " cleni AT.");
        }
        else {
            sender.sendMessage(" §a§lAktualne je pripojeno " + players.size() + " clenu AT.");
        }
        sender.sendMessage("§r");
        sender.sendMessage("§7§l§m--------§r§7[ §e§lSeznam clenu Admin Teamu online §7]§m--------");
    }


    public void jsonMessage(ProxiedPlayer receiver, ArrayList<ProxiedPlayer> players, String group) {
        if (players.size() != 0) {
            ArrayList<TextComponent> components = new ArrayList<>();
            TextComponent component = null;

            for (int x=1; x<=players.size(); x++) { //TODO: Prejmenovat nejak normalne...
                if (players.size() > x) {
                    component = new TextComponent("§7" + players.get(x-1) + ", ");
                }
                else if (players.size() == x) {
                    component = new TextComponent("§7" + players.get(x-1).getName());
                }
                component.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Pripojen na §f" + BungeeUtils.getServer(players.get(x-1))).create() ) );
                if (BungeeUtils.getGroupBool(receiver)) {
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + players.get(x-1).getServer().getInfo().getName()));
                }
                components.add(component);
            }

            TextComponent serazeni = new TextComponent();
            serazeni.addExtra(new TextComponent(group + "§7: §7"));
            for (TextComponent componenttt : components) {
                serazeni.addExtra(componenttt);
            }
            receiver.sendMessage(serazeni);
            players.clear();
        }

    }

}
