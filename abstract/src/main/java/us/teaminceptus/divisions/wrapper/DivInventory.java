package us.teaminceptus.divisions.wrapper;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface DivInventory extends Inventory {

    @Override
    @NotNull
    default String getTitle() {
        return getAttribute("_name", String.class);
    }

    default boolean isCancelled() {
        return getAttribute("_cancelled", Boolean.class);
    }

    default void setCancelled(boolean cancelled) {
        setAttribute("_cancelled", cancelled);
    }

    default void setCancelled() {
        setCancelled(true);
    }

    @NotNull
    String getIdentifier();

    @Unmodifiable
    @NotNull
    Map<String, Object> getAllAttributes();

    default Object getAttribute(@NotNull String key) {
        return getAllAttributes().get(key);
    }

    default Object getAttribute(@NotNull String key, @Nullable Object def) {
        return getAllAttributes().getOrDefault(key, def);
    }

    default <T> T getAttribute(@NotNull String key, @NotNull Class<T> type) {
        return type.cast(getAttribute(key));
    }

    default <T> T getAttribute(@NotNull String key, @NotNull T def, @NotNull Class<T> type) {
        return type.cast(getAttribute(key, def));
    }

    void setAttribute(@NotNull String key, @Nullable Object value);

    void removeAttribute(@NotNull String key);

}
