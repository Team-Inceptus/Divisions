package us.teaminceptus.divisions;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.loader.DivLoader;

final class AdventureLoader implements DivLoader {

    private static Plugin plugin;

    public AdventureLoader(@NotNull Plugin plugin) {
        if (AdventureLoader.plugin != null) return;
        AdventureLoader.plugin = plugin;

        plugin.getLogger().config("Detected Adventure API === Using Adventure Loader");

        plugin.getLogger().config("Finished Loader{Adventure}");
    }

    // Implementation

    @Override
    public boolean isAdventureLoader() {
        return true;
    }
}
