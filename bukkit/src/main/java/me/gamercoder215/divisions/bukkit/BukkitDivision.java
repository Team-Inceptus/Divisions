package me.gamercoder215.divisions.bukkit;

import me.gamercoder215.divisions.api.Division;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Bukkit Division
 * @since 1.0.0
 */
public final class BukkitDivision implements Division {

    private BukkitDivision() {}

    @Override
    public @NotNull OfflinePlayer getOwner() {
        return null;
    }
}
