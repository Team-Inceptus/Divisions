package us.teaminceptus.divisions.api.events.division;

import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Division is created.
 * @since 1.0.0
 */
public class DivisionCreateEvent extends DivisionEvent {

    /**
     * Constructs a DivisionCreateEvent.
     * @param division Division created
     * @since 1.0.0
     * @throws IllegalArgumentException if division is null
     */
    public DivisionCreateEvent(@NotNull Division division) throws IllegalArgumentException {
        super(division);
    }

}
