package me.gamercoder215.divisions.api.player;

import org.jetbrains.annotations.NotNull;

/**
 * Represents Divisions Player Settings
 * @since 1.0.0
 */
public enum PlayerSettings {

    /**
     * Whether the player receives plugin notifications.
     */
    NOTIFICATIONS("settings.notifications", true);

    private final String displayKey;
    private final Class<?> type;
    private final Object defaultValue;

    <T> PlayerSettings(String key, Class<T> type, T defaultV) {
        this.displayKey = key;
        this.type = type;
        this.defaultValue = defaultV;
    }

    PlayerSettings(String key, boolean defaultV) {
        this(key, boolean.class, defaultV);
    }

    /**
     * Fetches the display name of this PlayerSetting.
     * @return Display Name
     * @since 1.0.0
     */
    @NotNull
    public String getDisplayName() {
        return displayKey;
    }


}
