package us.teaminceptus.divisions;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import us.teaminceptus.divisions.api.DivConfig;
import us.teaminceptus.divisions.api.division.Division;
import us.teaminceptus.divisions.api.division.logs.AuditLogEntry;
import us.teaminceptus.divisions.events.DivInventoryManager;
import us.teaminceptus.divisions.util.inventory.ItemBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static us.teaminceptus.divisions.wrapper.Wrapper.isCompatible;
import static us.teaminceptus.divisions.wrapper.Wrapper.isOutdatedSubversion;

public final class Divisions extends JavaPlugin implements DivConfig {

    private static Logger LOGGER;
    private static FileConfiguration config;

    private boolean checkCompatible() {
        if (isOutdatedSubversion()) {
            LOGGER.severe("** This subversion of Minecraft is not compatible with Divisions! **");
            LOGGER.severe("** Please update your MC to the latest version. **");

            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        if (!isCompatible()) {
            LOGGER.severe("** This version of Minecraft is not compatible with Divisions! **");
            LOGGER.severe("** Please update the Divisions plugin or Minecraft to the latest version. **");

            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    private static final int BSTATS_ID = 17230;

    private void loadItems() {
        ItemBuilder.loadItems();
    }

    private void loadClasses() {
        new DivCommands(this);

        new DivInventoryManager(this);

        SERIALIZABLE.forEach(ConfigurationSerialization::registerClass);
    }

    private static final List<Class<? extends ConfigurationSerializable>> SERIALIZABLE = Collections.singletonList(
            AuditLogEntry.class
    );

    // onEnable & onDisable

    @Override
    public void onEnable() {
        if (!checkCompatible()) return;
        LOGGER = getLogger();

        saveDefaultConfig();
        config = getConfig();
        Division.getDivisions(); // Verify that the divisions are valid
        getLogger().info("Loading Files...");

        loadItems();
        loadClasses();
        getLogger().info("Loaded Classes...");

        getLogger().info("Loaded Tasks...");

        Metrics m = new Metrics(this, BSTATS_ID);
        m.addCustomChart(new SimplePie("language", getLocale()::getDisplayLanguage));
        m.addCustomChart(new SingleLineChart("division_count", Division.getDivisions()::size));

        getLogger().info("Loaded Add-ons...");

        getLogger().info("Done!");
    }

    @Override
    public void onDisable() {
        SERIALIZABLE.forEach(ConfigurationSerialization::unregisterClass);
        getLogger().info("Unregistered Classes...");

        getLogger().info("Done!");
    }

    // Configuration Implementation

    @Override
    public String get(String key) {
        if (key == null) return null;
        String id = getLanguage().equalsIgnoreCase("en") ? "" : "_" + getLanguage() ;

        Properties p = new Properties();
        try (InputStream str = DivConfig.getPlugin().getClass().getResourceAsStream("/lang/divisions" + id + ".properties")) {
            if (str == null) throw new IOException("Unknown Language: " + id);

            p.load(str);
            str.close();
            return ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"));
        } catch (IOException e) {
            DivConfig.print(e);
            return "Unknown Value";
        }
    }

    @Override
    public String getMessage(String key) {
        return get("plugin.prefix") + " " + get(key);
    }

    @Override
    public String getLanguage() {
        return config.getString("language", "en");
    }

    @Override
    public int getMaxDivisionSize() {
        return Math.max(10, Math.min(config.getInt("divisions.max-players", Division.MAX_PLAYERS), Division.MAX_PLAYERS));
    }

}
