package us.teaminceptus.divisions.wrapper;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public interface DivInventory extends Inventory {

    @NotNull
    String getIdentifier();

    @Override
    @NotNull
    default String getTitle() {
        return getAttribute("_name", String.class);
    }

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
