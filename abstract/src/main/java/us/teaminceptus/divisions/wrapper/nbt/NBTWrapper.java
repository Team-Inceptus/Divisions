package us.teaminceptus.divisions.wrapper.nbt;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static us.teaminceptus.divisions.wrapper.Wrapper.getWrapper;

public abstract class NBTWrapper {

    protected static final String ROOT = "Divisions";

    protected ItemStack item;

    protected NBTWrapper(ItemStack item) { this.item = item; }

    public ItemStack getItem() { return item; }

    public static NBTWrapper of(ItemStack item) {
        return getWrapper().createNBTWrapper(item);
    }

    public static float getFloat(ItemStack item, String key) {
        return of(item).getFloat(key);
    }

    public static ItemStack setID(ItemStack item, String id) {
        NBTWrapper nbt = of(item);
        nbt.setID(id);
        return nbt.getItem();
    }

    public final void setID(String value) {
        set("id", value);
    }

    public final String getID() {
        return getString("id");
    }

    public final boolean hasID() { return getID() != null && !getID().isEmpty(); }

    public final boolean hasString(String key) { return getString(key) != null && !getString(key).isEmpty(); }

    public abstract String getString(String key);

    public abstract void set(String key, String value);

    public abstract boolean getBoolean(String key);

    public abstract void set(String key, boolean value);

    public abstract int getInt(String key);

    public abstract void set(String key, int value);

    public abstract double getDouble(String key);

    public abstract void set(String key, double value);

    public abstract UUID getUUID(String key);

    public abstract void set(String key, UUID value);

    public abstract Class<?> getClass(String key);

    public abstract void set(String key, Class<?> clazz);

    public abstract float getFloat(String key);

    public abstract void set(String key, float value);

    @Nullable
    public final <T> Class<? extends T> getClass(String key, Class<T> parent) {
        Class<?> clazz = getClass(key);
        return parent.isAssignableFrom(clazz) ? clazz.asSubclass(parent) : null;
    }

}
