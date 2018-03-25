package cz.wake.craftbungee.listeners;

import cz.wake.craftbungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VPNListener implements Listener {

    @EventHandler
    public void onLogin(PreLoginEvent e) {
        final String address = e.getConnection().getAddress().getAddress().getHostAddress();

        Main.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW + "Kontrola hrace s IP: " + address);

        try {
            OkHttpClient caller = new OkHttpClient();
            Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + address).addHeader("X-Key", Main.getIphubKey()).build();

            caller.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String state = (String) json.get("countryCode");
                        int validCheck = (int) json.get("block");

                        Main.getInstance().getLogger().log(Level.INFO,"STATE: " + state + ", CHECK: " + String.valueOf(validCheck));

                        finalCheck(e,address,state,validCheck);

                    } catch (NullPointerException exp){
                        exp.printStackTrace();
                    }
                }
            });

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void finalCheck(PreLoginEvent e, String address, String state, int validCheck){

        // Ignorovani ceskych a slovensky VPN
        // Kvuli tomu, ze maly poskytovatele (zvlaste na slovensku) maji mene IP, takze je to detekuje jako VPN.
        if(state.equalsIgnoreCase("CZ") || state.equalsIgnoreCase("SK") || state.equalsIgnoreCase("AT") || state.equalsIgnoreCase("PL")){
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP je z CZ/SK kraje, hrac pusten na server.");
            return;
        }

        // Kontrola whitelisted IPs
        if(!Main.getInstance().allowedIps.isEmpty()){
            for (Pattern pattern : Main.getInstance().allowedIps) {
                Matcher matcher = pattern.matcher(address);
                if (matcher.find()) {
                    Main.getInstance().getLogger().log(Level.INFO, ChatColor.GREEN + "IP nalezena ve whitelistu!");
                    return;
                }
            }
        }

        // 0 -> safe IP
        // 1 -> host/proxy/vpn
        // 2 -> providers (UPC atd.)
        if(validCheck == 1){
            Main.getInstance().getLogger().log(Level.INFO, ChatColor.RED + "Zahranicni VPN/Proxy (IP: " + address  + "). Hrac zablokovan!");
            e.setCancelReason("§c§lDetekce VPN/IP nebo zahranicni IP!\n§fTvoje IP je zahranicni, nebo se jedna o VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
            e.setCancelled(true);
            return;
        }

        e.setCancelReason("§c§lDetekce VPN/IP nebo zahranicni IP!\n§fTvoje IP je zahranicni, nebo se jedna o VPN.\n§fV takovem pripade se za normalnich podminek nelze pripojit.");
        e.setCancelled(true);

    }
}
