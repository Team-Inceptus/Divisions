package us.teaminceptus.divisions.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents Divisions Utilities and Configuration
 * @since 1.0.0
 */
public interface DivConfig {

    /**
     * Fetches the Divisions Plugin.
     * @return The Divisions Plugin.
     * @since 1.0.0
     */
    static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Divisions");
    }

    /**
     * Fetches the Divisions Plugin's Data Folder.
     * @return Plugin Data Folder
     * @since 1.0.0
     */
    static File getDataFolder() { return getPlugin().getDataFolder(); }

    /**
     * Fetches the player directory that player data is stored in.
     * @return Player Directory
     * @since 1.0.0
     */
    static File getPlayerDirectory() {
        return new File(getDataFolder(), "players");
    }

    /**
     * Fetches the plugin's logger.
     * @return Plugin Logger
     * @since 1.0.0
     */
    static Logger getLogger() {
        return getPlugin().getLogger();
    }

    /**
     * Fetches the configuration instance.
     * @return Divisions Configuration
     * @since 1.0.0
     */
    static DivConfig getConfiguration() { return (DivConfig) getPlugin(); }

    /**
     * Prints a Throwable in the Plugin Namespace.
     * @param e Throwable to Print
     * @since 1.0.0
     */
    static void print(@NotNull Throwable e) {
        getLogger().info(e.getClass().getSimpleName());
        getLogger().info("----------------------------------------");
        getLogger().info(e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) getLogger().info(element.toString());
    }

    /**
     * Fetches the folder that all divisions are stored in.
     * @return Division Directory
     * @since 1.0.0
     */
    static File getDivisionsDirectory() {
        return new File(getDataFolder(), "divisions");
    }

    // Configuration

    /**
     * Fetches a String from the current language file.
     * @param key Key to fetch
     * @return String fetched, based on {@link #getLanguage()}
     * @since 1.0.0
     */
    String get(String key);

    /**
     * Fetches a String from the current language file, with the plugin prefix in front.
     * @param key Key to fetch
     * @return Message fetched, based on {@link #getLanguage()}
     */
    String getMessage(String key);

    /**
     * Fetches the current language in the configuration.
     * @return Current Language
     * @since 1.0.0
     */
    String getLanguage();

    /**
     * Whether Adventure API is enabled and is currently used.
     * @return true if enabled, else false
     * @since 1.0.0
     */
    boolean isAdventureEnabled();

}
