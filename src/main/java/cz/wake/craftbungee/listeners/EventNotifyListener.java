package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
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

    @EventHandler
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
                    announceMessage(eventType, reward, eventer);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void announceMessage(final String eventType, final String reward, final String eventer) {
        for (ProxiedPlayer p : plugin.getOnlinePlayers()) {
            p.sendMessage("");
            p.sendMessage("§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            p.sendMessage("");
            p.sendMessage("§a§lEvent brzy zacne!");
            if (eventType.equals("") && reward.equals("") && eventer.equals("")) {
                p.sendMessage("§fPozor! Na Event serveru brzo zacne celoserverovy event!");
            }
            if(!eventType.equals("")) {
                p.sendMessage("§7Typ: §f" + eventType);
            }
            if (!reward.equals("")) {
                p.sendMessage("§7Odmena: §f" + reward);
            }
            if (!eventer.equals("")) {
                p.sendMessage("§7Eventer: §f" + eventer);
            }
            p.sendMessage("");
            TextComponent textComponent = new TextComponent();
            textComponent.setText("§eKliknutim zde se pripojis na server!");
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klikni pro pripojeni!").create()));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/eventserver-tp"));
            p.sendMessage(textComponent);
            p.sendMessage("");
            p.sendMessage("§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            p.sendMessage("");
        }
    }

}
