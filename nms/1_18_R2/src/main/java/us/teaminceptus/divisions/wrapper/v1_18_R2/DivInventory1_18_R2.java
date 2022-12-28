package us.teaminceptus.divisions.wrapper.v1_18_R2;

import com.google.common.collect.ImmutableMap;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryCustom;
import us.teaminceptus.divisions.wrapper.DivInventory;

import java.util.HashMap;
import java.util.Map;

public final class DivInventory1_18_R2 extends CraftInventoryCustom implements DivInventory {

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public DivInventory1_18_R2(String key, int size, String title) {
        super(null, size, title);
        this.id = key;

        setAttribute("_name", title);
    }

    @Override
    public String getIdentifier() {
        return this.id;
    }

    @Override
    public Map<String, Object> getAllAttributes() {
        return ImmutableMap.copyOf(attributes);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

}
