package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.WhitelistedIP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class VPNListener implements Listener {

    private static List<WhitelistedIP> allowedIps = new ArrayList<>();

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.vpnblocker.net/v2/json/" + address + "/" + Main.getAPIKey()).build();

        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());

            boolean vpn;
            String countryCode;

            vpn = (boolean) json.get("host-ip");

            JSONObject countyObject = json.getJSONObject("country");
            countryCode = (String) countyObject.get("code"); // cz, sk atd.

            // Finalni kontrola IP
            finalCheck(e, address, countryCode, vpn);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void finalCheck(PreLoginEvent e, String address, String state, boolean validCheck) {

        // Hrac ma CZ / SK IP
        if(Main.allowOnlyCZSK()) {
            if(state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK")) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP " + address + " je z CZ/SK kraje, hrac pusten na server.");
                return;
            }

            // Hráč nema CZ / SK IP
            // Kontrola whitelisted IPs
            if(!allowedIps.isEmpty()) {
                for(WhitelistedIP ip : allowedIps) {
                    if(ip.getAddress().equals(address)) {
                        Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP " + address + " nalezena ve whitelistu, hrac pusten na server. Duvod: " + ip.getDescription());
                        return;
                    }
                }
            }

            // Hráč není na IP whitelistu a nema CZ / SK IP
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni IP / VPN (IP: " + address + "). Hrac zablokovan!");
            e.setCancelReason("§c§lTvá IP Nepochází z České nebo Slovenské republiky!\n§fTvoje IP pochází z jiného státu, než je česká nebo slovenská republika.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
            e.setCancelled(true);
        }
    }

    public static void setAllowedIps(List<WhitelistedIP> allowedIps) {
        VPNListener.allowedIps = allowedIps;
    }

    public static List<WhitelistedIP> getAllowedIps() {
        return allowedIps;
    }
}
