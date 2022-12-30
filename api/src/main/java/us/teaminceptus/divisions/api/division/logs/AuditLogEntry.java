package us.teaminceptus.divisions.api.division.logs;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a Divsiion Audit Log Entry.
 * @since 1.0.0
 */
public final class AuditLogEntry implements ConfigurationSerializable {

    private long timestamp;
    private AuditAction action;
    private OfflinePlayer player;

    /**
     * Creates a new Audit Log Entry with no player.
     * @param timestamp Timestamp of Entry
     * @param action Action Involved
     */
    public AuditLogEntry(@NotNull Date timestamp, @NotNull AuditAction action) {
        this(timestamp, action, null);
    }

    /**
     * Constructs an Audit Log Entry.
     * @param timestamp Timestamp of Entry
     * @param action Action Involved
     * @param player Optional Player Involved
     * @since 1.0.0
     * @throws IllegalArgumentException if the action or timestamp is null.
     */
    public AuditLogEntry(@NotNull Date timestamp, @NotNull AuditAction action, @Nullable OfflinePlayer player) throws IllegalArgumentException {
        if (timestamp == null) throw new IllegalArgumentException("timestamp cannot be null");
        if (action == null) throw new IllegalArgumentException("action cannot be null");

        this.timestamp = timestamp.getTime();
        this.action = action;
        this.player = player;
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
    public void setAction(@NotNull AuditAction action) throws IllegalArgumentException {
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
    public AuditAction getAction() {
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
        return "AuditLogEntry{" +
                "timestamp=" + timestamp +
                ", action=" + action +
                ", player=" + player.getName() +
                '}';
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
                AuditAction.valueOf((String) serial.get("action")),
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
                put("player", player);
            }
        };
    }
}
