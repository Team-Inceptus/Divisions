package us.teaminceptus.divisions.api.division;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.DivConfig;

/**
 * Represents Achievements a Division can earn.
 * @since 1.0.0
 */
public enum DivisionAchievement {

    ;

    private final String key;

    DivisionAchievement(String key) {
        this.key = key;
    }

    /**
     * Fetches the Achievement's Key.
     * @return Achievement Key
     * @since 1.0.0
     */
    @NotNull
    public String getKey() {
        return key;
    }

    /**
     * Fetches the Achievement's NamespacedKey.
     * @return NamespacedKey
     * @since 1.0.0
     */
    @NotNull
    public NamespacedKey getBukkitKey() {
        return new NamespacedKey(DivConfig.getPlugin(), key);
    }

}
