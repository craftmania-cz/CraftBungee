package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;
import cz.wake.craftbungee.utils.WhitelistedIP;
import cz.wake.craftbungee.utils.WhitelistedNames;
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
    private static List<WhitelistedNames> allowedNames = new ArrayList<>();

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();
        final String name = e.getConnection().getName();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);
        Logger.info("Whitelist Checker - [name=" + name + ", address=" + address + "]");

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("https://proxycheck.io/v2/" + address + "?key=" + Main.getAPIKey()
            + "&vpn=1&asn=1&node=1&time=1&inf=0&risk=1&port=1&seen=1&days=7&tag=Bungeecord").build();

        try {

            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONObject adressInfo = json.getJSONObject(address);

            boolean vpn = false;
            String countryCode = null;

            if (adressInfo.has("proxy")) {
                if (adressInfo.get("proxy").equals("yes")) { // Kdyz je proxy true, tak se jedná o VPN/Proxy.
                    vpn = true;
                }
            }

            if (adressInfo.get("isocode") == JSONObject.NULL) { // Nezjistitelna IP?
                vpn = true;
            }

            if (adressInfo.has("isocode")) {
                countryCode = (String) adressInfo.get("isocode");
            }

            // Finalni kontrola IP
            finalCheck(e, address, name, countryCode, vpn);

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    private void finalCheck(PreLoginEvent e, String address, String name, String state, boolean isVPN) {

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

            // Kontrola nicku na whitelistu
            if (isOnNameWhitelist(name)) {
                return;
            }

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

    private boolean isOnNameWhitelist(String playerName) {
        if (!allowedNames.isEmpty()) {
            for (WhitelistedNames name : allowedNames) {
                if (name.getName().equals(playerName)) {
                    Logger.success("Nick " + playerName + " nalezena ve whitelistu, hrac pusten na server. Duvod: " + name.getDescription());
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

    public static void setAllowedNames(List<WhitelistedNames> allowedNames) {
        VPNListener.allowedNames = allowedNames;
    }

    public static List<WhitelistedNames> getAllowedNames() {
        return allowedNames;
    }
}
