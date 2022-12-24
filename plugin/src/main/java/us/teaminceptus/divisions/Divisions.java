package us.teaminceptus.divisions;

import us.teaminceptus.divisions.api.DivConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public final class Divisions extends JavaPlugin implements DivConfig {

    private static Logger LOGGER;
    private static FileConfiguration config;

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        saveDefaultConfig();

        getLogger().info("Loading Files...");
        config = getConfig();

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
        return null;
    }

}
