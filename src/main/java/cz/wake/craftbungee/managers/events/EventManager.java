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

    public static Event event = new Event();

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase(Main.CRAFTEVENTS_CHANNEL)) return;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
            DataInputStream data = new DataInputStream(stream);
            String type = data.readUTF();
            if (type.equalsIgnoreCase("announce")) {
                try {
                    String eventType = data.readUTF();
                    String reward = data.readUTF();
                    String eventer = data.readUTF();
                    announceMessage(eventType, reward, eventer);

                } catch (Exception err) {
                    err.printStackTrace();
                }
                return;
            }
            try {
                Event.EventState eventState = Event.EventState.valueOf(type);

                event.setEventState(eventState);

                Logger.info("Updating event...");
                // Event is selected
                try {
                    String name = data.readUTF();
                    String category = data.readUTF();
                    int reward = Integer.parseInt(data.readUTF());

                    event.setName(name);
                    event.setCategory(category);
                    event.setReward(reward);
                } catch (Exception er) {
                    // No other values
                    Logger.warning("Null event.");
                    event.setName(null);
                    event.setCategory(null);
                    event.setReward(0);
                }

                data.close();
                stream.close();
            } catch (IllegalArgumentException ignored) {
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
        Logger.info(event.toString());
    }

    private void announceMessage(final String eventType, final String reward, final String eventer) {
        for (ProxiedPlayer p : Main.getInstance().getOnlinePlayers()) {
            p.sendMessage("");
            p.sendMessage("§b\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac\u25ac");
            p.sendMessage("");
            p.sendMessage("§e§l[*] §e§lEvent brzy začne!");
            if (eventType.equals("") && reward.equals("") && eventer.equals("")) {
                p.sendMessage("§fPozor! Na Event serveru brzo začne celoserverový event!");
            }
            if (!eventType.equals("")) {
                p.sendMessage("§7Typ: §f" + eventType);
            }
            if (!reward.equals("")) {
                p.sendMessage("§7Odměna: §f" + reward);
            }
            if (!eventer.equals("")) {
                p.sendMessage("§7Eventer: §f" + eventer);
            }
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
