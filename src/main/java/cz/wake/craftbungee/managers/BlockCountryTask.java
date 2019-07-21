package cz.wake.craftbungee.managers;

import cz.wake.craftbungee.Main;
import cz.wake.craftbungee.utils.Logger;

public class BlockCountryTask implements Runnable {

    @Override
    public void run() {

        Main.blockCountry = Boolean.valueOf(Main.getInstance().getSQLManager().getConfigValue("block_country"));

        Logger.info("BlockCountry updatovan (aktualni hodnota: " + Main.blockCountry + ")");
    }
}
