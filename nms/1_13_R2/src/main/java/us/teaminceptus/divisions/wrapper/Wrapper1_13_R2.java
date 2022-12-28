package us.teaminceptus.divisions.wrapper;

import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.v1_13_R2.DivInventory1_13_R2;

public final class Wrapper1_13_R2 implements Wrapper {

    @NotNull
    @Override
    public DivInventory createInventory(@NotNull String identifier, int size, @NotNull String title) {
        return new DivInventory1_13_R2(identifier, size, title);
    }

}
