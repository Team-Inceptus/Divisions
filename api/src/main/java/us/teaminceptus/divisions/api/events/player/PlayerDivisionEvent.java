package us.teaminceptus.divisions.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Player is involved in a Division Event.
 * @since 1.0.0
 */
public class PlayerDivisionEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Division division;

    /**
     * Constructs a PlayerDivisionEvent.
     * @param division Division involved in the event
     * @param player Player involved in the event
     * @since 1.0.0
     * @throws IllegalArgumentException if division is null
     */
    public PlayerDivisionEvent(@NotNull Division division, @Nullable Player player) throws IllegalArgumentException {
        super(player);
        if (division == null) throw new IllegalArgumentException("Division cannot be null");

        this.division = division;
    }

    /**
     * Fetches the Division involved in the event.
     * @return Division involved
     * @since 1.0.0
     */
    @NotNull
    public final Division getDivision() {
        return division;
    }

    /**
     * Fetches the handlers for this event.
     * @return Event Handlers
     * @since 1.0.0
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
