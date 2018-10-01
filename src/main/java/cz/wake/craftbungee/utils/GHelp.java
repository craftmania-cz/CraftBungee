package cz.wake.craftbungee.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Calendar;

public class GHelp {

    public static ArrayList<GHelp> helps = new ArrayList<>();

    private Integer id;
    private ProxiedPlayer p;
    private String message;
    private long ms;

    public GHelp(Integer id, ProxiedPlayer p, String message, long ms) {
        this.id = id;
        this.p = p;
        this.message = message;
        this.ms = ms;
    }

    public String getMessage() {
        return message;
    }

    public Integer getId() {
        return id;
    }

    public ProxiedPlayer getPlayer() {
        return p;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPlayer(ProxiedPlayer p) {
        this.p = p;
    }

    public void setLong(long ms) {
        this.ms = ms;
    }

    public long getLong() {
        return ms;
    }

    public static GHelp getGHelpById(Integer id) {
        for (GHelp ghelp : helps) {
            if (ghelp.getId() == id) {
                return ghelp;
            }
        }
        return null;
    }


    public static void saveGhelp(ProxiedPlayer p, String zprava) {
        int size = helps.size();

        if (size == 10) {
            ArrayList<GHelp> ghelps = new ArrayList<>(helps);

            helps.clear();

            for (GHelp ghelp : ghelps) {
                if (ghelp.getId() != 1) {
                    helps.add(new GHelp(ghelp.getId() -1, p, ghelp.getMessage(), ghelp.getLong()));
                }
            }
        }
        helps.add(new GHelp(GHelp.helps.size() +1, p, zprava, System.currentTimeMillis()));
    }
}