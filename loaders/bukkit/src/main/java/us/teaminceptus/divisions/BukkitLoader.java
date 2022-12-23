package us.teaminceptus.divisions;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.loader.DivLoader;

public final class BukkitLoader implements DivLoader {

    private static Plugin plugin;

    public BukkitLoader(@NotNull Plugin plugin) {
        if (BukkitLoader.plugin != null) return;

        BukkitLoader.plugin = plugin;

        plugin.getLogger().config("Detected Spigot API === Using Bukkit Loader");

        plugin.getLogger().config("Finished Loader{Bukkit}");
    }

    // Implementation

    @Override
    public boolean isAdventureLoader() {
        return false;
    }
}
