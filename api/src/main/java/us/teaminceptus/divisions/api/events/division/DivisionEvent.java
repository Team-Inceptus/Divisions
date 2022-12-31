package us.teaminceptus.divisions.api.events.division;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Division-related Event is fired.
 * @since 1.0.0
 */
public abstract class DivisionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Division division;

    /**
     * Constructs a DivisionEvent.
     * @param division Division invovled
     * @since 1.0.0
     * @throws IllegalArgumentException if division is null
     */
    public DivisionEvent(@NotNull Division division) throws IllegalArgumentException {
        if (division == null) throw new IllegalArgumentException("Division cannot be null");
        this.division = division;
    }

    /**
     * Fetches the division involved in this event.
     * @return Division involved
     * @since 1.0.0
     */
    @NotNull
    public final Division getDivision() {
        return division;
    }

    /**
     * Fetches the HandlerList for this event.
     * @return HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
