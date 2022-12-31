package us.teaminceptus.divisions.api.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.division.Division;
import us.teaminceptus.divisions.api.events.player.PlayerMessageEvent;

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

    /**
     * Fetches the Division this DivPlayer is in.
     * @return Division
     * @since 1.0.0
     */
    @Nullable
    public Division getDivision() {
        return Division.byOwner(p) == null ? Division.byMember(p) : null;
    }

    /**
     * Determines whether this DivPlayer is the owner of the Division they are in.
     * @return true if owner, false otherwise
     * @since 1.0.0
     */
    public boolean isDivisionOwner() {
        return getDivision() != null && getDivision().getOwner().getUniqueId().equals(p.getUniqueId());
    }

    /**
     * <p>Sends a message in the Divisions Chat Format.</p>
     * <p>This method will fail (do nothing) if the player is not online.</p>
     * @param message Message to send
     */
    public void sendDivisionMessage(@NotNull String message) {
        if (!p.isOnline()) return;
        Division d = getDivision();
        if (d == null) return;

        Player pl = p.getPlayer();
        PlayerMessageEvent event = new PlayerMessageEvent(d, pl, message);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        String plPrefix = pl.getDisplayName() == null ? pl.getName() : pl.getDisplayName() + ChatColor.RESET;
        String prefix = plPrefix + ChatColor.GOLD + "[" + (isDivisionOwner() ? "O" : "M") + "]" + ChatColor.RESET + ": ";

        d.broadcastMessage(prefix + event.getMessage());
    }

}
