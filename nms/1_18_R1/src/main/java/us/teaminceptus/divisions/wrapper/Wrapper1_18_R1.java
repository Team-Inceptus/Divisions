package us.teaminceptus.divisions.wrapper;

import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.v1_18_R1.DivInventory1_18_R1;

public final class Wrapper1_18_R1 implements Wrapper {

    @NotNull
    @Override
    public DivInventory createInventory(@NotNull String identifier, int size, @NotNull String title) {
        return new DivInventory1_18_R1(identifier, size, title);
    }

}
