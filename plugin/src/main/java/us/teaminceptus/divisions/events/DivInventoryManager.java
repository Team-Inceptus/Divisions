package us.teaminceptus.divisions.events;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.divisions.Divisions;
import us.teaminceptus.divisions.util.inventory.ItemBuilder;
import us.teaminceptus.divisions.wrapper.DivInventory;
import us.teaminceptus.divisions.wrapper.nbt.NBTWrapper;

import java.util.Map;
import java.util.function.BiConsumer;

import static us.teaminceptus.divisions.wrapper.nbt.NBTWrapper.of;

public final class DivInventoryManager implements Listener {

    private final Divisions plugin;

    public DivInventoryManager(Divisions plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Consumers

    private static final Map<String, BiConsumer<DivInventory, InventoryClickEvent>> CLICK_ITEMS = ImmutableMap.<String, BiConsumer<DivInventory, InventoryClickEvent>>builder()

            .build();

    private static final Map<String, BiConsumer<DivInventory, InventoryClickEvent>> CLICK_INVENTORY = ImmutableMap.<String, BiConsumer<DivInventory, InventoryClickEvent>>builder()

            .build();

    // Logic

    @EventHandler
    public void click(@NotNull InventoryClickEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getClickedInventory() instanceof DivInventory)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getCurrentItem() == null) return;

        ItemStack item = e.getCurrentItem();
        if (item.isSimilar(ItemBuilder.GUI_BACKGROUND)) {
            e.setCancelled(true);
            return;
        }

        DivInventory inv = (DivInventory) e.getClickedInventory();
        e.setCancelled(inv.isCancelled());

        if (CLICK_INVENTORY.containsKey(inv.getIdentifier())) CLICK_INVENTORY.get(inv.getIdentifier()).accept(inv, e);

        NBTWrapper w = of(item);

        if (w.hasID()) {
            String id = w.getID();
            if (CLICK_ITEMS.containsKey(id)) {
                e.setCancelled(true);
                CLICK_ITEMS.get(id).accept(inv, e);
            }
        }
    }

    @EventHandler
    public void drag(@NotNull InventoryDragEvent e) {
        Inventory inv = e.getView().getTopInventory();
        if (inv == null) return;
        if (inv instanceof PlayerInventory) return;

        if (inv instanceof DivInventory) {
            DivInventory dinv = (DivInventory) inv;
            e.setCancelled(dinv.isCancelled());
        }

        for (ItemStack item : e.getNewItems().values()) {
            if (item == null) continue;
            if (item.isSimilar(ItemBuilder.GUI_BACKGROUND)) e.setCancelled(true);
        }
    }



}
