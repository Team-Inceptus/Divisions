package us.teaminceptus.divisions.api.division;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Represents settings for a {@link Division}.
 * @param <T> The type of the setting
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class DivSetting<T> {

    private final String displayKey;
    private final Class<T> type;
    private final T defaultValue;
    private final List<T> possibleValues;

    @SafeVarargs
    DivSetting(String key, Class<T> type, T defaultV, T... possibleValues) {
        this.displayKey = key;
        this.type = type;
        this.defaultValue = defaultV;
        this.possibleValues = Arrays.asList(possibleValues);
    }

    // Default Value Impl

    private static DivSetting<Boolean> ofBoolean(String displayKey, boolean def) {
        return new DivSetting<>(displayKey, Boolean.class, def, true, false);
    }

    private static <T extends Enum<T>> DivSetting<T> ofEnum(String displayKey, T def) {
        return new DivSetting<>(displayKey, (Class<T>) def.getClass(), def, (T[]) def.getClass().getEnumConstants());
    }

    /**
     * Fetches the display name of this DivSetting.
     * @return Display Name
     * @since 1.0.0
     */
    @NotNull
    public String getDisplayName() {
        return displayKey;
    }

}
