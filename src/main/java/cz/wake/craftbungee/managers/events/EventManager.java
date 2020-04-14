package cz.wake.craftbungee.managers.events;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.util.concurrent.TimeUnit;

public class EventManager implements Listener {

    public static Event event = new Event();

    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase(Main.CRAFTEVENTS_CHANNEL)) return;
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
            DataInputStream data = new DataInputStream(stream);
            String eventStateString = data.readUTF();
            Event.EventState eventState = Event.EventState.valueOf(eventStateString);

            event.setEventState(eventState);

            Logger.info("Updating event...");
            // Event is selected
            try {
                String name = String.copyValueOf(data.readUTF().toCharArray());
                String category = String.copyValueOf(data.readUTF().toCharArray());
                int reward = Integer.parseInt(String.copyValueOf(data.readUTF().toCharArray()));

                event.setName(name);
                event.setCategory(category);
                event.setReward(reward);
            } catch (EOFException er) {
                // No other values
                Logger.warning("Null event.");
                event.setName(null);
                event.setCategory(null);
                event.setReward(0);
            }

            data.close();
            stream.close();
        } catch (Exception er) {
            er.printStackTrace();
        }
        Logger.info(event.toString());
    }

}
