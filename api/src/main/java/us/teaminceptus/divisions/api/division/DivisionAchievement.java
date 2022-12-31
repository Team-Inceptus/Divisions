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
    POPULATION_GROWTH("population_growth", 4, 1000),

    /**
     * Represents the Achievement for having 100K, 1M, 10M, 500M, 3B, and 500B Experience.
     * @since 1.0.0
     */
    EXPERIENCE_COLLECTOR("experience_collector", 6, 0),
    ;

    private final String key;
    private final int maxLevel;
    private final double experienceIncrease;

    DivisionAchievement(String key, int maxLevel, double experienceIncrease) {
        this.key = key;
        this.maxLevel = maxLevel;
        this.experienceIncrease = experienceIncrease;
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
     * Fetches the amount of experience the Division will gain upon achieving this Achievement for level 1.
     * @return Experience Increase
     * @since 1.0.0
     */
    public double getExperienceIncrease() {
        return getExperienceIncrease(1);
    }

    /**
     * Fetches the amount of experience the Division will gain upon achieving this Achievement.
     * @param level The level of the Achievement
     * @return Experience Increase
     * @since 1.0.0
     * @throws IllegalArgumentException If the level is less than 1 or greater than the maximum level
     */
    public double getExperienceIncrease(int level) throws IllegalArgumentException {
        if (level < 1 || level > maxLevel) throw new IllegalArgumentException("Level must be between 1 and " + maxLevel + " inclusive.");
        if (level == 1) return experienceIncrease;

        double num = Math.pow(2, level) * (experienceIncrease / 2);
        return num + (50 - (num % 50));
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
