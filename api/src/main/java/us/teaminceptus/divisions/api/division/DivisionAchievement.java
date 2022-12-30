package us.teaminceptus.divisions.api.division;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.DivConfig;

/**
 * Represents Achievements a Division can earn.
 * @since 1.0.0
 */
public enum DivisionAchievement {

    /**
     * Represents the Achievement for having 10, 25, 100, and 1,000 Members.
     * @since 1.0.0
     */
    POPULATION_GROWTH("population_growth", 4),

    /**
     * Represents the Achievement for having 100K, 1M, 10M, 500M, 3B, and 500B Experience.
     * @since 1.0.0
     */
    EXPERIENCE_COLLECTOR("experience_collector", 6),
    ;

    private final String key;
    private final int maxLevel;

    DivisionAchievement(String key, int maxLevel) {
        this.key = key;
        this.maxLevel = maxLevel;
    }

    /**
     * Fetches the maximum level of this Achievement.
     * @return Maximum Level
     */
    public int getMaxLevel() {
        return maxLevel;
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
