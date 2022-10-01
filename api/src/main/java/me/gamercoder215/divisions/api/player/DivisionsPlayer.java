package me.gamercoder215.divisions.api.player;

import me.gamercoder215.divisions.api.DivisionsConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Represents a Divisions Player
 */
public final class DivisionsPlayer {

    private final OfflinePlayer p;
    private final File file;
    private final FileConfiguration config;

    /**
     * Constructs a DivisionsPlayer.
     * @param player OfflinePlayer to use
     * @since 1.0.0
     */
    public DivisionsPlayer(@NotNull OfflinePlayer player) {
        this.p = player;

        this.file = new File(DivisionsConfig.getPlayerDirectory(), player.getUniqueId().toString() + ".yml");
        if (!file.exists()) try { file.createNewFile(); } catch (Exception e) { DivisionsConfig.print(e); }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Fetches the OfflinePlayer this DivisionsPlayer belongs to.
     * @return OfflinePlayer
     * @since 1.0.0
     */
    @NotNull
    public OfflinePlayer getPlayer() {
        return p;
    }

    /**
     * Fetches the File the information in this DivisionsPlayer is stored in.
     * @return File stored in
     * @since 1.0.0
     */
    @NotNull
    public File getFile() {
        return file;
    }

    /**
     * Fetches the FileConfiguration used by this DivisionsPlayer.
     * @return FileConfiguration used
     * @since 1.0.0
     */
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }
}
