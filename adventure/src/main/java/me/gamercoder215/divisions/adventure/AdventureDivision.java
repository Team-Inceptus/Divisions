package me.gamercoder215.divisions.adventure;

import me.gamercoder215.divisions.api.Division;
import me.gamercoder215.divisions.bukkit.BukkitDivision;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an Adventure Division
 * @since 1.0.0
 */
public final class AdventureDivision implements Division {

    private AdventureDivision() {}

    @Override
    public @NotNull OfflinePlayer getOwner() {
        return null;
    }
}
