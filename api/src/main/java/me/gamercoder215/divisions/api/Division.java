package me.gamercoder215.divisions.api;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Division
 * @since 1.0.0
 */
public interface Division {

    /**
     * Fetches the Owner of this Division.
     * @return OfflinePlayer
     */
    @NotNull
    OfflinePlayer getOwner();
}
