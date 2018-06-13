package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
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
import java.util.regex.Pattern;

public class VPNListener implements Listener {

    private static List<Pattern> allowedIps = new ArrayList<>();

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

        // Ignorovani ceskych a slovensky VPN
        // Kvuli tomu, ze maly poskytovatele (zvlaste na slovensku) maji mene IP, takze je to detekuje jako VPN.
        if (Main.isBlockCountry()) {
            if (state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK")) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP je z CZ/SK kraje, hrac pusten na server.");
                return;
            }
        }

        // Kontrola whitelisted IPs
        if (!allowedIps.isEmpty()) {
            for (Pattern pattern : allowedIps) {
                Matcher matcher = pattern.matcher(address);
                if (matcher.find()) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP nalezena ve whitelistu!");
                    return;
                }
            }
        }

        // Pokud true, tak ma VPN
        if (validCheck) {
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni VPN/Proxy (IP: " + address + "). Hrac zablokovan!");
            e.setCancelReason("§c§lDetekce VPN!\n§fTvoje IP byla detekovana jako VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
            e.setCancelled(true);
            return;
        }

        // Jinak je z zahranici...
        e.setCancelReason("§c§lDetekce zahranicni IP!\n§fTvoje IP je zahranicni.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
        e.setCancelled(true);
    }

    public static void setAllowedIps(List<Pattern> allowedIps) {
        VPNListener.allowedIps = allowedIps;
    }

    public static List<Pattern> getAllowedIps() {
        return allowedIps;
    }
}
