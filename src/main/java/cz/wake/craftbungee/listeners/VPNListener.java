package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPNListener implements Listener {

    private static List<Pattern> allowedIps = new ArrayList<>();

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);

        HttpURLConnection localHttpURLConnection = null;
        Scanner localScanner = null;

        boolean vpn;
        try {
            URL localURL = new URL("http://api.vpnblocker.net/v2/json/" + address + "/" + Main.getIphubKey());
            localHttpURLConnection = (HttpURLConnection) localURL.openConnection();
            localHttpURLConnection.setConnectTimeout(3000);
            localHttpURLConnection.setReadTimeout(3000);

            localScanner = new Scanner(localHttpURLConnection.getInputStream());
            if (localScanner.hasNextLine()) {
                String str = localScanner.nextLine();
                JSONObject json = new JSONObject(str);
                vpn = (boolean) json.get("host-ip");
                JSONObject countyObject = json.getJSONObject("country");
                String state = (String) countyObject.get("code");

                finalCheck(e,address,state, vpn);
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void finalCheck(PreLoginEvent e, String address, String state, boolean vpn){

        // Ignorovani ceskych a slovensky VPN
        // Kvuli tomu, ze maly poskytovatele (zvlaste na slovensku) maji mene IP, takze je to detekuje jako VPN.
        if(state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK")){
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP je z CZ/SK kraje, hrac pusten na server.");
            return;
        }

        // Kontrola whitelisted IPs
        if(!allowedIps.isEmpty()){
            for (Pattern pattern : allowedIps) {
                Matcher matcher = pattern.matcher(address);
                if (matcher.find()) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP nalezena ve whitelistu!");
                    return;
                }
            }
        }

        // If true player has VPN
        if(vpn){
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Detekce VPN/Proxy (IP: " + address  + "). Hrac zablokovan!");
            e.setCancelReason("§c§lDetekce VPN/IP nebo zahranicni IP!\n§fTvoje IP je zahranicni, nebo se jedna o VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
            e.setCancelled(true);
            return;
        }

        // Nu jinak ma zahranicni, jiny zpusob neni...
        e.setCancelReason("§c§lDetekce zahranicni IP!\n§fTvoje IP je zahranicni, nebo se jedna o VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
        e.setCancelled(true);

    }

    public static void setAllowedIps(List<Pattern> allowedIps) {
        VPNListener.allowedIps = allowedIps;
    }

    public static List<Pattern> getAllowedIps() {
        return allowedIps;
    }
}
