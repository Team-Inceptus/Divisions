package us.teaminceptus.divisions.api.events.division;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Division bans a player.
 * @since 1.0.0
 */
public class DivisionBanEvent extends DivisionEvent {

    private final OfflinePlayer player;
    private OfflinePlayer initiator;

    /**
     * Constructs a DivisionKickEvent.
     * @param division Division banned from
     * @param banned Player banned
     * @since 1.0.0
     * @throws IllegalArgumentException if division or banned is null
     */
    public DivisionBanEvent(@NotNull Division division, @NotNull OfflinePlayer banned) throws IllegalArgumentException {
        this(division, banned, null);
    }

    /**
     * Constructs a DivisionBanEvent.
     * @param division Division banned from
     * @param banned Player banned
     * @param initiator Player that initiated the kick
     * @since 1.0.0
     * @throws IllegalArgumentException if division or banned is null
     */
    public DivisionBanEvent(@NotNull Division division, @NotNull OfflinePlayer banned, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        super(division);
        if (banned == null) throw new IllegalArgumentException("Player banned cannot be null");

        this.player = banned;
    }

    /**
     * Fetches the player banned.
     * @return Player banned
     * @since 1.0.0
     */
    @NotNull
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Fetches the player that initiated the kick.
     * @return Player that initiated the kick
     * @since 1.0.0
     */
    @Nullable
    public OfflinePlayer getInitiator() {
        return initiator;
    }

    /**
     * Sets the player that initiated the kick.
     * @param initiator Player that initiated the kick
     * @since 1.0.0
     */
    public void setInitiator(@Nullable OfflinePlayer initiator) {
        this.initiator = initiator;
    }
    
}
