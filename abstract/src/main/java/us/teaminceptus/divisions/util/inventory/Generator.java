package us.teaminceptus.divisions.util.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.wrapper.DivInventory;
import us.teaminceptus.divisions.wrapper.Wrapper;

public final class Generator {

    public static DivInventory genGUI(int size, String name) {
        return genGUI("", size, name);
    }

    @Nullable
    public static DivInventory genGUI(String key, int size, String name) {
        if (size < 9 || size > 54) return null;
        if (size % 9 > 0) return null;

        DivInventory inv = Wrapper.getWrapper().createInventory(key, size, name);
        ItemStack bg = ItemBuilder.GUI_BACKGROUND;

        if (size < 27) return inv;

        for (int i = 0; i < 9; i++) inv.setItem(i, bg);
        for (int i = size - 9; i < size; i++) inv.setItem(i, bg);
        for (int i = 1; i < Math.floor((double) size / 9D) - 1; i++) {
            inv.setItem(i * 9, bg);
            inv.setItem(((i + 1) * 9) - 1, bg);
        }

        return inv;
    }

}
