package us.teaminceptus.divisions.wrapper;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;
import us.teaminceptus.divisions.wrapper.v1_16_R3.DivInventory1_16_R3;
import us.teaminceptus.divisions.wrapper.v1_16_R3.NBTWrapper1_16_R3;

public final class Wrapper1_16_R3 implements Wrapper {

    @NotNull
    @Override
    public DivInventory createInventory(@NotNull String identifier, int size, @NotNull String title) {
        return new DivInventory1_16_R3(identifier, size, title);
    }

    @Override
    public NBTWrapper createNBTWrapper(@NotNull ItemStack item) {
        return new NBTWrapper1_16_R3(item);
    }

}
