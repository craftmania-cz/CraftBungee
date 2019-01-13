package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedUUID;
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
import java.util.regex.Matcher;

public class VPNListener implements Listener {

    private static List<WhitelistedIP> allowedIps = new ArrayList<>();
    private static List<WhitelistedUUID> allowedUUIDs = new ArrayList<>();

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();
        final String uuid = /*e.getConnection().getUniqueId().toString()*/ "NEEXISTUJE";
        final String name = e.getConnection().getName();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);
        Logger.info("Whitelist Checker - [name=" + name + ", uuid=null, address=" + address + "]");

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
            finalCheck(e, address, uuid, countryCode, vpn);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    private void finalCheck(PreLoginEvent e, String address, String uuid, String state, boolean isVPN) {

        // Kdyz je povoleno jenom CZ/SK tak se vše kontroluje, jinak jsou kontroly off!
        if(Main.allowOnlyCZSK()) {

            // Hrac ma CZ / SK IP
            if(state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK")) {
                if(isVPN){
                    if (isOnWhitelist(address)) {
                        return;
                    }
                    Logger.danger("IP " + address + " je z CZ/SK, ale je to VPN, hrac nebyl pusten na server");
                    e.setCancelReason("§c§lTato IP je vedena jako VPN.\n§fPokud si myslis, ze to tak neni, napis\n§fna webu nebo na Discordu uzivateli §e§lMrWakeCZ §rnebo §e§lKrosta8");
                    e.setCancelled(true);
                    return;
                }

                Logger.success("IP " + address + " je z CZ/SK, hrac pusten na server.");
                return;
            }

            // Hráč nema CZ / SK IP
            // Kontrola whitelisted IPs
            if (isOnWhitelist(address)) {
                return;
            }

            // Kontrola UUID na whitelistu
            /*if(!allowedUUIDs.isEmpty()) {
                for(WhitelistedUUID id : allowedUUIDs) {
                    Matcher matcher = id.getUUID().matcher(uuid);
                    if(matcher.find()) {
                        Logger.success("UUID " + uuid + " nalezeno ve whitelistu, hrac pusten na server. Duvod: " + id.getDescription());
                        return;
                    }
                }
            }*/

            // Hráč není na IP whitelistu a nema CZ / SK IP
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni IP / VPN (IP: " + address + ", NICK: " + e.getConnection().getName() + "). Hrac zablokovan!");
            e.setCancelReason("§c§lTva IP dle overeni nepochazi z CZ/SK.\n§fPokud si myslis, ze to tak neni, napis\n§fna webu nebo na Discordu uzivateli §e§lMrWakeCZ §rnebo §e§lKrosta8");
            e.setCancelled(true);
        }
    }

    private boolean isOnWhitelist(String address) {
        if(!allowedIps.isEmpty()) {
            for(WhitelistedIP ip : allowedIps) {
                Matcher matcher = ip.getAddress().matcher(address);
                if(matcher.find()) {
                    Logger.success("IP " + address + " nalezena ve whitelistu, hrac pusten na server. Duvod: " + ip.getDescription());
                    return true;
                }
            }
        }
        return false;
    }

    public static void setAllowedIps(List<WhitelistedIP> allowedIps) {
        VPNListener.allowedIps = allowedIps;
    }

    public static List<WhitelistedIP> getAllowedIps() {
        return allowedIps;
    }

    public static void setAllowedUUIDs(List<WhitelistedUUID> allowedUUIDs) {
        VPNListener.allowedUUIDs = allowedUUIDs;
    }

    public static List<WhitelistedUUID> getAllowedUUIDs() {
        return allowedUUIDs;
    }
}
