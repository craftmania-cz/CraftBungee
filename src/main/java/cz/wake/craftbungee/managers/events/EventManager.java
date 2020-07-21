package cz.wake.craftbungee.managers.events;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;

public class EventManager implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase(Main.CRAFTEVENTS_CHANNEL)) return;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
            DataInputStream data = new DataInputStream(stream);
            String type = data.readUTF();
            switch (type.toLowerCase()) {
                case "announce": {
                    try {
                        String eventType = data.readUTF();
                        int reward = data.readInt();
                        String eventer = data.readUTF();
                        announceMessage(eventType, reward, eventer);
                        break;
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    private void announceMessage(final String eventType, final int reward, final String eventer) {
        Logger.info("Announcing event (" + eventType + "), reward: " + reward + ", eventer: " + eventer);
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            p.sendMessage("");
            p.sendMessage("§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            p.sendMessage("");
            p.sendMessage("§c§lEvent brzy začne!");
            //p.sendMessage("§fPozor! Na Event serveru brzo začne celoserverový event!");
            p.sendMessage("§7Typ: §f" + eventType + "§7, odměna: §f" + reward + " EP");
            p.sendMessage("§7Eventer: §f" + eventer);
            p.sendMessage("");
            TextComponent textComponent = new TextComponent();
            textComponent.setText("§eKliknutím zde se připojíš na server!");
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro připojení!").create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/eventserver-tp"));
            p.sendMessage(textComponent);
            p.sendMessage("");
            p.sendMessage("§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            p.sendMessage("");
        }
    }

}
