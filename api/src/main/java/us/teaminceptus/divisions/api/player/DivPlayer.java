package us.teaminceptus.divisions.api.player;

import us.teaminceptus.divisions.api.DivConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Represents a Divisions Player
 * @since 1.0.0
 */
public final class DivPlayer {

    private final OfflinePlayer p;
    private final File file;
    private final FileConfiguration config;

    /**
     * Constructs a DivisionsPlayer.
     * @param player OfflinePlayer to use
     * @since 1.0.0
     */
    public DivPlayer(@NotNull OfflinePlayer player) {
        this.p = player;

        this.file = new File(DivConfig.getPlayerDirectory(), player.getUniqueId().toString() + ".yml");
        if (!file.exists()) try { file.createNewFile(); } catch (Exception e) { DivConfig.print(e); }

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
