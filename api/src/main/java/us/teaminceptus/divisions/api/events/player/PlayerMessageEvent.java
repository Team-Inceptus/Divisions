package us.teaminceptus.divisions.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.division.Division;

/**
 * Called when a Player messages in a Division
 */
public class PlayerMessageEvent extends PlayerDivisionEvent implements Cancellable {

    private String message;

    private boolean cancelled = false;

    /**
     * Constructs a PlayerMessageEvent.
     * @param division Division involved
     * @param player Player involved
     * @param message Message sent
     * @since 1.0.0
     * @throws IllegalArgumentException if division, player, or message is null
     */
    public PlayerMessageEvent(@NotNull Division division, @NotNull Player player, @NotNull String message) throws IllegalArgumentException {
        super(division, player);
        if (message == null) throw new IllegalArgumentException("Message cannot be null");
        this.message = message;
    }

    /**
     * Fetches the message sent.
     * @return Message sent
     * @since 1.0.0
     */
    @NotNull
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message sent.
     * @param message Message sent
     * @since 1.0.0
     * @throws IllegalArgumentException if message is null
     */
    public void setMessage(@NotNull String message) throws IllegalArgumentException {
        if (message == null) throw new IllegalArgumentException("Message cannot be null");
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
