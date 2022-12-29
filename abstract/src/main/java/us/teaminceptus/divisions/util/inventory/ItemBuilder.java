package us.teaminceptus.divisions.util.inventory;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.Wrapper;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;

import java.util.Arrays;
import java.util.function.Consumer;

public final class ItemBuilder {

    private ItemStack item;

    private static boolean loaded = false;

    public static void loadItems() {
        if (loaded) throw new IllegalArgumentException("Items already loaded!");

        SAVE = ItemBuilder.of(Material.LIME_WOOL)
                .name(ChatColor.GREEN + Wrapper.get("constants.save"))
                .nbt(nbt -> nbt.set("item", "save"))
                .build();

        GUI_BACKGROUND = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE)
                .id("gui_background")
                .name(" ")
                .build();

        loaded = true;
    }

    public static ItemStack GUI_BACKGROUND;
    public static ItemStack SAVE;


    private ItemBuilder(ItemStack item) {
        this.item = item;
    }

    @NotNull
    public static ItemBuilder of(@NotNull ItemStack item) {
        return new ItemBuilder(item);
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material m) {
        return of(m, 1);
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material m, int amount) {
        return of(m, amount, 0);
    }

    @NotNull
    public static ItemBuilder ofHead(@NotNull String head) {
        return of(DivInventoryUtil.getHead(head));
    }

    public static ItemBuilder of(@NotNull Material m, int amount, int data) {
        return new ItemBuilder(new ItemStack(m, amount, (short) data));
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder name(@NotNull String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder id(@NotNull String id) {
        NBTWrapper w = NBTWrapper.of(item);
        w.setID(id);
        this.item = w.getItem();
        return this;
    }

    public ItemBuilder nbt(@NotNull Consumer<NBTWrapper> nbt) {
        NBTWrapper w = NBTWrapper.of(item);
        nbt.accept(w);
        this.item = w.getItem();
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(@NotNull Iterable<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(ImmutableList.copyOf(lore));
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

}
