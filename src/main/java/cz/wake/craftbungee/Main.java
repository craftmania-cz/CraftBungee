package cz.wake.craftbungee;

import com.google.common.io.Files;
import cz.wake.craftbungee.commands.*;
import cz.wake.craftbungee.commands.internal.Eventserver_tp_command;
import cz.wake.craftbungee.listeners.*;
import cz.wake.craftbungee.managers.*;
import cz.wake.craftbungee.prometheus.MetricsController;
import cz.wake.craftbungee.sql.SQLManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.eclipse.jetty.server.Server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    private static Main instance;
    private static Configuration config;
    private static File configFile;
    private SQLManager sql;
    private static HashSet<ProxiedPlayer> online_players = new HashSet<>();
    private static String iphubKey = "";
    public static boolean blockCountry = false;
    private static List<String> voteServers = new ArrayList<>();
    private Server server;

    // Channels
    public final static String CRAFTEVENTS_CHANNEL = "craftevents:plugin"; // Channel pro zasilani notifikaci pro zacatek eventu

    @Override
    public void onEnable() {

        // Instance
        instance = this;

        // Nacteni configu
        loadConfig();
        iphubKey = getConfig().getString("iphub-key");
        voteServers = getConfig().getStringList("vote-servers");

        this.getProxy().registerChannel("craftbungee"); // Channel pro channeling hlasu
        this.getProxy().registerChannel(CRAFTEVENTS_CHANNEL);

        getProxy().getPluginManager().registerCommand(this, new GA_command(this));
        getProxy().getPluginManager().registerCommand(this, new AT_command(this));
        getProxy().getPluginManager().registerCommand(this, new GHelp_command(this));
        getProxy().getPluginManager().registerCommand(this, new GBC_command(this));
        getProxy().getPluginManager().registerCommand(this, new GBP_command(this));
        getProxy().getPluginManager().registerCommand(this, new CB_command(this));
        getProxy().getPluginManager().registerCommand(this, new IPWL_command(this));
        getProxy().getPluginManager().registerCommand(this, new Eventserver_tp_command(this));

        // Napojeni na MySQL
        initDatabase();

        // Nacteni block-country z db
        blockCountry = Boolean.valueOf(getSQLManager().getConfigValue("block_country"));

        // IP Whitelist
        VPNListener.setAllowedIps(getSQLManager().getWhitelistedIPs());

        // Registrace eventu
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new VPNListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new VoteListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new EventNotifyListener(this));

        // Tasks
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new SQLChecker(), 1L, 1L, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new PlayerUpdateTask(), 1L, 1L, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new WhitelistTask(), 10L, 60L, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new CooldownUpdateTask(), 1L, 1L, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new BlockCountryTask(), 1L, 1L, TimeUnit.MINUTES);
        //ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new BroadcastTask(), 1L, getConfig().getSection("automessages").getLong("sendevery"), TimeUnit.MINUTES);

        // Jetty server
        if (getConfig().getBoolean("prometheus.state")) {
            int port = getConfig().getInt("prometheus.port");
            server = new Server(port);
            server.setHandler(new MetricsController());
            try {
                server.start();
                getLogger().info("Started Prometheus metrics endpoint on port " + port);
            } catch (Exception e) {
                getLogger().severe("Could not start embedded Jetty server");
            }
        }
    }

    @Override
    public void onDisable() {

        // MySQL
        sql.onDisable();

        // Jetty server
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Plugi instance
        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }

    public static Configuration getConfig() {
        return Main.config;
    }

    public void initDatabase() {
        sql = new SQLManager(this);
    }

    public SQLManager getSQLManager() {
        return sql;
    }

    public HashSet<ProxiedPlayer> getOnlinePlayers() {
        return online_players;
    }

    public void loadConfig() {
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdir();
            }
            Main.configFile = new File(this.getDataFolder().getPath(), "config.yml");
            if (!Main.configFile.exists()) {
                Main.configFile.createNewFile();
            }
            final InputStream configInputStream = Files.asByteSource(Main.configFile).openStream();
            Main.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new BufferedReader(new InputStreamReader(configInputStream)));
            configInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        this.loadConfig();
    }

    public static void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.config, Main.configFile);
        } catch (IOException e) {
            getInstance().getLogger().warning("Config could not be saved!");
            e.printStackTrace();
        }
    }

    public static String getAPIKey() {
        return iphubKey;
    }

    public static boolean allowOnlyCZSK() {
        return blockCountry;
    }

    public static List<String> getVoteServers() {
        return voteServers;
    }
}
