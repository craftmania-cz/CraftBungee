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

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPNListener implements Listener {

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);

        if (!isAllowed(address)) {
            e.setCancelReason("§c§lDetekce VPN/IP nebo zahranicni IP!\n§fTvoje IP je zahranicni, nebo se jedna o VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
            e.setCancelled(true);
        }
    }


    private boolean isAllowed(final String ipAdress) {
        String state;
        int validCheck;

        OkHttpClient caller = new OkHttpClient();
        Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + ipAdress).addHeader("X-Key", Main.getIphubKey()).build();
        try {
            Response response = caller.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            state = (String) json.get("countryCode");
            validCheck = (int) json.get("block");

            // Ignorovani ceskych a slovensky VPN
            // Kvuli tomu, ze maly poskytovatele (zvlaste na slovensku) maji mene IP, takze je to detekuje jako VPN.
            if ((state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK") || state.equalsIgnoreCase("AT") || state.equalsIgnoreCase("PL"))) {
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP je z CZ/SK kraje, hrac pusten na server.");
                return true;
            }

            // Kontrola whitelisted IPs
            for (Pattern pattern : Main.getInstance().allowedIps) {
                Matcher matcher = pattern.matcher(ipAdress);
                if (matcher.find()) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP nalezena ve whitelistu!");
                    return true;
                }
            }

            // 0 -> safe IP
            // 1 -> host/proxy/vpn
            // 2 -> providers (UPC atd.)
            //if(validCheck == 1){
            //Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni VPN/Proxy (IP: " + ipAdress  + "). Hrac zablokovan!");
            //return false;
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }
        Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Hrac zastaven, nepripusten na server.");
        return false;
    }
}
