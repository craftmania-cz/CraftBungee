package cz.wake.craftbungee;

import com.google.common.io.Files;
import cz.wake.craftbungee.commands.*;
import cz.wake.craftbungee.commands.internal.Eventserver_tp_command;
import cz.wake.craftbungee.listeners.*;
import cz.wake.craftbungee.managers.*;
import cz.wake.craftbungee.managers.notes.NoteManager;
import cz.wake.craftbungee.prometheus.MetricsController;
import cz.wake.craftbungee.sql.SQLManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.eclipse.jetty.server.Server;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {

    private static Main instance;
    private static Configuration config;
    private static File configFile;
    private SQLManager sql;
    private static HashSet<ProxiedPlayer> online_players = new HashSet<>();
    private static String iphubKey = "";
    public static boolean blockCountry = true;
    private static List<String> voteServers = new ArrayList<>();
    private Server server;
    private boolean isDefaultBlacklist = false;
    private boolean isDebug = false;
    private final Set<String> defaults = new HashSet<>();
    private final Map<String, GroupData> groups = new HashMap<>();
    private static NoteManager noteManager;

    // Channels
    public final static String CRAFTEVENTS_CHANNEL = "craftevents:plugin"; // Channel pro zasilani notifikaci pro zacatek eventu

    @Override
    public void onEnable() {

        // Instance
        instance = this;

        // Kontrola Watterfall eventů
        try {
            Class.forName("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent");
        } catch (ClassNotFoundException e) {
            getLogger().warning("Bungeecord neni Watterfall, plugin nebude fungovat!");
            return;
        }

        // Nacteni configu
        loadConfig();
        iphubKey = getConfig().getString("iphub-key");
        voteServers = getConfig().getStringList("vote-servers");
        this.isDebug = getConfig().getBoolean("debug", false);

        final Configuration defaultsSection = getConfig().getSection("help-commands.defaults");
        this.isDefaultBlacklist = defaultsSection.getBoolean("blacklist", false);
        this.defaults.addAll(defaultsSection.getStringList("completions"));

        final Configuration groupsSection = getConfig().getSection("help-commands.groups");
        for (String key : groupsSection.getKeys()) {
            final Configuration groupSection = groupsSection.getSection(key);
            boolean isWhitelist = !groupSection.getBoolean("blacklist", isDefaultBlacklist);
            Set<String> completions = new HashSet<>(groupSection.getStringList("completions"));
            this.groups.put(key, new GroupData(completions, isWhitelist));
        }

        // Event manager & eventy
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
        getProxy().getPluginManager().registerCommand(this, new Note_command(this));

        // Napojeni na MySQL
        initDatabase();

        // Nacteni block-country z db
        blockCountry = Boolean.parseBoolean(getSQLManager().getConfigValue("block_country"));

        // IP Whitelist
        VPNListener.setAllowedIps(getSQLManager().getWhitelistedIPs());

        // NameBlacklist
        NameBlacklistListener.setWhitelistedNames(getSQLManager().getAllowedBlacklistedNames());
        NameBlacklistListener.setBlacklistedWords(getSQLManager().getBlacklistedNameWords());

        // Registrace eventu
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new VPNListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new NameBlacklistListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PingListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new VoteListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new EventNotifyListener(this));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new HelpCommandListener(this));

        // Tasks
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new SQLChecker(), 1L, 1L, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new PlayerUpdateTask(), 1L, 1L, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new WhitelistTask(), 10L, 60L, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(Main.getInstance(), new NameBlacklistTask(), 10L, 60L, TimeUnit.SECONDS);
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

        // Note manager
        noteManager = new NoteManager();
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
                //e.printStackTrace();
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

    /**
     * Kontrola zda hráč obrží nápovědu pro příkaz (dle configu)
     * @param player Hráč
     * @param label Název příkazu
     * @param command Bungeecord Command class
     * @return boolean zda bude mít nápovědu nebo ne
     */
    public synchronized boolean checkCommand(ProxiedPlayer player, String label, Command command) {
        boolean isAllowed = false; // Default blokace všeho!

        // Pokud je na default whitelistu vzdy povoleno!
        if (this.defaults.contains(label)) {
            return true;
        }

        for (Map.Entry<String, GroupData> entry : this.groups.entrySet()) {
            String groupName = entry.getKey();
            GroupData groupData = entry.getValue();

            if (player.hasPermission("craftbungee.completions.group." + groupName)) {
                if (isDebug()) {
                    System.out.println("Processing group '" + groupName + "'{" + groupData + "} for " + player.getName());
                }

                // Pokud je prikaz na seznamu, bude povolen, jinak blokovan
                if (!groupData.has(label)) {
                    isAllowed = false;
                } else {
                    isAllowed = true;
                }
            }
        }
        return isAllowed;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void debug(String message) {
        if (isDebug) {
            getLogger().info(String.format("[debug] %s", message));
        }
    }

    public static NoteManager getNoteManager() {
        return noteManager;
    }
}
