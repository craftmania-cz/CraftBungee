package cz.wake.craftbungee.listeners;

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

/*
    Oznameni o startu eventu
    1. announce, 2. eventType, 3. reward, 4. eventer
 */
public class EventNotifyListener implements Listener {

    private Main plugin;

    public EventNotifyListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = 5)
    public void onPluginMessage(PluginMessageEvent message) {
        if(!message.getTag().equals(Main.CRAFTEVENTS_CHANNEL)) {
            return;
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(message.getData());
            DataInputStream data = new DataInputStream(stream);
            String type = data.readUTF();
            switch (type) {
                case "announce":
                    String eventType = data.readUTF();
                    String reward = data.readUTF();
                    String eventer = data.readUTF();
                    announceMessage(eventType, Integer.parseInt(reward), eventer);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void announceMessage(final String eventType, final int reward, final String eventer) {
        Logger.info("Announcing event (" + eventType + "), reward: " + reward + ", eventer: " + eventer);
        for (ProxiedPlayer p : Main.getInstance().getProxy().getPlayers()) {
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
