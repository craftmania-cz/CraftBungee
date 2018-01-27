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

public class VPNListener implements Listener {

    @EventHandler
    public void onLogin(PreLoginEvent e){
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);

        if(!isAllowed(address)){
            e.setCancelReason("§c§lDetekce VPN/IP!\n§fNedovolujeme se pripojovat na server pomoci proxy/VPN/host IP.");
            e.setCancelled(true);
        }
    }


    private boolean isAllowed(final String ipAdress){
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
            if(state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK")){
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP je z CZ/SK kraje, hrac pusten na server.");
                return true;
            }

            // 0 -> safe IP
            // 1 -> host/proxy/vpn
            // 2 -> providers (UPC atd.)
            if(validCheck == 1){
                Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni VPN/Proxy (IP: " + ipAdress  + "). Hrac zablokovan!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
