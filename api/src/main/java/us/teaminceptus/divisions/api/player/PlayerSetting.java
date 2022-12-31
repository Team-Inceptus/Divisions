package us.teaminceptus.divisions.api.player;

import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.DivConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Represents Divisions Player Settings
 * @param <T> The type of the setting
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class PlayerSetting<T> {

    /**
     * Whether the player receives plugin notifications.
     * @since 1.0.0
     */
    public static final PlayerSetting<Boolean> NOTIFICATIONS =
            new PlayerSetting<>("settings.notifications", boolean.class, true);

    private final String displayKey;
    private final Class<T> type;
    private final T defaultValue;
    private final List<T> possibleValues;

    @SafeVarargs
    PlayerSetting(String key, Class<T> type, T defaultV, T... possibleValues) {
        this.displayKey = key;
        this.type = type;
        this.defaultValue = defaultV;
        this.possibleValues = Arrays.asList(possibleValues);
    }

    // Default Value Impl

    private static PlayerSetting<Boolean> ofBoolean(String displayKey, boolean def) {
        return new PlayerSetting<>(displayKey, Boolean.class, def, true, false);
    }

    private static <T extends Enum<T>> PlayerSetting<T> ofEnum(String displayKey, T def) {
        return new PlayerSetting<>(displayKey, (Class<T>) def.getClass(), def, (T[]) def.getClass().getEnumConstants());
    }

    /**
     * Fetches the display name of this PlayerSetting.
     * @return Display Name
     * @since 1.0.0
     */
    @NotNull
    public String getDisplayName() {
        return DivConfig.getConfiguration().get(displayKey);
    }


}
