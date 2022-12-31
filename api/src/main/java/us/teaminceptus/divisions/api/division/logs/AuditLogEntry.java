package us.teaminceptus.divisions.api.division.logs;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a Divsiion Audit Log Entry.
 * @since 1.0.0
 */
public final class AuditLogEntry implements ConfigurationSerializable, Serializable {

    private long timestamp;
    private Action action;
    private Object data;

    private UUID playerUUID;
    private transient OfflinePlayer player;

    /**
     * Creates a new Audit Log Entry with no player.
     * @param timestamp Timestamp of Entry
     * @param action Action Involved
     * @since 1.0.0
     */
    public AuditLogEntry(@NotNull Date timestamp, @NotNull AuditLogEntry.Action action) {
        this(timestamp, action, null);
    }

    /**
     * Creates a new Audit Log Entry with no player.
     * @param timestamp Timestamp of Entry
     * @param action Action Involved
     * @param data Primary Object involved in this action
     * @since 1.0.0
     */
    public AuditLogEntry(@NotNull Date timestamp, @NotNull AuditLogEntry.Action action, @Nullable Object data) {
        this(timestamp, action, data, null);
    }

    /**
     * Constructs an Audit Log Entry.
     * @param timestamp Timestamp of Entry
     * @param action Action Involved
     * @param data Primary Object involved in this action
     * @param player Optional Player Involved
     * @since 1.0.0
     * @throws IllegalArgumentException if the action or timestamp is null
     */
    public AuditLogEntry(@NotNull Date timestamp, @NotNull AuditLogEntry.Action action, @Nullable Object data, @Nullable OfflinePlayer player) throws IllegalArgumentException {
        if (timestamp == null) throw new IllegalArgumentException("timestamp cannot be null");
        if (action == null) throw new IllegalArgumentException("action cannot be null");

        this.timestamp = timestamp.getTime();
        this.action = action;
        this.data = data;
        this.player = player;
        this.playerUUID = player == null ? null : player.getUniqueId();
    }

    /**
     * Sets the timestamp of this Audit Log Entry.
     * @param timestamp Timestamp of Entry
     * @since 1.0.0
     * @throws IllegalArgumentException if the timestamp is null
     */
    public void setTimestamp(@NotNull Date timestamp) throws IllegalArgumentException{
        if (timestamp == null) throw new IllegalArgumentException("timestamp cannot be null");
        this.timestamp = timestamp.getTime();
    }

    /**
     * Sets the action of this Audit Log Entry.
     * @param action Action Involved
     * @since 1.0.0
     * @throws IllegalArgumentException if the action is null
     */
    public void setAction(@NotNull AuditLogEntry.Action action) throws IllegalArgumentException {
        if (action == null) throw new IllegalArgumentException("action cannot be null");
        this.action = action;
    }

    /**
     * Sets the player of this Audit Log Entry.
     * @param player Player Involved
     * @since 1.0.0
     */
    public void setPlayer(@Nullable OfflinePlayer player) {
        this.player = player;
        this.playerUUID = player == null ? null : player.getUniqueId();
    }

    /**
     * Fetches the timestamp of this Audit Log Entry.
     * @return Timestamp of Entry
     * @since 1.0.0
     */
    @NotNull
    public Date getTimestamp() {
        return new Date(timestamp);
    }

    /**
     * Fetches the action of this Audit Log Entry.
     * @return Action Involved
     * @since 1.0.0
     */
    @NotNull
    public AuditLogEntry.Action getAction() {
        return action;
    }

    /**
     * Fetches the player involved in this Audit Log Entry. May be null.
     * @return Player Involved
     * @since 1.0.0
     */
    @Nullable
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Fetches the data involved in this Audit Log Entry. May be null.
     * @return Data Involved
     * @since 1.0.0
     */
    @Nullable
    public Object getData() {
        return data;
    }

    /**
     * Sets the data involved in this Audit Log Entry.
     * @param data Data Involved
     * @since 1.0.0
     */
    public void setData(@Nullable Object data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogEntry that = (AuditLogEntry) o;
        return timestamp == that.timestamp && action == that.action && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, action, player == null ? player.getUniqueId().hashCode() : 0);
    }

    @Override
    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(new SimpleDateFormat("[HH:mm:ss] ").format(getTimestamp()))
                .append(action.toString());

        if (data != null) sb.append(" - ").append(data);
        if (player != null) sb.append(" | ").append(player.getName());

        return sb.toString();
    }

    // Serialization

    /**
     * Deserializes a Map into an AuditLogEntry.
     * @param serial Serialization from {@link AuditLogEntry#serialize()}
     * @return Deserialized Audit Log Entry
     * @since 1.0.0
     */
    @NotNull
    public static AuditLogEntry deserialize(@NotNull Map<String, Object> serial) {
        return new AuditLogEntry(
                new Date((long) serial.get("timestamp")),
                Action.valueOf((String) serial.get("action")),
                serial.get("data"),
                (OfflinePlayer) serial.get("player")
        );
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {
            {
                put("timestamp", timestamp);
                put("action", action.name());
                put("data", data);
                put("player", player);
            }
        };
    }

    private void readObject(@NotNull ObjectInputStream is) throws IOException, ClassNotFoundException {
        is.defaultReadObject();
        if (playerUUID != null) player = Bukkit.getOfflinePlayer(playerUUID);
    }

    // Enum Action

    /**
     * Represents Actions used in the Audit Log.
     * @since 1.0.0
     */
    public enum Action {

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
         * Represents the Action for a Player joining a Division.
         * @since 1.0.0
         */
        MEMBER_JOINED("division_member_joined"),

        /**
         * Represents the Action for a Division's Member being kicked.
         * @since 1.0.0
         */
        MEMBER_KICKED("division_member_kicked"),

        /**
         * Represents the Action for a Player being banned from a Division.
         * @since 1.0.0
         */
        PLAYER_BANNED("division_player_banned"),

        /**
         * Represents the Action for a Player being unbanned from a Division.
         * @since 1.0.0
         */
        PLAYER_UNBANNED("division_player_unbanned"),

        ;

        private final String key;

        Action(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "AuditAction[" + key + "]";
        }

        /**
         * Fetches the key of this Action.
         * @return Key of Action
         * @since 1.0.0
         */
        @NotNull
        public String getKey() {
            return key;
        }

        /**
         * Fetches the bukkit key of this Action.
         * @return Bukkit Key of Action
         * @since 1.0.0
         */
        @NotNull
        public NamespacedKey getBukkitKey() {
            return new NamespacedKey(DivConfig.getPlugin(), key);
        }

        /**
         * Fetches an AuditAction by its Key.
         * @param key Key
         * @return AuditAction found, or null if not found.
         * @since 1.0.0
         */
        @Nullable
        public static AuditLogEntry.Action fromKey(@Nullable String key) {
            return Arrays.stream(values())
                    .filter(a -> a.getKey().equals(key))
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
        public static AuditLogEntry.Action fromKey(@Nullable NamespacedKey key) {
            return Arrays.stream(values())
                    .filter(a -> a.getKey().equals(key))
                    .findFirst()
                    .orElse(null);
        }




    }
}
