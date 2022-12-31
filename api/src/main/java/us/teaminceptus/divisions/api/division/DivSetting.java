package us.teaminceptus.divisions.api.division;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import us.teaminceptus.divisions.api.DivConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents settings for a {@link Division}.
 * @param <T> The type of the setting
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class DivSetting<T> {

    private static final DivSetting<?>[] VALUES;

    static {
        List<DivSetting<?>> values = new ArrayList<>();

        try {
            for (Field f : DivSetting.class.getDeclaredFields()) {
                if (!Modifier.isFinal(f.getModifiers())) continue;
                if (!Modifier.isStatic(f.getModifiers())) continue;
                if (!Modifier.isPublic(f.getModifiers())) continue;
                if (!DivSetting.class.isAssignableFrom(f.getType())) continue;

                f.setAccessible(true);
                values.add((DivSetting<?>) f.get(null));
            }
        } catch (ReflectiveOperationException e) {
            DivConfig.print(e);
        }

        VALUES = values.toArray(DivSetting[]::new);
    }

    /**
     * Setting for Divisions to have colored chat.
     * @since 1.0.0
     */
    public static final DivSetting<Boolean> COLOR_CHAT = ofBoolean(3, "color-chat", "settings.division.color_chat", true);

    private final int unlockedLevel;
    private final String displayKey;
    private final String key;
    private final Class<T> type;
    private final T defaultValue;
    private final List<T> possibleValues;

    @SafeVarargs
    DivSetting(int unlockedLevel, String key, String displayKey, Class<T> type, T defaultV, T... possibleValues) {
        this.unlockedLevel = unlockedLevel;
        this.displayKey = displayKey;
        this.key = key;
        this.type = type;
        this.defaultValue = defaultV;
        this.possibleValues = Arrays.asList(possibleValues);
    }

    // Default Value Impl

    @NotNull
    private static DivSetting<Boolean> ofBoolean(int unlockedLevel, String key, String displayKey, boolean def) {
        return new DivSetting<>(unlockedLevel, key, displayKey, Boolean.class, def, true, false);
    }

    @NotNull
    private static <T extends Enum<T>> DivSetting<T> ofEnum(int unlockedLevel, String key, String displayKey, T def) {
        return new DivSetting<>(unlockedLevel, key, displayKey, (Class<T>) def.getClass(), def, (T[]) def.getClass().getEnumConstants());
    }

    /**
     * Fetches the display name of this DivSetting.
     * @return Display Name
     * @since 1.0.0
     */
    @NotNull
    public String getDisplayName() {
        return DivConfig.getConfiguration().get(displayKey);
    }

    /**
     * Fetches the key of this DivSetting.
     * @return DivSetting Key
     * @since 1.0.0
     */
    @NotNull
    public String getKey() {
        return key;
    }

    /**
     * Fetches the default value for this DivSetting.
     * @return Default Value
     * @since 1.0.0
     */
    @NotNull
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Fetches the Division Level that this DivSetting is unlocked at.
     * @return Unlocked Level
     */
    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    /**
     * Fetches the type of this DivSetting.
     * @return Setting Type
     * @since 1.0.0
     */
    @NotNull
    public Class<T> getType() {
        return type;
    }

    /**
     * Fetches all of the possible values for this DivSetting.
     * @return Possible Values
     * @since 1.0.0
     */
    @NotNull
    @Unmodifiable
    public List<T> getPossibleValues() {
        return ImmutableList.copyOf(possibleValues);
    }

    /**
     * Fetches all of the DivSettings.
     * @return All DivSettings
     * @since 1.0.0
     */
    public static DivSetting<?>[] values() {
        return VALUES;
    }
}
