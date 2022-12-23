package us.teaminceptus.divisions.loader.adventure;

import us.teaminceptus.divisions.api.Division;
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
