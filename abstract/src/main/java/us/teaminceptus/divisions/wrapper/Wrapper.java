package us.teaminceptus.divisions.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;

import java.util.Arrays;
import java.util.List;

public interface Wrapper {

    static boolean isCompatible() {
        return getWrapper() != null && !isOutdatedSubversion();
    }

    static boolean isOutdatedSubversion() {
        String ver = Bukkit.getBukkitVersion().split("-")[0];
        return OUTDATED_SUBVERSIONS.contains(ver);
    }

    // Only includes Sub-Versions that won't compile
    List<String> OUTDATED_SUBVERSIONS = Arrays.asList(
            "1.17",
            "1.19",
            "1.19.1"
    );

    @NotNull
    static String getServerVersion() {
        if (Bukkit.getServer() == null) return ""; // Using Test Server
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
    }

    @Nullable
    static Wrapper getWrapper() {
        try {
            return Class.forName("us.teaminceptus.divisions.wrapper.Wrapper" + getServerVersion())
                    .asSubclass(Wrapper.class)
                    .getConstructor()
                    .newInstance();
        } catch (ClassNotFoundException e) { // Using unsupported version
            return null;
        } catch (Exception e) {
            DivConfig.print(e);
        }
        return null;
    }

    static String get(String key) {
        return DivConfig.getConfiguration().get(key);
    }

    // Wrapper Implementation

    DivInventory createInventory(@NotNull String identifier, int size, @NotNull String title);

    NBTWrapper createNBTWrapper(@NotNull ItemStack item);

}
