package us.teaminceptus.divisions.wrapper;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;
import us.teaminceptus.divisions.wrapper.v1_14_R1.DivInventory1_14_R1;
import us.teaminceptus.divisions.wrapper.v1_14_R1.NBTWrapper1_14_R1;

public final class Wrapper1_14_R1 implements Wrapper {

    @NotNull
    @Override
    public DivInventory createInventory(@NotNull String identifier, int size, @NotNull String title) {
        return new DivInventory1_14_R1(identifier, size, title);
    }

    @Override
    public NBTWrapper createNBTWrapper(@NotNull ItemStack item) {
        return new NBTWrapper1_14_R1(item);
    }

}
