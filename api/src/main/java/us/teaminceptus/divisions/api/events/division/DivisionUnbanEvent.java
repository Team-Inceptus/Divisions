package us.teaminceptus.divisions.api.events.division;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Division unbans a player.
 * @since 1.0.0
 */
public class DivisionUnbanEvent extends DivisionEvent {

    private final OfflinePlayer player;
    private OfflinePlayer initiator;

    /**
     * Constructs a DivisionUnbanEvent.
     * @param division Division unbanned from
     * @param unbanned Player unbanned
     * @since 1.0.0
     * @throws IllegalArgumentException if division or unbanned is null
     */
    public DivisionUnbanEvent(@NotNull Division division, @NotNull OfflinePlayer unbanned) throws IllegalArgumentException {
        this(division, unbanned, null);
    }

    /**
     * Constructs a DivisionUnbanEvent.
     * @param division Division unbanned from
     * @param unbanned Player unbanned
     * @param initiator Player that initiated the kick
     * @since 1.0.0
     * @throws IllegalArgumentException if division or unbanned is null
     */
    public DivisionUnbanEvent(@NotNull Division division, @NotNull OfflinePlayer unbanned, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        super(division);
        if (unbanned == null) throw new IllegalArgumentException("Player unbanned cannot be null");

        this.player = unbanned;
    }

    /**
     * Fetches the player unbanned.
     * @return Player unbanned
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
