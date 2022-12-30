package us.teaminceptus.divisions.api.division.logs;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;

import java.util.Arrays;

/**
 * Represents Actions used in the Audit Log.
 * @since 1.0.0
 */
public enum AuditAction implements Keyed {

    /**
     * Represents the Action for a Division being created.
     * @since 1.0.0
     */
    CREATED("division_created"),

    /**
     * Represents the Action for a Division being disbanded.
     * @since 1.0.0
     */
    DISBANDED("division_disbanded"),

    /**
     * Represents the Action for a Division being renamed.
     * @since 1.0.0
     */
    RENAMED("division_renamed"),

    /**
     * Represents the Action for a Division's Tagline being changed.
     * @since 1.0.0
     */
    TAGLINE_CHANGED("division_tagline_changed"),

    /**
     * Represents the Action for a Division's Member being added.
     * @since 1.0.0
     */
    MEMBER_ADDED("division_member_added"),

    /**
     * Represents the Action for a Division's Member being kicked.
     * @since 1.0.0
     */
    MEMBER_KICKED("division_member_kicked"),

    /**
     * Represents the Action for a Division's Member being banned.
     * @since 1.0.0
     */
    MEMBER_BANNED("division_member_banned"),

    ;

    private final String key;

    AuditAction(String key) {
        this.key = key;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(DivConfig.getPlugin(), key);
    }

    /**
     * Fetches an AuditAction by its Key.
     * @param key Key
     * @return AuditAction found, or null if not found.
     * @since 1.0.0
     */
    @Nullable
    public static AuditAction fromKey(@Nullable String key) {
        return Arrays.stream(values())
                .filter(a -> a.getKey().getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetches an AuditAction by its Key.
     * @param key NamespacedKey
     * @return AuditAction found, or null if not found.
     * @since 1.0.0
     */
    @Nullable
    public static AuditAction fromKey(@Nullable NamespacedKey key) {
        return Arrays.stream(values())
                .filter(a -> a.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }




}
