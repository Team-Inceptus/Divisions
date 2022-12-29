package us.teaminceptus.divisions.wrapper.v1_16_R1;

import net.minecraft.server.v1_16_R1.ItemStack;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.api.DivConfig;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;

import java.util.UUID;

public final class NBTWrapper1_16_R1 extends NBTWrapper {

    public NBTWrapper1_16_R1(org.bukkit.inventory.ItemStack item) {
        super(item);
    }
    
    @Override
    public String getString(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.getString(key);
    }

    @Override
    public void set(String key, String value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setString(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public boolean getBoolean(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.getBoolean(key);
    }

    @Override
    public void set(String key, boolean value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setBoolean(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public int getInt(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.getInt(key);
    }

    @Override
    public void set(String key, int value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setInt(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public double getDouble(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.getDouble(key);
    }

    @Override
    public void set(String key, double value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setDouble(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public UUID getUUID(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.a(key);
    }

    @Override
    public void set(String key, UUID value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.a(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public Class<?> getClass(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        try {
            return Class.forName(new String(divisions.getByteArray(key)));
        } catch (ClassNotFoundException e) {
            DivConfig.print(e);
            return null;
        }
    }

    @Override
    public void set(String key, @NotNull Class<?> clazz) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setByteArray(key, clazz.getName().getBytes());
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public float getFloat(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        return divisions.getFloat(key);
    }

    @Override
    public void set(String key, float value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound divisions = tag.getCompound(ROOT);

        divisions.setFloat(key, value);
        tag.set(ROOT, divisions);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

}
