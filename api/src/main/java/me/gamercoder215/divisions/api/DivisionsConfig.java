package me.gamercoder215.divisions.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

/**
 * Represents Divisions Utilities and Configuration
 */
public interface DivisionsConfig {

    /**
     * Fetches the Divisions Plugin.
     * @return The Divisions Plugin.
     */
    static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("Divisions");
    }

    /**
     * Fetches the plugin's logger.
     * @return Plugin Logger
     */
    static Logger getLogger() {
        return getPlugin().getLogger();
    }

}
