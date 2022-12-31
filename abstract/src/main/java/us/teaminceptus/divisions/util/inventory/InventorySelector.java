package us.teaminceptus.divisions.util.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.wrapper.DivInventory;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;

import static us.teaminceptus.divisions.wrapper.Wrapper.get;
import static us.teaminceptus.divisions.wrapper.nbt.NBTWrapper.of;

public final class InventorySelector {

    public static void confirm(@NotNull Player p, @NotNull Runnable confirmR, @NotNull Runnable cancelR) {
        DivInventory inv = Generator.genGUI("confirm_inv", 27, get("menu.are_you_sure"));
        inv.setCancelled();
        inv.setAttribute("confirm_action", confirmR);
        inv.setAttribute("cancel_action", cancelR);

        ItemStack confirm = new ItemStack(Material.LIME_WOOL);
        ItemMeta cMeta = confirm.getItemMeta();
        cMeta.setDisplayName(ChatColor.GREEN + get("constants.confirm"));
        confirm.setItemMeta(cMeta);

        NBTWrapper confirmNBT = of(confirm);
        confirmNBT.set("item", "confirm");
        confirm = confirmNBT.getItem();
        inv.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta caMeta = cancel.getItemMeta();
        caMeta.setDisplayName(ChatColor.RED + get("constants.cancel"));
        cancel.setItemMeta(caMeta);

        NBTWrapper cancelNBT = of(cancel);
        cancelNBT.set("item", "cancel");
        cancel = cancelNBT.getItem();
        inv.setItem(15, cancel);

        p.openInventory(inv);
    }

}
