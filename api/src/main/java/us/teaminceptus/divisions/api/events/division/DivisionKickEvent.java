package us.teaminceptus.divisions.api.events.division;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Division kicks a player
 * @since 1.0.0
 */
public class DivisionKickEvent extends DivisionEvent {

    private final OfflinePlayer player;
    private OfflinePlayer initiator;

    /**
     * Constructs a DivisionKickEvent.
     * @param division Division kicked from
     * @param kicked Player kicked
     * @since 1.0.0
     * @throws IllegalArgumentException if division or kicked is null
     */
    public DivisionKickEvent(@NotNull Division division, @NotNull OfflinePlayer kicked) throws IllegalArgumentException {
        this(division, kicked, null);
    }

    /**
     * Constructs a DivisionKickEvent.
     * @param division Division kicked from
     * @param kicked Player kicked
     * @param initiator Player that initiated the kick
     * @since 1.0.0
     * @throws IllegalArgumentException if division or kicked is null
     */
    public DivisionKickEvent(@NotNull Division division, @NotNull OfflinePlayer kicked, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        super(division);
        if (kicked == null) throw new IllegalArgumentException("Player kicked cannot be null");

        this.player = kicked;
    }

    /**
     * Fetches the player kicked.
     * @return Player kicked
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
